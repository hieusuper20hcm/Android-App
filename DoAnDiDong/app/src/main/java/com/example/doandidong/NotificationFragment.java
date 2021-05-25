package com.example.doandidong;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;



import adapter.NotificationDataAdapter;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import model.Product;

public class NotificationFragment extends Fragment {
    private RecyclerView myrv ;
    private TextView note;
    NotificationDataAdapter notificationDataAdapter;

    private String userID,name,phone,email,idRemove;
    private List<Product> lstProduct = new ArrayList<>();

    String urlLike="http://10.0.2.2:5000/api/like/";
    JsonArrayRequest ArrayRequest;
    RequestQueue requestQueue;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://172.21.105.117:5000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    // Inflate the layout for this fragment

        return inflater.inflate(R.layout.notification_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        myrv = view.findViewById(R.id.rvNotification);
        note=view.findViewById(R.id.note);
        mSocket.connect();
        mSocket.on("app xoa",AppXoaSanPham);
        getData();
        jsonCallLike();
    }
    private void jsonCallLike(){
        requestQueue = Volley.newRequestQueue(getContext());
        ArrayRequest = new JsonArrayRequest(Request.Method.GET,urlLike+userID,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                    boolean flag = true;
                    note.setVisibility(TextView.VISIBLE);
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            flag = false;
                            Product products = new Product();
                            products.setId(jsonObject.getString("productID"));
                            products.setName(jsonObject.getString("nameProduct"));
                            products.setPrice(jsonObject.getInt("price"));
                            products.setImg(jsonObject.getString("img"));
                            products.setDescription(jsonObject.getString("description"));
                            products.setStatus(jsonObject.getBoolean("status"));
                            lstProduct.add(products);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    if (!flag) {
                        note.setVisibility(TextView.GONE);
                        setRvadapter(lstProduct);
                    } else {
                        note.setVisibility(TextView.VISIBLE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Lỗi",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(ArrayRequest);
    }

    public void setRvadapter (List<Product> lst) {

        notificationDataAdapter = new NotificationDataAdapter(lst,getContext()) ;
        myrv.setLayoutManager(new LinearLayoutManager(getContext()));
        myrv.setAdapter(notificationDataAdapter);
        notificationDataAdapter.setOnItemClickListener(new NotificationDataAdapter.OnItemClickListener() {
            @Override
            public void onClick(int postion) {
                Intent intent=new Intent(getContext(), DetailActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("detail", (Serializable) lst.get(postion));
                startActivity(intent);
            }
        });
    }

    public void getData(){
        userID=getArguments().getString("userID");
        phone=getArguments().getString("phone");
        name=getArguments().getString("name");
        email=getArguments().getString("email");
    }

    private Emitter.Listener AppXoaSanPham = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            idRemove = data.getString("productID");
                            for (int i = 0; i < lstProduct.size(); i++) {
                                if (lstProduct.get(i).getId().equals(idRemove)) {
                                    Toast.makeText(getContext(), data.getString("note"), Toast.LENGTH_LONG).show();
                                    lstProduct.clear();
                                    notificationDataAdapter.clear();
                                    jsonCallLike();
                                    break;
                                }
                            }

                        } catch (Exception e) {
                            return;
                        }
                    }
                });
            }
        }
    };


}
