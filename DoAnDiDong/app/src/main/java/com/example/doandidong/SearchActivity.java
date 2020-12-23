package com.example.doandidong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.RecyclerDataAdapter;
import me.gujun.android.taggroup.TagGroup;
import model.Product;

public class SearchActivity extends AppCompatActivity {
    List<Product> list=new ArrayList<>();
    SearchView searchView;
    RecyclerView myrv;
    RecyclerDataAdapter myAdapter;
    TagGroup mTagGroup;
    GridLayoutManager gridLayoutManager;
    String userID,phone,name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        myrv = findViewById(R.id.rvProduct);
        searchView = findViewById(R.id.search);
        getData();
        Back();


        gridLayoutManager = new GridLayoutManager(SearchActivity.this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        myAdapter = new RecyclerDataAdapter(list,userID,SearchActivity.this) ;
        myrv.setLayoutManager(gridLayoutManager);
        myrv.setAdapter(myAdapter);
        setSearchView(myAdapter);

        mTagGroup = findViewById(R.id.tag_group);
        mTagGroup.setTags(new String[]{"Quần", "Áo","Phụ kiện"});

        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                searchView.setQuery(tag,false);
                hideSoftKeyboard(searchView);
            }
        });
    }

    public void Back() {
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

        Bundle bundle = getIntent().getExtras();
        list = bundle.getParcelableArrayList("lstProduct");
    }

    public void setSearchView(final RecyclerDataAdapter myAdapter){
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchFurniture(newText);
                return false;
            }
        });
    }
    private void searchFurniture(String newText) {
        ArrayList<Product> tmp = new ArrayList<>();
        if (list == null) {
            myrv.setVisibility(View.GONE);
            return;
        }
        for(Product product : list){
            if(product.getName().toLowerCase().contains(newText.toLowerCase())){
                tmp.add(product);
            }

            if(product.getCategories().toLowerCase().equals(newText.toLowerCase())){
                tmp.add(product);
            }

        }
        if(tmp.size() > 0){
            myAdapter.filterList(tmp);
            myrv.setVisibility(View.VISIBLE);
        }
        if(newText.isEmpty()){
            myrv.setVisibility(View.GONE);
        }
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm
                =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        searchView.clearFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard(this.searchView);
    }

}