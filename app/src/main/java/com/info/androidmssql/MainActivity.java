package com.info.androidmssql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.snackbar.Snackbar;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int BARCODE_READER_ACTIVITY_REQUEST = 1208;

    // views
    private Button mBtnSpecs;
    private Button mBtnLoading;
    private Button mBtnClosing;
    private Button mBtnCleaning;
    private Button mBtnPacking;

    // data
    private String mPhoneID = "";
    private String mStrActivityId = "";

    // connection
    private Connection mConnection;

    private boolean hasFrontCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get phone id
        mPhoneID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // views
        mBtnSpecs = findViewById(R.id.btnSpecs);
        mBtnLoading = findViewById(R.id.btnLoading);
        mBtnClosing = findViewById(R.id.btnClosing);
        mBtnCleaning = findViewById(R.id.btnCleaning);
        mBtnPacking = findViewById(R.id.btnPacking);

        mBtnSpecs.setOnClickListener(view -> {
            startScanOrder("1");
        });

        mBtnLoading.setOnClickListener(view -> {
            startScanOrder("2");
        });

        mBtnClosing.setOnClickListener(view -> {
            startScanOrder("3");
        });

        mBtnCleaning.setOnClickListener(view -> {
            startScanOrder("4");
        });

        mBtnPacking.setOnClickListener(view -> {
            startScanOrder("5");
        });

        // connect
        new ConnectionAsyncTask().execute();
    }

    private void onFail(String strMessage) {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 500);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 700);

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), strMessage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onSuccess(String strMessage) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), strMessage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void startScanOrder(String strActivityId) {
        if (!hasFrontCamera()) {
            Toast.makeText(MainActivity.this, "There is no camera. Simulating dummy bar code", Toast.LENGTH_SHORT).show();
            saveOrder("123456", strActivityId);
        } else {
            // save activity id
            mStrActivityId = strActivityId;

            // stat scan the barcode
            Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false);
            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST);
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "Error in  scanning", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);

            saveOrder(barcode.rawValue, mStrActivityId);
        }

    }

    private void saveOrder(String strOrderNumber, String strActivityId) {
        Boolean searchResult = false;
        try {
            searchResult = new SearchAsyncTask().execute(strOrderNumber, strActivityId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (searchResult) {
            String strErrorMsg = "";
            switch (strActivityId) {
                case "1":
                    strErrorMsg = "Specs was already done for this order!";
                    break;
                case "2":
                    strErrorMsg = "Loading was already done for this order!";
                    break;
                case "3":
                    strErrorMsg = "Closing was already done for this order!";
                    break;
                case "4":
                    strErrorMsg = "Cleaning was already done for this order!";
                    break;
                case "5":
                    strErrorMsg = "Packing was already done for this order!";
                    break;
            }

            onFail(strErrorMsg);
        } else {
            // save to the database
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            String strCurrentTimeStamp = simpleDateFormat.format(new Date());

            new SaveAsyncTask().execute(strOrderNumber, strActivityId, strCurrentTimeStamp,
                    strCurrentTimeStamp, mPhoneID);
        }
    }

    private class ConnectionAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mProgressDialog;
        private boolean mIsConnected = false;
        private String mConnectionResult = "";

        public ConnectionAsyncTask() {
            mProgressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setMessage("Connecting to the server.\nPlease wait...");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                mConnection = connectionHelper.connection();
                if (mConnection == null) {
                    mConnectionResult = "Connection failed. Check your login information.";
                    mIsConnected = false;
                } else {
                    mConnectionResult = "Successfully connected!";
                    mIsConnected = true;
                }
            } catch (Exception e) {
                mConnectionResult = e.getMessage();
                mIsConnected = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            Toast.makeText(MainActivity.this, mConnectionResult, Toast.LENGTH_LONG).show();
        }
    }

    private class SaveAsyncTask extends AsyncTask<String, Void, Void> {
        private boolean mIsSuccess = false;
        private String mResultMessage = "";

        public SaveAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if(mConnection != null) {
                    String strOrderNumber = params[0];
                    String strActivityID = params[1];
                    String strStartTime = params[2];
                    String strEndTime = params[3];
                    String strUserID = params[4];

                    String query = "INSERT INTO tbl_OrderActivities ([Order - Number], "
                            + "Activity_ID, StartTime, EndTime, User_ID) VALUES ('"
                            + strOrderNumber + "','"
                            + strActivityID + "','"
                            + strStartTime + "','"
                            + strEndTime + "','"
                            + strUserID + "')";

                    PreparedStatement preparedStatement = mConnection.prepareStatement(query);
                    preparedStatement.executeUpdate();

                    mIsSuccess = true;

                    switch (strActivityID) {
                        case "1":
                            mResultMessage = "Specs stored!";
                            break;
                        case "2":
                            mResultMessage = "Loading stored!";
                            break;
                        case "3":
                            mResultMessage = "Closing stored!";
                            break;
                        case "4":
                            mResultMessage = "Cleaning stored!";
                            break;
                        case "5":
                            mResultMessage = "Packing stored!";
                            break;
                    }
                }
            }
            catch (Exception e) {
                mIsSuccess = false;
                mResultMessage = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mIsSuccess) {
                onSuccess(mResultMessage);
            } else {
                onFail(mResultMessage);
            }
        }
    }

    private class SearchAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if(mConnection != null) {
                    String strOrderNumber = params[0];
                    String strActivityID = params[1];

                    String query = "SELECT * FROM tbl_OrderActivities WHERE [Order - Number]='" +
                            strOrderNumber + "' AND Activity_ID='" +
                            strActivityID + "'";
                    Statement statement = mConnection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next())
                        return true;
                }
            }
            catch (Exception e) {

            }

            return false;
        }
    }
}
