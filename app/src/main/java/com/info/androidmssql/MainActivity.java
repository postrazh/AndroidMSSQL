package com.info.androidmssql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // views
    private EditText mEdtScanOrder;
    private TextView mTxtStartTime;
    private TextView mTxtEndTime;

    private Button mBtnClosing;
    private Button mBtnSpecs;
    private Button mBtnLoading;
    private Button mBtnCleaning;
    private Button mBtnPacking;

    private Button mBtnStartTime;
    private Button mBtnEndTime;
    private Button mBtnSave;

    // start and end time
    private String mStartTimestamp = "";
    private String mEndTimestamp = "";


    private int mActivityID = -1;

    // connection
    private Connection mConnection;

    // phone id
    private String mPhoneID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get phone id
        mPhoneID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // views
        mEdtScanOrder = findViewById(R.id.edtScanOrder);
        mTxtStartTime = findViewById(R.id.txtStartTime);
        mTxtEndTime = findViewById(R.id.txtEndTime);

        mBtnSpecs = findViewById(R.id.btnSpecs);
        mBtnSpecs.setOnClickListener(view -> {
            mActivityID = 1;

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

        mBtnLoading = findViewById(R.id.btnLoading);
        mBtnLoading.setOnClickListener(view -> {
            mActivityID = 2;

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

        mBtnClosing = findViewById(R.id.btnClosing);
        mBtnClosing.setOnClickListener(view -> {
            mActivityID = 3;

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

        mBtnCleaning = findViewById(R.id.btnCleaning);
        mBtnCleaning.setOnClickListener(view -> {
            mActivityID = 4;

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

        mBtnPacking = findViewById(R.id.btnPacking);
        mBtnPacking.setOnClickListener(view -> {
            mActivityID = 5;

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

        // barcode
        mEdtScanOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEdtScanOrder.getText().toString().isEmpty()) {
                    mBtnStartTime.setEnabled(false);
                } else {
                    mBtnStartTime.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // start button
        mBtnStartTime = findViewById(R.id.btnStartTime);
        mBtnStartTime.setOnClickListener(view -> {
            SimpleDateFormat s1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            String format = s1.format(new Date());
            mTxtStartTime.setText(format);
            mStartTimestamp = format;

            // buttons
            mBtnStartTime.setEnabled(false);
            mBtnEndTime.setEnabled(true);
            mBtnSave.setEnabled(false);
            mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        });
        mBtnStartTime.setEnabled(false);

        // end button
        mBtnEndTime = findViewById(R.id.btnEndTime);
        mBtnEndTime.setOnClickListener(view -> {
            SimpleDateFormat s1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            String format = s1.format(new Date());
            mTxtEndTime.setText(format);
            mEndTimestamp = format;

            // buttons
            mBtnStartTime.setEnabled(false);
            mBtnEndTime.setEnabled(false);
            mBtnSave.setEnabled(true);
            mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 69, 0)));
        });
        mBtnEndTime.setEnabled(false);

        mBtnSave = findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(view -> {
            String strOrderNumber = mEdtScanOrder.getText().toString();
            String strActivityID = String.valueOf(mActivityID);

            if (strOrderNumber.isEmpty()) {
                Toast.makeText(this, "Scan Order can not be empty!", Toast.LENGTH_SHORT).show();
            } else if (mActivityID < 0) {
                Toast.makeText(this, "Please select the Activity ID", Toast.LENGTH_SHORT).show();
            } else {
                new SaveAsyncTask().execute(strOrderNumber, strActivityID, mStartTimestamp, mEndTimestamp, mPhoneID);

                // buttons
                mBtnStartTime.setEnabled(false);
                mBtnEndTime.setEnabled(false);
                mBtnSave.setEnabled(false);
                mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

                mTxtStartTime.setText("");
                mTxtEndTime.setText("");
                mEdtScanOrder.setText("");

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
