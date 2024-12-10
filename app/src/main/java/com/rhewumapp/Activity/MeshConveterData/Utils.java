package com.rhewumapp.Activity.MeshConveterData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.TIFFConstants;
import com.itextpdf.text.xml.xmp.DublinCoreProperties;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.Activity.interfaces.DeleteDialog;
import com.rhewumapp.Activity.interfaces.VibChekerDeleteDialog;
import com.rhewumapp.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class Utils {
    private static final String AUDIO_FILE_NAME = "rhewumAudio.wav";
    private static final String VIDEO_FILE_NAME = "test.3gp";
    private static RhewumDbHelper dbHelper = null;
    public static final String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static Matcher matcher = null;
    private static ArrayList<MeasurementDao> measureMentList = new ArrayList<>();
    public static Pattern pattern = null;
    public static final String urlPattern = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public static void setFontFamily(String str) {
       // ViewPump.init(ViewPump.builder().addInterceptor(new CalligraphyInterceptor(new CalligraphyConfig.Builder().setDefaultFontPath(str).setFontAttrId(io.github.inflationx.calligraphy3.R.attr.fontPath).build())).build());
    }

    public static boolean validateEmail(String str) {
        Pattern compile = Pattern.compile(emailPattern);
        pattern = compile;
        Matcher matcher2 = compile.matcher(str);
        matcher = matcher2;
        return matcher2.matches();
    }

    public static boolean validateUrl(String str) {
        Pattern compile = Pattern.compile(urlPattern);
        pattern = compile;
        Matcher matcher2 = compile.matcher(str);
        matcher = matcher2;
        return matcher2.matches();
    }

    public static boolean checkExternalStorageAvailable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static String getFileName(boolean z) {
        Object[] objArr = new Object[2];
        objArr[0] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android";
        objArr[1] = z ? AUDIO_FILE_NAME : VIDEO_FILE_NAME;
        return String.format("%s/%s", objArr);
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date());
    }

    public static Bitmap getBitmapFromURL(String str) {
        try {
            showLog("src: " + str);
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo networkInfo;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(0);
            if ((networkInfo2 == null || networkInfo2.getState() != NetworkInfo.State.CONNECTED) && ((networkInfo = connectivityManager.getNetworkInfo(1)) == null || networkInfo.getState() != NetworkInfo.State.CONNECTED)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showNetworkErrorDialog(Context context) {
        CustomDialog(context, context.getString(R.string.app_name), context.getString(R.string.check_internet), context.getString(R.string.ok));
    }

    public static void CustomDialog(Context context, String str, String str2, String str3) {
        AlertDialog create = new AlertDialog.Builder(context).create();
        create.setTitle(str);
        create.setMessage(str2);
        create.setButton(-3, str3, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        create.show();
    }

    public static String getCurrentDateTimeStamp() {
        Calendar instance = Calendar.getInstance();
        int i = instance.get(5);
        int i2 = instance.get(2);
        String str = i + "-" + (i2 + 1) + "-" + instance.get(1) + "_" + instance.get(11) + "-" + instance.get(12) + "-" + instance.get(13);
        showLog("Time Stamp: " + str);
        return str;
    }

    public static ProgressDialog getProgressDialog(Context context, String str) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(str);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static ProgressDialog getDownloadingProgressDialog(Context context, String str) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(str);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(1);
        progressDialog.setProgress(0);
        progressDialog.setSecondaryProgress(1);
        progressDialog.setMax(1);
        return progressDialog;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static int getScreenOrientation(Context context) {
        Activity activity = (Activity) context;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        if (((rotation == 0 || rotation == 2) && i2 > i) || ((rotation == 1 || rotation == 3) && i > i2)) {
            if (rotation != 0) {
                if (rotation != 1) {
                    if (rotation != 2) {
                        if (rotation == 3) {
                            return 8;
                        }
                        Log.e("RhewumApp", "Unknown screen orientation. Defaulting to portrait.");
                    }
                }
                return 0;
            }
            return 1;
        }
        if (rotation != 0) {
            if (rotation != 1) {
                if (rotation == 2) {
                    return 8;
                }
                if (rotation != 3) {
                    Log.e("RhewumApp", "Unknown screen orientation. Defaulting to landscape.");
                }
            }
            return 1;
        }
        return 0;
    }

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String str) {
        Log.v("RhewumApp", str);
    }

    public static void showErrorLog(String str, IOException iOException) {
        Log.e("RhewumApp", str, iOException);
    }

    public static void showAppAlert(Context context, String str) {
        new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.app_name)).setMessage(str).setPositiveButton(context.getResources().getString(R.string.ok), (DialogInterface.OnClickListener) null).show();
    }

    public static void showAlert(Context context, String str) {
        new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.alert)).setMessage(str).setCancelable(false).setPositiveButton(context.getResources().getString(R.string.ok), (DialogInterface.OnClickListener) null).show();
    }

    public static void showalert_yesBtn(final Context context, String str, final EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.alert));
        builder.setMessage(str).setCancelable(false).setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.showKeyboard(context, editText);
            }
        });
        builder.create().show();
    }

    public static void showalert_yes_no(final Context context, String str, String str2, String str3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(str).setCancelable(false).setPositiveButton(str2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ((Activity) context).finish();
            }
        }).setNegativeButton(str3, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public static void hideKeyboard_new(Context context, EditText editText) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, EditText editText) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 2);
    }

   /* public static String getIncreasedValue(String str) {
        String str2;
        double nextUp = Math.nextUp(Double.parseDouble(str) + 0.1d);
        String valueOf = String.valueOf(nextUp);
        if (nextUp >= 10.0d) {
            str2 = valueOf.substring(0, 4);
        } else {
            str2 = valueOf.substring(0, 3);
        }
        return str2.equals("100.") ? "100.0" : str2;
    }*/

    public static String getDecreasedValue(String str) {
        double nextUp = Math.nextUp(Double.parseDouble(str) - 0.1d);
        String valueOf = String.valueOf(nextUp);
        if (nextUp > 10.0d) {
            return valueOf.substring(0, 4);
        }
        return valueOf.substring(0, 3);
    }

    public static String ImageToPdf(String str, String str2) {
        int i;
        Bitmap bitmap;
        Bitmap bitmap2;
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(str2));
            Bitmap bitmap3 = null;
            int i2 = 0;
            OutOfMemoryError e;
            try {
                Bitmap decodeFile = BitmapFactory.decodeFile(str);
                bitmap = Bitmap.createBitmap(decodeFile, 0, 0, decodeFile.getWidth(), decodeFile.getHeight() / 2);
                try {
                    bitmap3 = Bitmap.createBitmap(decodeFile, 0, decodeFile.getHeight() / 2, decodeFile.getWidth(), decodeFile.getHeight() / 2);
                    i = bitmap.getWidth() + 50;
                } catch (OutOfMemoryError e1) {
                    e = e1;
                    i = 0;
                    Bitmap bitmap4 = bitmap;
                    bitmap2 = bitmap3;
                    bitmap3 = bitmap4;
                    e.printStackTrace();
                    Bitmap bitmap5 = bitmap2;
                    bitmap = bitmap3;
                    bitmap3 = bitmap5;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                    bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
                    byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
                    document.setPageSize(new Rectangle((float) i, (float) i2));
                    document.setPageCount(2);
                    document.open();
                    document.add(Image.getInstance(byteArray));
                    document.newPage();
                    document.add(Image.getInstance(byteArray2));
                    document.close();
                    return str2;
                } catch (Exception unused) {
                    i = 0;
                    Bitmap bitmap6 = bitmap;
                    bitmap2 = bitmap3;
                    bitmap3 = bitmap6;
                    Bitmap bitmap52 = bitmap2;
                    bitmap = bitmap3;
                    bitmap3 = bitmap52;
                    ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream3);
                    byte[] byteArray3 = byteArrayOutputStream3.toByteArray();
                    ByteArrayOutputStream byteArrayOutputStream22 = new ByteArrayOutputStream();
                    bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream22);
                    byte[] byteArray22 = byteArrayOutputStream22.toByteArray();
                    document.setPageSize(new Rectangle((float) i, (float) i2));
                    document.setPageCount(2);
                    document.open();
                    document.add(Image.getInstance(byteArray3));
                    document.newPage();
                    document.add(Image.getInstance(byteArray22));
                    document.close();
                    return str2;
                }
                try {
                    i2 = bitmap.getHeight() + 50;
                } catch (OutOfMemoryError e2) {
                    e = e2;
                    Bitmap bitmap42 = bitmap;
                    bitmap2 = bitmap3;
                    bitmap3 = bitmap42;
                    e.printStackTrace();
                    Bitmap bitmap522 = bitmap2;
                    bitmap = bitmap3;
                    bitmap3 = bitmap522;
                    ByteArrayOutputStream byteArrayOutputStream32 = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream32);
                    byte[] byteArray32 = byteArrayOutputStream32.toByteArray();
                    ByteArrayOutputStream byteArrayOutputStream222 = new ByteArrayOutputStream();
                    bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream222);
                    byte[] byteArray222 = byteArrayOutputStream222.toByteArray();
                    document.setPageSize(new Rectangle((float) i, (float) i2));
                    document.setPageCount(2);
                    document.open();
                    document.add(Image.getInstance(byteArray32));
                    document.newPage();
                    document.add(Image.getInstance(byteArray222));
                    document.close();
                    return str2;
                } catch (Exception unused2) {
                    Bitmap bitmap62 = bitmap;
                    bitmap2 = bitmap3;
                    bitmap3 = bitmap62;
                    Bitmap bitmap5222 = bitmap2;
                    bitmap = bitmap3;
                    bitmap3 = bitmap5222;
                    ByteArrayOutputStream byteArrayOutputStream322 = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream322);
                    byte[] byteArray322 = byteArrayOutputStream322.toByteArray();
                    ByteArrayOutputStream byteArrayOutputStream2222 = new ByteArrayOutputStream();
                    bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2222);
                    byte[] byteArray2222 = byteArrayOutputStream2222.toByteArray();
                    document.setPageSize(new Rectangle((float) i, (float) i2));
                    document.setPageCount(2);
                    document.open();
                    document.add(Image.getInstance(byteArray322));
                    document.newPage();
                    document.add(Image.getInstance(byteArray2222));
                    document.close();
                    return str2;
                }
            } catch (OutOfMemoryError e3) {
                e = e3;
                bitmap2 = null;
                i = 0;
                e.printStackTrace();
                Bitmap bitmap52222 = bitmap2;
                bitmap = bitmap3;
                bitmap3 = bitmap52222;
                ByteArrayOutputStream byteArrayOutputStream3222 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream3222);
                byte[] byteArray3222 = byteArrayOutputStream3222.toByteArray();
                ByteArrayOutputStream byteArrayOutputStream22222 = new ByteArrayOutputStream();
                bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream22222);
                byte[] byteArray22222 = byteArrayOutputStream22222.toByteArray();
                document.setPageSize(new Rectangle((float) i, (float) i2));
                document.setPageCount(2);
                document.open();
                document.add(Image.getInstance(byteArray3222));
                document.newPage();
                document.add(Image.getInstance(byteArray22222));
                document.close();
                return str2;
            } catch (Exception unused3) {
                bitmap2 = null;
                i = 0;
                Bitmap bitmap522222 = bitmap2;
                bitmap = bitmap3;
                bitmap3 = bitmap522222;
                ByteArrayOutputStream byteArrayOutputStream32222 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream32222);
                byte[] byteArray32222 = byteArrayOutputStream32222.toByteArray();
                ByteArrayOutputStream byteArrayOutputStream222222 = new ByteArrayOutputStream();
                bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream222222);
                byte[] byteArray222222 = byteArrayOutputStream222222.toByteArray();
                document.setPageSize(new Rectangle((float) i, (float) i2));
                document.setPageCount(2);
                document.open();
                document.add(Image.getInstance(byteArray32222));
                document.newPage();
                document.add(Image.getInstance(byteArray222222));
                document.close();
                return str2;
            }
            ByteArrayOutputStream byteArrayOutputStream322222 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream322222);
            byte[] byteArray322222 = byteArrayOutputStream322222.toByteArray();
            ByteArrayOutputStream byteArrayOutputStream2222222 = new ByteArrayOutputStream();
            bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2222222);
            byte[] byteArray2222222 = byteArrayOutputStream2222222.toByteArray();
            document.setPageSize(new Rectangle((float) i, (float) i2));
            document.setPageCount(2);
            document.open();
            document.add(Image.getInstance(byteArray322222));
            document.newPage();
            document.add(Image.getInstance(byteArray2222222));
            document.close();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        return str2;
    }

    public static void takeSnapOfWebview(android.webkit.WebView r8, Context r9) {
        throw new UnsupportedOperationException("Method not decompiled: com.rhewumapp.utility.Utils.takeSnapOfWebview(android.webkit.WebView, android.content.Context):void");
    }
    public static void takeSnapOfWebviewVibSonic(android.webkit.WebView r9, Context r10) {
        throw new UnsupportedOperationException("Method not decompiled: com.rhewumapp.utility.Utils.takeSnapOfWebviewVibSonic(android.webkit.WebView, android.content.Context):void");
    }


  /*  public static String readHtmlVibSonic(Context context, int i, String str,String maxValue) {
        InputStream inputStream;
        getHelper(context);
        measureMentList.clear();
        if (!str.equals("ArchieveList")) {
            measureMentList = dbHelper.getListById(dbHelper.getLastId());
        } else {
            measureMentList = dbHelper.getListById(i);
        }
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#0.0#", decimalFormatSymbols);
        String str2 = decimalFormat.format(measureMentList.get(0).decibleForFreq32) + " db(A)";
        String str3 = decimalFormat.format(measureMentList.get(0).decibleForFreq63) + " db(A)";
        String str4 = decimalFormat.format(measureMentList.get(0).decibleForFreq125) + " db(A)";
        String str5 = decimalFormat.format(measureMentList.get(0).decibleForFreq250) + " db(A)";
        String str6 = decimalFormat.format(measureMentList.get(0).decibleForFreq500) + " db(A)";
        String str7 = decimalFormat.format(measureMentList.get(0).decibleForFreq1k) + " db(A)";
        String str8 = decimalFormat.format(measureMentList.get(0).decibleForFreq2k) + " db(A)";
        String str9 = decimalFormat.format(measureMentList.get(0).decibleForFreq4k) + " db(A)";
        String str10 = decimalFormat.format(measureMentList.get(0).decibleForFreq8k) + " db(A)";
        String str11 = decimalFormat.format(measureMentList.get(0).decibleForFreq16k) + " db(A)";
        @SuppressLint("SimpleDateFormat") String replace = new SimpleDateFormat("MMM dd yyyy, hh:mm:ss aa").format(measureMentList.get(0).measurementDate).replace("AM", "am").replace("PM", "pm");
        String valueOf = String.valueOf(measureMentList.get(0).measurementTotalTime);
        String valueOf2 = String.valueOf(measureMentList.get(0).graphImage);
        String valueOf3 = String.valueOf(measureMentList.get(0).meanLevelTotal);
        Log.e("MeanLevel Total",valueOf3);
        try {
            inputStream = context.getResources().getAssets().open("vibsonic.html");
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = null;
        }
        try {
            return getStringFromInputStream(inputStream).replace(DublinCoreProperties.DATE, replace).replace("time", valueOf).replace("freq_0", str2).replace("freq_1", str3).replace("freq_2", str4).replace("freq_3", str5).replace("freq_4", str6).replace("freq_5", str7).replace("freq_6", str8).replace("freq_7", str9).replace("freq_8", str10).replace("freq_9", str11).replace("meanLevelTotal", valueOf3).replace("imagelogo", " \" " + valueOf2 + " \" ")
                    .replace("freq", String.valueOf(maxValue));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }*/


    //added new for mean
    public static String readHtmlVibSonic(Context context, int i, String str, String maxValue, String meanValue) {
        InputStream inputStream;
        getHelper(context);
        measureMentList.clear();

        if (!str.equals("ArchieveList")) {
            measureMentList = dbHelper.getListById(dbHelper.getLastId());
        } else {
            measureMentList = dbHelper.getListById(i);
        }

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#0.0#", decimalFormatSymbols);

        String str2 = decimalFormat.format(measureMentList.get(0).decibleForFreq32) + " db(A)";
        String str3 = decimalFormat.format(measureMentList.get(0).decibleForFreq63) + " db(A)";
        String str4 = decimalFormat.format(measureMentList.get(0).decibleForFreq125) + " db(A)";
        String str5 = decimalFormat.format(measureMentList.get(0).decibleForFreq250) + " db(A)";
        String str6 = decimalFormat.format(measureMentList.get(0).decibleForFreq500) + " db(A)";
        String str7 = decimalFormat.format(measureMentList.get(0).decibleForFreq1k) + " db(A)";
        String str8 = decimalFormat.format(measureMentList.get(0).decibleForFreq2k) + " db(A)";
        String str9 = decimalFormat.format(measureMentList.get(0).decibleForFreq4k) + " db(A)";
        String str10 = decimalFormat.format(measureMentList.get(0).decibleForFreq8k) + " db(A)";
        String str11 = decimalFormat.format(measureMentList.get(0).decibleForFreq16k) + " db(A)";

        @SuppressLint("SimpleDateFormat")
        String replace = new SimpleDateFormat("MMM dd yyyy, hh:mm:ss aa")
                .format(measureMentList.get(0).measurementDate)
                .replace("AM", "am").replace("PM", "pm");

        String valueOf = String.valueOf(measureMentList.get(0).measurementTotalTime);
        String valueOf2 = String.valueOf(measureMentList.get(0).graphImage);
        String valueOf3 = String.valueOf(measureMentList.get(0).meanLevelTotal);

        Log.e("MeanLevel Total", valueOf3);

        try {
            inputStream = context.getResources().getAssets().open("vibsonic.html");
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = null;
        }

        try {
            // Replace placeholders in the HTML template with maxValue and meanValue
            return getStringFromInputStream(inputStream)
                    .replace(DublinCoreProperties.DATE, replace)
                    .replace("time", valueOf)
                    .replace("freq_0", str2)
                    .replace("freq_1", str3)
                    .replace("freq_2", str4)
                    .replace("freq_3", str5)
                    .replace("freq_4", str6)
                    .replace("freq_5", str7)
                    .replace("freq_6", str8)
                    .replace("freq_7", str9)
                    .replace("freq_8", str10)
                    .replace("freq_9", str11)
                    .replace("meanLevelTotal", valueOf3)
                    .replace("imagelogo", " \" " + valueOf2 + " \" ")
                    .replace("freq", String.valueOf(maxValue))
                    .replace("mean", meanValue); // Add placeholder for mean value if needed
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    //

    public static String getTwoDigits(String str) {
        return new DecimalFormat("#0.00").format(Double.parseDouble(str));
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x002c A[SYNTHETIC, Splitter:B:18:0x002c] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x003b A[SYNTHETIC, Splitter:B:25:0x003b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            reader.close();
        }

        return stringBuilder.toString();
    }

    public static String getDrawableImage(Context context) {
        try {
            Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            decodeResource.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, 0);
        } catch (Exception unused) {
            return "";
        }
    }

    public static RhewumDbHelper getHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = (RhewumDbHelper) OpenHelperManager.getHelper(context, RhewumDbHelper.class);
        }
        return dbHelper;
    }

    public static void showAlertToDelete(Context context, final DeleteDialog deleteDialog, final ArrayList<MeasurementDao> arrayList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(context.getResources().getString(R.string.delete_measurement)).setCancelable(false).setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDialog.onDeleteYesNo(arrayList, true);
                dialogInterface.cancel();
            }
        }).setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }


    public static void showAlertToDeleteSummary(Context context, final VibChekerDeleteDialog vibChekerDeleteDialog, final ArrayList<VibCheckerSummaryDao> arrayList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(context.getResources().getString(R.string.delete_measurement)).setCancelable(false).setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                vibChekerDeleteDialog.onDeleteYesNo(arrayList, true);
                dialogInterface.cancel();
            }
        }).setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }


    public static void setCameraDisplayOrientation(Activity activity, int i, Camera camera) {
        int i2;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(i, cameraInfo);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int i3 = 0;
        if (rotation != 0) {
            if (rotation == 1) {
                i3 = 90;
            } else if (rotation == 2) {
                i3 = 180;
            } else if (rotation == 3) {
                i3 = TIFFConstants.TIFFTAG_IMAGEDESCRIPTION;
            }
        }
        if (cameraInfo.facing == 1) {
            i2 = (360 - ((cameraInfo.orientation + i3) % 360)) % 360;
        } else {
            i2 = ((cameraInfo.orientation - i3) + 360) % 360;
        }
        camera.setDisplayOrientation(i2);
    }

    public static int setImageOrientation(Activity activity) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int i = 0;
        Camera.getCameraInfo(0, cameraInfo);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation != 0) {
            if (rotation == 1) {
                i = 90;
            } else if (rotation == 2) {
                i = 180;
            } else if (rotation == 3) {
                i = TIFFConstants.TIFFTAG_IMAGEDESCRIPTION;
            }
        }
        if (cameraInfo.facing == 1) {
            return (360 - ((cameraInfo.orientation + i) % 360)) % 360;
        }
        return ((cameraInfo.orientation - i) + 360) % 360;
    }

    public static byte[] rotate(byte[] bArr) {
        Matrix matrix = new Matrix();
        Device.load();
        matrix.postRotate((float) Device.getOrientation());
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
        if (decodeByteArray.getWidth() < decodeByteArray.getHeight()) {
            return bArr;
        }
        Bitmap createBitmap = Bitmap.createBitmap(decodeByteArray, 0, 0, decodeByteArray.getWidth(), decodeByteArray.getHeight(), matrix, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String getIncreasedValue(String str) {
        // Replace comma with dot to handle locale-specific decimal points
        str = str.replace(",", ".");

        double nextUp = Math.nextUp(Double.valueOf(str) + 0.1d);
        String valueOf = String.valueOf(nextUp);
        String str2;

        // Ensure correct string formatting based on the value
        if (nextUp >= 10.0d) {
            str2 = valueOf.substring(0, 4);
        } else {
            str2 = valueOf.substring(0, 3);
        }

        return str2.equals("100.") ? "100.0" : str2;
    }
}