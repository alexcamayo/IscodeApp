package com.proyecto.iscodeapp.Notificaciones;

import com.google.gson.annotations.SerializedName;

public class RequestNotificaton {

    @SerializedName("to") //  "to" changed to token
    private String to;

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
