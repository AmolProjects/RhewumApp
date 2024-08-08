package com.rhewum.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.rhewum.Activity.MeshConveterData.Constants;
import com.rhewum.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.Activity.database.MeasurementDao;
import com.rhewum.Activity.database.RhewumDbHelper;
import com.rhewum.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.core.net.MailTo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.opencsv.CSVWriter;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class VibSonicArchiveActivity extends AppCompatActivity implements View.OnClickListener,OnChartValueSelectedListener {
    private RelativeLayout backLayout;
    /* access modifiers changed from: private */
    public String csvFilePath;
    /* access modifiers changed from: private */
    public String currentDateTime;
    /* access modifiers changed from: private */
    public RhewumDbHelper dbHelper;
    /* access modifiers changed from: private */
    public String[] freqList;
    private String htmlPdf;
    /* access modifiers changed from: private */
    public String jumpFrom;
    private BarChart mChart;
    private String[] mXaxisValues = {"32", "63", "125", "250", "500", "1K", "2K", "4K", "8K", "16K"};
    /* access modifiers changed from: private */
    public String mailBody = "<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><head><meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" /></head><body><table width=\\\"100%%\\\" border=\\\"0\\\" cellspacing=\\\"15\\\" cellpadding=\\\"0\\\"><tr><td>Dear user,<br />Please find attached the results of your measurements with the RHEWUM VibSonic App as of [HTML_DATE_STRING]</td></tr><br><br><tr><td>We hope that our service was of use to you. Please do not hesitate to contact us if you need any more information, more precise measurements or a personal consultation.</td></tr><br><br><tr><td>We are looking forward to support you and your project ideas.</td></tr><br><br><tr><td>RHEWUM GmbH<br />Rosentalstr. 24<br />42899 Remscheid<br />Germany</td></tr><br><br><tr><td>Mail : <a href=\"mailto:info@rhewum.com\">info@rhewum.com</a><br /> Web: <a href=\"http://www.rhewum.com\">http://www.rhewum.com</a></td></tr><tr><td>&nbsp;</td></tr></table></body></html>";
    private String mailSubject = "Result of RHEWUM VibSonic App";
    private ArrayList<MeasurementDao> measurementList = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Double> measurementListDouble = new ArrayList<>();
    private RelativeLayout moreLayout;
    private String pdfFilePath;
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setFontFamily("fonts/heebo.ttf");
        setContentView(R.layout.activity_vib_sonic_archive);
        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_backgrounds), false);
        ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.grey_background), false, false);
        setUpViews();
        getHelper();
        this.backLayout.setOnClickListener(this);
        this.moreLayout.setOnClickListener(this);
    }
    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.freqList = getResources().getStringArray(R.array.freqList);
        String string = Objects.requireNonNull(getIntent().getExtras()).getString("JumpFrom");
        this.jumpFrom = string;
        assert string != null;
        if (string.equals("ArchieveList")) {
            int i = getIntent().getExtras().getInt(SecurityConstants.Id);
            this.htmlPdf = Utils.readHtmlVibSonic(this, i, this.jumpFrom);
            ArrayList<MeasurementDao> listById = this.dbHelper.getListById(i);
            this.measurementList = listById;
            addValuesToIntegerArrayList(listById);
        } else {
            this.htmlPdf = Utils.readHtmlVibSonic(this, 0, this.jumpFrom);
            ArrayList<MeasurementDao> listById2 = this.dbHelper.getListById(this.dbHelper.getLastId());
            this.measurementList = listById2;
            Log.e("VibSonicArchiveActivity","VibSonicArchiveActivity List Is::"+this.measurementList.size());
            addValuesToIntegerArrayList(listById2);
        }
        this.wv.loadDataWithBaseURL("", this.htmlPdf, "text/html", "UTF-8", "");
        this.wv.setDrawingCacheEnabled(true);
        this.wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (str.startsWith("tel:")) {
                    VibSonicArchiveActivity.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(str)));
                } else if (str.startsWith("http:") || str.startsWith("https:")) {
                    webView.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                } else if (str.startsWith(MailTo.MAILTO_SCHEME)) {
                    android.net.MailTo parse = android.net.MailTo.parse(str);
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("message/rfc822");
                    intent.putExtra("android.intent.extra.EMAIL", new String[]{parse.getTo()});
                    intent.putExtra("android.intent.extra.SUBJECT", parse.getSubject());
                    intent.putExtra("android.intent.extra.CC", parse.getCc());
                    intent.putExtra("android.intent.extra.TEXT", parse.getBody());
                    VibSonicArchiveActivity.this.startActivity(intent);
                    webView.reload();
                }
                return true;
            }
        });
        setUpGraph();
    }

    private void setUpViews() {
        this.backLayout = (RelativeLayout) findViewById(R.id.activity_vib_sonic_archive_back_layout);
        this.moreLayout = (RelativeLayout) findViewById(R.id.activity_vib_sonic_archive_more_layout);
        this.wv = (WebView) findViewById(R.id.activity_vib_sonic_archive_wv);
        this.mChart = (BarChart) findViewById(R.id.activity_vib_sonic_archieve_soundGraph);
    }
    private void setUpGraph() {
        this.mChart.setOnChartValueSelectedListener(this);
        this.mChart.setDrawBarShadow(false);
        this.mChart.setDrawValueAboveBar(true);
        this.mChart.zoom(0.0f, 0.0f, 0.0f, 0.0f);
        this.mChart.setDescription("");
        this.mChart.setPinchZoom(false);
        this.mChart.setClickable(false);
        // this.mChart.setHighlightEnabled(false);
        this.mChart.setDrawGridBackground(true);
        XAxis xAxis = this.mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setTextColor(getResources().getColor(R.color.black));
        YAxis axisLeft = this.mChart.getAxisLeft();
        //axisLeft.setLabelCount(12);
        axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        axisLeft.enableGridDashedLine(40.0f, 10.0f, 40.0f);
        axisLeft.setSpaceTop(0.0f);
        axisLeft.setAxisMinValue(10.0f);
        axisLeft.setAxisMaxValue(120.0f);
        axisLeft.setStartAtZero(true);
        this.mChart.getLegend().setEnabled(false);
        this.mChart.getAxisRight().setEnabled(false);
        this.mChart.setTouchEnabled(false);
        setData(10, 10.0f);
    }

    private void setData(int i, float f) {
        ArrayList<Double> arrayList = this.measurementListDouble;
        if (arrayList != null && !arrayList.isEmpty()) {
            ArrayList<String> arrayList2 = new ArrayList<>();
            int i2 = 0;
            for (int i3 = 0; i3 < i; i3++) {
                arrayList2.add(this.mXaxisValues[i3 % 10]);
            }
            ArrayList arrayList3 = new ArrayList();
            while (i2 < i) {
                try {
                    arrayList3.add(new BarEntry(this.measurementListDouble.get(i2).floatValue(), i2));
                    i2++;
                } catch (Exception unused) {
                    return;
                }
            }
            BarDataSet barDataSet = new BarDataSet(arrayList3, "DataSet");
            barDataSet.setBarSpacePercent(35.0f);
            barDataSet.setColor(getResources().getColor(R.color.blue_bar));
            ArrayList<IBarDataSet> arrayList4 = new ArrayList<>();
            arrayList4.add(barDataSet);
            BarData barData = new BarData((List<String>) arrayList2,arrayList4);
            barData.setValueTextSize(10.0f);
            this.mChart.setData(barData);
            this.mChart.invalidate();
        }
    }

    public void onClick(View view) {
        if (view.equals(this.backLayout)) {
            goBack();
        } else if (view.equals(this.moreLayout)) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    VibSonicArchiveActivity.this.createPDF();
                    progressDialog.dismiss();
                    VibSonicArchiveActivity.this.callMoreMenus();
                }
            }, 1000);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private RhewumDbHelper getHelper() {
        if (this.dbHelper == null) {
            this.dbHelper = (RhewumDbHelper) OpenHelperManager.getHelper(this, RhewumDbHelper.class);
        }
        return this.dbHelper;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e != null) {
            this.mChart.zoom(0.0f, 0.0f, 0.0f, 0.0f);
            //this.mChart.setHighlightEnabled(false);
            this.mChart.setPinchZoom(false);
        }

    }

    @Override
    public void onNothingSelected() {
        this.mChart.setPinchZoom(false);
        this.mChart.zoom(0.0f, 0.0f, 0.0f, 0.0f);
    }
    /* access modifiers changed from: private */
    public void goBack() {
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
    /* access modifiers changed from: private */
    public void callMoreMenus() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.vib_sonic_more);
        dialog.setTitle(getResources().getString(R.string.app_name));
        ((RelativeLayout) dialog.findViewById(R.id.delete_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ArrayList<MeasurementDao> arrayList;
                new ArrayList();
                if (VibSonicArchiveActivity.this.jumpFrom.equals("ArchieveList")) {
                    arrayList = VibSonicArchiveActivity.this.dbHelper.getListById(Objects.requireNonNull(VibSonicArchiveActivity.this.getIntent().getExtras()).getInt(SecurityConstants.Id));
                } else {
                    arrayList = VibSonicArchiveActivity.this.dbHelper.getListById(VibSonicArchiveActivity.this.dbHelper.getLastId());
                }
                VibSonicArchiveActivity.this.dbHelper.deleteMeasurementList(arrayList);
                VibSonicArchiveActivity.this.goBack();
            }
        });

        ((RelativeLayout) dialog.findViewById(R.id.export_csv_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new ExportMeasurementListAsync().execute(new String[0]);
                dialog.dismiss();
            }
        });


        ((RelativeLayout) dialog.findViewById(R.id.export_pdf_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] strArr = {VibSonicArchiveActivity.this.getCacheDir() + "/" + VibSonicArchiveActivity.this.getResources().getString(R.string.result_file_vib_sonic_pdf) + ".pdf"};
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
                            arrayList.add(FileProvider.getUriForFile(VibSonicArchiveActivity.this, "com.rhewum.provider", file));
                        } catch (Exception e) {
                            Log.e("VibSonicArchive","VibSonicArchive:::"+e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                    String unused = VibSonicArchiveActivity.this.currentDateTime = Utils.getCurrentDateTime();
                    VibSonicArchiveActivity vibSonicArchiveActivity = VibSonicArchiveActivity.this;
                    String unused2 = vibSonicArchiveActivity.mailBody = vibSonicArchiveActivity.mailBody.replace("HTML_DATE_STRING", VibSonicArchiveActivity.this.currentDateTime);
                    VibSonicArchiveActivity.this.sendEmail(arrayList);
                    dialog.dismiss();
                }
            }
        });
        ((RelativeLayout) dialog.findViewById(R.id.cancel_layout)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void addValuesToIntegerArrayList(ArrayList<MeasurementDao> arrayList) {
        this.measurementListDouble.clear();
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq32));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq63));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq125));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq250));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq500));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq1k));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq2k));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq4k));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq8k));
        this.measurementListDouble.add(Double.valueOf(arrayList.get(0).decibleForFreq16k));
    }

    /* access modifiers changed from: private */
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

        // Ensure arrayList is not null
        if (arrayList != null && !arrayList.isEmpty()) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
        }
        startActivity(Intent.createChooser(intent, getString(R.string.choose_email)));
    }

    @SuppressLint("StaticFieldLeak")
    private class ExportMeasurementListAsync extends AsyncTask<String, Boolean, Boolean> {
        private final ProgressDialog dialog;

        public ExportMeasurementListAsync() {
            this.dialog = new ProgressDialog(VibSonicArchiveActivity.this);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.dialog.setMessage(VibSonicArchiveActivity.this.getResources().getString(R.string.loading));
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(String... strArr) {
            File databasePath = VibSonicArchiveActivity.this.getDatabasePath(Constants.DbName);
            Utils.showLog("Database path is: " + databasePath);
            File file = new File(VibSonicArchiveActivity.this.getCacheDir().getAbsolutePath() + "/" + VibSonicArchiveActivity.this.getResources().getString(R.string.result_file_vib_sonic_csv) + ".csv");
            StringBuilder sb = new StringBuilder();
            sb.append("CSV File: ");
            sb.append(file.toString());
            Utils.showLog(sb.toString());
            try {
                if (!file.exists() && !file.createNewFile()) {
                    Toast.makeText(VibSonicArchiveActivity.this, "SOMETHING WENT WORNG", Toast.LENGTH_LONG).show();
                }
                CSVWriter cSVWriter = new CSVWriter(new FileWriter(file));
                VibSonicArchiveActivity.this.dbHelper.getMeasurementList();
                ArrayList<MeasurementDao> listById = VibSonicArchiveActivity.this.dbHelper.getListById(VibSonicArchiveActivity.this.dbHelper.getLastId());
                cSVWriter.writeNext(new String[]{"Frequency", "Decibel(A)", ""});
                if (!listById.isEmpty()) {
                    DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
                    decimalFormatSymbols.setDecimalSeparator(ClassUtils.PACKAGE_SEPARATOR_CHAR);
                    decimalFormatSymbols.setGroupingSeparator(',');
                    DecimalFormat decimalFormat = new DecimalFormat("##.##", decimalFormatSymbols);
                    for (int i = 0; i < 10; i++) {
                        MeasurementDao measurementDao = listById.get(0);
                        String[] arrStr;
                        if (i == 9) {
                            arrStr = new String[]{VibSonicArchiveActivity.this.freqList[i], decimalFormat.format(VibSonicArchiveActivity.this.measurementListDouble.get(i)), "Mean Level Total " + measurementDao.meanLevelTotal};
                        } else {
                            arrStr = new String[]{VibSonicArchiveActivity.this.freqList[i], decimalFormat.format(VibSonicArchiveActivity.this.measurementListDouble.get(i)), ""};
                        }
                        cSVWriter.writeNext(arrStr);
                    }
                }
                cSVWriter.close();
                return true;
            } catch (IOException e) {
                Log.e("DB Export", e.getMessage(), e);
                return false;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (bool) {
                ArrayList<Uri> arrayList = new ArrayList<>();
                VibSonicArchiveActivity vibSonicArchiveActivity = VibSonicArchiveActivity.this;
                String unused = vibSonicArchiveActivity.csvFilePath = VibSonicArchiveActivity.this.getCacheDir().getAbsolutePath() + "/" + VibSonicArchiveActivity.this.getResources().getString(R.string.result_file_vib_sonic_csv) + ".csv";
                String[] strArr = {VibSonicArchiveActivity.this.csvFilePath};
                for (int i = 0; i < 1; i++) {
                    File file2 = new File(strArr[i]);
                    if (file2.exists()) {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(strArr[i]);
                            byte[] bArr = new byte[fileInputStream.available()];
                            fileInputStream.read(bArr);
                            fileInputStream.close();
                            FileOutputStream fileOutputStream = new FileOutputStream(file2);
                            fileOutputStream.write(bArr);
                            fileOutputStream.close();
                            arrayList.add(FileProvider.getUriForFile(VibSonicArchiveActivity.this, "com.rhewum.provider", file2));
                            Log.e("VibSonicArchive","VibSoincArchive::::::::::::::"+arrayList.size());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                String unused2 = VibSonicArchiveActivity.this.currentDateTime = Utils.getCurrentDateTime();
                VibSonicArchiveActivity vibSonicArchiveActivity2 = VibSonicArchiveActivity.this;
                String unused3 = vibSonicArchiveActivity2.mailBody = vibSonicArchiveActivity2.mailBody.replace("HTML_DATE_STRING", VibSonicArchiveActivity.this.currentDateTime);
                VibSonicArchiveActivity.this.sendEmail(arrayList);
                return;
            }
            VibSonicArchiveActivity vibSonicArchiveActivity3 = VibSonicArchiveActivity.this;
            Utils.showToast(vibSonicArchiveActivity3, vibSonicArchiveActivity3.getResources().getString(R.string.csv_falied));
        }
    }
    /* access modifiers changed from: private */
    public void createPDF() {
        File file = new File(getCacheDir() + "/" + getResources().getString(R.string.result_file_vib_sonic_pdf) + ".pdf");
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
            paragraph.add((Element) new Paragraph(getResources().getString(R.string.pdf_sub_title), font2));
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
        PdfPCell pdfPCell = new PdfPCell(new Phrase(getResources().getString(R.string.pdf_table_heading_1), font));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell);
        PdfPCell pdfPCell2 = new PdfPCell(new Phrase(getResources().getString(R.string.pdf_table_heading_2), font));
        pdfPCell2.setHorizontalAlignment(1);
        pdfPCell2.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell2);
        pdfPTable.setHeaderRows(1);
        PdfPCell pdfPCell3 = new PdfPCell(new Phrase("32 Hz"));
        pdfPCell3.setHorizontalAlignment(1);
        pdfPCell3.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell3);
        PdfPCell pdfPCell4 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(0))));
        pdfPCell4.setHorizontalAlignment(1);
        pdfPCell4.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell4);
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase("63 Hz"));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell5);
        PdfPCell pdfPCell6 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(1))));
        pdfPCell6.setHorizontalAlignment(1);
        pdfPCell6.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell6);
        PdfPCell pdfPCell7 = new PdfPCell(new Phrase("125 Hz"));
        pdfPCell7.setHorizontalAlignment(1);
        pdfPCell7.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell7);
        PdfPCell pdfPCell8 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(2))));
        pdfPCell8.setHorizontalAlignment(1);
        pdfPCell8.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell8);
        PdfPCell pdfPCell9 = new PdfPCell(new Phrase("250 Hz"));
        pdfPCell9.setHorizontalAlignment(1);
        pdfPCell9.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell9);
        PdfPCell pdfPCell10 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(3))));
        pdfPCell10.setHorizontalAlignment(1);
        pdfPCell10.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell10);
        PdfPCell pdfPCell11 = new PdfPCell(new Phrase("500 Hz"));
        pdfPCell11.setHorizontalAlignment(1);
        pdfPCell11.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell11);
        PdfPCell pdfPCell12 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(4))));
        pdfPCell12.setHorizontalAlignment(1);
        pdfPCell12.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell12);
        PdfPCell pdfPCell13 = new PdfPCell(new Phrase("1 kHz"));
        pdfPCell13.setHorizontalAlignment(1);
        pdfPCell13.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell13);
        PdfPCell pdfPCell14 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(5))));
        pdfPCell14.setHorizontalAlignment(1);
        pdfPCell14.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell14);
        PdfPCell pdfPCell15 = new PdfPCell(new Phrase("2 kHz"));
        pdfPCell15.setHorizontalAlignment(1);
        pdfPCell15.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell15);
        PdfPCell pdfPCell16 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(6))));
        pdfPCell16.setHorizontalAlignment(1);
        pdfPCell16.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell16);
        PdfPCell pdfPCell17 = new PdfPCell(new Phrase("4 kHz"));
        pdfPCell17.setHorizontalAlignment(1);
        pdfPCell17.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell17);
        PdfPCell pdfPCell18 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(7))));
        pdfPCell18.setHorizontalAlignment(1);
        pdfPCell18.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell18);
        PdfPCell pdfPCell19 = new PdfPCell(new Phrase("8 kHz"));
        pdfPCell19.setHorizontalAlignment(1);
        pdfPCell19.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell19);
        PdfPCell pdfPCell20 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(8))));
        pdfPCell20.setHorizontalAlignment(1);
        pdfPCell20.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell20);
        PdfPCell pdfPCell21 = new PdfPCell(new Phrase("16 kHz"));
        pdfPCell21.setHorizontalAlignment(1);
        pdfPCell21.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell21);
        PdfPCell pdfPCell22 = new PdfPCell(new Phrase(decimalFormat.format(this.measurementListDouble.get(9))));
        pdfPCell22.setHorizontalAlignment(1);
        pdfPCell22.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell22);
        PdfPCell pdfPCell23 = new PdfPCell(new Phrase("Mean Level", font));
        pdfPCell23.setHorizontalAlignment(1);
        pdfPCell23.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell23);
        PdfPCell pdfPCell24 = new PdfPCell(new Phrase(this.measurementList.get(0).meanLevelTotal, font));
        pdfPCell24.setHorizontalAlignment(1);
        pdfPCell24.setPadding(4.0f);
        pdfPTable.addCell(pdfPCell24);
        paragraph.add((Element) pdfPTable);
    }

    private void addEmptyLine(Paragraph paragraph, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            paragraph.add((Element) new Paragraph(StringUtils.SPACE));
        }
    }


}