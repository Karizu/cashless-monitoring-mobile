package com.selada.cashlessmonitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.selada.cashlessmonitoring.adapter.DetailTransactionAdapter;
import com.selada.cashlessmonitoring.network.Api;
import com.selada.cashlessmonitoring.network.ApiMireta;
import com.selada.cashlessmonitoring.network.Response.ApiResponse;
import com.selada.cashlessmonitoring.network.Response.TransactionMember.TransactionMember;
import com.selada.cashlessmonitoring.network.Response.TransactionResponse;
import com.selada.cashlessmonitoring.util.Loading;
import com.selada.cashlessmonitoring.util.MethodUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTransactionActivity extends AppCompatActivity {

    private Context context;
    private String trxCode;

    @BindView(R.id.tvDateTime)
    TextView tvDateTime;
    @BindView(R.id.tvMerchantName)
    TextView tvMerchantName;
    @BindView(R.id.tvMerchantAddress)
    TextView tvMerchantAddress;
    @BindView(R.id.tvTransactionCode)
    TextView tvTransactionCode;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @OnClick(R.id.btnBack)
    void onClickbtnBack(){
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);
        ButterKnife.bind(this);
        context = this;

        if (getIntent() != null){
            trxCode = getIntent().getStringExtra("order_no");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        getDetailTransaction();
    }

    private void getDetailTransaction() {
        Loading.show(context);
        ApiMireta.apiInterface().getTransactionDetail(trxCode).enqueue(new Callback<ApiResponse<TransactionMember>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<TransactionMember>> call, Response<ApiResponse<TransactionMember>> response) {
                Loading.hide(context);
                try {
                    TransactionMember transactionResponse = response.body().getData();
                    Date d = null;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        d = sdf.parse(transactionResponse.getCreatedAt());
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }

                    sdf.applyPattern("dd MMM yyyy");
                    String orderDate = sdf.format(d);
                    sdf.applyPattern("HH:mm");
                    String orderTime = sdf.format(d);

                    tvDateTime.setText(orderDate+", "+orderTime);
                    tvMerchantName.setText(transactionResponse.getLocation().getName());
                    tvMerchantAddress.setText(transactionResponse.getLocation().getAddress());
                    tvTransactionCode.setText(transactionResponse.getTransactionCode());
                    tvTotal.setText("Rp "+MethodUtil.toCurrencyFormat(transactionResponse.getTotalPrice()));

                    DetailTransactionAdapter adapter = new DetailTransactionAdapter(transactionResponse.getDetails(), context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scheduleLayoutAnimation();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionMember>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }
}