package com.example.pointco.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface FoursquareApi {

    @GET("places/search")
    Call<Object> searchCafes(

            @Header("X-Places-Api-Version") String version,

            @Header("Authorization") String apiKey,

            @Query("ll") String latLng,

            @Query("query") String query,

            @Query("limit") int limit
    );
}