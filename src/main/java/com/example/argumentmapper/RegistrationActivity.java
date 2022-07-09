package com.example.argumentmapper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;
import okhttp3.ResponseBody;

public class RegistrationActivity extends AppCompatActivity {

    @Inject
    APIService apiService;
    private EditText etEmail, etLogin, etPassword;
    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ((ArgumentMapperApplication)getApplication()).getApplicationComponent().inject(this);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
    }

    public void onButton(View v)
    {
        String username = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        if(v.getId() == R.id.btnRegister) {
            if(email.isEmpty() || username.isEmpty() || password.isEmpty()) return;
            apiService.register(email, username, password).enqueue(new retrofit2.Callback<ResponseBody>()
            {
                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    try {
                        if (!response.isSuccessful()) {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            Log.v(TAG, jsonError.getString("error"));
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}