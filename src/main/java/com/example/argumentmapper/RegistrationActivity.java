package com.example.argumentmapper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.argumentmapper.exceptions.AuthException;
import com.example.argumentmapper.exceptions.ConnectionException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class RegistrationActivity extends AppCompatActivity {

    @Inject APIService apiService;
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
                public void onFailure(@NotNull retrofit2.Call<ResponseBody> call, @NotNull Throwable t) {
                    if(t instanceof ConnectionException || t instanceof AuthException)
                    {
                        Toast.makeText(RegistrationActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onResponse(@NotNull retrofit2.Call<ResponseBody> call, @NotNull retrofit2.Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            RegistrationActivity.this.finish();
                        }else {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            Log.v(TAG, jsonError.getString("message"));
                        }
                    } catch (IOException | JSONException e) {
                        Toast.makeText(RegistrationActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        Log.v(TAG, e.getMessage());
                    }
                }
            });
        }
    }
}