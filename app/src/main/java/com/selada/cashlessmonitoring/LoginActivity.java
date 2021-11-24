package com.selada.cashlessmonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.selada.cashlessmonitoring.network.Api;
import com.selada.cashlessmonitoring.network.Response.ApiResponse;
import com.selada.cashlessmonitoring.network.Response.LoginResponse;
import com.selada.cashlessmonitoring.network.Response.Member;
import com.selada.cashlessmonitoring.util.Loading;
import com.selada.cashlessmonitoring.util.MethodUtil;
import com.selada.cashlessmonitoring.util.PreferenceManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.input_username)
    EditText etUsername;
    @BindView(R.id.input_password)
    EditText etPassword;

    @OnClick(R.id.login_btn)
    void onClickLoginBtn(){
        if (etUsername.getText().toString().equals("") || etPassword.getText().toString().equals("")){
            Toast.makeText(context, "Silahkan isi username dan password anda", Toast.LENGTH_SHORT).show();
        } else {
            getMemberVa();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;

    }

    private void getMemberVa(){
        Loading.show(context);
        Api.apiInterface().getMemberVa(etUsername.getText().toString()).enqueue(new Callback<ApiResponse<Member>>() {
            @Override
            public void onResponse(Call<ApiResponse<Member>> call, Response<ApiResponse<Member>> response) {
                try {
                    Member member = Objects.requireNonNull(response.body()).getData();
                    loginArdi(member.getId(), member.getCreatedAt());
                } catch (Exception e){
                    Loading.hide(context);
                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Member>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void loginArdi(String member_id, String created_at){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", etUsername.getText().toString())
                .addFormDataPart("password", MethodUtil.doActivatePin(etPassword.getText().toString(), member_id, created_at))
                .build();

        Api.apiInterface().loginArdi(requestBody).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                Loading.hide(context);
                if (response.isSuccessful()){
                    PreferenceManager.setSessionId(member_id);
                    PreferenceManager.setSessionTokenArdi("Bearer "+Objects.requireNonNull(response.body()).getAccess_token());
                    PreferenceManager.setUserIdArdi(response.body().getData().getId());
                    PreferenceManager.saveLogIn(response.body().getAccess_token(), response.body().getData().getId(), response.body().getData().getFullname(), response.body().getData().getUsername(), response.body().getData().getUser_last_login());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Log.d("TAG", "MASUK LOGIN ARDI");
                } else {
                    try {
                        Toast.makeText(context, MethodUtil.getResponseError(Objects.requireNonNull(response.errorBody()).string()), Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }
}