package com.rhewumapp.Activity;


import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.rhewumapp.Activity.Adapter.VibCheckerAdapter;
import com.rhewumapp.Activity.Fragments.PsdFragment;
import com.rhewumapp.Activity.Fragments.SummeryFragment;
import com.rhewumapp.Activity.MeshConveterData.Constants;
import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.Activity.VibcheckerGraph.PlotViewMaxValues;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.PsdSummaryDao;
import com.rhewumapp.Activity.database.RawDao;
import com.rhewumapp.Activity.database.RawSensor;
import com.rhewumapp.Activity.database.RawSensorDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.databinding.ActivityVibCheckerListBinding;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class VibChecker_ListActivity extends DrawerBaseActivity {
    private TabLayout tab_layout;
    private ViewPager2 viewPager;
    private VibCheckerAdapter vibCheckerAdapter;
    TextView txtBack;
    ImageView imgBack,img_direction;
    Button archive,bt_share;
    float XMaxAcceleration;
    public String currentDateTime,listId;

    private Calendar calendar;
    TextView txtZZ,txtX,txty,txtfrZ,txtfrx,txtfry,
            txtampz,txtampx,txtampy,
            txtDateTime;
    private Context context;
    static float accelerationX;
    static float accelerationY;
    static float accelerationZ;

    private float[] buffer;
    RelativeLayout relativeLayout;
    public ArrayList<Double> measurementListDouble = new ArrayList<>();
    private ArrayList<MeasurementDao> measurementList = new ArrayList<>();
    List<PsdSummaryDao> psdSummaryDaoArrayList;
    public String mailBody = "<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><head><meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" /></head><body><table width=\\\"100%%\\\" border=\\\"0\\\" cellspacing=\\\"15\\\" cellpadding=\\\"0\\\"><tr><td>Dear user,<br />Please find attached the results of your measurements with the RHEWUM VibSonic App as of [HTML_DATE_STRING]</td></tr><br><br><tr><td>We hope that our service was of use to you. Please do not hesitate to contact us if you need any more information, more precise measurements or a personal consultation.</td></tr><br><br><tr><td>We are looking forward to support you and your project ideas.</td></tr><br><br><tr><td>RHEWUM GmbH<br />Rosentalstr. 24<br />42899 Remscheid<br />Germany</td></tr><br><br><tr><td>Mail : <a href=\"mailto:info@rhewum.com\">info@rhewum.com</a><br /> Web: <a href=\"http://www.rhewum.com\">http://www.rhewum.com</a></td></tr><tr><td>&nbsp;</td></tr></table></body></html>";
    ActivityVibCheckerListBinding activityVibCheckerListBinding;
    private ArrayList<VibCheckerSummaryDao> SummeryList;
    private String mailSubject = "Result of RHEWUM Summary App";
    static float replace2,valueYaxis,valueZaxis,amplitudeX,amplitudeY,amplitudeZ;
    static float xDominantFrequency;
    static float yDominantFrequency;
    static float zDominantFrequency;
    List<RawDao> vibCheckerSummaryDaoArrayLists;
    List<RawSensor>rawSensorList;
    List<RawSensorDao>rawSensorDaoList;
    RhewumDbHelper dbHelper;
    LinearLayout ll_plots;
    //plot
    private Handler uiHandler;
    private PlotViewMaxValues pvPlot;

    public static float dominantXFrequency(){
        return xDominantFrequency;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_vib_checker_list);
        activityVibCheckerListBinding = ActivityVibCheckerListBinding.inflate(getLayoutInflater());
        setContentView(activityVibCheckerListBinding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
        initObjects();

        // click on info image
        img_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the custom dialog layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_image, null);

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(VibChecker_ListActivity.this);
                builder.setView(dialogView)
                        .setCancelable(true); // Makes the dialog dismissible

                // Display the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        // Retrieve buffer from the intent
        float[] buffer = getIntent().getFloatArrayExtra("sensor_data");

        // Initialize GUI with the retrieved buffer
        if (buffer != null) {
            Log.d("BufferCheck", "Buffer received with size in Intent: " + buffer.length);
            initGUI(buffer);
        } else {
            Log.d("BufferData", "No buffer data received");
        }

        // Retrieve the float values from Intent extras
         replace2 = getIntent().getFloatExtra("replace2", 0.0f);
         valueYaxis = getIntent().getFloatExtra("valueYaxis", 0.0f);
         valueZaxis = getIntent().getFloatExtra("valueZaxis", 0.0f);
        String dateValue = getIntent().getStringExtra("dateValue");
        txtDateTime.setText(" " + dateValue);
        Log.d("DateVAlue","Getintent Date "+dateValue);

         xDominantFrequency = getIntent().getFloatExtra("dominantFreqX", 0.0f);
         yDominantFrequency = getIntent().getFloatExtra("dominantFreqY", 0.0f);
         zDominantFrequency = getIntent().getFloatExtra("dominantFreqZ", 0.0f);

        Log.d("VibChecker_ListActivity", "Frequency_X: " + xDominantFrequency);
        Log.d("VibChecker_ListActivity", "Frequency_Y: " + yDominantFrequency);
        Log.d("VibChecker_ListActivity", "Frequency_Z: " + zDominantFrequency);

        amplitudeX = getIntent().getFloatExtra("amplitudeX", 0.0f);
        amplitudeY = getIntent().getFloatExtra("amplitudeY", 0.0f);
        amplitudeZ = getIntent().getFloatExtra("amplitudeZ", 0.0f);
        listId = String.valueOf(getIntent().getIntExtra("id",0));

        Log.e("LastId","LastId:::::::"+listId);


//        // Set the values in the TextViews
//        txtX.setText(String.format(Locale.US, "X: %.2f", replace2 +"/n "));

        txtX.setText(Html.fromHtml(String.format(Locale.US, "X: %.2f<br>m/s²", replace2)));

        txty.setText(Html.fromHtml(String.format(Locale.US, "Y: %.2f<br>m/s²", valueYaxis)));
        txtZZ.setText(Html.fromHtml(String.format(Locale.US, "Z: %.2f<br>m/s²", valueZaxis)));
        txtfrZ.setText(Html.fromHtml(String.format(Locale.US, "Z: %.2f<br>Hz", zDominantFrequency)));
        txtfrx.setText(Html.fromHtml(String.format(Locale.US, "X: %.2f<br>Hz", xDominantFrequency)));
        txtfry.setText(Html.fromHtml(String.format(Locale.US, "Y: %.2f<br>Hz", yDominantFrequency)));

        txtampz.setText(Html.fromHtml(String.format(Locale.US, "Z: %.2f<br>mm", amplitudeZ)));
        txtampy.setText(Html.fromHtml(String.format(Locale.US, "Y: %.2f<br>mm", amplitudeY)));
        txtampx.setText(Html.fromHtml(String.format(Locale.US, "X: %.2f<br>mm", amplitudeX)));


        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });

       //archieve activity
       archive.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
               Intent intent = new Intent(VibChecker_ListActivity.this,VibChekerArchiveActivity.class);
               startActivity(intent);


           }
       });

       //share activity
        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(VibChecker_ListActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createPdf();
                        progressDialog.dismiss();
                        try {
                            callMoreMenus();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, 1000);

            }
        });


    }

    //call more menus
    public void callMoreMenus() throws IOException {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.vib_checker_more);
        dialog.setTitle(getResources().getString(R.string.app_name));
        //csv
        ((RelativeLayout) dialog.findViewById(R.id.export_csv_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
              //  vibCheckerSummaryDaoArrayLists = dbHelper.getLatestRawValues(100);
              //  rawSensorList=dbHelper.fetchSensorDataByRecordId(Integer.parseInt(listId));
                List<RawSensorDao> rawSensorList = dbHelper.fetchSensorDataById(Integer.parseInt(listId));
                Log.e("VibcheckerList","VibcheckerList"+rawSensorList.size());
                if (!rawSensorList.isEmpty()) {

                    Log.e("FRAGMENT", "FRAGMENT LIST" + rawSensorList.size());
                    // Step 2: Get the CSV file reference
                    File csvFile = new File(getCacheDir() + "/" + getResources().getString(R.string.psd_summary) + ".csv");
                     try {
                        saveVibCheckerDataToCSV((context), (ArrayList<RawSensorDao>) rawSensorList, csvFile);
                        // Step 3: Share the CSV file via email
                        shareCSVFileViaEmail(VibChecker_ListActivity.this, csvFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(VibChecker_ListActivity.this, "Error saving CSV file.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("FRAGMENT", "FRAGMENT LIST " + rawSensorList.size());
                    Toast.makeText(VibChecker_ListActivity.this, "Save the data first?", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //pdf
        ((RelativeLayout) dialog.findViewById(R.id.export_pdf_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] strArr = {VibChecker_ListActivity.this.getCacheDir() + "/" + "VibChecker Summary" + ".pdf"};
                ArrayList<Uri> arrayList = new ArrayList<>();
                for (int i = 0; i < 1; i++) {
                    File file = new File(strArr[i]);
                    if (file.exists()) {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] bArr = new byte[fileInputStream.available()];
                            fileInputStream.read(bArr);
                            fileInputStream.close();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(bArr);
                            fileOutputStream.close();
                            arrayList.add(FileProvider.getUriForFile(VibChecker_ListActivity.this, "com.rhewumapp.provider", file));
                        } catch (Exception e) {
                            Log.e("VibChecker_ListActivity","VibChecker_ListActivity:::"+e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                   String unused = currentDateTime = Utils.getCurrentDateTime();
                    VibChecker_ListActivity vibSonicArchiveActivity = VibChecker_ListActivity.this;
                    String unused2 = vibSonicArchiveActivity.mailBody = vibSonicArchiveActivity.mailBody.replace("HTML_DATE_STRING", VibChecker_ListActivity.this.currentDateTime);
                    VibChecker_ListActivity.this.sendEmail(arrayList);
                    dialog.dismiss();
                }
                createSendFile();
            }

        });

        //cancel dialog
        ((RelativeLayout) dialog.findViewById(R.id.cancel_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    ///adeed new
    //added new for rawx
    public void saveVibCheckerDataToCSV(Context context, ArrayList<RawSensorDao> vibCheckerSummaryDaoArrayList, File file) throws IOException {
        // Check if external storage is available
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("CSV Creation", "External storage not mounted or writable");
            return;
        }

        // Create the CSV file in cache directory
        if (!file.exists() && !file.createNewFile()) {
            Toast.makeText(context, "Error creating file.", Toast.LENGTH_LONG).show();
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.append("DateTime,Time [Ms],xRawValue,yRawValue,zRawValue\n");

            // Write data to CSV
            for (RawSensorDao dao : vibCheckerSummaryDaoArrayList) {

                writer.append(String.valueOf(dao.getDateTime()))
                        .append(",")
                        .append(String.valueOf(dao.getTime()))
                        .append(",")
                        .append(String.valueOf(dao.getxAxisRawValue()))
                        .append(",")
                        .append(String.valueOf(dao.getyAxisRawValue()))
                        .append(",")
                        .append(String.valueOf(dao.getzAxisRawValue()))
                        .append(",")
                        .append("\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    //create and send file
    private void createSendFile() {
        String[] strArr = {getCacheDir() + "/" + getResources().getString(R.string.summary) + ".pdf"};
        ArrayList<Uri> arrayList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            File file = new File(strArr[i]);
            if (file.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] bArr = new byte[fileInputStream.available()];
                    fileInputStream.read(bArr);
                    fileInputStream.close();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bArr);
                    fileOutputStream.close();
                    arrayList.add(FileProvider.getUriForFile(VibChecker_ListActivity.this, "com.rhewumapp.provider", file));
                } catch (Exception e) {
                    Log.e("Summary", "Summary:::" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            sendEmail(arrayList);
        }
    }

    //send email
    public void sendEmail(ArrayList<Uri> arrayList) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("message/rfc822");
        // Convert Html.fromHtml to a CharSequence
        CharSequence mailBodyText = Html.fromHtml(this.mailBody);
        // Create an ArrayList of CharSequence and add mailBodyText
        ArrayList<CharSequence> textArrayList = new ArrayList<>();
        textArrayList.add(mailBodyText);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.putExtra(Intent.EXTRA_SUBJECT, this.mailSubject);
        intent.putCharSequenceArrayListExtra(Intent.EXTRA_TEXT, textArrayList);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Ensure arrayList is not null
        if (arrayList != null && !arrayList.isEmpty()) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
        }
        startActivity(Intent.createChooser(intent, getString(R.string.choose_email)));
    }

    //csv file
    //added csv file
    @SuppressLint("QueryPermissionsNeeded")
    public void shareCSVFileViaEmail(Context context, File csvFile) {
        if (csvFile.exists()) {
            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", csvFile);
            Log.e("PSD", context.getPackageName() + ".provider");

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/csv");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Displacement Data CSV");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the attached CSV file containing the displacement data.");
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(emailIntent, "Send email using:"));
            } else {
                Toast.makeText(context, "No email client found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "CSV file not found.", Toast.LENGTH_SHORT).show();
        }
    }

    //craedte pdf
    public void createPdf() {
        File file = new File(getCacheDir() + "/" + getResources().getString(R.string.summary) + ".pdf");
        if (file.exists() && !file.delete()) {
            Toast.makeText(this, "SOMETHING WENT WRONG CREATING THE PDF FILE", Toast.LENGTH_LONG).show();
        }
        try {
            if (file.createNewFile()) {
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, Files.newOutputStream(file.toPath()));
                    document.open();
                    addContent(document);
                    document.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "SOMETHING WENT WRONG CREATING THE PDF FILE", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    //add content
    private void addContent(Document document) throws DocumentException {
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 0);
        Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 0, Constants.blueColor);
        Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 1);
        Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 4, Constants.blueColorLink);
        try {
            Bitmap decodeStream = BitmapFactory.decodeStream(getAssets().open("rhewumLogo.jpg"));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            decodeStream.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            Image instance = Image.getInstance(byteArrayOutputStream.toByteArray());
            instance.scalePercent(50.0f);
            Paragraph paragraph = new Paragraph();
            Phrase phrase = new Phrase();
            Phrase phrase2 = new Phrase();
            Phrase phrase3 = new Phrase();
            Phrase phrase4 = new Phrase();
            Phrase phrase5 = new Phrase();
            Phrase phrase6 = new Phrase();
            paragraph.add((Element) instance);
            addEmptyLine(paragraph, 1);
            paragraph.add((Element) new Paragraph(getResources().getString(R.string.pdf_title), font));
            addEmptyLine(paragraph, 1);
            paragraph.add((Element) new Paragraph(getResources().getString(R.string.pdf_sub_titleVibChecker), font2));
            addEmptyLine(paragraph, 1);
            //create table for data
            createTable(paragraph, font3);
            addEmptyLine(paragraph, 1);
            paragraph.add((Element) new Paragraph(getResources().getString(R.string.pdf_html_1), font));
            addEmptyLine(paragraph, 1);
            paragraph.add((Element) new Paragraph(getResources().getString(R.string.pdf_html_2), font));
            addEmptyLine(paragraph, 1);
            phrase.add((Element) new Chunk(getResources().getString(R.string.pdf_html_3), font));
            paragraph.add((Element) new Paragraph(phrase));
            phrase2.add((Element) new Chunk(getResources().getString(R.string.pdf_html_4), font));
            paragraph.add((Element) new Paragraph(phrase2));
            phrase3.add((Element) new Chunk(getResources().getString(R.string.pdf_html_5), font));
            paragraph.add((Element) new Paragraph(phrase3));
            phrase4.add((Element) new Chunk(getResources().getString(R.string.pdf_html_6), font));
            paragraph.add((Element) new Paragraph(phrase4));
            phrase5.add((Element) new Chunk(getResources().getString(R.string.pdf_html_7), font));
            phrase5.add((Element) new Chunk(getResources().getString(R.string.pdf_html_8), font4));
            paragraph.add((Element) new Paragraph(phrase5));
            phrase6.add((Element) new Chunk(getResources().getString(R.string.pdf_html_9), font));
            phrase6.add((Element) new Chunk(getResources().getString(R.string.pdf_html_10), font4));
            paragraph.add((Element) new Paragraph(phrase6));
            document.add(paragraph);
        } catch (IOException unused) {
        }
    }

    //add empty line & create table
    private void createTable(Paragraph paragraph, Font font) throws BadElementException {
        PdfPTable pdfPTable = new PdfPTable(2);
        pdfPTable.setWidthPercentage(100.0f);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        decimalFormatSymbols.setDecimalSeparator(ClassUtils.PACKAGE_SEPARATOR_CHAR);
        decimalFormatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("##.##", decimalFormatSymbols);


        PdfPCell pdfPCell = new PdfPCell(new Phrase("Data", font));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell);

        PdfPCell pdfPCell2 = new PdfPCell(new Phrase("Value", font));
        pdfPCell2.setHorizontalAlignment(1);
        pdfPCell2.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell2);

        pdfPTable.setHeaderRows(1);
        PdfPCell pdfPCell3 = new PdfPCell(new Phrase("Peak Acceleration X"));
        pdfPCell3.setHorizontalAlignment(1);
        pdfPCell3.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell3);

        PdfPCell pdfPCell4 = new PdfPCell(new Phrase(decimalFormat.format(replace2)));
        pdfPCell4.setHorizontalAlignment(1);
        pdfPCell4.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell4);

        PdfPCell pdfPCell5 = new PdfPCell(new Phrase("Peak Acceleration Y"));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell5);

        PdfPCell pdfPCell6 = new PdfPCell(new Phrase(decimalFormat.format(valueYaxis)));
        pdfPCell6.setHorizontalAlignment(1);
        pdfPCell6.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell6);


        PdfPCell pdfPCell8 = new PdfPCell(new Phrase("Peak Acceleration Z"));
        pdfPCell8.setHorizontalAlignment(1);
        pdfPCell8.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell8);

        PdfPCell pdfPCell9 = new PdfPCell(new Phrase(decimalFormat.format(valueZaxis)));
        pdfPCell9.setHorizontalAlignment(1);
        pdfPCell9.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell9);

        //Frequency
        PdfPCell pdfPCell7 = new PdfPCell(new Phrase("Peak Frequency X"));
        pdfPCell7.setHorizontalAlignment(1);
        pdfPCell7.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell7);

        PdfPCell pdfPCell12 = new PdfPCell(new Phrase(decimalFormat.format(xDominantFrequency)));
        pdfPCell12.setHorizontalAlignment(1);
        pdfPCell12.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell12);

        PdfPCell pdfPCell13 = new PdfPCell(new Phrase("Peak Frequency Y"));
        pdfPCell13.setHorizontalAlignment(1);
        pdfPCell13.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell13);

        PdfPCell pdfPCell10 = new PdfPCell(new Phrase(decimalFormat.format(yDominantFrequency)));
        pdfPCell10.setHorizontalAlignment(1);
        pdfPCell10.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell10);

        PdfPCell pdfPCell11 = new PdfPCell(new Phrase("Peak Frequency Z"));
        pdfPCell11.setHorizontalAlignment(1);
        pdfPCell11.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell11);

        PdfPCell pdfPCell15 = new PdfPCell(new Phrase(decimalFormat.format(zDominantFrequency)));
        pdfPCell15.setHorizontalAlignment(1);
        pdfPCell15.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell15);

        //amplitude added
        PdfPCell pdfPCell16 = new PdfPCell(new Phrase("Amplitude X"));
        pdfPCell16.setHorizontalAlignment(1);
        pdfPCell16.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell16);

        PdfPCell pdfPCell17 = new PdfPCell(new Phrase(decimalFormat.format(amplitudeX)));
        pdfPCell17.setHorizontalAlignment(1);
        pdfPCell17.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell17);


        PdfPCell pdfPCell18 = new PdfPCell(new Phrase("Amplitude Y"));
        pdfPCell18.setHorizontalAlignment(1);
        pdfPCell18.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell18);

        PdfPCell pdfPCell19 = new PdfPCell(new Phrase(decimalFormat.format(amplitudeY)));
        pdfPCell19.setHorizontalAlignment(1);
        pdfPCell19.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell19);


        PdfPCell pdfPCell20 = new PdfPCell(new Phrase("Amplitude Z"));
        pdfPCell20.setHorizontalAlignment(1);
        pdfPCell20.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell20);

        PdfPCell pdfPCell21 = new PdfPCell(new Phrase(decimalFormat.format(amplitudeZ)));
        pdfPCell21.setHorizontalAlignment(1);
        pdfPCell21.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell21);

        paragraph.add((Element) pdfPTable);
    }

    private void addEmptyLine(Paragraph paragraph, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            paragraph.add((Element) new Paragraph(StringUtils.SPACE));
        }
    }

    private void handleMaxAcceleration() {
        // max acceleration x
        XMaxAcceleration = VibChecker_ListActivity.maxXAcceleration();
        float YMaxAcceleration = SummeryFragment.maxYAcceleration();
        float ZMaxAcceleration = SummeryFragment.maxZAcceleration();
        String timer=SummeryFragment.timer();

        PsdFragment.xMaxAcceleration(XMaxAcceleration);
        PsdFragment.yMaxAcceleration(YMaxAcceleration);
        PsdFragment.zMaxAcceleration(ZMaxAcceleration);
    }

    public static float maxXAcceleration(){
        return accelerationX;
    }

    private void handleMaxFrequency() {
        // max dominant frequency
        float XMaxFrequency = SummeryFragment.dominantXFrequency();
        float YMaxFrequency = SummeryFragment.dominantYFrequency();
        float ZMaxFrequency = SummeryFragment.dominantZFrequency();

        PsdFragment.xMaxFrequency(XMaxFrequency);
        PsdFragment.yMaxFrequency(YMaxFrequency);
        PsdFragment.zMaxFrequency(ZMaxFrequency);
    }


//    private void initObjects(){
//        txtZZ=findViewById(R.id.txtZZ);
//        txty=findViewById(R.id.txty);
//        txtX=findViewById(R.id.txtx);
//        txtfrZ=findViewById(R.id.txtfrZ);
//        txtfrx=findViewById(R.id.txtfrx);
//        txtfry=findViewById(R.id.txtfry);
//        txtampz=findViewById(R.id.txtampZ);
//        txtampy=findViewById(R.id.txtampy);
//        txtampx=findViewById(R.id.txtampx);
//
//        txtDateTime=findViewById(R.id.txtDateTime);
//        txtBack=findViewById(R.id.activity_vib_sonic_archive_list_back_tv);
//        imgBack=findViewById(R.id.activity_vib_sonic_archive_list_back_iv);
//        relativeLayout=findViewById(R.id.activity_vib_sonic_archive_list_back_layout);
//        archive=findViewById(R.id.bt_save);
//        bt_share=findViewById(R.id.bt_share);
////        dbHelper = new RhewumDbHelper(context);
//        // Initialize the database helper
//        dbHelper = OpenHelperManager.getHelper(this, RhewumDbHelper.class);
//        calendar = Calendar.getInstance();
//
//    }


    private void initObjects(){
        img_direction=findViewById(R.id.img_direction);
        txtZZ=findViewById(R.id.txtZZ);
        txty=findViewById(R.id.txty);
        txtX=findViewById(R.id.txtx);

        txtfrZ=findViewById(R.id.pftxtZZ);
        txtfrx=findViewById(R.id.pftxtx);
        txtfry=findViewById(R.id.pftxty);

        txtampz=findViewById(R.id.amptxtZZ);
        txtampy=findViewById(R.id.amptxty);
        txtampx=findViewById(R.id.amptxtx);

        txtDateTime=findViewById(R.id.txtDateTime);
        txtBack=findViewById(R.id.activity_vib_sonic_archive_list_back_tv);
        imgBack=findViewById(R.id.activity_vib_sonic_archive_list_back_iv);
        relativeLayout=findViewById(R.id.activity_vib_sonic_archive_list_back_layout);
        archive=findViewById(R.id.bt_save);
        bt_share=findViewById(R.id.bt_share);
//        dbHelper = new RhewumDbHelper(context);
        // Initialize the database helper
        dbHelper = OpenHelperManager.getHelper(this, RhewumDbHelper.class);
        calendar = Calendar.getInstance();
        // In your onCreate() method in VibChecker_ListActivity
        ll_plots = findViewById(R.id.vibrationLineCharts);


    }


    public void onBackPressed() {
        super.onBackPressed();
      // Cancel any scheduled stops
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    //taken from summary fragment
    private void initGUI(float[] buffer) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Log.d("BufferCheck", "Buffer received with size: " + buffer.length);
        pvPlot = new PlotViewMaxValues(getApplicationContext());
        pvPlot.setBuffer(buffer);
        pvPlot.setNumChannels(3);
        pvPlot.setChannel(0); // Not used but kept for compatibility
        pvPlot.setBackgroundColor(0xFFFFFF);
        pvPlot.setColor(0, R.color.black); // black for X
        pvPlot.setColor(1, 0xFFFF0000); // Red for Y
        pvPlot.setColor(2, 0xFF0000FF); // Blue for Z
        pvPlot.setLineWidth(3);

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll_plots.addView(pvPlot, params);
    }

}