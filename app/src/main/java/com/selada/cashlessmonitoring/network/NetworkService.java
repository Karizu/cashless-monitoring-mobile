package com.selada.cashlessmonitoring.network;

import com.selada.cashlessmonitoring.network.Response.ApiResponse;
import com.selada.cashlessmonitoring.network.Response.LoginResponse;
import com.selada.cashlessmonitoring.network.Response.Member;
import com.selada.cashlessmonitoring.network.Response.TransactionMember.TransactionMember;
import com.selada.cashlessmonitoring.network.Response.TransactionResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {
    String BASE_NEW_URL_LOCAL = "http://36.94.58.178/api/pos/public/index.php/api/";
    String SELADA_SEC_URL = "http://36.94.58.178/api/core/public/index.php/api/";
    String BASE_ARDI = "http://36.94.58.181/api/ardi-api/public/index.php/api/";
    String BASE_MIRETA = "http://36.94.58.181/api/mireta-pos/public/index.php/api/";

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> loginArdi(@Body RequestBody requestBody);

    @GET("transactions")
    Call<ApiResponse<List<TransactionResponse>>> getHistoryTransactions(@Query("member_id") String member_id,
                                                                        @Header("Authorization") String token);

    @GET("members/{member_id}")
    Call<ApiResponse<Member>> getMember(@Path("member_id") String member_id,
                                        @Header("Authorization") String token);

    @GET("va/inquiry")
    Call<ApiResponse<Member>> getMemberVa(@Query("va_no") String vaNo);

    @GET("members/transactions/{trx_code}")
    Call<ApiResponse<TransactionMember>> getTransactionDetail(@Path("trx_code") String trx_code);
}
