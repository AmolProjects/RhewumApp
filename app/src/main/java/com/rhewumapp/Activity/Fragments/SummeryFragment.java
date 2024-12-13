package com.rhewumapp.Activity.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.itextpdf.text.pdf.security.SecurityConstants;
import com.rhewumapp.Activity.Adapter.VibCheckerArchiveAdapter;
import com.rhewumapp.Activity.MeshConveterData.Constants;
import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.Activity.VibChekerArchiveActivity;
import com.rhewumapp.Activity.VibSonicArchiveActivity;
import com.rhewumapp.Activity.VibcheckerGraph.PlotViewMaxValues;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.PsdSummaryDao;
import com.rhewumapp.Activity.database.RawDao;
import com.rhewumapp.Activity.database.RawSensorDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.Activity.interfaces.VibCheckerDeleteListner;
import com.rhewumapp.R;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SummeryFragment extends Fragment implements VibCheckerDeleteListner {
    View view;
    TextView txtZZ, txtX, txty, txtfrZ, txtfrx, txtfry,txtampZ, txtampY,txtampX,txtDateTime;
    LinearLayout ll_plots;
    private PlotViewMaxValues pvPlot;
    private float[] buffer;
    static float accelerationX;
    static float accelerationY;
    static float accelerationZ;

    static float xDominantFrequency;
    static float yDominantFrequency;
    static float zDominantFrequency;
    //added
    static float xAmplitude;
    static float yAmplitude;
    static float zAmplitude;
    // declare the frequency magnitude list
    private static List<Float> xFrequencyMagnitude = new ArrayList<>();
    private static List<Float> yFrequencyMagnitude = new ArrayList<>();
    private static List<Float> zFrequencyMagnitude = new ArrayList<>();
    // declare the displacement list
    private static List<Float> xDisplacement = new ArrayList<>();
    private static List<Float> yDisplacement = new ArrayList<>();
    private static List<Float> zDisplacement = new ArrayList<>();
    private Calendar calendar;
    Button bt_archieve, bt_share;
    RhewumDbHelper dbHelper;
    ArrayList<VibCheckerSummaryDao> vibCheckerAccList;
    private Handler handler = new Handler();
    private Runnable runnable;
    public String mailBody = "<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><head><meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" /></head><body><table width=\\\"100%%\\\" border=\\\"0\\\" cellspacing=\\\"15\\\" cellpadding=\\\"0\\\"><tr><td>Dear user,<br />Please find attached the results of your measurements with the RHEWUM VibSonic App as of [HTML_DATE_STRING]</td></tr><br><br><tr><td>We hope that our service was of use to you. Please do not hesitate to contact us if you need any more information, more precise measurements or a personal consultation.</td></tr><br><br><tr><td>We are looking forward to support you and your project ideas.</td></tr><br><br><tr><td>RHEWUM GmbH<br />Rosentalstr. 24<br />42899 Remscheid<br />Germany</td></tr><br><br><tr><td>Mail : <a href=\"mailto:info@rhewum.com\">info@rhewum.com</a><br /> Web: <a href=\"http://www.rhewum.com\">http://www.rhewum.com</a></td></tr><tr><td>&nbsp;</td></tr></table></body></html>";
    private String mailSubject = "Result of RHEWUM Summary App";
    static String timer; // Replace with your actual value
    public String jumpFrom;
    public String currentDateTime;
    List<PsdSummaryDao> psdSummaryDaoArrayList;
//    List<VibCheckerSummaryDao> vibCheckerSummaryDaoArrayList;
    List<RawSensorDao> vibCheckerSummaryDaoArrayLists;
    RhewumDbHelper databaseHelper = new RhewumDbHelper(getContext());


    public SummeryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_summery, container, false);
        initObjects();
        getData();
        initGUI();
       displayPeakFrequencies();
        onClickUi();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initObjects() {
        txtZZ = view.findViewById(R.id.txtZZ);
        txty = view.findViewById(R.id.txty);
        txtX = view.findViewById(R.id.txtx);

        txtfrZ = view.findViewById(R.id.txtfrZ);
        txtfry = view.findViewById(R.id.txtfry);
        txtfrx = view.findViewById(R.id.txtfrx);

        txtampZ = view.findViewById(R.id.txtampZ);
        txtampY = view.findViewById(R.id.txtampy);
        txtampX = view.findViewById(R.id.txtampx);


        txtfry = view.findViewById(R.id.txtfry);
        ll_plots = view.findViewById(R.id.vibrationLineChart);
        txtDateTime = view.findViewById(R.id.txtDateTime);
        vibCheckerAccList = new ArrayList<>();
        bt_archieve = view.findViewById(R.id.bt_save);
        bt_share = view.findViewById(R.id.bt_share);
        dbHelper = new RhewumDbHelper(getActivity());
        calendar = Calendar.getInstance();
        txtDateTime.setText("New Record " + new SimpleDateFormat("MM-dd-yyyy,hh:mm:ss a", Locale.US).format(calendar.getTime()));
    }

    private void onClickUi() {
        // click on button save
        bt_archieve.setOnClickListener(v -> {
            // Add accelerometer data to the database
//                Log.e("Timer",);
//                dbHelper.maxAccelerometerData(accelerationX, accelerationY, accelerationZ,timer);

            // dbHelper.maxDominantFrequencyData(xDominantFrequency, yDominantFrequency, zDominantFrequency);

            runnable = new Runnable() {
                @Override
                public void run() {
                    vibCheckerAccList = dbHelper.getVibCheckerAcc();
//                        createPdf();
                    startActivity(new Intent(getActivity(), VibChekerArchiveActivity.class));

                }
            };
            handler.postDelayed(runnable, 1000);

        });


        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SummeryFragment.this.createPdf();
                        progressDialog.dismiss();
                        SummeryFragment.this.callMoreMenus();
                    }
                }, 1000);

            }
        });
    }

    public void callMoreMenus() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.vib_checker_more);
        dialog.setTitle(getResources().getString(R.string.app_name));

        //csv file
        ((RelativeLayout) dialog.findViewById(R.id.export_csv_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                vibCheckerSummaryDaoArrayLists = dbHelper.fetchSensorRawData();

                if (!vibCheckerSummaryDaoArrayLists.isEmpty()) {

                    Log.e("FRAGMENT", "FRAGMENT LIST" + vibCheckerSummaryDaoArrayLists.size());
                    // Step 2: Get the CSV file reference
                    File csvFile = new File(requireActivity().getCacheDir() + "/" + getResources().getString(R.string.psd_summary) + ".csv");
                    // Step 3: Share the CSV file via email
//                    shareCSVFileViaEmail(getActivity(), csvFile);
//                } else {
//                    Log.e("FRAGMENT", "FRAGMENT LIST" + psdSummaryDaoArrayList.size());
////                    Toast.makeText(getActivity(),"Save the data first ?",Toast.LENGTH_SHORT).show();
//                }
//            }

                    try {
                        saveVibCheckerDataToCSV((Context) requireActivity(), (ArrayList<RawSensorDao>) vibCheckerSummaryDaoArrayLists, csvFile);
                        // Step 3: Share the CSV file via email
                        shareCSVFileViaEmail(requireActivity(), csvFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireActivity(), "Error saving CSV file.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("FRAGMENT", "FRAGMENT LIST " + vibCheckerSummaryDaoArrayLists.size());
                    Toast.makeText(requireActivity(), "Save the data first?", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //pdf send
        ((RelativeLayout) dialog.findViewById(R.id.export_pdf_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
        }  catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void createSendFile() {
        String[] strArr = {requireActivity().getCacheDir() + "/" + requireActivity().getResources().getString(R.string.summary) + ".pdf"};
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
                    arrayList.add(FileProvider.getUriForFile(requireActivity(), "com.rhewumapp.provider", file));
                } catch (Exception e) {
                    Log.e("Summary", "Summary:::" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            sendEmail(arrayList);
        }
    }

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


    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unchecked")
    private void getData() {
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            accelerationX = intent.getFloatExtra("accelerationMax_X", 0);
            accelerationY = intent.getFloatExtra("accelerationMax_Y", 0);
            accelerationZ = intent.getFloatExtra("accelerationMax_Z", 0);

            //added for ampltitude to get data from VibChekaerAcclerometer
            xAmplitude = intent.getFloatExtra("displacementAmplitudeX", 0);
            yAmplitude = intent.getFloatExtra("displacementAmplitudeY", 0);
            zAmplitude = intent.getFloatExtra("displacementAmplitudeZ", 0);

            timer = String.valueOf(intent.getIntExtra("timer",0));
            buffer = intent.getFloatArrayExtra("sensor_data");

            xDominantFrequency = intent.getFloatExtra("Frequency_X", 0);
            yDominantFrequency = intent.getFloatExtra("Frequency_Y", 0);
            zDominantFrequency = intent.getFloatExtra("Frequency_Z", 0);

            Bundle args = intent.getBundleExtra("BUNDLE");
            assert args != null;
            // receive the frequency magnitude list
            xFrequencyMagnitude = (List<Float>) args.getSerializable("Frequency_xMagnitudes");
            yFrequencyMagnitude = (List<Float>) args.getSerializable("Frequency_yMagnitudes");
            zFrequencyMagnitude = (List<Float>) args.getSerializable("Frequency_zMagnitudes");

            // receive the displacement list
            xDisplacement = (List<Float>) args.getSerializable("displacement_dataX");
            yDisplacement = (List<Float>) args.getSerializable("displacement_dataY");
            zDisplacement = (List<Float>) args.getSerializable("displacement_dataZ");

            String formattedValueX = String.format(Locale.US, "%.1f", accelerationX);
            String formattedValueY = String.format(Locale.US, "%.1f", accelerationY);
            String formattedValueZ = String.format(Locale.US, "%.1f", accelerationZ);

            //added
            String formattedampX = String.format(Locale.US, "%.1f", xAmplitude);
            String formattedampY = String.format(Locale.US, "%.1f", yAmplitude);
            String formattedampZ = String.format(Locale.US, "%.1f", zAmplitude);


            txtX.setText(Html.fromHtml(formattedValueX + " " + "m/s<sup>2</sup><br>x"));
            txty.setText(Html.fromHtml(formattedValueY + " " + "m/s<sup>2</sup><br>y"));
            txtZZ.setText(Html.fromHtml(formattedValueZ + " " + "m/s<sup>2</sup><br>z"));

            txtampX.setText(Html.fromHtml(formattedampX + " " + "mm <br>x"));
            txtampY.setText(Html.fromHtml(formattedampY + " " + "mm <br>y"));
            txtampZ.setText(Html.fromHtml(formattedampZ + " " + "mm <br>z"));

        }
    }

    private void initGUI() {
        pvPlot = new PlotViewMaxValues(getActivity());
        pvPlot.setBuffer(buffer);
        pvPlot.setNumChannels(3);
        pvPlot.setChannel(0); // Not used but kept for compatibility
        pvPlot.setBackgroundColor(0xFFFFFF);
        pvPlot.setColor(0, R.color.black); // black for X
        pvPlot.setColor(1, 0xFFFF0000); // Red for Y
        pvPlot.setColor(2, 0xFF0000FF); // Blue for Z
        pvPlot.setLineWidth(3);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll_plots.addView(pvPlot, params);
    }

    @SuppressLint("SetTextI18n")
    private void displayPeakFrequencies() {
        String formattedValueX = String.format(Locale.US, "%.1f", xDominantFrequency);
        String formattedValueY = String.format(Locale.US, "%.1f", yDominantFrequency);
        String formattedValueZ = String.format(Locale.US, "%.1f", zDominantFrequency);

       txtfrx.setText(formattedValueX + "Hz" + "\n" + "x");
        txtfry.setText(formattedValueY + "Hz" + "\n" + "y");
        txtfrZ.setText(formattedValueZ + "Hz" + "\n" + "z");
    }

    public static List<Float> getDataListX() {
        return xDisplacement;
    }

    public static List<Float> getDataListY() {
        return yDisplacement;
    }

    public static List<Float> getDataListZ() {
        return zDisplacement;
    }

    public static List<Float> xFrequencyMagnitude() {
        return xFrequencyMagnitude;
    }

    public static List<Float> yFrequencyMagnitude() {
        return yFrequencyMagnitude;
    }

    public static List<Float> zFrequencyMagnitude() {
        return zFrequencyMagnitude;
    }

    public static float maxXAcceleration() {
        return accelerationX;
    }

    public static float maxYAcceleration() {
        return accelerationY;
    }

    public static float maxZAcceleration() {
        return accelerationZ;
    }

    public static float maxXAmplitude() {
        return xAmplitude;
    }
    public static float maxYAmplitude() {
        return yAmplitude;
    }
    public static float maxZAmplitude() {
        return zAmplitude;
    }

    public static String timer() {
        return timer;
    }

    public static float dominantXFrequency() {
        return xDominantFrequency;
    }

    public static float dominantYFrequency() {
        return yDominantFrequency;
    }

    public static float dominantZFrequency() {
        return zDominantFrequency;
    }


    public void createPdf() {
        File file = new File(requireActivity().getCacheDir() + "/" + getResources().getString(R.string.summary) + ".pdf");
        if (file.exists() && !file.delete()) {
            Toast.makeText(getActivity(), "SOMETHING WENT WRONG CREATING THE PDF FILE", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "SOMETHING WENT WRONG CREATING THE PDF FILE", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void addContent(Document document) throws DocumentException {
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 0);
        Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 0, Constants.blueColor);
        Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 1);
        Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 13.0f, 4, Constants.blueColorLink);
        try {
            Bitmap decodeStream = BitmapFactory.decodeStream(requireActivity().getAssets().open("rhewumLogo.jpg"));
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
            paragraph.add((Element) new Paragraph(getResources().getString(R.string.pdf_summary_title), font));
            addEmptyLine(paragraph, 1);
            paragraph.add((Element) new Paragraph(getResources().getString(R.string.pdf_sub_summarytitle), font2));
            addEmptyLine(paragraph, 1);
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

        PdfPCell pdfPCell4 = new PdfPCell(new Phrase(decimalFormat.format(accelerationX)));
        pdfPCell4.setHorizontalAlignment(1);
        pdfPCell4.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell4);

        PdfPCell pdfPCell5 = new PdfPCell(new Phrase("Peak Acceleration Y"));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell5);

        PdfPCell pdfPCell6 = new PdfPCell(new Phrase(decimalFormat.format(accelerationY)));
        pdfPCell6.setHorizontalAlignment(1);
        pdfPCell6.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell6);


        PdfPCell pdfPCell8 = new PdfPCell(new Phrase("Peak Acceleration Z"));
        pdfPCell8.setHorizontalAlignment(1);
        pdfPCell8.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell8);

        PdfPCell pdfPCell9 = new PdfPCell(new Phrase(decimalFormat.format(accelerationZ)));
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

        PdfPCell pdfPCell17 = new PdfPCell(new Phrase(decimalFormat.format(xAmplitude)));
        pdfPCell17.setHorizontalAlignment(1);
        pdfPCell17.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell17);


        PdfPCell pdfPCell18 = new PdfPCell(new Phrase("Amplitude Y"));
        pdfPCell18.setHorizontalAlignment(1);
        pdfPCell18.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell18);

        PdfPCell pdfPCell19 = new PdfPCell(new Phrase(decimalFormat.format(yAmplitude)));
        pdfPCell19.setHorizontalAlignment(1);
        pdfPCell19.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell19);


        PdfPCell pdfPCell20 = new PdfPCell(new Phrase("Amplitude Z"));
        pdfPCell20.setHorizontalAlignment(1);
        pdfPCell20.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell20);

        PdfPCell pdfPCell21 = new PdfPCell(new Phrase(decimalFormat.format(zAmplitude)));
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


    //save data to csv file
    public void saveDisplacementDataToCSV(Context context, ArrayList<PsdSummaryDao> psdSummaryDaoArrayList) throws IOException {
        // Check if external storage is available
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("CSV Creation", "External storage not mounted or writable");
            return;
        }
        File file = new File(requireActivity().getCacheDir().getAbsolutePath() + "/" + requireActivity().getResources().getString(R.string.psd_summary) + ".csv");

        // Create a directory and CSV file in external storage
        if (!file.exists() && !file.createNewFile()) {
            Toast.makeText(requireActivity(), "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();
        }
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.append("xDisplacement,yDisplacement,zDisplacement,xFrequency,yFrequency,zFrequency\n");

            // Write data to CSV
            for (PsdSummaryDao dao : psdSummaryDaoArrayList) {
                writer.append(String.valueOf(dao.xDisplacement))
                        .append(",")
                        .append(String.valueOf(dao.yDisplacement))
                        .append(",")
                        .append(String.valueOf(dao.zDisplacement))
                        .append(",")
                        .append(String.valueOf(dao.xFrequencyMagnitude))
                        .append(",")
                        .append(String.valueOf(dao.yFrequencyMagnitude))
                        .append(",")
                        .append(String.valueOf(dao.zFrequencyMagnitude))
                        .append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;  // Re-throw exception if needed
        }
    }


    @Override
    public void onDelete(ArrayList<VibCheckerSummaryDao> arrayList, boolean z, boolean z2) {
        if (arrayList != null && !arrayList.isEmpty()) {
            RhewumDbHelper databaseHelper = new RhewumDbHelper(getContext());

            for (VibCheckerSummaryDao dao : arrayList) {
                // Remove from the database
//                databaseHelper.deleteVibcheckerList(dao.getSu());
            }

            // Notify the user
            Toast.makeText(getContext(), "Measurements deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No measurements to delete", Toast.LENGTH_SHORT).show();
        }
    }
}