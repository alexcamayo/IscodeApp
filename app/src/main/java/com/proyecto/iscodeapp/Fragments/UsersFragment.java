package com.proyecto.iscodeapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.proyecto.iscodeapp.Adapters.UserAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.Models.User;

import java.util.ArrayList;
import java.util.List;

import com.proyecto.iscodeapp.R;

public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    SwipeRefreshLayout refrescar_personas;
    String [] datos=new String[4];

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    //Leer los datos
    SharedPreferences preferences_personas;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        refrescar_personas=view.findViewById(R.id.refrescar_personas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mUsers=new ArrayList<>();
        preferences_personas = this.requireActivity().getSharedPreferences("PERSONAS_DATOS",Context.MODE_PRIVATE);
        preferences =  this.requireActivity().getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);

        readUsers();

        refrescar_personas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readUsers();
                refrescar_personas.setRefreshing(false);
            }
        });

        return view;


    }

    private void readUsers() {
        //int cantidadusuarios=Integer.parseInt(preferences.getString("CantidadUsuarios","0"));
        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(getContext());
        mUsers.clear();

        Cursor cursor=myDataBasePersonas.readAllData();

        if(cursor.getCount()==0){
            mUsers.clear();
        }
        else{
            while (cursor.moveToNext()){
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
            }
        }

        userAdapter=new UserAdapter(getContext(),mUsers,"usersfragment",datos);
        recyclerView.setAdapter(userAdapter);
    }


}