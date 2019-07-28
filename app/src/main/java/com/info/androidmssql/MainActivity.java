package com.info.androidmssql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // views
    private EditText mEdtScanOrder;
    private TextView mTxtStartTime;
    private TextView mTxtEndTime;

    private Button mBtnPicking;
    private Button mBtnSpecs;
    private Button mBtnLoading;
    private Button mBtnCleaning;
    private Button mBtnPacking;

    private Button mBtnSave;

    // date
    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;

    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;


    private int mActivityID = 1;

    // connection
    private Connection mConnection;

    // phone id
    private String mPhoneID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // calendar
        final Calendar cal = Calendar.getInstance();
        mStartYear = mEndYear = cal.get(Calendar.YEAR);
        mStartMonth = mEndMonth = cal.get(Calendar.MONTH);
        mStartDay = mEndDay = cal.get(Calendar.DAY_OF_MONTH);

        // get phone id
        mPhoneID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // views
        mEdtScanOrder = findViewById(R.id.edtScanOrder);
        mTxtStartTime = findViewById(R.id.txtStartTime);
        mTxtEndTime = findViewById(R.id.txtEndTime);

        mBtnPicking = findViewById(R.id.btnPicking);
        mBtnPicking.setOnClickListener(view -> {
            mActivityID = 1;

            mBtnPicking.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnPicking.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });
        mBtnPicking.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

        mBtnSpecs = findViewById(R.id.btnSpecs);
        mBtnSpecs.setOnClickListener(view -> {
            mActivityID = 2;

            mBtnPicking.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnPicking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        mBtnLoading = findViewById(R.id.btnLoading);
        mBtnLoading.setOnClickListener(view -> {
            mActivityID = 3;

            mBtnPicking.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnPicking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        mBtnCleaning = findViewById(R.id.btnCleaning);
        mBtnCleaning.setOnClickListener(view -> {
            mActivityID = 4;

            mBtnPicking.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabEnabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabDisabled));

            mBtnPicking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        mBtnPacking = findViewById(R.id.btnPacking);
        mBtnPacking.setOnClickListener(view -> {
            mActivityID = 5;

            mBtnPicking.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnSpecs.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnLoading.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnCleaning.setTextColor(getResources().getColor(R.color.colorTabDisabled));
            mBtnPacking.setTextColor(getResources().getColor(R.color.colorTabEnabled));

            mBtnPicking.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnSpecs.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnLoading.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnCleaning.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            mBtnPacking.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        });

        //
        Button btnStartTime = findViewById(R.id.btnStartTime);
        btnStartTime.setOnClickListener(view -> {
            new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mStartYear = year;
                    mStartMonth = month;
                    mStartDay = day;

                    mTxtStartTime.setText((mStartMonth+1) + "/" + mStartDay + "/" + mStartYear);
                }
            }, mStartYear, mStartMonth, mStartDay).show();
        });
        mTxtStartTime.setText((mStartMonth+1) + "/" + mStartDay + "/" + mStartYear);

        Button btnEndTime = findViewById(R.id.btnEndTime);
        btnEndTime.setOnClickListener(view -> {
            new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mEndYear = year;
                    mEndMonth = month;
                    mEndDay = day;

                    mTxtEndTime.setText((mEndMonth+1) + "/" + mEndDay + "/" + mEndYear);
                }
            }, mEndYear, mEndMonth, mEndDay).show();

        });
        mTxtEndTime.setText((mEndMonth+1) + "/" + mEndDay + "/" + mEndYear);

        mBtnSave = findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(view -> {
            String strOrderNumber = mEdtScanOrder.getText().toString();
            String strActivityID = String.valueOf(mActivityID);
            String strStartTime = String.format("%d-%d-%d", mStartYear, mStartMonth + 1, mStartDay);
            String strEndTime = String.format("%d-%d-%d", mEndYear, mEndMonth + 1, mEndDay);

            if (strOrderNumber.isEmpty()) {
                Toast.makeText(this, "Scan Order can not be empty!", Toast.LENGTH_SHORT).show();
            } else {
                new SaveAsyncTask().execute(strOrderNumber, strActivityID, strStartTime, strEndTime, mPhoneID);
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

            if (mIsConnected) {
                mBtnSave.setEnabled(true);
                mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 69, 0)));
            } else {
                mBtnSave.setEnabled(false);
                mBtnSave.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            }
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
