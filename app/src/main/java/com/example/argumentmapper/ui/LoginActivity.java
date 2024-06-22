package com.example.argumentmapper.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.argumentmapper.APIService;
import com.example.argumentmapper.ArgumentMapperApplication;
import com.example.argumentmapper.R;
import com.example.argumentmapper.exceptions.AuthException;
import com.example.argumentmapper.exceptions.ConnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Inject
    APIService apiService;
    @Inject SharedPreferences sharedPreferences;
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
                    if(t instanceof ConnectionException || t instanceof AuthException)
                    {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            String jsonToken = new JSONObject(response.body().string()).getString("access_token");
                            String[] chunks = jsonToken.split("\\.");
                            byte[] bytes = android.util.Base64.decode(chunks[1], Base64.URL_SAFE);
                            String payload = new String(bytes, StandardCharsets.UTF_8);
                            JSONObject jsonPayload = new JSONObject(payload);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("access_token", jsonToken);
                            editor.putString("user_ID", jsonPayload.getString("userID"));
                            editor.putString("username", jsonPayload.getString("username"));
                            editor.putString("role", jsonPayload.getString("role"));
                            editor.apply();
                            LoginActivity.this.finish();
                        }else {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            Toast.makeText(LoginActivity.this, jsonError.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | JSONException e) {
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        Log.v(TAG, e.getMessage());
                    }
                }
            });
        }else if(v.getId() == R.id.btnRegister) {
            Intent myIntent = new Intent(this, RegistrationActivity.class);
            startActivity(myIntent);
        }
    }
}