package com.codespurt.chatusingfirebase.chatModule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codespurt.chatusingfirebase.R;
import com.codespurt.chatusingfirebase.chatModule.adapter.UsersAdapter;
import com.codespurt.chatusingfirebase.chatModule.pojo.UserDetails;
import com.codespurt.chatusingfirebase.chatModule.utils.Urls;
import com.codespurt.chatusingfirebase.chatModule.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Code Spurt on 12-05-18.
 */

public class UsersActivity extends AppCompatActivity {

    private TextView noUsers;
    private RecyclerView recyclerView;
    private List<UserDetails> list;
    private UsersAdapter adapter;
    private int totalUsers = 0;
    private Util util;
    private UserDetails details;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        initViews();

        util = new Util(this);
        list = new ArrayList<>();

        initAdapter();

        if (getIntent() != null) {
            if (getIntent().hasExtra("userData")) {
                details = new UserDetails();
                details = (UserDetails) getIntent().getSerializableExtra("userData");
                getUsers();
            }
        } else {
            recyclerView.setVisibility(View.GONE);
            noUsers.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        noUsers = (TextView) findViewById(R.id.tv_no_users);
        recyclerView = (RecyclerView) findViewById(R.id.list_users);
    }

    private void initAdapter() {
        adapter = new UsersAdapter(this, list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void getUsers() {
        util.showProgressDialog();

        // get list of all users
        StringRequest request = new StringRequest(Request.Method.GET, Urls.FIREBASE_DATABASE_URL + Urls.FIREBASE_TABLE_NAME_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                util.hideProgressDialog();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(UsersActivity.this);
        rQueue.add(request);
    }

    private void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()) {
                key = i.next().toString();
                if (!key.equals(details.getUsername())) {
                    UserDetails item = new UserDetails();
                    item.setUsername(key);
                    list.add(item);
                }
                totalUsers++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers <= 1) {
            recyclerView.setVisibility(View.GONE);
            noUsers.setVisibility(View.VISIBLE);
        } else {
            noUsers.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }

        util.hideProgressDialog();
    }
}