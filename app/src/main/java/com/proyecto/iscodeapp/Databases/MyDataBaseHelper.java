package com.proyecto.iscodeapp.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="DATOS_USUARIO.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="recordatorios";
    private static final String TABLE_NAME_FC="registrosfc";
    private static final String TABLE_NAME_PRED="predicciones";


    //Columnas Recordatorios
    private static final String COLUMNA_ID="ID";
    private static final String TITULO_REC="TÍTULO";
    private static final String CONTENIDO_REC="CONTENIDO";
    private static final String COLOR_REC="COLOR";
    private static final String FECHA_REC="FECHA";
    private static final String HORA_REC="HORA";
    private static final String IMPORTANCIA_REC="IMPORTANCIA";
    private static final String SEREPITE_REC="SE_REPITE";
    private static final String TIEMPOREP_REC="TIEMPO_REPETICION";
    private static final String INTERVALOREP_REC="INTERVALO_REPETICION";


    //columnas Registros FC
    private static final String COLUMNA_ID_FC="ID";
    private static final String TITULO_FC="TÍTULO";
    private static final String VALOR_FC="VALOR";
    private static final String ESTADO_FC="ESTADO";
    private static final String FECHA_FC="FECHA";
    private static final String HORA_FC="HORA";

    //columnas Predicciones
    private static final String COLUMNA_ID_PRED="ID";
    private static final String TITULO_PRED="TÍTULO";
    private static final String VALOR_PRED="VALOR";
    private static final String FECHA_PRED="FECHA";
    private static final String HORA_PRED="HORA";
    private static final String MODELO_PRED="MODELO";
    private static final String ATR1="ATR1";
    private static final String ATR2="ATR2";
    private static final String ATR3="ATR3";
    private static final String ATR4="ATR4";
    private static final String ATR5="ATR5";
    private static final String ATR6="ATR6";
    private static final String ATR7="ATR7";
    private static final String ATR8="ATR8";
    private static final String ATR9="ATR9";
    private static final String ATR10="ATR10";
    private static final String ATR11="ATR11";
    private static final String ATR12="ATR12";
    private static final String ATR13="ATR13";





    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME +
                        "  (" + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TITULO_REC + " TEXT, " +
                        CONTENIDO_REC + " TEXT, "+
                        COLOR_REC + " TEXT, "+
                        FECHA_REC + " TEXT, "+
                        HORA_REC + " TEXT, "+
                        IMPORTANCIA_REC + " TEXT, "+
                        SEREPITE_REC + " TEXT, "+
                        TIEMPOREP_REC + " TEXT, "+
                        INTERVALOREP_REC + " TEXT);";
        db.execSQL(query);


        String query_fc=
                "CREATE TABLE " + TABLE_NAME_FC +
                        "  (" + COLUMNA_ID_FC + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TITULO_FC + " TEXT, " +
                        VALOR_FC + " TEXT, " +
                        ESTADO_FC + " TEXT, "+
                        FECHA_FC + " TEXT, "+
                        HORA_FC + " TEXT);";

        db.execSQL(query_fc);

        String query_pred=
                "CREATE TABLE " + TABLE_NAME_PRED +
                        "  (" + COLUMNA_ID_PRED + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TITULO_PRED + " TEXT, " +
                        VALOR_PRED + " TEXT, " +
                        FECHA_PRED + " TEXT, "+
                        HORA_PRED + " TEXT, "+
                        MODELO_PRED + " TEXT, "+
                        ATR1 + " TEXT, "+
                        ATR2 + " TEXT, "+
                        ATR3 + " TEXT, "+
                        ATR4 + " TEXT, "+
                        ATR5 + " TEXT, "+
                        ATR6 + " TEXT, "+
                        ATR7 + " TEXT, "+
                        ATR8 + " TEXT, "+
                        ATR9 + " TEXT, "+
                        ATR10 + " TEXT, "+
                        ATR11 + " TEXT, "+
                        ATR12 + " TEXT, "+
                        ATR13 + " TEXT);";


        db.execSQL(query_pred);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_FC);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_PRED);
        onCreate(db);
    }

    public long agregar_recordatorio(String titulo,String contenido,String color,
                                     String fecha,String hora,String importancia,
                                     String serepite,String tiemporep,String intervalorep){
        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TITULO_REC,titulo);
        cv.put(CONTENIDO_REC,contenido);
        cv.put(COLOR_REC,color);
        cv.put(FECHA_REC,fecha);
        cv.put(HORA_REC,hora);
        cv.put(IMPORTANCIA_REC,importancia);
        cv.put(SEREPITE_REC,serepite);
        cv.put(TIEMPOREP_REC,tiemporep);
        cv.put(INTERVALOREP_REC,intervalorep);

        result = db.insert(TABLE_NAME,null,cv);
        if(result==-1){
            Toast.makeText(context,"No se pudo guardar su recordatorio",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Recordatorio guardado con éxito: "+result,Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public long agregar_registrofc(String titulo, String valorFC, String estado,String fecha,String hora){
        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TITULO_FC,titulo);
        cv.put(VALOR_FC,valorFC);
        cv.put(ESTADO_FC,estado);
        cv.put(FECHA_FC,fecha);
        cv.put(HORA_FC,hora);

        result = db.insert(TABLE_NAME_FC,null,cv);
        if(result==-1){
            Toast.makeText(context,"No se pudo guardar su registro",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Registro guardado con éxito: "+result,Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public long agregar_prediccion(String titulo, String valorpred, String fecha,String hora, String modelo,
                                    String atr1, String atr2, String atr3, String atr4, String atr5,
                                   String atr6, String atr7,  String atr8, String atr9, String atr10,
                                   String atr11, String atr12, String atr13){
        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TITULO_PRED,titulo);
        cv.put(VALOR_PRED,valorpred);
        cv.put(FECHA_PRED,fecha);
        cv.put(HORA_PRED,hora);
        cv.put(MODELO_PRED,modelo);
        cv.put(ATR1,atr1);
        cv.put(ATR2,atr2);
        cv.put(ATR3,atr3);
        cv.put(ATR4,atr4);
        cv.put(ATR5,atr5);
        cv.put(ATR6,atr6);
        cv.put(ATR7,atr7);
        cv.put(ATR8,atr8);
        cv.put(ATR9,atr9);
        cv.put(ATR10,atr10);
        cv.put(ATR11,atr11);
        cv.put(ATR12,atr12);
        cv.put(ATR13,atr13);

        result = db.insert(TABLE_NAME_PRED,null,cv);
        if(result==-1){
            Toast.makeText(context,"No se pudo guardar su registro",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Registro guardado con éxito: "+result,Toast.LENGTH_SHORT).show();
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

    public Cursor readOneRow(String row_id){
        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMNA_ID+"= " +row_id;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if(db != null){
            cursor= db.rawQuery(query,null);
        }
        return cursor;
    }

    public Cursor readAllDataFC(){
        String query="SELECT * FROM "+TABLE_NAME_FC;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=null;
        if(db != null){
            cursor= db.rawQuery(query,null);
        }
        return cursor;
    }

    public Cursor readAllDataPRED(){
        String query="SELECT * FROM "+TABLE_NAME_PRED;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=null;
        if(db != null){
            cursor= db.rawQuery(query,null);
        }
        return cursor;
    }

    public void updateData(String row_id, String titulo, String contenido, String color,
                           String fecha, String hora, String importancia,
                           String serepite,String tiemporep,String intervalorep){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(TITULO_REC,titulo);
        cv.put(CONTENIDO_REC,contenido);
        cv.put(COLOR_REC,color);
        cv.put(FECHA_REC,fecha);
        cv.put(HORA_REC,hora);
        cv.put(IMPORTANCIA_REC,importancia);
        cv.put(SEREPITE_REC,serepite);
        cv.put(TIEMPOREP_REC,tiemporep);
        cv.put(INTERVALOREP_REC,intervalorep);

        long result= db.update(TABLE_NAME,cv,"ID=?",new String[]{row_id});
        if(result==-1){
            Toast.makeText(context,"Error al actualizar el recordatorio",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Se actualizó correctamente el recordatorio",Toast.LENGTH_SHORT).show();
        }

    }

    public void updateDataFC(String row_id, String titulo, String valorFC, String estado,String fecha,String hora){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(TITULO_FC,titulo);
        cv.put(VALOR_FC,valorFC);
        cv.put(ESTADO_FC,estado);
        cv.put(FECHA_FC,fecha);
        cv.put(HORA_FC,hora);

        long result= db.update(TABLE_NAME_FC,cv,"ID=?",new String[]{row_id});
        if(result==-1){
            Toast.makeText(context,"Error al actualizar el registro",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Se actualizó correctamente el registro",Toast.LENGTH_SHORT).show();
        }

    }

    public void updateDataPRED(String row_id, String titulo, String valorpred,String fecha,String hora,String modelo,
                               String atr1, String atr2, String atr3, String atr4, String atr5,
                               String atr6, String atr7,  String atr8, String atr9, String atr10,
                               String atr11, String atr12, String atr13){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(TITULO_PRED,titulo);
        cv.put(VALOR_PRED,valorpred);
        cv.put(FECHA_PRED,fecha);
        cv.put(HORA_PRED,hora);
        cv.put(MODELO_PRED,modelo);
        cv.put(ATR1,atr1);
        cv.put(ATR2,atr2);
        cv.put(ATR3,atr3);
        cv.put(ATR4,atr4);
        cv.put(ATR5,atr5);
        cv.put(ATR6,atr6);
        cv.put(ATR7,atr7);
        cv.put(ATR8,atr8);
        cv.put(ATR9,atr9);
        cv.put(ATR10,atr10);
        cv.put(ATR11,atr11);
        cv.put(ATR12,atr12);
        cv.put(ATR13,atr13);

        long result= db.update(TABLE_NAME_PRED,cv,"ID=?",new String[]{row_id});
        if(result==-1){
            Toast.makeText(context,"Error al actualizar el registro",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Se actualizó correctamente el registro",Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteOneRow(String row_id){
        SQLiteDatabase db=this.getWritableDatabase();
        long result= db.delete(TABLE_NAME,"ID=?",new String[]{row_id});
        if(result==-1){
            Toast.makeText(context,"Error al borrar el recordatorio",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Se eliminó correctamente el recordatorio",Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOneRowFC(String row_id){
        SQLiteDatabase db=this.getWritableDatabase();
        long result= db.delete(TABLE_NAME_FC,"ID=?",new String[]{row_id});
        if(result==-1){
            Toast.makeText(context,"Error al borrar el registro",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Se eliminó correctamente el registro",Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOneRowPRED(String row_id){
        SQLiteDatabase db=this.getWritableDatabase();
        long result= db.delete(TABLE_NAME_PRED,"ID=?",new String[]{row_id});
        if(result==-1){
            Toast.makeText(context,"Error al borrar el registro",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Se eliminó correctamente el registro",Toast.LENGTH_SHORT).show();
        }
    }

}
