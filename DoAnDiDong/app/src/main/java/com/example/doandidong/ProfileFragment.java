package com.example.doandidong;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private LinearLayout history;
    private String userID,name,phone,email;
    TextView tv_name,tv_phone,tv_email;
    Button logout,btnOK,btnCancel;
    View view;
    private static final String mypreference = "PREFER_NAME";
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view=inflater.inflate(R.layout.profile_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getData();
        AnhXa();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_custom);
                btnOK = dialog.findViewById(R.id.btnOK);
                btnCancel = dialog.findViewById(R.id.btnCancel);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent=new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();

            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), HistoryActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("phone",phone);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                startActivity(intent);

            }
        });
    }
    public void AnhXa(){
        history=view.findViewById(R.id.history);
        logout=view.findViewById(R.id.btn_Logout);
        tv_name=view.findViewById(R.id.tv_name);
        tv_email=view.findViewById(R.id.tv_email);
        tv_phone=view.findViewById(R.id.tv_phone);
        tv_name.setText(name);
        tv_phone.setText(phone);
        tv_email.setText(email);
    }
    public void getData(){
        userID=getArguments().getString("userID");
        phone=getArguments().getString("phone");
        name=getArguments().getString("name");
        email=getArguments().getString("email");
    }
}
