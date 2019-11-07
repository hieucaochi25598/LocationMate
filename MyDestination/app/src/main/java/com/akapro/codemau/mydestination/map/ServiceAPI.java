package com.akapro.codemau.mydestination.map;
import com.akapro.codemau.mydestination.map.direction.DirectionRoot;
import com.akapro.codemau.mydestination.map.geocoding.GeocodingRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceAPI {
    @GET("geocode/json?&key=AIzaSyD9EagKsWiKToCHCpLGsoEUJ1zEPDEp3Fs")
    Call<GeocodingRoot> getLocation(@Query("address") String address);

    @GET("directions/json?&key=AIzaSyD9EagKsWiKToCHCpLGsoEUJ1zEPDEp3Fs")
    Call<DirectionRoot> getDirection(@Query("origin") String origin,
                                     @Query("destination") String destination);
}
