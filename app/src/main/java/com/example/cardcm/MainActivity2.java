package com.example.cardcm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "Tag";
    Button startBtn;
    private static int code = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            String[] permisson = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE
            };

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity2.this, "This apps to run proparly need some permission", Toast.LENGTH_SHORT).show();
                if(checkParmisson( Manifest.permission.CAMERA) || checkParmisson(Manifest.permission.RECORD_AUDIO) || checkParmisson(Manifest.permission.CALL_PHONE)){
                    requestPermissions(permisson,1 );
                }else {

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String text = "you need ";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0){
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                        text = text + "camera";
                        Log.d(TAG, "Camera parmisson ok!");
                    }
                    if(grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                        text = text + ",microphon";
                        Log.d(TAG, "Microphon parmisson ok!");
                    }
                    if(grantResults[2] != PackageManager.PERMISSION_GRANTED){
                        text = text + ",call";
                        Log.d(TAG, "Call parmisson ok!");
                    }
                    startActivity(new Intent(this,MainActivity.class));
                    Toast.makeText(this, text+ " Parmissons!", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "NO PERMISSON", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    
    }

    Boolean checkParmisson(String p){
        if(ContextCompat.checkSelfPermission(MainActivity2.this , p) != PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return  false;
        }
    }

    

}