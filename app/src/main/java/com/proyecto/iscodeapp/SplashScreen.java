package com.proyecto.iscodeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SplashScreen extends AppCompatActivity {

    String white="#FFFFFF";
    String black="#000000";
    FirebaseUser firebaseUser;
    Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor(white));
        window.setNavigationBarColor(Color.parseColor(white));
        setContentView(R.layout.activity_splashscreen);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        new CountDownTimer(2500, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {

                /*if (GetRBstate()){
                    Toast.makeText(SplashScreen.this,"¡Bienvenido!",Toast.LENGTH_SHORT).show();
                    Intent gohome=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(gohome);
                    finish();
                }
                else{
                    Intent goingresar=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(goingresar);
                    finish();
                }*/

                try {
                    interpreter = new Interpreter(loadModelFile(),null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (firebaseUser!=null){
                    Intent goMain=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(goMain);
                    finish();
                }
                else{
                    Intent goingresar=new Intent(SplashScreen.this,IngresarActivity.class);
                    startActivity(goingresar);
                    finish();
                }



            }
        }.start();


    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset=assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);

    }
}