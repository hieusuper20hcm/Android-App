package com.example.doandidong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgetPasword extends AppCompatActivity {
    Button sendOTP, btnOK;
    EditText inputEmail, inputOTP;
    String URL_FORGOT="http://10.0.2.2:5000/api/forgetPassword";
    ProgressBar loading;
    TextView changeEmail;
    int randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pasword);
        AnhXa();
        Back();

        sendOTP.setOnClickListener(v -> {
            Random rand = new Random();
            randomNumber = rand.nextInt(9999-1000) + 1000;
            sendOTP.setEnabled(false);
            Forget();
            loading.setVisibility(View.VISIBLE);
        });

        btnOK.setOnClickListener(v->{
            if(randomNumber==Integer.valueOf(inputOTP.getText().toString())){
                Intent intent=new Intent(ForgetPasword.this,UpdatePassword.class);
                intent.putExtra("email",inputEmail.getText().toString());
                startActivityForResult(intent, 102);
            }else {
                Toast.makeText(ForgetPasword.this, "Sai mã OTP", Toast.LENGTH_LONG).show();
            }
        });

        changeEmail.setOnClickListener(v -> {
            inputEmail.setVisibility(View.VISIBLE);
            inputOTP.setVisibility(View.GONE);
            btnOK.setVisibility(View.GONE);
            changeEmail.setVisibility(View.GONE);
            sendOTP.setText("Gửi mã OTP");
        });
    }

    public void AnhXa(){
        sendOTP=findViewById(R.id.btnOTP);
        btnOK=findViewById(R.id.btnOK);
        inputEmail=findViewById(R.id.inputEmail);
        inputOTP=findViewById(R.id.inputOTP);
        loading=findViewById(R.id.loading);
        changeEmail=findViewById(R.id.changeEmail);
    }

    public void Back() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void Forget(){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_FORGOT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("msg").equals("Vui lòng kiểm tra mail để lấy mã OTP"))
                            {
                                changeEmail.setVisibility(View.VISIBLE);
                                inputEmail.setVisibility(View.GONE);
                                inputOTP.setVisibility(View.VISIBLE);
                                btnOK.setVisibility(View.VISIBLE);
                                sendOTP.setText("Gửi lại OTP");
                                Toast.makeText(ForgetPasword.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                sendOTP.setEnabled(true);
                                loading.setVisibility(View.GONE);

                            }
                            else {
                                Toast.makeText(ForgetPasword.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                sendOTP.setEnabled(true);
                                loading.setVisibility(View.GONE);
                            }

                        }
                        catch (JSONException e)
                        {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            sendOTP.setEnabled(true);
                            loading.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgetPasword.this,"Lỗi",Toast.LENGTH_LONG).show();
                        sendOTP.setEnabled(true);
                        loading.setVisibility(View.GONE);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("email",inputEmail.getText().toString());
                params.put("OTP", String.valueOf(randomNumber));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102 && resultCode == 101){
            Intent intent=new Intent(ForgetPasword.this,LoginActivity.class);
            intent.putExtra("email", data.getStringExtra("email"));
            intent.putExtra("password", data.getStringExtra("password"));
            setResult(101, intent);
            finish();
        }

    }


    public void hideKeyboard(View view) {
        inputEmail.clearFocus();
        inputOTP.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}