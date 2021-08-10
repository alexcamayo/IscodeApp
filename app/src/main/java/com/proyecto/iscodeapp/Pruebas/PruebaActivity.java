package com.proyecto.iscodeapp.Pruebas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.proyecto.iscodeapp.R;


import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class PruebaActivity extends AppCompatActivity {
    Interpreter interpreter;
    Button prueba;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        prueba=findViewById(R.id.prueba);
        imagen=findViewById(R.id.imagen);

        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInference();
            }
        });prueba.setVisibility(View.GONE);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("modelC.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset=assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);

    }

    public void doInference(){
        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] mean = {54.542088f, 0.676768f ,3.158249f ,131.693603f, 247.350168f, 0.144781f, 0.996633f,
                149.599327f, 0.326599f, 1.055556f, 1.602694f, 0.676768f, 4.730640f};
        float[] std ={ 9.049736f, 0.468500f, 0.964859f, 17.762806f, 51.997583f, 0.352474f, 0.994914f,
                22.941562f, 0.469761f, 1.166123f, 0.618187f, 0.938965f, 1.938629f};


        float[] muestra = {67,1,4,160,286,0,2,108,1,1.5f,2,3,3};
        float[] inputs=new float[13];

        for (int i=0;i<muestra.length;i++){

            inputs[i]=(muestra[i] - mean[i]) / (std[i]);
        }

        float[][] output = new  float[1][1];// Output tensor shape is [3, 2].

        interpreter.run(inputs,output);

        float val_ML=output[0][0];
        String pred="";
        if (val_ML>=0.5){
            pred="SÍ";
        }
        else{
            pred="NO";
        }

        Log.d("tag1721", "la salida "+ pred);
    }
}