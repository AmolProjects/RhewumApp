package com.rhewum.Activity.MeshConveterData;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.codec.TIFFConstants;

import java.util.ArrayList;

public class Constants {
    public static final boolean DEBUG_MODE = false;
    public static String DbName = "rhewum.db";
    public static String KEY_BLOB_LOWER = "BlobLower";
    public static String KEY_BLOB_NUM = "BlobNum";
    public static String KEY_BLOB_SUM = "BlobSum";
    public static String KEY_BLOB_SUM_IO = "BlobSumIO";
    public static String KEY_BLOB_UPPAR = "BlobUpper";
    public static String KEY_COMPANY_NAME = "KEY_COMPANY_NAME";
    public static String KEY_COUNTER = "KEY_COUNTER";
    public static String KEY_EMAIL = "KEY_EMAIL";
    public static String KEY_ID = "ID";
    public static String KEY_IMAGE_URL = "ImageUrl";
    public static String KEY_LOWER = "Lower";
    public static String KEY_MAX_D = "MaxD";
    public static String KEY_MAX_GRAIN = "KEY_MAX_GRAIN";
    public static String KEY_MEASUREMENT = "Measurement";
    public static String KEY_MIN_D = "MinD";
    public static String KEY_MIN_GRAIN = "KEY_MIN_GRAIN";
    public static String KEY_NAME = "KEY_NAME";
    public static String KEY_NORM = "KEY_NORM";
    public static String KEY_NUM_SIZE_STEPS = "NumSizeSteps";
    public static String KEY_RESULT = "Result";
    public static String KEY_SCM_RESULT = "SCM_Result";
    public static String KEY_SIZE = "Size";
    public static String KEY_SIZE_STEPS = "SizeSteps";
    public static String KEY_TELEPHONE = "KEY_TELEPHONE";
    public static String KEY_UPPAR = "Upper";
    public static String KEY_VOLL_REL = "VollRel";
    public static String KEY_VOL_ABS = "VolAbs";
    public static String KEY_VOL_LOWER = "VolLower";
    public static String KEY_VOL_REL_SUM = "VolRelSum";
    public static String KEY_VOL_TOTAL = "VolTotal";
    public static String KEY_VOL_TOTAL_IO = "VolTotalIO";
    public static String KEY_VOL_UPPAR = "VolUpper";
    public static final double PI = (Math.atan(1.0d) * 4.0d);
    public static final double SCALE_FACTOR = 100.0d;
    public static ArrayList<Activity> activityArrayList = new ArrayList<>();
    public static final String[] astm_Values = {"5 in.", "-", "4.24 in.", "4 in.", "3.1/2 in.", "-", "3 in.", "-", "2.1/2 in.", "-", "2.12 in.", "2 in.", "1.3/4 in.", "-", "1.1/2 in.", "-", "1.1/4 in.", "-", "1.06 in.", "1 in.", "7/8 in.", "-", "3 / 4 in.", "-", "5/8 in.", "-", "0.530 in.", "1 / 2 in.", "7/16 in.", "-", "3/8 in.", "-", "5/16 in.", "-", "0.265 in.", "1 / 4 in.", "3.1/2 Me.", "-", "4 Me.", "-", "5 Me.", "-", "6 Me.", "-", "7 Me.", "-", "8 Me.", "-", "10 Me.", "-", "12 Me.", "-", "14 Me.", "-", "16 Me.", "-", "18 Me.", "-", "20 Me.", "-", "25 Me.", "-", "30 Me.", "-", "35 Me.", "-", "40 Me.", "-", "45 Me.", "-", "50 Me.", "-", "60 Me.", "-", "70 Me.", "-", "80 Me.", "-", "100 Me.", "-", "120 Me.", "-", "140 Me.", "-", "170 Me.", "-", "200 Me.", "-", "230 Me.", "-", "270 Me.", "-", "325 Me.", "-", "400 Me.", "-", "450 Me.", "500 Me.", "635 Me."};
    public static String[] astm_ValuesNew = new String[0];
    public static String baseUrl = "http://217.144.138.194/rhewum/index.php?calculate=calculate";
    public static final BaseColor blueColor = new BaseColor(80, 124, (int) TIFFConstants.TIFFTAG_SUBFILETYPE);
    public static final BaseColor blueColorLink = new BaseColor(0, 0, 255);
    public static final String[] din_1_Values = {"125 mm", "112 mm", "106 mm", "100 mm", "90 mm", "80 mm", "75 mm", "71 mm", "63 mm", "56 mm", "53 mm", "50 mm", "45 mm", "40 mm", "37.5 mm", "35.5 mm", "31.5 mm", "28 mm", "26.5 mm", "25 mm", "22.4 mm", "20 mm", "19 mm", "18 mm", "16 mm", "14 mm", "13.2 mm", "12.5 mm", "11.2 mm", "10 mm", "9.5 mm", "9 mm", "8 mm", "7.1 mm", "6.7 mm", "6.3 mm", "5.6 mm", "5 mm", "4.75 mm", "4.5 mm", "4 mm", "3.55 mm", "3.35 mm", "3.15 mm", "2.8 mm", "2.5 mm", "2.36 mm", "2.24 mm", "2 mm", "1.8 mm", "1.7 mm", "1.6 mm", "1.4 mm", "1.25 mm", "1.18 mm", "1.12 mm", "1 mm", "900 μm", "850 μm", "800 μm", "710 μm", "630 μm", "600 μm", "560 μm", "500 μm", "450 μm", "425 μm", "400 μm", "355 μm", "315 μm", "300 μm", "280 μm", "250 μm", "224 μm", "212 μm", "200 μm", "180 μm", "160 μm", "150 μm", "140 μm", "125 μm", "112 μm", "106 μm", "100 μm", "90 μm", "80 μm", "75 μm", "71 μm", "63 μm", "56 μm", "53 μm", "50 μm", "45 μm", "40 μm", "38 μm", "36 μm", "32 μm", "25 μm", "20 μm"};
    public static final String[] din_2_Values = {"4.921 in.", "4.409 in.", "4.173 in.", "3.937 in.", "3.543 in.", "3.150 in.", "2.953 in.", "2.795 in.", "2.480 in.", "2.205 in.", "2.087 in.", "1.969 in.", "1.772 in.", "1.575 in.", "1.476 in.", "1.398 in.", "1.240 in.", "1.102 in.", "1.043 in.", ".984 in.", ".882 in.", ".787 in.", ".748 in.", ".709 in.", ".630 in.", ".551 in.", ".520 in.", ".492 in.", ".441 in.", ".394 in.", ".374 in.", ".354 in.", ".315 in.", ".280 in.", ".264 in.", ".248 in.", ".220 in.", ".197 in.", ".187 in.", ".177 in.", ".157 in.", ".140 in.", ".132 in.", ".124 in.", ".110 in.", ".098 in.", ".093 in.", ".088 in.", ".079 in.", ".071 in.", ".067 in.", ".063 in.", ".055 in.", ".049 in.", ".046 in.", ".044 in.", ".039 in.", ".0354 in.", ".0334 in.", ".0314 in.", ".0279 in.", ".0248 in.", ".0236 in.", ".0220 in.", ".0196 in.", ".0177 in.", ".0167 in.", ".0157 in.", ".0139 in.", ".0124 in.", ".0118 in.", ".0110 in.", ".0098 in.", ".0088 in.", ".0083 in.", ".0078 in.", ".0070 in.", ".0062 in.", ".0059 in.", ".0055 in.", ".0049 in.", ".0044 in.", ".0041 in.", ".0039 in.", ".0035 in.", ".0031 in.", ".0029 in.", ".0027 in.", ".0024 in.", ".0022 in.", ".0020 in.", ".0019 in.", ".0017 in.", ".0015 in.", ".0014 in.", ".00141 in.", ".00126 in.", ".000984 in.", ".000787 in."};
    public static final int mNumberOfFFTPoints = 512;
    public static final double mSampleRateInHz = 8000.0d;
    public static int rhewum_blue = Color.parseColor("#1995cc");
    public static int rhewum_grey = Color.parseColor("#555555");
    public static int rhewum_white = Color.parseColor("#FFFFFF");
    public static final String[] tyler_Values = {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "1.05 in.", "-", ".883 in.", "-", ".742 in.", "-", ".624 in.", "-", ".525 in.", "-", ".441 in.", "-", ".371 in.", "-", "2.1/2 Me.", "-", "3 Me.", "-", "3.1/2 Me.", "-", "4 Me.", "-", "5 Me.", "-", "6 Me.", "-", "7 Me.", "-", "8 Me.", "-", "9 Me.", "-", "10 Me.", "-", "12 Me.", "-", "14 Me.", "-", "16 Me.", "-", "20 Me.", "-", "24 Me.", "-", "28 Me.", "-", "32 Me.", "-", "35 Me.", "-", "42 Me.", "-", "48 Me.", "-", "60 Me.", "-", "65 Me.", "-", "80 Me.", "-", "100 Me.", "-", "115 Me.", "-", "150 Me.", "-", "170 Me.", "-", "200 Me.", "-", "250 Me.", "-", "270 Me.", "-", "325 Me.", "-", "400 Me.", "-", "450 Me.", "500 Me.", "635 Me."};
    public static String[] tyler_ValuesNew = new String[0];
}
