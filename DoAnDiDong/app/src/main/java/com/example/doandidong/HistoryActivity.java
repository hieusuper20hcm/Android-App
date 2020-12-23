package com.example.doandidong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.HistoryDaTaAdapter;
import model.DetailProduct;
import model.History;

public class HistoryActivity extends AppCompatActivity {
    private List<History> histories = new ArrayList<>();
    private RecyclerView myrv ;
    private TextView note;
    private String userID,name,phone,email;
    private String url = "http://10.0.2.2:5000/api/history";
    private String URL_PREORDER="http://10.0.2.2:5000/api/history/preOrder";
    private HistoryDaTaAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        myrv=findViewById(R.id.rv_history);
        note=findViewById(R.id.note);
        getData();
        Back();
        jsoncall();
    }
    public void Back(){
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void getData(){
        Intent intent=getIntent();
        userID=intent.getStringExtra("userID");
        phone=intent.getStringExtra("phone");
        name=intent.getStringExtra("name");
        email=intent.getStringExtra("email");
    }

    public void jsoncall() {
        RequestQueue requestQueue = Volley.newRequestQueue(HistoryActivity.this);

        JsonArrayRequest ArrayRequest = new JsonArrayRequest(Request.Method.GET,url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()<=0){
                    note.setVisibility(TextView.VISIBLE);
                }
                else {
                    boolean flag=true;
                    for (int i = 0 ; i<response.length();i++) {
                        try {
                            List<DetailProduct> details=new ArrayList<>();
                            History history = new History();
                            JSONObject jsonObject = response.getJSONObject(i);
                            if(jsonObject.getString("userID").equals(userID)) {
                                flag=false;
                                JSONArray jsonArray = (JSONArray)jsonObject.getJSONArray("cart");
                                int tongTien=0;
                                int tongSoLuong=0;
                                for(int j=0;j<jsonArray.length();j++){
                                    DetailProduct detail=new DetailProduct();
                                    JSONObject cart = jsonArray.getJSONObject(j);
                                    tongSoLuong+=cart.getInt("count");
                                    tongTien+=cart.getInt("price")*cart.getInt("count");
                                    detail.setImg(cart.getString("img"));
                                    detail.setProductID(cart.getString("productID"));
                                    detail.setSize(cart.getString("size"));
                                    detail.setColor(cart.getString("color"));
                                    detail.setPrice(cart.getInt("price"));
                                    detail.setNameProduct(cart.getString("nameProduct"));
                                    detail.setCount(cart.getInt("count"));
                                    details.add(detail);
                                }
                                history.setId(jsonObject.getString("_id"));
                                history.setUserID(jsonObject.getString("userID"));
                                history.setPhone(jsonObject.getString("phone"));
                                history.setAddress(jsonObject.getString("address"));
                                history.setCreateDate(jsonObject.getString("createDate").replaceAll("(\\.).+"," ").replace("T"," "));
                                history.setTotalMoney(tongTien);
                                history.setTotalNumber(tongSoLuong);
                                history.setDetailProductList(details);
                                histories.add(history);
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!flag){
                        Collections.reverse(histories);
                        setRvadapter(histories);
                    }else {
                        note.setVisibility(TextView.VISIBLE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HistoryActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(ArrayRequest);
    }
    public void setRvadapter(List<History> lst) {
        myAdapter = new HistoryDaTaAdapter(lst,this) ;
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new HistoryDaTaAdapter.OnItemClickListener() {
            @Override
            public void onClick(int postion) {
                Intent intent=new Intent(HistoryActivity.this, HistoryDetail.class);
                intent.putExtra("userID",userID);
                intent.putExtra("phone",phone);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("detail", histories.get(postion));
                startActivity(intent);
            }
        });
    }

    public void preOrder(String Id){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_PREORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            Intent intent=new Intent(HistoryActivity.this,CartActivity.class);
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
                        Toast.makeText(HistoryActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
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