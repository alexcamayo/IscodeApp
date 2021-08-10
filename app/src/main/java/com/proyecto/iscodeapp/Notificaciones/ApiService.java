package com.proyecto.iscodeapp.Notificaciones;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({"Authorization: key=AAAAOnl2yHQ:APA91bFo6KzPhcOCZh8jnsqahacf0jocBGCRlefIk90bp12RDbk7yuAmNh7XNHt0-3WMZVcT874PVhbTGzzMsNZdCrASVyv0TQ5CVnf3U2DrmYItAhPaLBiYQcsFYUhK85cvE-bNhRBI",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendChatNotification(@Body RequestNotificaton requestNotificaton);
}