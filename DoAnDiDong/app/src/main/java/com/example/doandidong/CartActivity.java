package com.example.doandidong;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adapter.CartDataAdapter;
import adapter.NotificationDataAdapter;
import io.socket.client.IO;
import io.socket.client.Socket;

import io.socket.emitter.Emitter;
import model.Cart;
import model.Product;


public class CartActivity extends AppCompatActivity {
    private String URL_JSON = "http://10.0.2.2:5000/api/cart/";
    private String URL_DELETE = "http://10.0.2.2:5000/api/cart/delete";
    private String URL_UPDATE = "http://10.0.2.2:5000/api/cart/update";

    private JsonObjectRequest ArrayRequest ;
    private List<Cart> lstCart = new ArrayList<>();
    private RecyclerView myrv ;
    private TextView sumCount,sumPrice,note,cartCount,cartPrice,tv_count;
    private EditText inputPhone,inputAddress;
    private LinearLayout layout,contact;
    private Button btnDatHang,btnIncrease, btnDecrease;;
    private String userID,phone,name,email,idRemove, time;
    private RadioGroup rdSize, rdColor;
    private RadioButton size, color;
    private boolean flaginput=true;
    private CartDataAdapter myAdapter;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    View bottomSheetView;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://172.21.105.117:5000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mSocket.connect();

        getData();
        AnhXa();
        Back();
        INPUT();
        jsoncall();

        mSocket.on("app xoa",AppXoaSanPham);

        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=true;
                String phoneRegex = "^0(?=.+[0-9]).{9}$|^\\+84[1-9](?=.+[0-9]).{8}$";
                if(!inputPhone.getText().toString().trim().matches(phoneRegex)){
                    inputPhone.setError("Số điện thoại sai định dạng");
                    check=false;
                }
                if(inputAddress.getText().toString().length() < 6){
                    inputAddress.setError("Đia chỉ ít nhất 6 ký tự");
                    check=false;
                }
                if(check==true){
                    btnDatHang.setEnabled(false);
                    DatHang();

                }
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

    public void Back() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void DatHang(){
        requestQueue= Volley.newRequestQueue(CartActivity.this);
        stringRequest=new StringRequest(Request.Method.POST,URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("msg").equals("Có sự thay đổi trong giỏ hàng vui lòng kiểm tra lại")){
                                lstCart.clear();
                                myAdapter.clear();
                                jsoncall();
                                Toast.makeText(CartActivity.this,jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                                btnDatHang.setEnabled(true);
                            }
                            else {
                                mSocket.emit("Order", "Có người vừa đặt hàng");
                                Toast.makeText(CartActivity.this,jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                                Intent intent3=new Intent(getApplicationContext(), HomeActivity.class);
                                intent3.putExtra("userID",userID);
                                intent3.putExtra("phone",phone);
                                intent3.putExtra("name",name);
                                intent3.putExtra("email",email);
                                startActivity(intent3);
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnDatHang.setEnabled(true);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CartActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
                        btnDatHang.setEnabled(true);
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("userID",userID);
                params.put("name",name);
                params.put("phone",phone);
                params.put("email",email);
                params.put("address",inputAddress.getText().toString());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void AnhXa(){
        myrv = findViewById(R.id.rvCart);
        inputPhone=findViewById(R.id.inputPhone);
        inputAddress=findViewById(R.id.inputAddress);
        layout=findViewById(R.id.layout);
        contact=findViewById(R.id.contact);
        note=findViewById(R.id.note);
        btnDatHang=findViewById(R.id.btnDatHang);

        inputPhone.setText(phone);
    }
    public void INPUT(){
        inputPhone.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_BACK)){
                    inputPhone.clearFocus();
                    checkFocus();
                    return true;
                }
                return false;
            }
        });
        inputPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                flaginput=false;
                if(hasFocus){
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                    layoutParams.width= LinearLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = 950;
                    layout.setLayoutParams(layoutParams);
                }
            }
        });
        inputPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(inputPhone.getWindowToken(), 0);
                    inputPhone.clearFocus();
                    checkFocus();

                    return true;

                }
                return false;
            }
        });
        inputAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_BACK)){
                    inputAddress.clearFocus();
                    checkFocus();
                    return true;
                }
                return false;
            }
        });
        inputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                flaginput=false;
                if(hasFocus){
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                    layoutParams.width= LinearLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = 850;
                    layout.setLayoutParams(layoutParams);
                }
            }
        });
        inputAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
                    inputAddress.clearFocus();
                    checkFocus();

                    return true;

                }
                return false;
            }
        });

    }
    public void checkFocus(){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.width= LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = 1200;
        layout.setLayoutParams(layoutParams);
    }

    public void Sum(){
        Integer count=0;
        Integer price=0;
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        for (Cart s : lstCart) {
            count+=s.getCount();
            price+=s.getPrice()*s.getCount();
        }
        sumCount=findViewById(R.id.tv_sumCount);
        sumPrice=findViewById(R.id.tv_sumPrice);
        sumCount.setText(count.toString());
        sumPrice.setText(currencyFormatter.format(price));
    }

    public void jsoncall() {
            ArrayRequest = new JsonObjectRequest(Request.Method.GET,URL_JSON+userID,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                boolean flag=true;
                boolean flagJson=true;
                try {
                    String msg = response.getString("msg");
                    if (!msg.equals("")){
                        flagJson=false;
                        jsoncall();
                    }
                    JSONArray jsonArray = response.getJSONArray("carts");
                    contact.setVisibility(LinearLayout.VISIBLE);
                    lstCart.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        flag = false;
                        Cart cart = new Cart();
                        cart.setId(jsonObject.getString("_id"));
                        cart.setProductID(jsonObject.getString("productID"));
                        cart.setName(jsonObject.getString("nameProduct"));
                        cart.setPrice(jsonObject.getInt("price"));
                        cart.setImg(jsonObject.getString("img"));
                        cart.setCount(jsonObject.getInt("count"));
                        cart.setSize(jsonObject.getString("size"));
                        cart.setColor(jsonObject.getString("color"));
                        lstCart.add(cart);

                    }
                    if(!flagJson){
                        Toast.makeText(CartActivity.this, "Có sự thay đổi trong giỏ hàng vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(!flag){
                        setRvadapter(lstCart);
                        Sum();
                }else {
                    lstCart.clear();
                    note.setVisibility(TextView.VISIBLE);
                }

                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(ArrayRequest);

    }

    public void DeleteCart(final String id){
        requestQueue= Volley.newRequestQueue(this);
        stringRequest=new StringRequest(Request.Method.POST,URL_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsoncall();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CartActivity.this,"Lỗi",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id",id);
                params.put("userID",userID);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    public void setRvadapter (List<Cart> lst) {
        myAdapter = new CartDataAdapter(lst,this) ;
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);
        Locale locale = new Locale("vi", "VN");
        final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        myAdapter.setOnItemClickListener(new CartDataAdapter.OnItemClickListener() {
            @Override
            public void onClick(int postion) {
                BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(CartActivity.this,R.style.BottomSheetDialogTheme);
                bottomSheetView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.modal_buy,(LinearLayout)findViewById(R.id.bottomContainer));
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                AnhXaBottomSheet();
                Cart lstCart=lst.get(postion);
                CheckRadio(lstCart);
                
                tv_count.setText(lstCart.getCount().toString());
                cartCount.setText(lstCart.getCount().toString()+" món");
                cartPrice.setText(currencyFormatter.format(lstCart.getCount()*lstCart.getPrice()));


                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int price=lstCart.getPrice();
                        int count = Integer.parseInt(tv_count.getText().toString())+1;
                        tv_count.setText(String.valueOf(count));
                        cartCount.setText(String.valueOf(count)+" món");
                        cartPrice.setText(currencyFormatter.format(price*count));

                    }
                });
                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int price=lstCart.getPrice();
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
                        if(lstCart.getSize()!=size.getText() || lstCart.getColor()!=color.getText() || lstCart.getCount().toString()!=tv_count.getText()){
                            UpdateCart(lstCart.getId(),size.getText().toString(),color.getText().toString(),Integer.parseInt((String) tv_count.getText()));
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
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

    public void CheckRadio(Cart cart){
        if(cart.getSize().equals("M")){
            rdSize.check(R.id.rdM);
        }
        else if(cart.getSize().equals("L")){
            rdSize.check(R.id.rdL);
        }

        if(cart.getColor().equals("Trắng")){
            rdColor.check(R.id.rdWhite);
        }
        else if(cart.getColor().equals("Xanh")){
            rdColor.check(R.id.rdRed);
        }
    }

    public void UpdateCart( final String id,final String size, final String color,final Integer count){
        requestQueue= Volley.newRequestQueue(this);
        stringRequest=new StringRequest(Request.Method.POST, URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsoncall();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CartActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id",id);
                params.put("userID",userID);
                params.put("size",size);
                params.put("color",color);
                params.put("count", count.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void hideKeyboard(View view) {
        inputPhone.clearFocus();
        inputAddress.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if(!flaginput)
            checkFocus();
    }

    private Emitter.Listener AppXoaSanPham = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        boolean flag=true;
                        idRemove=data.getString("productID");
                        for(int i=0;i<lstCart.size();i++){
                            if(lstCart.get(i).getProductID().equals(idRemove)){
                                Toast.makeText(CartActivity.this, data.getString("note"), Toast.LENGTH_LONG).show();
                                lstCart.clear();
                                myAdapter.clear();
                                jsoncall();
                                break;
                            }
                        }


                    } catch (Exception e) {
                        return;
                    }
                }
            });
        }
    };

}