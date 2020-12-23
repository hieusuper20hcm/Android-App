package com.example.doandidong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.RecyclerDataAdapter;
import model.Product;

public class TrouserActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private String URL_PRODUCT="http://10.0.2.2:5000/api/product";
    private List<Product> lstProduct = new ArrayList<>();
    private RecyclerView myrv ;
    private String userID,phone,name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trouser);
        myrv = findViewById(R.id.rvProduct);
        getData();
        Back();
        jsoncall();
    }
    public void getData(){
        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        phone=intent.getStringExtra("phone");
        name=intent.getStringExtra("name");
        email=intent.getStringExtra("email");
    }
    public void Back() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void jsoncall() {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest ArrayRequest = new JsonArrayRequest(Request.Method.GET,URL_PRODUCT,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0 ; i<response.length();i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if(jsonObject.getString("categories").equals("Quần")) {
                            Product products = new Product();
                            products.setId(jsonObject.getString("_id"));
                            products.setName(jsonObject.getString("name"));
                            products.setPrice(jsonObject.getInt("price"));
                            products.setImg("http://10.0.2.2:5000" + jsonObject.getString("img"));
                            products.setCategories(jsonObject.getString("categories"));
                            products.setDescription(jsonObject.getString("description"));
                            lstProduct.add(products);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                setRvadapter(lstProduct);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TrouserActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(ArrayRequest);
    }
    public void setRvadapter (List<Product> lst) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        RecyclerDataAdapter myAdapter = new RecyclerDataAdapter(lst,userID,this) ;
        myrv.setLayoutManager(gridLayoutManager);
        myrv.setAdapter(myAdapter);
    }
}