package com.proyecto.iscodeapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.slider.Slider;
import com.proyecto.iscodeapp.Models.Prediccion;
import com.proyecto.iscodeapp.Models.RecomModel;
import com.proyecto.iscodeapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecomendacionesAdapter extends RecyclerView.Adapter<RecomendacionesAdapter.ViewHolder>{

    Context context;
    List<RecomModel> mModelsList;

    public RecomendacionesAdapter(Context context, List<RecomModel> mModelsList) {
        this.context = context;
        this.mModelsList = mModelsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recomendaciones_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecomendacionesAdapter.ViewHolder holder, int position) {
        RecomModel recomModel= mModelsList.get(position);

        Log.d("tag1231","tit: " +recomModel.getTitulo());

        holder.titulo.setText(recomModel.getTitulo());
        holder.contenido.setText(recomModel.getContenido());
        holder.tipo.setText(recomModel.getTipo());
        holder.fecha.setText(recomModel.getFecha());

        Drawable myIcon;
        switch (recomModel.getTipo()) {
            case "Alimentación":
                 myIcon= AppCompatResources.getDrawable(context, R.drawable.back_recom1);
                holder.layout_contenido.setBackground(myIcon);
                holder.animacion_icono.setAnimation("comida.json");
                break;

            case "Entretenimiento":
                myIcon= AppCompatResources.getDrawable(context, R.drawable.back_recom2);
                holder.layout_contenido.setBackground(myIcon);
                holder.animacion_icono.setAnimation("entretenimiento.json");
                break;
            case "Ejercicio":
                myIcon= AppCompatResources.getDrawable(context, R.drawable.back_recom3);
                holder.layout_contenido.setBackground(myIcon);
                holder.animacion_icono.setAnimation("ejercicio.json");
                break;
            case "Lectura":
                myIcon= AppCompatResources.getDrawable(context, R.drawable.back_recom4);
                holder.layout_contenido.setBackground(myIcon);
                holder.animacion_icono.setAnimation("lectura.json");
                break;
        }

        holder.url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempUrl=recomModel.getUrl();
                if (!tempUrl.startsWith("http://") && !tempUrl.startsWith("https://")){
                    tempUrl = "http://" + tempUrl;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tempUrl));
                context.startActivity(browserIntent);
            }
        });

        String tempUrl=recomModel.getUrl();
        SpannableString spanString = new SpannableString(tempUrl);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        holder.url.setText(spanString);


    }

    @Override
    public int getItemCount() {
        return mModelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titulo,contenido,fecha,tipo,url;
        RelativeLayout layout_contenido;
        LottieAnimationView animacion_icono;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            contenido=itemView.findViewById(R.id.contenido);
            fecha=itemView.findViewById(R.id.fecha);
            tipo=itemView.findViewById(R.id.tipo);
            url=itemView.findViewById(R.id.url);
            layout_contenido=itemView.findViewById(R.id.layout_contenido);
            animacion_icono=itemView.findViewById(R.id.animacion_icono);
        }
    }
}