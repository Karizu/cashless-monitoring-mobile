package com.selada.cashlessmonitoring.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiMireta {
    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .client(NetworkManager.client())
                .baseUrl(NetworkService.BASE_MIRETA)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static NetworkService apiInterface() {
        return builder(NetworkService.class);
    }
}