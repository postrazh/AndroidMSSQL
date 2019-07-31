package com.info.androidmssql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int BARCODE_READER_ACTIVITY_REQUEST = 1208;

    // views
    private Button mBtnSpecs;
    private Button mBtnLoading;
    private Button mBtnClosing;
    private Button mBtnCleaning;
    private Button mBtnPacking;

    private Button mBtnScanOrder;
    private TextView mTxtOrderNumber;

    private Button mBtnSave;

    // data
    private String mPhoneID = "";
    private int mActivityID = -1;

    // connection
    private Connection mConnection;


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

        mBtnScanOrder = findViewById(R.id.btnScanOrder);
        mTxtOrderNumber = findViewById(R.id.txtOrderNumber);

        mBtnSave = findViewById(R.id.btnSave);

        mBtnSpecs.setOnClickListener(view -> {
            mActivityID = 1;
            mBtnScanOrder.setEnabled(true);
            mBtnScanOrder.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(59, 118, 235)));

            mBtnClosing.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnClosing.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(121, 185, 225)));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        mBtnLoading.setOnClickListener(view -> {
            mActivityID = 2;
            mBtnScanOrder.setEnabled(true);
            mBtnScanOrder.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(59, 118, 235)));

            mBtnClosing.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnClosing.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(229, 225, 184)));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        mBtnClosing.setOnClickListener(view -> {
            mActivityID = 3;
            mBtnScanOrder.setEnabled(true);
            mBtnScanOrder.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(59, 118, 235)));

            mBtnClosing.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnClosing.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(229, 193, 0)));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        mBtnCleaning.setOnClickListener(view -> {
            mActivityID = 4;
            mBtnScanOrder.setEnabled(true);
            mBtnScanOrder.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(59, 118, 235)));

            mBtnClosing.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnClosing.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(0, 229, 0)));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        mBtnPacking.setOnClickListener(view -> {
            mActivityID = 5;
            mBtnScanOrder.setEnabled(true);
            mBtnScanOrder.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(59, 118, 235)));

            mBtnClosing.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabEnabled));

            mBtnClosing.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(229, 126, 0)));
        });

        mBtnScanOrder.setOnClickListener(view -> {
            Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false);
            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST);
        });
        mBtnScanOrder.setEnabled(false);
        mBtnScanOrder.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

        mBtnSave.setOnClickListener(view -> {
            String strOrderNumber = mTxtOrderNumber.getText().toString();
            String strActivityID = String.valueOf(mActivityID);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            String strCurrentTimeStamp = simpleDateFormat.format(new Date());

            if (strOrderNumber.isEmpty()) {
                Toast.makeText(this, "Scan Order can not be empty!", Toast.LENGTH_SHORT).show();
            } else if (mActivityID < 0) {
                Toast.makeText(this, "Please select the Activity ID", Toast.LENGTH_SHORT).show();
            } else {
                new SaveAsyncTask().execute(strOrderNumber, strActivityID, strCurrentTimeStamp, strCurrentTimeStamp, mPhoneID);

                // buttons
                mBtnSave.setEnabled(false);
                mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

                mBtnScanOrder.setEnabled(false);
                mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                mTxtOrderNumber.setText("");

                mActivityID = -1;
                mBtnClosing.setTextColor(getResources().getColor(R.color.colorTabDisabled));
                mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
                mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
                mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
                mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

                mBtnClosing.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            }
        });
        mBtnSave.setEnabled(false);
        mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

        // connect
        new ConnectionAsyncTask().execute();
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "error in  scanning", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);
            Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show();

            mTxtOrderNumber.setText(barcode.rawValue);

            mBtnScanOrder.setEnabled(false);
            mBtnScanOrder.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

            mBtnSave.setEnabled(true);
            mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 69, 0)));
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
                    mResultMessage = "Successfully saved!";
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

            Toast.makeText(MainActivity.this, mResultMessage, Toast.LENGTH_LONG).show();
        }
    }
}
