package com.rhewum.Activity.MeshConveterData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rhewum.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public final class PDFUtility {

    private static final String TAG = PDFUtility.class.getSimpleName();
    private static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static Font FONT_SUBTITLE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    private static Font FONT_CELL = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    private static Font FONT_COLUMN = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);
    private static Font FONT_SIZE = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.NORMAL);

    public interface OnDocumentClose {
        void onPDFDocumentClose(File file);
    }

    public static void createPdf(@NonNull Context mContext, OnDocumentClose mCallback, List<String[]> items, @NonNull String filePath, boolean isPortrait) throws Exception {
        if (filePath.equals("")) {
            throw new NullPointerException("PDF File Name can't be null or blank. PDF File can't be created");
        }

        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
            Thread.sleep(50);
        }

        Document document = new Document();
        document.setMargins(24f, 24f, 32f, 32f);
        document.setPageSize(isPortrait ? PageSize.A4 : PageSize.A4.rotate());

        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        pdfWriter.setFullCompression();
        //  pdfWriter.setPageEvent(new PageNumeration());

        document.open();

        setMetaData(document);

        addHeader(mContext, document);

        addEmptyLine(document, 3);
        document.add(createDataTable(items));
        addEmptyLine(document, 2);
        addFooter(mContext, document);
        document.close();

        try {
            pdfWriter.close();
        } catch (Exception ex) {
            Log.e(TAG, "Error While Closing pdfWriter : " + ex.toString());
        }

        if (mCallback != null) {
            mCallback.onPDFDocumentClose(file);
        }
    }

    private static void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }

    private static void setMetaData(Document document) {
        document.addCreationDate();
        //document.add(new Meta("",""));
        document.addAuthor("RAVEESH G S");
        document.addCreator("RAVEESH G S");
        document.addHeader("DEVELOPER", "RAVEESH G S");
    }

    private static void addHeader(Context mContext, Document document) throws Exception {

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {
            /*LEFT TOP LOGO*/
            Drawable d = ContextCompat.getDrawable(mContext, R.drawable.rhewumt);
            Bitmap bmp = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

            Image logo = Image.getInstance(stream.toByteArray());
            logo.setWidthPercentage(80);
            logo.scaleToFit(105, 55);

            cell = new PdfPCell(logo);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            cell.setUseAscender(true);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(2f);
            table.addCell(cell);
        }
        // create subTitle
        {
            cell = new PdfPCell(new Paragraph("Please find the attach the results of your measurements with the RHEWUM APP.", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(4f);
            table.addCell(cell);

        }

        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("VibSonic", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLUE)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(8f);
            table.addCell(cell);

        }

        document.add(table);


    }


    private static PdfPTable createDataTable(List<String[]> dataTable) throws DocumentException {
        PdfPTable table1 = new PdfPTable(2);
        table1.setWidthPercentage(100);
        table1.setWidths(new float[]{1f, 2f});
        table1.setHeaderRows(1);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {
            cell = new PdfPCell(new Phrase("Frequency", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.BOLD, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Sound Pressure Level", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.BOLD, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            table1.addCell(cell);
        }

        float top_bottom_Padding = 8f;
        float left_right_Padding = 4f;
        boolean alternate = false;

        int size = dataTable.size();

        for (int i = 0; i < size; i++) {
            String[] temp = dataTable.get(i);
            cell = new PdfPCell(new Phrase(temp[0], new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp[1], new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            table1.addCell(cell);
            alternate = !alternate;
        }
        return table1;
    }


    private static void addFooter(Context mContext, Document document) throws Exception {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        // create subTitle
        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("We hope that our service was of use to you. Please do not hesitate to contact us if you need any more information, more precise measurements or a personal consultation.", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(4f);
            table.addCell(cell);

        }

        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("We are looking forward to support you and your project ideas.", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(8f);
            table.addCell(cell);

        }

        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("RHEWUM GmbH", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(1f);
            table.addCell(cell);

        }
        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("Rosentlar.24", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(1f);
            table.addCell(cell);

        }
        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("42899 Rmeschied", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(1f);
            table.addCell(cell);

        }
        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("Germany", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLACK)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(1f);
            table.addCell(cell);

        }

        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("Mail:info@rhewum.com", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLUE)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(1f);
            table.addCell(cell);

        }
        {
            //*MIDDLE TEXT*//*
            cell = new PdfPCell(new Paragraph("Web:www.rhewum.com", new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.NORMAL, BaseColor.BLUE)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(1f);
            table.addCell(cell);

        }
        document.add(table);
    }


}
