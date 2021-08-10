package com.proyecto.iscodeapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyecto.iscodeapp.Funciones.MostrarPersonas;
import com.proyecto.iscodeapp.IngresarActivity;
import com.proyecto.iscodeapp.MessageActivity;
import com.proyecto.iscodeapp.Models.Chat;
import com.proyecto.iscodeapp.Models.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.List;

import com.proyecto.iscodeapp.R;
import com.proyecto.iscodeapp.ShowImageActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private String mOrigen;
    private String[] mDatos;
    String theLastMessage;
    String mensajeOrigen=""; //Si el yo lo envío, coloco un Yo:

    Dialog PersonasPerfil;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    Calendar calendar=Calendar.getInstance();



    public UserAdapter(Context mContext, List<User> mUsers, String mOrigen, String[] mDatos){
        this.mUsers=mUsers;
        this.mContext=mContext;
        this.mOrigen=mOrigen;
        this.mDatos=mDatos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageurl().equals("default")){
            holder.profile_image.setVisibility(View.VISIBLE);
            holder.profile_image.setImageResource(R.drawable.icon_user);
        }
        else  if(user.getImageurl().equals("waiting")){
            holder.profile_image.setVisibility(View.INVISIBLE);
        }
        else{
            //Glide.with(mContext).load(user.getImageurl()).into(holder.profile_image);
            Uri foto=Uri.parse(user.getImageurl());
            holder.profile_image.setVisibility(View.VISIBLE);
            holder.profile_image.setImageURI(foto);
        }

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOrigen.equals("chatsfragment") || mOrigen.equals("usersfragment")){
                    MostrarPersonas mostrarPersonas=new MostrarPersonas(mContext);
                    mostrarPersonas.mostrar(user.getId());

                }
            }
        });


        holder.cuerpo_useritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                intent.putExtra("nombre",user.getUsername());
                intent.putExtra("imageurl",user.getImageurl());

                if(mOrigen.equals("chatsfragment") || mOrigen.equals("usersfragment")){
                    intent.putExtra("ORIGEN","ListChatActivity");
                }

                if(mOrigen.equals("FCregistro") || mOrigen.equals("historicos")){
                    intent.putExtra("registrofc",mDatos[0]);
                    intent.putExtra("titulofc",mDatos[1]);
                    intent.putExtra("estadofc",mDatos[2]);
                    intent.putExtra("fechafc",mDatos[3]);
                }

                if(mOrigen.equals("historicos_pred")){
                    intent.putExtra("valorpred",mDatos[0]);
                    intent.putExtra("titulopred",mDatos[1]);
                    intent.putExtra("fechapred",mDatos[2]);
                }

                else if (mOrigen.equals("createnote") || mOrigen.equals("updatenote")){
                    intent.putExtra("TITULO",mDatos[0]);
                    intent.putExtra("CONTENIDO",mDatos[1]);
                    intent.putExtra("COLOR",mDatos[2]);
                    intent.putExtra("FECHA",mDatos[3]);
                    intent.putExtra("HORA",mDatos[4]);
                    intent.putExtra("IMPORTANCIA",mDatos[5]);
                }

                mContext.startActivity(intent);
            }
        });

        if (mOrigen.equals("chatsfragment")){
            lastMessage(user.getId(),holder.sub_info,holder.icono_mensaje,holder.fecha);
        }
        else{
            holder.sub_info.setText("Amigo");
            holder.fecha.setVisibility(View.GONE);
            holder.icono_mensaje.setImageResource(R.drawable.ic_iconusersimple);
        }
        Log.d("TAG905","Origen: "+mOrigen);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public TextView sub_info,fecha;
        public ImageView icono_mensaje;
        public RelativeLayout cuerpo_useritem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
            cuerpo_useritem=itemView.findViewById(R.id.cuerpo_useritem);
            sub_info=itemView.findViewById(R.id.sub_info);
            icono_mensaje=itemView.findViewById(R.id.icono_mensaje);
            fecha=itemView.findViewById(R.id.fecha);
        }
    }

    private void lastMessage(String userid,TextView last_msg, ImageView icono_mensaje,TextView fecha){
        theLastMessage="default";

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if((chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid))){
                        theLastMessage=chat.getMessage();
                        mensajeOrigen="";
                        fecha.setText(chat.getFecha()+" "+chat.getHora());
                    }

                    if(chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage=chat.getMessage();
                        mensajeOrigen="Yo: ";
                        fecha.setText(chat.getFecha()+" "+chat.getHora());
                    }
                }

                if (theLastMessage.equals("default")) {
                    last_msg.setText("No hay mensaje");
                } else {
                    if (theLastMessage.substring(0,1).equals("M")){
                        last_msg.setText(mensajeOrigen+theLastMessage.substring(1,theLastMessage.length()));
                        icono_mensaje.setImageResource(R.drawable.ic_mensaje);
                    }
                    else if(theLastMessage.substring(0,1).equals("R")) {
                        last_msg.setText(mensajeOrigen+"Recordatorio");
                        icono_mensaje.setImageResource(R.drawable.ic_recordatorio);
                    }
                    else if(theLastMessage.substring(0,1).equals("F")) {
                        last_msg.setText(mensajeOrigen+"Registro FC");
                        icono_mensaje.setImageResource(R.drawable.ic_registrofc);
                    }
                    else if(theLastMessage.substring(0,1).equals("P")){
                        last_msg.setText(mensajeOrigen+"Predicción");
                        icono_mensaje.setImageResource(R.drawable.icono_pred);
                    }
                    else if(theLastMessage.substring(0,1).equals("D")){
                        last_msg.setText(mensajeOrigen+"\uD83C\uDFC3 Recomendación");
                        icono_mensaje.setVisibility(View.GONE);

                    }

                }

                theLastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
