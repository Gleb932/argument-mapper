package com.example.argumentmapper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Inject APIService apiService;
    private EditText etLogin, etPassword;
    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((ArgumentMapperApplication)getApplication()).getApplicationComponent().inject(this);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
    }

    public void onButton(View v)
    {
        String username = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        if(v.getId() == R.id.btnLogin)
        {
            if(username.isEmpty() || password.isEmpty()) return;
            apiService.login(username, password).enqueue(new retrofit2.Callback<ResponseBody>()
            {
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (!response.isSuccessful()) {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            Log.v(TAG, jsonError.getString("error"));
                        }else {
                            JSONObject jsonToken = new JSONObject(response.body().string());
                            Log.v(TAG, jsonToken.getString("access_token"));
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else if(v.getId() == R.id.btnRegister) {
            Intent myIntent = new Intent(this, RegistrationActivity.class);
            startActivity(myIntent);
        }
    }
}