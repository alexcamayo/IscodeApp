package com.proyecto.iscodeapp.Funciones;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.proyecto.iscodeapp.R;

public class prueba extends AppCompatActivity {
    ImageView imagen;
    TextView texto;
    Button boton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba);
        imagen=findViewById(R.id.imagen);
        texto=findViewById(R.id.texto);
        boton=findViewById(R.id.boton);
    }
}


