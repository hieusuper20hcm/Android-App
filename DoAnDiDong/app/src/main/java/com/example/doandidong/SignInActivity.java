package com.example.doandidong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    EditText inputName,inputEmail,inputPhone;
    TextInputEditText edtPassword;
    Button btnSignUp;
    ProgressBar loading;
    String URL_SIGNIN = "http://10.0.2.2:5000/api/user/create";
    JsonArrayRequest ArrayRequest;
    boolean check=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inputName=findViewById(R.id.inputName);
        inputEmail=findViewById(R.id.inputEmail);
        inputPhone=findViewById(R.id.inputPhone);
        edtPassword=findViewById(R.id.inputPassword);
        loading=findViewById(R.id.loading);
        btnSignUp=findViewById(R.id.signIn);
        btnSignUp.setOnClickListener(v -> {
                checkInput();
                if(check) {
                    loading.setVisibility(View.VISIBLE);
                    SignIn();
                }

                check=true;
        });
        Back();
    }
    public void checkInput(){
        String phoneRegex = "^0(?=.+[0-9]).{9}$|^\\+84[1-9](?=.+[0-9]).{8}$";
        String nameRegex="\\b[A-Za-zÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝàáâãèéêìíòóôõùúýĂăĐđĨĩŨũƠơƯưẠ-ỹ ]+.{1}$";
        String emailRegex="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if(!inputName.getText().toString().trim().matches(nameRegex)){
            inputName.setError("Tên sai định dạng (Ít nhất 2 ký tự alphabet)");
            check=false;
        }
        if(!inputPhone.getText().toString().trim().matches(phoneRegex)){
            inputPhone.setError("Số điện thoại sai định dạng");
            check=false;
        }
        if(!inputEmail.getText().toString().trim().matches(emailRegex)){
            inputEmail.setError("Email sai định dạng");
            check=false;
        }
        if(edtPassword.getText().toString().trim().length()<6){
            edtPassword.setError("Password ít nhất 6 ký tự");
            edtPassword.setText("");
            check=false;
        }
    }
    public void SignIn(){
        loading.setVisibility(View.VISIBLE);
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_SIGNIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("msg").equals("Đăng ký thành công"))
                            {
                                Intent intent=new Intent(SignInActivity.this,LoginActivity.class);
                                intent.putExtra("email", inputEmail.getText().toString());
                                intent.putExtra("password", edtPassword.getText().toString());
                                setResult(101, intent);
                                Toast.makeText(SignInActivity.this,"Đăng ký thành công",Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else
                            {
                                loading.setVisibility(View.GONE);
                                edtPassword.setText("");
                                inputEmail.requestFocus();
                                inputEmail.setError(jsonObject.getString("msg"));
                            }
                        }
                        catch (JSONException e)
                        {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignInActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("name",inputName.getText().toString().trim());
                params.put("phone",inputPhone.getText().toString().trim());
                params.put("email",inputEmail.getText().toString().trim());
                params.put("password",edtPassword.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }
    public void Back() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}