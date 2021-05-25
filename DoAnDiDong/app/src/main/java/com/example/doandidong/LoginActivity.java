package com.example.doandidong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String mypreference = "PREFER_NAME";
    private String URL_LOGIN = "http://10.0.2.2:5000/api/login";
    private JsonArrayRequest ArrayRequest;
    private EditText edtEmail;
    private TextInputEditText edtPassword;
    private Button btnLogin;
    private Button buttonRegister;
    private ProgressBar loading;
    private String userID,name,phone,emailPre;
    private TextView forgetPassword;
    Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AnhXa();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=true;
                hideKeyboard(v);
                if(edtEmail.getText().toString().trim().isEmpty()||edtPassword.getText().toString().trim().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
                }else {
                    Login(edtEmail.getText().toString(),edtPassword.getText().toString());
                    btnLogin.setEnabled(false);
                }



            }
        });


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        forgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasword.class);
            startActivityForResult(intent, 102);
        });
    }

    public void AnhXa(){
        edtEmail=findViewById(R.id.inputEmail);
        edtPassword=findViewById(R.id.inputPassword);
        btnLogin=findViewById(R.id.buttonLogin);
        loading=findViewById(R.id.loading);
        buttonRegister = findViewById(R.id.buttonRegister);
        forgetPassword=findViewById(R.id.forgetPassword);
    }
    public void Login(final String email, final String password){
        loading.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("msg").equals("Đăng nhập thành công"))
                            {
                                JSONObject user = new JSONObject(jsonObject.getString("user"));
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                userID=user.getString("_id");
                                phone=user.getString("phone");
                                name=user.getString("name");
                                emailPre=user.getString("email");
                                intent.putExtra("userID",userID);
                                intent.putExtra("phone",phone);
                                intent.putExtra("name",name);
                                intent.putExtra("email",email);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                loading.setVisibility(View.GONE);
                                edtPassword.setText("");
                                Toast.makeText(LoginActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                btnLogin.setEnabled(true);
                            }
                        }
                        catch (JSONException e)
                        {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            btnLogin.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //gọi hàm lưu trạng thái ở đây
        savingPreferences();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //gọi hàm đọc trạng thái ở đây
        restoringPreferences();
    }

    public void savingPreferences()
    {
        //tạo đối tượng getSharedPreferences
        SharedPreferences pre=getSharedPreferences
                (mypreference, MODE_PRIVATE);
        //tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor=pre.edit();
        //lưu vào editor

        editor.putString("userID",userID);
        editor.putString("phone",phone);
        editor.putString("name",name);
        editor.putString("email",emailPre);
        editor.commit();
    }
    public void restoringPreferences()
    {
        SharedPreferences pre=getSharedPreferences
                (mypreference,MODE_PRIVATE);
        if(pre.getString("userID", "")!=""){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("userID",pre.getString("userID", ""));
            intent.putExtra("phone",pre.getString("phone", ""));
            intent.putExtra("name",pre.getString("name", ""));
            intent.putExtra("email",pre.getString("email", ""));
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 101){
            edtEmail.setText(data.getStringExtra("email"));
            edtPassword.setText(data.getStringExtra("password"));
        }
        if(requestCode == 102 && resultCode == 101){
            edtEmail.setText(data.getStringExtra("email"));
            edtPassword.setText(data.getStringExtra("password"));
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
}