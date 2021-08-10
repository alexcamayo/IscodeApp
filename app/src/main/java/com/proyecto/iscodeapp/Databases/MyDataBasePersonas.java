package com.proyecto.iscodeapp.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDataBasePersonas extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="DATOS_PERSONAS.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="personas";


    //Columnas personas
    private static final String COLUMNA_ID="ID";
    private static final String FIREBASE_ID="FIREBASE_ID";
    private static final String NOMBRE="NOMBRE";
    private static final String CORREO="CORREO";
    private static final String FECHA="FECHA";
    private static final String URL="URL";




    public MyDataBasePersonas(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME +
                        "  (" + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FIREBASE_ID + " TEXT, " +
                        NOMBRE + " TEXT, "+
                        CORREO + " TEXT, "+
                        FECHA + " TEXT, "+
                        URL + " TEXT);";
        db.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long agregar_usuario(String firebaseid,String nombre,String correo,String fecha,String url){
        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(FIREBASE_ID,firebaseid);
        cv.put(NOMBRE,nombre);
        cv.put(CORREO,correo);
        cv.put(FECHA,fecha);
        cv.put(URL,url);

        result = db.insert(TABLE_NAME,null,cv);

        if(result==-1){
            Toast.makeText(context,"No se pudo crear personas",Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public Cursor readAllData(){
        String query="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=null;
        if(db != null){
            cursor= db.rawQuery(query,null);
        }
        return cursor;
    }

    public Cursor readOneUser(String user_id){
        String columna='"'+user_id+'"';

        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+FIREBASE_ID+"= " +columna;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if(db != null){
            cursor= db.rawQuery(query,null);
        }
        return cursor;
    }

    public void updateData(String row_id, String firebaseid, String nombre, String correo, String fecha, String url){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(FIREBASE_ID,firebaseid);
        cv.put(NOMBRE,nombre);
        cv.put(CORREO,correo);
        cv.put(FECHA,fecha);
        cv.put(URL,url);

        long result= db.update(TABLE_NAME,cv,"ID=?",new String[]{row_id});
        if(result==-1){
            //Toast.makeText(context,"Error al actualizar el recordatorio",Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context,"Se actualizó correctamente el recordatorio",Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteOneRow(String row_id){
        SQLiteDatabase db=this.getWritableDatabase();
        long result= db.delete(TABLE_NAME,"ID=?",new String[]{row_id});
        if(result==-1){

        }else{

        }
    }
    public void clearTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }

}
