package com.proyecto.iscodeapp.Models;

public class Prediccion {
    String atributo;
    String valor_atributo;
    int icono;

    public Prediccion(String atributo, String valor_atributo, int icono) {
        this.atributo = atributo;
        this.valor_atributo = valor_atributo;
        this.icono = icono;
    }
    public Prediccion() {
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getValor_atributo() {
        return valor_atributo;
    }

    public void setValor_atributo(String valor_atributo) {
        this.valor_atributo = valor_atributo;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }
}
