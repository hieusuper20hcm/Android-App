package com.example.doandidong;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import model.Cart;
import model.Product;

public class DetailActivity extends AppCompatActivity {
    TextView tv_id,tv_name,tv_price,tv_description,tv_img,tv_count,cartCount,cartPrice;
    ImageView img;
    Button btnDatHang, btnIncrease, btnDecrease;
    RadioGroup rdSize, rdColor;
    RadioButton size, color;
    RequestOptions options;
    ImageButton like;

    String url = "http://10.0.2.2:5000/api/cart/add";
    String urlLike="http://10.0.2.2:5000/api/like/";
    String addLike="http://10.0.2.2:5000/api/like/add";
    String removeLike="http://10.0.2.2:5000/api/like/delete";

    View bottomSheetView;
    RequestQueue requestQueue;
    JsonArrayRequest ArrayRequest;
    StringRequest stringRequest;


    Boolean status;

    boolean flag=true;
    boolean isFlag=true;
    String userID, idLike;
    Integer priceProduct;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        AnhXa();
        Back();
        getData();
        jsonCallLike();
    }

    public void onDestroy()
    {
        super.onDestroy();

        if(!flag){
            if(isFlag){
                Like();
            }
        }
        else {
            if(!isFlag){
                RemoveLike();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getData(){
        Locale locale = new Locale("vi", "VN");
        final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        Intent intent=getIntent();
        final Product product=(Product)intent.getSerializableExtra("detail");
        userID=intent.getStringExtra("userID");
        status=product.getStatus();
        priceProduct=product.getPrice();
        tv_id.setText(product.getId());
        tv_name.setText(product.getName());
        tv_price.setText(currencyFormatter.format(priceProduct));
        tv_description.setText(product.getDescription());
        tv_img.setText(product.getImg());
        Glide.with(DetailActivity.this).load(product.getImg()).apply(options).into(img);

        if(!status){
            btnDatHang.setBackgroundTintList(getColorStateList(R.color.grey));
            btnDatHang.setEnabled(false);
            btnDatHang.setText("Đã hết hàng");
        }



        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(DetailActivity.this,R.style.BottomSheetDialogTheme);
                bottomSheetView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.modal_buy,(LinearLayout)findViewById(R.id.bottomContainer));
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                AnhXaBottomSheet();
                cartPrice.setText(tv_price.getText());
                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int price=product.getPrice();
                        int count = Integer.parseInt(tv_count.getText().toString())+1;
                        tv_count.setText(String.valueOf(count));
                        cartCount.setText(String.valueOf(count)+" món");
                        cartPrice.setText(currencyFormatter.format(price*count));

                    }
                });
                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int price=product.getPrice();
                        int count = Integer.parseInt(tv_count.getText().toString())-1;
                        if(count<=0){
                            count=1;
                        }
                        tv_count.setText(String.valueOf(count));
                        cartCount.setText(String.valueOf(count)+" món");
                        cartPrice.setText(currencyFormatter.format(price*count));
                    }
                });
                bottomSheetView.findViewById(R.id.btnXacNhan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int radioSize=rdSize.getCheckedRadioButtonId();
                        size=bottomSheetView.findViewById(radioSize);
                        int radioColor=rdColor.getCheckedRadioButtonId();
                        color=bottomSheetView.findViewById(radioColor);

                        DatHang(url,userID,product.getId(),product.getName(),product.getPrice(),product.getImg(),Integer.parseInt((String) tv_count.getText()),
                                size.getText().toString(),color.getText().toString());
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });

    }

    public void AnhXa(){
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground);
        tv_id=findViewById(R.id.tv_id);
        tv_name=findViewById(R.id.tv_name);
        tv_price=findViewById(R.id.tv_price);
        tv_description=findViewById(R.id.tv_description);
        tv_img=findViewById(R.id.tv_img);
        img=findViewById(R.id.img);
        btnDatHang=findViewById(R.id.btnDatHang);
        like=findViewById(R.id.like);
    }

    public void AnhXaBottomSheet(){
        rdSize=bottomSheetView.findViewById(R.id.rdSize);
        rdColor=bottomSheetView.findViewById(R.id.rdColor);
        btnIncrease=bottomSheetView.findViewById(R.id.btnIncrease);
        btnDecrease=bottomSheetView.findViewById(R.id.btnDecrease);
        tv_count=bottomSheetView.findViewById(R.id.tv_count);
        cartCount=bottomSheetView.findViewById(R.id.cartCount);
        cartPrice=bottomSheetView.findViewById(R.id.cartPrice);
    }

    public void Back(){
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        like.setOnClickListener(v -> {
            if(!flag){
                like.clearColorFilter();
                Toast.makeText(DetailActivity.this, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
                flag=true;
            }else {
                like.setColorFilter(getResources().getColor(R.color.red),PorterDuff.Mode.SRC_IN);
                Toast.makeText(DetailActivity.this, "Đã thích", Toast.LENGTH_SHORT).show();
                flag=false;
            }
        });
    }

    private void DatHang(String url, String userID, final String productID, final String name, final Integer price, final String img, final Integer count, final String size, final String color){
        requestQueue= Volley.newRequestQueue(this);
        stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DetailActivity.this,"Đã thêm vào giỏ hàng",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailActivity.this,"Lỗi",Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("userID",userID);
                params.put("productID",productID);
                params.put("nameProduct",name);
                params.put("price",price.toString());
                params.put("img",img);
                params.put("count",count.toString());
                params.put("size",size);
                params.put("color",color);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void jsonCallLike(){
        requestQueue = Volley.newRequestQueue(this);
        ArrayRequest = new JsonArrayRequest(Request.Method.GET,urlLike+userID,null, new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
                for (int i = 0 ; i<response.length();i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if(jsonObject.getString("productID").equals(tv_id.getText().toString())) {
                            idLike=jsonObject.getString("_id");
                            like.setColorFilter(getResources().getColor(R.color.red),PorterDuff.Mode.SRC_IN);
                            flag=false;
                            isFlag=false;
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                }
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(DetailActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
        }
    });
        requestQueue.add(ArrayRequest);
    }

    private void Like(){
        requestQueue= Volley.newRequestQueue(this);
        stringRequest=new StringRequest(Request.Method.POST, addLike,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("userID",userID);
                params.put("productID",tv_id.getText().toString());
                params.put("nameProduct",tv_name.getText().toString());
                params.put("price",priceProduct.toString());
                params.put("img",tv_img.getText().toString());
                params.put("description",tv_description.getText().toString());
                params.put("status",status.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void RemoveLike(){
        requestQueue= Volley.newRequestQueue(this);
        stringRequest=new StringRequest(Request.Method.POST, removeLike,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("userID",userID);
                params.put("id",idLike);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}