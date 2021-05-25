package com.example.doandidong;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import adapter.RecyclerDataAdapter;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import model.Cart;
import model.Product;

public class HomeFragment extends Fragment {
    private RequestQueue requestQueue;
    private String URL_PRODUCT="http://10.0.2.2:5000/api/product";
    private String URL_CART="http://10.0.2.2:5000/api/cart/";
    static List<Product> lstProduct = new ArrayList<>();
    private RecyclerView myrv ;
    private EditText searchView;
    private LinearLayout btnShirt,btnTrouser,cart,other;
    private String userID,name,phone,email;
    private TextView sumCount;
    private static final String mypreference = "PREFER_NAME";
    private GridLayoutManager gridLayoutManager;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable listState;
    private RecyclerDataAdapter myAdapter;
    View view;

    public List<Product> getLstProduct() {
        return this.lstProduct;
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.2:5000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public HomeFragment() {
        // Requir/*ed empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view=inflater.inflate(R.layout.home_fragment, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        AnhXa();
        getData();
        jsoncall();
        jsonCart();

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
                Intent intent=new Intent(getContext(),CartActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("phone",phone);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });
        btnShirt.setOnClickListener(v -> {
            searchView.clearFocus();
            Intent intent=new Intent(getContext(),ShirtActivity.class);
            intent.putExtra("userID",userID);
            intent.putExtra("phone",phone);
            intent.putExtra("name",name);
            intent.putExtra("email",email);
            startActivity(intent);
        });
        btnTrouser.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(),TrouserActivity.class);
            intent.putExtra("userID",userID);
            intent.putExtra("phone",phone);
            intent.putExtra("name",name);
            intent.putExtra("email",email);
            startActivity(intent);
        });
        other.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(),Accessories.class);
            intent.putExtra("userID",userID);
            intent.putExtra("phone",phone);
            intent.putExtra("name",name);
            intent.putExtra("email",email);
            startActivity(intent);
        });
        searchView.setOnClickListener(v->{
            Intent intent =new Intent(getContext(),SearchActivity.class);
            intent.putParcelableArrayListExtra("lstProduct", (ArrayList<? extends Parcelable>) lstProduct);
            intent.putExtra("userID",userID);
            intent.putExtra("phone",phone);
            intent.putExtra("name",name);
            intent.putExtra("email",email);
            startActivity(intent);
        });


    }
    public void AnhXa(){
        myrv = view.findViewById(R.id.rvProduct);
        searchView=view.findViewById(R.id.search);
        sumCount=view.findViewById(R.id.tv_count);
        cart=view.findViewById(R.id.cart);
        btnTrouser=view.findViewById(R.id.btnTrousers);
        btnShirt=view.findViewById(R.id.btnShirt);
        other=view.findViewById(R.id.other);
    }

    public void getData(){
        userID=getArguments().getString("userID");
        phone=getArguments().getString("phone");
        name=getArguments().getString("name");
        email=getArguments().getString("email");
    }

    public void jsoncall() {
        requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest ArrayRequest = new JsonArrayRequest(Request.Method.GET,URL_PRODUCT,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                lstProduct.clear();
                for (int i = 0 ; i<response.length();i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Product products = new Product();
                        products.setId(jsonObject.getString("_id"));
                        products.setName(jsonObject.getString("name"));
                        products.setPrice(jsonObject.getInt("price"));
                        products.setImg("http://10.0.2.2:5000"+jsonObject.getString("img"));
                        products.setCategories(jsonObject.getString("categories"));
                        products.setDescription(jsonObject.getString("description"));
                        products.setStatus(jsonObject.getBoolean("status"));
                        lstProduct.add(products);
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
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(ArrayRequest);
    }

    public void jsonCart() {
        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest ArrayRequest = new JsonObjectRequest(Request.Method.GET,URL_CART+userID,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Integer count=0;
                try {
                    JSONArray jsonArray=response.getJSONArray("carts");
                    String msg=response.getString("msg");
                    for (int i = 0 ; i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                            count+=jsonObject.getInt("count");
                    }
                    if(!msg.equals("")){
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(count>0){
                    sumCount.setText(count.toString());
                }else{
                    sumCount.setText("");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(ArrayRequest);
    }

    public void setRvadapter (List<Product> lst) {
        myrv.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        myAdapter = new RecyclerDataAdapter(lst,userID,getContext());

        myrv.setLayoutManager(gridLayoutManager);
        if (mBundleRecyclerViewState != null) {
            listState=mBundleRecyclerViewState.getParcelable("recycler_state");
            myrv.getLayoutManager().onRestoreInstanceState(listState);
        }
        myrv.setAdapter(myAdapter);

    }


    @Override
    public void onResume() {
        super.onResume();

        jsonCart();
        savingPreferences();
    }

    public void onPause()
    {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = myrv.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }
    public void savingPreferences()
    {
        //tạo đối tượng getSharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        //tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor=sharedPreferences.edit();
        //lưu vào editor

        editor.putString("userID",userID);
        editor.putString("phone",phone);
        editor.putString("name",name);
        editor.putString("email",email);
        editor.commit();
    }


}
