package com.info.androidmssql;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(view -> {
            List<Map<String,String>> MyDataList = null;
            GetData myData = new GetData();
            MyDataList = myData.getData();
            
            Toast.makeText(this,"Connection trying...", Toast.LENGTH_LONG).show();
        });
    }
}
