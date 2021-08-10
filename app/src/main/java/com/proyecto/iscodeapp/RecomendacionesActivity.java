package com.proyecto.iscodeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyecto.iscodeapp.Adapters.RecomendacionesAdapter;
import com.proyecto.iscodeapp.Models.RecomModel;

import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecomendacionesActivity extends AppCompatActivity {

    RecomendacionesAdapter adapter;
    List<RecomModel> models;

    RelativeLayout layout_default,cargando;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String id_usuario;

    Animation animTransition;
    ViewPager2 view_pager;
    LottieAnimationView animacion_icono;

    int posicion_anterior=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendaciones);

        layout_default=findViewById(R.id.layout_default);
        view_pager=findViewById(R.id.view_pager);
        cargando=findViewById(R.id.cargando);

        view_pager.setVisibility(View.GONE);
        layout_default.setVisibility(View.GONE);
        cargando.setVisibility(View.VISIBLE);

        animTransition= AnimationUtils.loadAnimation(this,R.anim.transition);



        //inicializamos el objeto firebaseAuth, los textos y los botones
        firebaseAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();

        id_usuario= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        models = new ArrayList<>();

        database.child("Usuarios").child(id_usuario).child("Recomendaciones").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int cont=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                   RecomModel recomModel=snapshot.getValue(RecomModel.class);
                   models.add(recomModel);

                   cont=cont+1;
               }

                Log.d("tag1231","cont: "+cont);

               if (cont==0){
                   layout_default.setVisibility(View.VISIBLE);
                   view_pager.setVisibility(View.GONE);
                   cargando.setVisibility(View.GONE);
               }
               else{
                   view_pager.setVisibility(View.VISIBLE);
                   view_pager.startAnimation(animTransition);
                   mostrarDatos();
               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

    private void mostrarDatos(){
        adapter=new RecomendacionesAdapter(RecomendacionesActivity.this,models);
        view_pager.setAdapter(adapter);

        view_pager.setClipToPadding(false);
        view_pager.setClipChildren(false);
        view_pager.setOffscreenPageLimit(3);
        view_pager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull @NotNull View page, float position) {
                float r=1-Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });
        view_pager.setPageTransformer(compositePageTransformer);

        cargando.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().hasExtra("ORIGEN")){
            if(getIntent().getStringExtra("ORIGEN").equals("notificaciones")){
                Intent goMain=new Intent(RecomendacionesActivity.this,MainActivity.class);
                startActivity(goMain);
            }
        }
        finish();
    }
}