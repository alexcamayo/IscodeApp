package com.proyecto.iscodeapp.Models;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String fecha;
    private String hora;

    public Chat(String sender, String receiver, String message, String fecha, String hora) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.fecha=fecha;
        this.hora=hora;
    }

    public Chat() {
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}