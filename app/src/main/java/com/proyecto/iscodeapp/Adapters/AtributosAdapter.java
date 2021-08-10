package com.proyecto.iscodeapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.iscodeapp.Models.Prediccion;
import com.proyecto.iscodeapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AtributosAdapter extends RecyclerView.Adapter<AtributosAdapter.ViewHolder> {

    private Context mContext;
    private List<Prediccion> mPrediccion;
    Activity activity;

    public AtributosAdapter(Activity activity, Context mContext, List<Prediccion> mPrediccion) {
        this.activity=activity;
        this.mContext = mContext;
        this.mPrediccion=mPrediccion;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.atributo_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Prediccion prediccion= mPrediccion.get(position);
        Log.d("tag948","Valor: "+prediccion.getAtributo());
        holder.atributo.setText(prediccion.getAtributo());
        holder.valor_atributo.setText(prediccion.getValor_atributo());
        holder.icono_atributo.setImageResource(prediccion.getIcono());


    }

    @Override
    public int getItemCount() {
        return mPrediccion.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView icono_atributo;
        public TextView atributo,valor_atributo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icono_atributo = itemView.findViewById(R.id.icono_atributo);
            atributo=itemView.findViewById(R.id.atributo);
            valor_atributo=itemView.findViewById(R.id.valor_atributo);

            valor_atributo.setText("HOLA");
        }
    }
}