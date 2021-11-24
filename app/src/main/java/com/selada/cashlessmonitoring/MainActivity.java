package com.selada.cashlessmonitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.$Gson$Preconditions;
import com.selada.cashlessmonitoring.adapter.HistoryAdapter;
import com.selada.cashlessmonitoring.network.Api;
import com.selada.cashlessmonitoring.network.Response.ApiResponse;
import com.selada.cashlessmonitoring.network.Response.Member;
import com.selada.cashlessmonitoring.network.Response.TransactionResponse;
import com.selada.cashlessmonitoring.util.Loading;
import com.selada.cashlessmonitoring.util.MethodUtil;
import com.selada.cashlessmonitoring.util.PreferenceManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private String member_id = PreferenceManager.getSessionId();
    private HistoryAdapter adapter;
    private Dialog dialog;

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvSaldo)
    TextView tvSaldo;
    @BindView(R.id.rvHistory)
    RecyclerView rvHistory;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @OnClick(R.id.btnLogout)
    void onClickLogout(){
        goToLoginPage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        new PreferenceManager(this);
        getHistoryTransaction();
        getDetailMember();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvHistory.setLayoutManager(layoutManager);

        swipeRefresh.setOnRefreshListener(() -> {
            getHistoryTransaction();
            getDetailMember();
        });
    }

    private void getHistoryTransaction(){
        swipeRefresh.setRefreshing(true);
        Api.apiInterface().getHistoryTransactions(PreferenceManager.getSessionId(), PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<TransactionResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TransactionResponse>>> call, Response<ApiResponse<List<TransactionResponse>>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    List<TransactionResponse> transactionResponses = Objects.requireNonNull(response.body()).getData();

                    if (transactionResponses.size()>0){
                        //skip
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                    adapter = new HistoryAdapter(transactionResponses, context);
                    rvHistory.setAdapter(adapter);
                    rvHistory.scheduleLayoutAnimation();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TransactionResponse>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    public void getDetailMember(){
        swipeRefresh.setRefreshing(true);
        Api.apiInterface().getMember(PreferenceManager.getSessionId(),  PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Member>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<Member>> call, Response<ApiResponse<Member>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    Member members = response.body().getData();
                    tvName.setText(members.getFullname());
                    String balance = String.valueOf(members.getBalance());
                    tvSaldo.setText("Rp " + MethodUtil.toCurrencyFormat(balance));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Member>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    public void goToLoginPage() {
        showDialog(R.layout.dialog_logout, context);
        Button button = dialog.findViewById(R.id.btnSubmit);
        button.setOnClickListener(view -> {
            PreferenceManager.logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showDialog(int layout, Context context) {
        dialog = new Dialog(Objects.requireNonNull(context));
        //set content
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}