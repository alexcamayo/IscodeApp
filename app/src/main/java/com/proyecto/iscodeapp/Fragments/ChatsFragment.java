package com.proyecto.iscodeapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyecto.iscodeapp.Adapters.UserAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.Models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.proyecto.iscodeapp.R;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String id_usuario="";
    int cantidadusuarios=0;
    List<String> list = new ArrayList<>();
    String[] datos=new String[4];
    FirebaseUser fuser;

    //Leer los datos
    SharedPreferences preferences_personas;
    SharedPreferences preferences;

    RelativeLayout layout_default;

    int chatNumber=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        layout_default=view.findViewById(R.id.layout_default);
        layout_default.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers=new ArrayList<>();
        preferences_personas = this.requireActivity().getSharedPreferences("PERSONAS_DATOS", Context.MODE_PRIVATE);
        preferences =  this.requireActivity().getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);
        cantidadusuarios=Integer.parseInt(preferences.getString("CantidadUsuarios","0"));

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();
        id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mUsers.clear();
        list.clear();
        database.child("Usuarios").child(id_usuario).child("InfoChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot:datasnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());
                }
                readUsersFirebase();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void readUsersFirebase() {
        chatNumber=0;

        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(getContext());
        mUsers.clear();

        Cursor cursor=myDataBasePersonas.readAllData();

        if(cursor.getCount()==0){
            mUsers.clear();
        }
        else{
            while (cursor.moveToNext()){
                for (int i=0;i<list.size();i++){
                    if(list.get(i).equals(cursor.getString(1))){
                        String personasid=cursor.getString(1);
                        String nombre=cursor.getString(2);
                        String correo=cursor.getString(3);
                        String fecha=cursor.getString(4);
                        String imageurl=cursor.getString(5);

                        User user=new User();
                        user.setId(personasid);
                        user.setUsername(nombre);
                        user.setCorreo(correo);
                        user.setFecha(fecha);
                        user.setImageurl(imageurl);
                        mUsers.add(user);
                        chatNumber=chatNumber+1;
                    }
                }
            }
        }

        userAdapter=new UserAdapter(getContext(),mUsers,"chatsfragment",datos);
        recyclerView.setAdapter(userAdapter);
        list.clear();

        if (chatNumber==0){
            layout_default.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}