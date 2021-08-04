package com.example.cardcm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Mode;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    CameraView camera;
    ImageButton capBtn, lightBnt;
    Button recharge;
    ImageView imageView;
    TextView deText;
    boolean stateCam = false;
    String cardNum;
    boolean flashState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedP = getSharedPreferences("com.example.cardcm.shuvo", MODE_PRIVATE);
        Boolean isFirstTime = sharedP.getBoolean("isFirstTime",false);
        if(isFirstTime){
            Log.d("TAG", "onCreate: "+ isFirstTime.toString());
        }else {
            SharedPreferences.Editor editor= getSharedPreferences("com.example.cardcm.shuvo", MODE_PRIVATE).edit();
            editor.putBoolean("isFirstTime", true);
            editor.apply();
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        //init
        recharge = findViewById(R.id.recharge);
        camera = findViewById(R.id.camera);
        capBtn = findViewById(R.id.capBtn);
        lightBnt = findViewById(R.id.lightBtn);
        imageView = findViewById(R.id.imageView);
        deText = findViewById(R.id.deText);
        //init end


        /**
        ****  this block is camera function
        ****  this block is camera function
        ****  this block is camera function
         */


        camera.setLifecycleOwner(this);
        camera.setMode(Mode.PICTURE);
        camera.setZoom(0.3F);


        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
//                super.onPictureTaken(result);
                result.toBitmap(3000, 4000, new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, (int) (bitmap.getHeight() * 0.43), bitmap.getWidth(), 320);
                        imageView.setImageBitmap(croppedBitmap);

                        InputImage image = InputImage.fromBitmap(croppedBitmap, 0);
                        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                        Task<Text> result =
                                recognizer.process(image)
                                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                                            @Override
                                            public void onSuccess(Text visionText) {
                                                // Task completed successfully
                                                // ...
                                                String text = null;

                                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                                    if (block.getText().length() > 16){
                                                        text = block.getText();
                                                    }
                                                }

                                                if (text != null){

                                                    String noSpaceStr = text.replaceAll("\\s", "");
                                                    String strCardNum = noSpaceStr.replaceAll("\\D+","");
                                                    cardNum = strCardNum;
                                                    stateCam = true;
                                                    recharge.setEnabled(true);
                                                    deText.setText(strCardNum);
                                                    //cardNum = Integer.parseInt(strCardNum);
                                                } else {
                                                    stateCam = false;
                                                    recharge.setEnabled(false);
                                                    deText.setText("Error..");
                                                }


//
//                                                try {
//
//
//                                                    deText.setText(String.valueOf(cardNum));
//                                                }catch (NumberFormatException nfex){
//                                                    deText.setText("No Card Number Valid.");
//                                                }







                                            }
                                        })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Task failed with an exception
                                                        // ...
                                                        deText.setText("Error!");
                                                        Toast.makeText(MainActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                    }
                });
            }

            @Override
            public void onPictureShutter() {
//                super.onPictureShutter();
            }
        });

//       " hear a modific" +
//               "ation need at last "

        lightBnt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {

                if(!flashState) {
                    lightBnt.setImageDrawable(getResources().getDrawable(R.drawable.flash_off));
                    camera.setFlash(Flash.TORCH);
                    flashState = true;
                }else {
                    lightBnt.setImageDrawable(getResources().getDrawable(R.drawable.flash_on));
                    camera.setFlash(Flash.OFF);
                    flashState = false;
                }

            }
        });

        capBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture();
            }
        });

        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stateCam){
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String mun = "*555*"+cardNum+Uri.encode("#");
                    callIntent.setData(Uri.parse("tel:"+mun));//change the number
                    startActivity(callIntent);
                }else {
                    Toast.makeText(MainActivity.this, "Scane Again!", Toast.LENGTH_SHORT).show();
                }

            }
        });


       //end onCreate view
    }
}