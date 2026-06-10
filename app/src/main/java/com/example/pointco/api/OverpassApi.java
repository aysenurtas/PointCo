package com.example.pointco.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OverpassApi {

    @Headers("Content-Type: text/plain")
    @POST("api/interpreter")
    Call<ResponseBody> getNearbyCafes(
            @Body RequestBody query
    );
}