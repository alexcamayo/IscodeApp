package com.proyecto.iscodeapp.Models;

public class RecomModel {
    public String titulo;
    public String contenido;
    public String tipo;
    public String url;
    public String fecha;

    public RecomModel(String titulo, String contenido, String tipo, String url, String fecha) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.tipo = tipo;
        this.url = url;
        this.fecha = fecha;
    }

    public RecomModel(){
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}