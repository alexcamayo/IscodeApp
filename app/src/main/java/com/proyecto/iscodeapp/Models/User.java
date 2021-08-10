package com.proyecto.iscodeapp.Models;

public class User {
    private String id;
    private String username;
    private String imageurl;
    private String correo;
    private String fecha;

    public User(String id, String username, String correo,String fecha, String imageurl) {
        this.id = id;
        this.correo=correo;
        this.fecha=fecha;
        this.username = username;
        this.imageurl = imageurl;
    }

    public User() {
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
