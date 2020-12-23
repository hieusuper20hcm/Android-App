package com.example.doandidong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adapter.DetailHistoryDataAdapter;
import model.DetailProduct;
import model.History;

public class HistoryDetail extends AppCompatActivity {
    TextView tv_create,tv_madh,tv_phone,tv_address,tv_count,tv_price;
    RecyclerView myrv;
    List<DetailProduct> details=new ArrayList<>();
    private String userID,name,phone,email;
    Button btnPre;
    private String URL_PREORDER="http://10.0.2.2:5000/api/history/preOrder";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        AnhXa();
        getData();
        Back();
        DetailHistoryDataAdapter myAdapter = new DetailHistoryDataAdapter(details,this) ;
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);
    }
    public void Back(){
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnPre.setOnClickListener(v -> {
            preOrder(tv_madh.getText().toString());
        });
    }
    public void getData(){
        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        phone=intent.getStringExtra("phone");
        name=intent.getStringExtra("name");
        email=intent.getStringExtra("email");
        final History history=(History) intent.getSerializableExtra("detail");
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        tv_create.setText(history.getCreateDate());
        tv_madh.setText(history.getId());
        tv_phone.setText(history.getPhone());
        tv_address.setText(history.getAddress());
        tv_price.setText(currencyFormatter.format(history.getTotalMoney()));
        tv_count.setText(history.getTotalNumber().toString());
        for (int i=0;i<history.getDetailProductList().size();i++){
            DetailProduct detailProduct= (DetailProduct) history.getDetailProductList().get(i);
            details.add(detailProduct);
        }
    }
    public void AnhXa(){
        tv_create=findViewById(R.id.tv_create);
        tv_madh=findViewById(R.id.tv_madh);
        tv_phone=findViewById(R.id.tv_phone);
        tv_address=findViewById(R.id.tv_address);
        tv_count=findViewById(R.id.tv_number);
        tv_price=findViewById(R.id.tv_money);
        myrv=findViewById(R.id.rvDetail);
        btnPre=findViewById(R.id.btnDatHang);

    }
    public void preOrder(String Id){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_PREORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent=new Intent(HistoryDetail.this,CartActivity.class);
                        intent.putExtra("userID",userID);
                        intent.putExtra("phone",phone);
                        intent.putExtra("name",name);
                        intent.putExtra("email",email);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HistoryDetail.this,"Lỗi",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id",Id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}