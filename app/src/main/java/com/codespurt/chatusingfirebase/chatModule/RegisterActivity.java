package com.codespurt.chatusingfirebase.chatModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codespurt.chatusingfirebase.LoginActivity;
import com.codespurt.chatusingfirebase.R;
import com.codespurt.chatusingfirebase.chatModule.security.AES;
import com.codespurt.chatusingfirebase.chatModule.utils.Urls;
import com.codespurt.chatusingfirebase.chatModule.utils.Util;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Code Spurt on 12-05-18.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, password;
    private Button register;
    private TextView login;
    private String user, pass;
    private Util util;
    private AES aes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        util = new Util(this);
        aes = new AES();

        Firebase.setAndroidContext(this);
    }

    private void initViews() {
        username = (EditText) findViewById(R.id.tv_username);
        password = (EditText) findViewById(R.id.tv_password);
        register = (Button) findViewById(R.id.btn_register);
        login = (TextView) findViewById(R.id.tv_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                util.hideKeyboard(this);
                openLoginActivity(null);
                break;
            case R.id.btn_register:
                util.hideKeyboard(this);
                doRegisterUser();
                break;
        }
    }

    private void doRegisterUser() {
        user = username.getText().toString();
        pass = password.getText().toString();

        if (user.trim().equals("")) {
            username.setError(getResources().getString(R.string.blank_error));
        } else if (pass.trim().equals("")) {
            password.setError(getResources().getString(R.string.blank_error));
        } else if (!user.trim().matches("[A-Za-z0-9]+")) {
            username.setError(getResources().getString(R.string.only_alphabet_or_number_allowed));
        } else {
            util.showProgressDialog();

            // get list of all users
            StringRequest request = new StringRequest(Request.Method.GET, Urls.FIREBASE_DATABASE_URL + Urls.FIREBASE_TABLE_NAME_USERS, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Firebase reference = new Firebase(Urls.FIREBASE_DATABASE_URL + "users");

                    if (s.equals("null")) {
                        // add user if doesn't exist
                        reference.child(user).child("password").setValue(aes.encrypt(pass, true));
                        util.showLongToast(getResources().getString(R.string.registration_successful));

                        openLoginActivity(user);
                    } else {
                        try {
                            JSONObject obj = new JSONObject(s);
                            if (!obj.has(user)) {
                                reference.child(user).child("password").setValue(aes.encrypt(pass, true));
                                util.showLongToast(getResources().getString(R.string.registration_successful));

                                openLoginActivity(user);
                            } else {
                                util.showLongToast(getResources().getString(R.string.username_already_exists));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    util.hideProgressDialog();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                    util.hideProgressDialog();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(RegisterActivity.this);
            rQueue.add(request);
        }
    }

    private void openLoginActivity(String username) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        if (username != null) {
            intent.putExtra("username", username);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        register.setOnClickListener(null);
        login.setOnClickListener(null);
    }
}