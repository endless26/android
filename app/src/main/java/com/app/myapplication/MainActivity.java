package com.app.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.opencsv.CSVReader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private static String applicationPath = "com.tokyo.app";
    private static String appPath;

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private EditText fileName;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            }
        }

        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startDownload();
            }
        });

        Button getLang = findViewById(R.id.getLang);
        getLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getLang();
                System.out.println(getLang());
            }
        });

        final Button download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });

        Button getBtn = findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fileText = findViewById(R.id.file);
                fileName = findViewById(R.id.fileName);
                ImageView myImage = findViewById(R.id.image);

                File file = new File(Environment.getExternalStorageDirectory(), appPath+"/"+fileName.getText());
                if(file.exists()){
                    String fileType = fileName.getText().toString().substring(fileName.getText().toString().lastIndexOf("."));

                    switch (fileType){
                        case ".csv":
                            fileText.setText(fileName.getText());
                            break;
                        case ".png":
                        case ".jpg":
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            myImage.setImageBitmap(myBitmap);
                            break;
                    }
                }else{
                    fileText.setText(null);
                    myImage.setImageBitmap(null);
                }
            }
        });

//        decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                if (visibility == 0) {
//                    decorView.setSystemUiVisibility(hideSystemBards());
//                }
//            }
//        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
//            decorView.setSystemUiVisibility(hideSystemBards());
        }
    }

    private int hideSystemBards() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    private void startDownload() {
        String file_url = "http://www.sixasix.com/tokyotg/";

        ArrayList<String> csvFile = new ArrayList<>();
        csvFile.add("id.csv");
        csvFile.add("en.csv");
        csvFile.add("zh.csv");
        csvFile.add("th.csv");

        for(int i = 0;i<csvFile.size();i++) {
            new DownloadFileFromURL().execute(file_url + csvFile.get(i));
        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    @SuppressLint("StaticFieldLeak")
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String[] f_url) {
            appPath = applicationPath;
            System.out.println("externalstorage "+Environment.getExternalStorageDirectory());
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);


                String fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
                System.out.println("fileName fileName "+fileName);
                // Output stream
                String fileType = fileName.substring(fileName.lastIndexOf("."));
                System.out.println("fileType "+fileType);
                switch (fileType){
                    case ".csv":
                        appPath = appPath+"/lang";
                        break;
                    case ".png":
                    case ".jpg":
                        appPath = appPath+"/img";
                        break;
                }
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), appPath);
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("App", "failed to create directory");
                    }
                }
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/"+appPath+"/"+fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String[] progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
        }
    }

    private String getLangBackup() {
        appPath = applicationPath;
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        List<String> mLines = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory()+"/"+appPath+"/lang/";
        File folder = new File(path);
        File[] files = folder.listFiles();
        JSONObject obLanguage = new JSONObject();
        for (File file : files) {
            JSONArray arLanguage = new JSONArray();
            String fileName = file.getName();
            fileName = fileName.replace(".csv", "");
            try {
                CSVReader reader = new CSVReader(new FileReader(path + file.getName()));
                String[] line;
                JSONObject obLine = new JSONObject();
                while ((line = reader.readNext()) != null) {
                    if (!line[0].contains("/") && !line[0].equals("")) {
                        if(line[1].compareTo("lang") != 0) {
                            obLine.put(line[0], line[1]);
                        }
                    }
                }
                obLanguage.put(fileName,obLine);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("lang",obLanguage);
        }catch (Exception e){
            e.printStackTrace();
        }
        return String.valueOf(object);
    }

    public static void download(){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("http://www.sixasix.com/tokyotg/toggle.apk");
        File file = new File(Environment.getExternalStorageDirectory()+"/Download/", "toggle.apk");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Tokyo Toggle");
        request.setDescription("Downloading...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationUri(Uri.fromFile(file));
        request.setDestinationInExternalPublicDir("/Download","toggle.apk");

        downloadManager.enqueue(request);
    }

    private String getLang(){
        appPath = applicationPath;
        JSONObject object = new JSONObject();

        String path = Environment.getExternalStorageDirectory()+"/"+appPath+"/lang/";
        File folder = new File(path);
        File[] files = folder.listFiles();

        JSONObject obLanguage = new JSONObject();

        assert files != null;
        for (File file : files) {
            String fileName = file.getName();
            File csvFile = new File(path, fileName);

            fileName = fileName.replace(".csv", "");
            try {
                JSONObject obLine = new JSONObject();

                InputStream inputStream = new FileInputStream(csvFile);
                CSVFile csv = new CSVFile(inputStream);
                String line;
                BufferedReader reader = csv.read();

                while ((line = reader.readLine()) != null) {
                    String[] row = line.split(",");
                    if (!row[0].contains("/") && !row[0].equals("")) {
                        if (row[1].compareTo("lang") != 0) {
                            obLine.put(row[0], row[1]);
                        }
                    }
                    obLanguage.put(fileName, obLine);
                }
                object.put("lang", obLanguage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(object);
    }
}
