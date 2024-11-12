package com.rhewumapp.Activity.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.rhewumapp.Activity.MeshConveterData.Constants;
import com.rhewumapp.Activity.VibcheckerGraph.PlotViewMaxValues;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.R;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SummeryFragment extends Fragment {
    View view;
    TextView txtZZ,txtX,txty,txtfrZ,txtfrx,txtfry,txtDateTime;
    LinearLayout ll_plots;
    private PlotViewMaxValues pvPlot;
    private float[] buffer;
    static float accelerationX;
    static float accelerationY;
    static float accelerationZ;
    static float xDominantFrequency;
    static float yDominantFrequency;
    static float zDominantFrequency;
    // declare the frequency magnitude list
    private static List<Float> xFrequencyMagnitude = new ArrayList<>();
    private static List<Float> yFrequencyMagnitude = new ArrayList<>();
    private static List<Float> zFrequencyMagnitude = new ArrayList<>();
   // declare the displacement list
    private static List<Float> xDisplacement = new ArrayList<>();
    private static List<Float> yDisplacement = new ArrayList<>();
    private static List<Float> zDisplacement = new ArrayList<>();
    private Calendar calendar;
    Button bt_save,bt_share;
    RhewumDbHelper dbHelper;
    ArrayList<VibCheckerSummaryDao> vibCheckerAccList;
    private Handler handler = new Handler();
    private Runnable runnable;
    public String mailBody = "<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><head><meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" /></head><body><table width=\\\"100%%\\\" border=\\\"0\\\" cellspacing=\\\"15\\\" cellpadding=\\\"0\\\"><tr><td>Dear user,<br />Please find attached the results of your measurements with the RHEWUM VibSonic App as of [HTML_DATE_STRING]</td></tr><br><br><tr><td>We hope that our service was of use to you. Please do not hesitate to contact us if you need any more information, more precise measurements or a personal consultation.</td></tr><br><br><tr><td>We are looking forward to support you and your project ideas.</td></tr><br><br><tr><td>RHEWUM GmbH<br />Rosentalstr. 24<br />42899 Remscheid<br />Germany</td></tr><br><br><tr><td>Mail : <a href=\"mailto:info@rhewum.com\">info@rhewum.com</a><br /> Web: <a href=\"http://www.rhewum.com\">http://www.rhewum.com</a></td></tr><tr><td>&nbsp;</td></tr></table></body></html>";
    private String mailSubject = "Result of RHEWUM Summary App";
    String formattedValueX = "m/s2"; // Replace with your actual value



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
        view= inflater.inflate(R.layout.fragment_summery, container, false);
        initObjects();
        getData();
        initGUI();
        displayPeakFrequencies();
        onClickUi();

        return view;
    }
    @SuppressLint("SetTextI18n")
    private void initObjects(){
        txtZZ=view.findViewById(R.id.txtZZ);
        txty=view.findViewById(R.id.txty);
        txtX=view.findViewById(R.id.txtx);
        txtfrZ=view.findViewById(R.id.txtfrZ);
        txtfrx=view.findViewById(R.id.txtfrx);
        txtfry=view.findViewById(R.id.txtfry);
        ll_plots=view.findViewById(R.id.vibrationLineChart);
        txtDateTime=view.findViewById(R.id.txtDateTime);
        vibCheckerAccList=new ArrayList<>();
        bt_save=view.findViewById(R.id.bt_save);
        bt_share=view.findViewById(R.id.bt_share);
        dbHelper = new RhewumDbHelper(getActivity());
        calendar = Calendar.getInstance();
        txtDateTime.setText("New Record "+new SimpleDateFormat("MM-dd-yyyy,hh:mm:ss a", Locale.US).format(calendar.getTime()));
    }

    private void onClickUi(){
        // click on button save
        bt_save.setOnClickListener(v->{
                // Add accelerometer data to the database
                dbHelper.maxAccelerometerData(accelerationX, accelerationY, accelerationZ);
                dbHelper.maxDominantFrequencyData(xDominantFrequency, yDominantFrequency, zDominantFrequency);
                Toast.makeText(getActivity(), "Data is save successfully !!", Toast.LENGTH_SHORT).show();

            runnable=new Runnable() {
                @Override
                public void run() {
                    if(vibCheckerAccList==null){
                        Toast.makeText(getActivity(),"First save the data ?",Toast.LENGTH_SHORT).show();
                    }else{
                        vibCheckerAccList = dbHelper.getVibCheckerAcc();
                        createPdf();
                    }
                }
            };handler.postDelayed(runnable,1000);

        });

        // click on button share
        bt_share.setOnClickListener(v->{
            // Adding a new measurement record
                if(!vibCheckerAccList.isEmpty()) {
                    createSendFile();
                }else{
                    Toast.makeText(getActivity(),"Save the data first ?",Toast.LENGTH_SHORT).show();
                }
        });
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
                    Log.e("Summary","Summary:::"+e.getMessage());
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
    private void getData(){
        Intent intent = requireActivity().getIntent();
        if(intent!=null) {
             accelerationX = intent.getFloatExtra("accelerationMax_X", 0);
             accelerationY = intent.getFloatExtra("accelerationMax_Y", 0);
             accelerationZ = intent.getFloatExtra("accelerationMax_Z", 0);
             buffer = intent.getFloatArrayExtra("sensor_data");

            xDominantFrequency = intent.getFloatExtra("Frequency_X",0);
            yDominantFrequency = intent.getFloatExtra("Frequency_Y",0);
            zDominantFrequency = intent.getFloatExtra("Frequency_Z",0);

            Bundle args = intent.getBundleExtra("BUNDLE");
            assert args != null;
           // receive the frequency magnitude list
            xFrequencyMagnitude= (List<Float>) args.getSerializable("Frequency_xMagnitudes");
            yFrequencyMagnitude= (List<Float>) args.getSerializable("Frequency_yMagnitudes");
            zFrequencyMagnitude= (List<Float>) args.getSerializable("Frequency_zMagnitudes");
            // receive the displacement list
            xDisplacement= (List<Float>) args.getSerializable("displacement_dataX");
            yDisplacement= (List<Float>) args.getSerializable("displacement_dataY");
            zDisplacement= (List<Float>) args.getSerializable("displacement_dataZ");

            String formattedValueX = String.format(Locale.US, "%.1f", accelerationX);
            String formattedValueY = String.format(Locale.US, "%.1f", accelerationY);
            String formattedValueZ = String.format(Locale.US, "%.1f", accelerationZ);

            txtX.setText(Html.fromHtml(formattedValueX + "m/s<sup>2</sup><br>x"));
            txty.setText(Html.fromHtml(formattedValueY + "m/s<sup>2</sup><br>y"));
            txtZZ.setText(Html.fromHtml(formattedValueZ + "m/s<sup>2</sup><br>z"));



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

        txtfrx.setText(formattedValueX+"Hz"+"\n"+"x");
        txtfry.setText(formattedValueY+"Hz"+"\n"+"y");
        txtfrZ.setText(formattedValueZ+"Hz"+"\n"+"z");
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
    public static List<Float> xFrequencyMagnitude(){
        return xFrequencyMagnitude;
    }
    public static List<Float> yFrequencyMagnitude(){
        return yFrequencyMagnitude;
    }
    public static List<Float> zFrequencyMagnitude(){
        return zFrequencyMagnitude;
    }
    public static float maxXAcceleration(){
       return accelerationX;
    }
    public static float maxYAcceleration(){
        return accelerationY;
    }
    public static float maxZAcceleration(){
        return accelerationZ;
    }

    public static float dominantXFrequency(){
        return xDominantFrequency;
    }
    public static float dominantYFrequency(){
        return yDominantFrequency;
    }
    public static float dominantZFrequency(){
        return zDominantFrequency;
    }


    public void createPdf(){
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


        PdfPCell pdfPCell = new PdfPCell(new Phrase("Acceleration and Frequency", font));
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

        paragraph.add((Element) pdfPTable);
    }

    /*private void createTable(Paragraph paragraph, Font font) throws BadElementException {
        // Set up a table with 7 columns for Peak Acceleration and Peak Frequency for X, Y, Z
        PdfPTable pdfPTable = new PdfPTable(6);
        pdfPTable.setWidthPercentage(100.0f);

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("##.##", decimalFormatSymbols);

        PdfPCell cell;

        // Headers
        cell = new PdfPCell(new Phrase("Peak Acceleration X", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4.0f);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Peak Acceleration Y", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4.0f);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Peak Acceleration Z", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4.0f);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Peak Frequency X", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4.0f);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Peak Frequency Y", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4.0f);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Peak Frequency Z", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4.0f);
        pdfPTable.addCell(cell);

        // Data rows
        for (int i = 0; i < vibCheckerAccList.size(); i++) {
            VibCheckerSummaryDao vibData = vibCheckerAccList.get(i);

            PdfPCell accelerationXCell = new PdfPCell(new Phrase(decimalFormat.format(vibData.xAxis)));
            accelerationXCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            accelerationXCell.setPadding(4.0f);
            pdfPTable.addCell(accelerationXCell);

            PdfPCell accelerationYCell = new PdfPCell(new Phrase(decimalFormat.format(vibData.yAxis)));
            accelerationYCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            accelerationYCell.setPadding(4.0f);
            pdfPTable.addCell(accelerationYCell);

            PdfPCell accelerationZCell = new PdfPCell(new Phrase(decimalFormat.format(vibData.zAxis)));
            accelerationZCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            accelerationZCell.setPadding(4.0f);
            pdfPTable.addCell(accelerationZCell);

            PdfPCell frequencyXCell = new PdfPCell(new Phrase(decimalFormat.format(vibData.dominantFrequencyX)));
            frequencyXCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            frequencyXCell.setPadding(4.0f);
            pdfPTable.addCell(frequencyXCell);

            PdfPCell frequencyYCell = new PdfPCell(new Phrase(decimalFormat.format(vibData.dominantFrequencyY)));
            frequencyYCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            frequencyYCell.setPadding(4.0f);
            pdfPTable.addCell(frequencyYCell);

            PdfPCell frequencyZCell = new PdfPCell(new Phrase(decimalFormat.format(vibData.dominantFrequencyZ)));
            frequencyZCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            frequencyZCell.setPadding(4.0f);
            pdfPTable.addCell(frequencyZCell);
        }

        paragraph.add(pdfPTable);
        Toast.makeText(getActivity(), "Generate Pdf Successfully", Toast.LENGTH_LONG).show();
    }*/

    private void addEmptyLine(Paragraph paragraph, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            paragraph.add((Element) new Paragraph(StringUtils.SPACE));
        }
    }

}