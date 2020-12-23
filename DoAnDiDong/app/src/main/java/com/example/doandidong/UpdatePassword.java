package com.example.doandidong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import model.DetailProduct;
import model.History;

public class UpdatePassword extends AppCompatActivity {
    String URL_UPDATE="http://10.0.2.2:5000/api/user/update";
    TextInputEditText edtPassword,edtConfirm;
    Button btnOK;
    String email;
    boolean check=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        Intent intent=getIntent();
        email=intent.getStringExtra("email");

        AnhXa();
        Back();

        btnOK.setOnClickListener(v -> {
            checkInput();
            if(check) {
                btnOK.setEnabled(false);
                ChangePassword();
            }
            check=true;
        });
    }
    
    public void AnhXa(){
        edtPassword=findViewById(R.id.inputPassword);
        edtConfirm=findViewById(R.id.RePassword);
        btnOK=findViewById(R.id.btnOK);
    }

    public void Back() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void checkInput(){
        if(edtPassword.getText().toString().trim().length()<6){
            edtPassword.setError("Password ít nhất 6 ký tự");
            check=false;
        }
        if(!edtPassword.getText().toString().equals(edtConfirm.getText().toString())){
            edtConfirm.setError("Xác nhận password không trùng khớp");
            check=false;
        }
        if(!check){
            edtPassword.setText("");
            edtConfirm.setText("");
        }
    }

    public void ChangePassword(){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("msg").equals("Thay đổi mật khẩu thành công")){
                                Intent intent=new Intent(UpdatePassword.this,LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(UpdatePassword.this,jsonObject.getString("msg") , Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(UpdatePassword.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                btnOK.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnOK.setEnabled(true);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdatePassword.this,"Lỗi",Toast.LENGTH_LONG).show();
                        btnOK.setEnabled(true);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("password", edtPassword.getText().toString());
                params.put("email", email);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void hideKeyboard(View view) {
        edtPassword.clearFocus();
        edtConfirm.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}