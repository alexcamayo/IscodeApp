package com.proyecto.iscodeapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.proyecto.iscodeapp.Funciones.MostrarPersonas;
import com.proyecto.iscodeapp.Models.Chat;
import com.proyecto.iscodeapp.R;
import com.proyecto.iscodeapp.RecordatoriosActivity;

import java.util.Calendar;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    String titulo="";String contenido="";String color="";
    String fecha="";String hora="";String importancia="";
    String serepite,tiemporep,intervalorep="";

    //Mensaje de registro FC
    String titulofc="";String registrofc="";String estadofc="";String fechafc="";

    //Mensaje prediccion
    String valor_pred,titulo_pred,fecha_pred="";

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    Activity activity;

    FirebaseUser fuser;

    public MessageAdapter(Activity activity,Context mContext, List<Chat> mChat, String imageurl) {
        this.activity=activity;
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat= mChat.get(position);
        String mensaje=chat.getMessage();
        String indicador=mensaje.substring(0,1);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if (chat.getSender().equals(fuser.getUid())){
            holder.profile_image.setVisibility(View.GONE);
            holder.botones_layout.setVisibility(View.GONE);
        }
        else{
            holder.profile_image.setVisibility(View.VISIBLE);
            holder.botones_layout.setVisibility(View.VISIBLE);
        }

        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.drawable.icon_user);
        }
        else{
            Uri foto=Uri.parse(imageurl);
            holder.profile_image.setImageURI(foto);
        }



        String horaMensaje=chat.getHora();
        holder.fecha_layout.setVisibility(View.GONE);

        if(indicador.equals("R")){
            holder.mensaje_layout.setVisibility(View.GONE);
            holder.recordatorio_layout.setVisibility(View.VISIBLE);
            holder.registrofc_layout.setVisibility(View.GONE);
            holder.prediccion_layout.setVisibility(View.GONE);

            titulo=mensaje.substring(1,mensaje.indexOf('^'));
            contenido=mensaje.substring(mensaje.indexOf('^')+1,mensaje.lastIndexOf('¬'));
            color=mensaje.substring(mensaje.lastIndexOf('¬')+1,mensaje.lastIndexOf('?'));
            fecha=mensaje.substring(mensaje.lastIndexOf('?')+1,mensaje.lastIndexOf('{'));
            hora=mensaje.substring(mensaje.lastIndexOf('{')+1,mensaje.lastIndexOf("}"));
            importancia=mensaje.substring(mensaje.lastIndexOf('}')+1,mensaje.length());

            Log.d("TAG901","Mensaje Adapter: " +titulo+'@'+contenido+'@'+color+'@'+fecha+'@'+hora+'@'+importancia);

            holder.titulo_record.setText(titulo);
            holder.t_fecha.setText(fecha);
            holder.t_hora.setText(hora);
            holder.horaR.setText(horaMensaje);

            if(importancia.equals("SI")){
                holder.importancia_record.setVisibility(View.VISIBLE);
            }
            else{
                holder.importancia_record.setVisibility(View.GONE);
            }

            switch (Integer.parseInt(color)){
                case 0:
                    holder.stick_note.getBackground().setTint(activity.getColor(R.color.Nota1));
                    break;
                case 1:
                    holder.stick_note.getBackground().setTint(activity.getColor(R.color.Nota2));
                    break;
                case 2:
                    holder.stick_note.getBackground().setTint(activity.getColor(R.color.Nota3));
                    break;
                case 3:
                    holder.stick_note.getBackground().setTint(activity.getColor(R.color.Nota4));
                    break;
            }

        }
        else if(indicador.equals("F")){
            holder.mensaje_layout.setVisibility(View.GONE);
            holder.recordatorio_layout.setVisibility(View.GONE);
            holder.registrofc_layout.setVisibility(View.VISIBLE);
            holder.prediccion_layout.setVisibility(View.GONE);

            registrofc=mensaje.substring(1,mensaje.indexOf('^'));
            titulofc=mensaje.substring(mensaje.indexOf('^')+1,mensaje.lastIndexOf('¬'));
            estadofc=mensaje.substring(mensaje.lastIndexOf('¬')+1,mensaje.lastIndexOf('?'));
            fechafc=mensaje.substring(mensaje.lastIndexOf('?')+1,mensaje.length());

            Log.d("TAG901","Mensaje RegistroFC: " +registrofc+'@'+titulofc+'@'+estadofc+'@'+fechafc);

            holder.fc_registro.setText(registrofc);
            holder.titulo_fcregistro.setText(titulofc);
            holder.estado_fcregistro.setText(estadofc);
            holder.t_fecharegistro.setText(fechafc);
            holder.horaF.setText(horaMensaje);


        }
        else if(indicador.equals("P")){
            holder.mensaje_layout.setVisibility(View.GONE);
            holder.recordatorio_layout.setVisibility(View.GONE);
            holder.registrofc_layout.setVisibility(View.GONE);
            holder.prediccion_layout.setVisibility(View.VISIBLE);

            valor_pred=mensaje.substring(1,mensaje.indexOf('^'));
            titulo_pred=mensaje.substring(mensaje.indexOf('^')+1,mensaje.lastIndexOf('¬'));
            fecha_pred=mensaje.substring(mensaje.lastIndexOf('¬')+1,mensaje.length());

            holder.valor_prediccion.setText(valor_pred);
            holder.titulo_prediccion.setText(titulo_pred);
            holder.fecha_prediccion.setText(fecha_pred);
            holder.horaP.setText(horaMensaje);

        }
        else if (indicador.equals("X")){
            holder.fecha_layout.setVisibility(View.VISIBLE);
            holder.mensaje_layout.setVisibility(View.GONE);
            holder.recordatorio_layout.setVisibility(View.GONE);
            holder.registrofc_layout.setVisibility(View.GONE);
            holder.prediccion_layout.setVisibility(View.GONE);
            holder.fechaMensajes.setText(chat.getFecha());
            holder.profile_image.setVisibility(View.GONE);
        }
        else if (indicador.equals("D")){
            holder.mensaje_layout.setVisibility(View.VISIBLE);
            holder.recordatorio_layout.setVisibility(View.GONE);
            holder.registrofc_layout.setVisibility(View.GONE);
            holder.prediccion_layout.setVisibility(View.GONE);
            //holder.show_message.setText("\uD83C\uDFC3 Se ha compartido una recomendación");

            SpannableString spanString=new SpannableString("");
            if (chat.getSender().equals(fuser.getUid())){
                spanString = new SpannableString("\uD83C\uDFC3 Has compartido una recomendación");
            }
            else{
                spanString = new SpannableString("\uD83C\uDFC3 Nueva recomendación. Ve a la sección Recomendaciones");
            }
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            holder.show_message.setText(spanString);
            holder.show_message.setTextSize(12);
            holder.horaM.setText(horaMensaje);
        }
        else{
            holder.mensaje_layout.setVisibility(View.VISIBLE);
            holder.recordatorio_layout.setVisibility(View.GONE);
            holder.registrofc_layout.setVisibility(View.GONE);
            holder.prediccion_layout.setVisibility(View.GONE);
            holder.show_message.setText(mensaje.substring(1,chat.getMessage().length()));
            holder.horaM.setText(horaMensaje);
        }

        holder.aceptar_rec_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Chat chat_rec= mChat.get(position);
                String mensaje=chat_rec.getMessage();

                titulo=mensaje.substring(1,mensaje.indexOf('^'));
                contenido=mensaje.substring(mensaje.indexOf('^')+1,mensaje.lastIndexOf('¬'));
                color=mensaje.substring(mensaje.lastIndexOf('¬')+1,mensaje.lastIndexOf('?'));
                fecha=mensaje.substring(mensaje.lastIndexOf('?')+1,mensaje.lastIndexOf('{'));
                hora=mensaje.substring(mensaje.lastIndexOf('{')+1,mensaje.lastIndexOf('}'));
                importancia=mensaje.substring(mensaje.lastIndexOf('}')+1,mensaje.lastIndexOf('~'));
                serepite=mensaje.substring(mensaje.lastIndexOf('~')+1,mensaje.lastIndexOf('$'));
                tiemporep=mensaje.substring(mensaje.lastIndexOf('$')+1,mensaje.lastIndexOf('&'));
                intervalorep=mensaje.substring(mensaje.lastIndexOf('&')+1,mensaje.length());

                Log.d("TAG901","Mensaje Button: " +titulo+'@'+contenido+'@'+color+'@'
                        +fecha+'@'+hora+'@'+importancia+'@'+serepite+'@'+tiemporep+'@'+intervalorep);

                Intent intent=new Intent(mContext , RecordatoriosActivity.class);
                intent.putExtra("ORIGEN","mensajes");
                intent.putExtra("NUEVOREC",mensaje);
                activity.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public RelativeLayout recordatorio_layout,botones_layout,mensaje_layout,registrofc_layout, prediccion_layout,fecha_layout;
        public ImageButton aceptar_rec_btn,rechazar_rec_btn;

        public TextView titulo_record,t_fecha,t_hora;
        public ImageView importancia_record;
        public CardView stick_note;

        public TextView titulo_fcregistro,estado_fcregistro,t_fecharegistro,fc_registro;

        public TextView titulo_prediccion,fecha_prediccion,valor_prediccion;

        public TextView fechaMensajes,horaM,horaR,horaF,horaP;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            mensaje_layout=itemView.findViewById(R.id.mensaje_layout);
            recordatorio_layout=itemView.findViewById(R.id.recordatorio_layout);
            registrofc_layout=itemView.findViewById(R.id.registrofc_layout);
            botones_layout=itemView.findViewById(R.id.botones_layout);
            aceptar_rec_btn=itemView.findViewById(R.id.aceptar_rec_btn);
            rechazar_rec_btn=itemView.findViewById(R.id.rechazar_rec_btn);

            botones_layout.setVisibility(View.GONE);
            profile_image.setVisibility(View.GONE);
            registrofc_layout.setVisibility(View.GONE);


            //Enviar o leer recordatorio
            titulo_record=itemView.findViewById(R.id.titulo_record);
            t_fecha=itemView.findViewById(R.id.t_fecha);
            t_hora=itemView.findViewById(R.id.t_hora);
            importancia_record = itemView.findViewById(R.id.importancia_record);
            stick_note=itemView.findViewById(R.id.stick_note);

            //Enviar o leer registro FC
            fc_registro=itemView.findViewById(R.id.fc_registro);
            titulo_fcregistro=itemView.findViewById(R.id.titulo_fcregistro);
            estado_fcregistro=itemView.findViewById(R.id.estado_fcregistro);
            t_fecharegistro=itemView.findViewById(R.id.t_fecharegistro);


            //Enviar o leer prediccion
            prediccion_layout=itemView.findViewById(R.id.prediccion_layout);
            valor_prediccion=itemView.findViewById(R.id.valor_prediccion);
            titulo_prediccion=itemView.findViewById(R.id.titulo_prediccion);
            fecha_prediccion=itemView.findViewById(R.id.fecha_prediccion);

            //Fecha
            fecha_layout=itemView.findViewById(R.id.fecha_layout);
            fechaMensajes=itemView.findViewById(R.id.fechaMensajes);
            horaM=itemView.findViewById(R.id.horaM);
            horaR=itemView.findViewById(R.id.horaR);
            horaF=itemView.findViewById(R.id.horaF);
            horaP=itemView.findViewById(R.id.horaP);







        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}