package com.info.androidmssql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SETTINGS_ACTIVITY = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

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

    //
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private final long ONE_DAY = 24 * 60 * 60 * 1000;

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

    AlertDialog permd;

    private Handler mHandler = new Handler();

    private int count = 0;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            count ++;
            if (count > 600) {
                count = 0;
                try {
                    mConnection.close();
                    mConnection = null;
                    connect();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            mHandler.postDelayed(this, 1000);
        }
    };

    private boolean isConnecting = false;

    private void connect() {
        if (!isConnecting) {
            try {
                if (mConnection == null) {
                    new ConnectionAsyncTask().execute();
                } else if (mConnection.isClosed()) {
                    new ConnectionAsyncTask().execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //
//        checkTime();

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

        // check permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            final String[] perms = {
                    Manifest.permission.CAMERA
            };
            for(String perm: perms)
                if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                    if (permd == null || !permd.isShowing())
                        permd = new AlertDialog.Builder(this)
                                .setMessage("The camera permission is needed to capture the barcode.")
                                .setTitle("Permissions")
                                .setCancelable(true)
                                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        requestPermissions(perms,REQUEST_CAMERA_PERMISSION);
                                    }
                                })
                                .show();
                    return;
                } else {
                    // connect
                    connect();
                }
        }

        // check
        mHandler.postDelayed(mRunnable, 10000);
    }

    private void checkTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String installDate = preferences.getString("InstallDate", null);
        if(installDate == null) {
            // First run, so save the current date
            SharedPreferences.Editor editor = preferences.edit();
            Date now = new Date();
            String dateString = formatter.format(now);
            editor.putString("InstallDate", dateString);
            // Commit the edits!
            editor.commit();
        }
        else {
            // This is not the 1st run, check install date
            Date before = null;
            try {
                before = (Date)formatter.parse(installDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date now = new Date();
            long diff = now.getTime() - before.getTime();
            long days = diff / ONE_DAY;
            if(days > 10) {
                System.exit(0);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // connect
                    connect();
                }
            }
        }
    }

    AlertDialog dlg;

    public void onFail(String strMessage) {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 500);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 700);

//        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), strMessage, Snackbar.LENGTH_LONG);
//        snackbar.show();

        if (dlg != null)
            dlg.dismiss();

        dlg = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(strMessage)
                .setCancelable(true)
                .setPositiveButton("OK",null)
                .create();

                dlg.show();
    }

    public void onSuccess(String strMessage) {
        if (dlg != null)
            dlg.dismiss();

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

    private final static int MAX_RETRY = 3;
    private int mRetryNumber = MAX_RETRY;

    private void startScanOrder(String strActivityId) {
        if( BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            if (mConnection == null) {
                onFail("Connection failed. Check your login information.");
                return;
            }
        }

        mRetryNumber = MAX_RETRY;

        if (!hasFrontCamera()) {
            Toast.makeText(MainActivity.this, "There is no camera. Simulating dummy bar code", Toast.LENGTH_SHORT).show();
            saveOrder("123456", strActivityId);
        } else {
            // save activity id
            mStrActivityId = strActivityId;

            // start scan the barcode
            new IntentIntegrator(this).initiateScan();
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTINGS_ACTIVITY) {
            // close connection
            try {
                if (mConnection != null)
                    mConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // connect
            connect();
        } else if (requestCode == IntentIntegrator.REQUEST_CODE){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                    data);
            if (result != null) {
                if (result.getContents() != null) {
                    String barcode = result.getContents();
//                    Toast.makeText(this, "Scanned: " + barcode, Toast.LENGTH_LONG).show();
                    saveOrder(barcode, mStrActivityId);
                }
            } else {
                Toast.makeText(this, "Error in  scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveOrder(String strOrderNumber, String strActivityId) {

        // check order number
        boolean isValidOrder = false;

        try {
            isValidOrder = new CheckOrderAsyncTask().execute(strOrderNumber).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isValidOrder) {
            mRetryNumber--;

            if (mRetryNumber > 0) {
                // scan barcode again
                new IntentIntegrator(this).initiateScan();
            } else {
                onFail("Scanned the invalid barcode. Please contact to your manager");
            }
            return;
        }

        // check order-activity
        boolean isNewOrderActivity = false;
        try {
            isNewOrderActivity = new CheckOrderActivitiesAsyncTask().execute(strOrderNumber, strActivityId).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNewOrderActivity) {
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

        String IP;
        String port;

        public ConnectionAsyncTask() {
            mProgressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isConnecting = true;

            mProgressDialog.setMessage("Connecting to the server.\nPlease wait...");
            mProgressDialog.show();

            mConnection = null;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            IP = sharedPreferences.getString(getString(R.string.pref_IP), "");
            port = sharedPreferences.getString(getString(R.string.pref_port), "");
            ConnectionHelper.setConnectionInfo(IP, port);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                mConnection = connectionHelper.connect();
                if (mConnection == null) {
                    mIsConnected = false;
                } else {
                    mIsConnected = true;
                }
            } catch (Exception e) {
                mIsConnected = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            if (mIsConnected) {
                onSuccess("Successfully connected!");
            } else {
                onFail("Connection failed. Check your login information.");
            }

            isConnecting = false;
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

                mConnection = null;
                connect();
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

    private class CheckOrderActivitiesAsyncTask extends AsyncTask<String, Void, Boolean> {

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
                    statement.setQueryTimeout(5);
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next())
                        return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    private class CheckOrderAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String strOrderNumber = params[0];

            // 114-0448369-2777052 : 19
            // 10316708 : 8
            if (!(strOrderNumber.matches("\\d{3}-\\d{7}-\\d{7}") ||
                strOrderNumber.matches("\\d{13}"))
            ) {
                return false;
            }

            try {
                if(mConnection != null) {
                    String query = "SELECT * FROM [Order] WHERE [Order - Number]='" +
                            strOrderNumber + "'";
                    Statement statement = mConnection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next())
                        return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_SETTINGS_ACTIVITY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
