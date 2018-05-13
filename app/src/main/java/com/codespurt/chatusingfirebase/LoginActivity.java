package com.codespurt.chatusingfirebase;

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
import com.codespurt.chatusingfirebase.chatModule.RegisterActivity;
import com.codespurt.chatusingfirebase.chatModule.UsersActivity;
import com.codespurt.chatusingfirebase.chatModule.pojo.UserDetails;
import com.codespurt.chatusingfirebase.chatModule.security.AES;
import com.codespurt.chatusingfirebase.chatModule.utils.Preferences;
import com.codespurt.chatusingfirebase.chatModule.utils.Urls;
import com.codespurt.chatusingfirebase.chatModule.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Code Spurt on 12-05-18.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, password;
    private Button login;
    private TextView register;
    private String user, pass;
    private Util util;
    private Preferences preferences;
    private AES aes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        util = new Util(this);
        preferences = new Preferences(this);
        aes = new AES();

        if (preferences.get(Preferences.IS_LOGGED_IN).equals(Preferences.TRUE)) {
            loginUserSuccess(true, null);
        } else {
            if (getIntent() != null) {
                if (getIntent().hasExtra("username")) {
                    username.setText(getIntent().getStringExtra("username"));
                    password.requestFocus();
                }
            }
        }
    }

    private void initViews() {
        username = (EditText) findViewById(R.id.tv_username);
        password = (EditText) findViewById(R.id.tv_password);
        login = (Button) findViewById(R.id.btn_login);
        register = (TextView) findViewById(R.id.tv_register);
    }

    @Override
    protected void onResume() {
        super.onResume();
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
            case R.id.btn_login:
                util.hideKeyboard(this);
                doUserLogin();
                break;
        }
    }

    private void doUserLogin() {
        user = username.getText().toString();
        pass = password.getText().toString();

        if (user.trim().equals("")) {
            username.setError(getResources().getString(R.string.blank_error));
        } else if (pass.trim().equals("")) {
            password.setError(getResources().getString(R.string.blank_error));
        } else {
            util.showProgressDialog();

            // get list of all users
            StringRequest request = new StringRequest(Request.Method.GET, Urls.FIREBASE_DATABASE_URL + Urls.FIREBASE_TABLE_NAME_USERS, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s.equals("null")) {
                        util.showLongToast(getResources().getString(R.string.no_users));
                    } else {
                        try {
                            JSONObject obj = new JSONObject(s);
                            if (!obj.has(user)) {
                                util.showLongToast(getResources().getString(R.string.no_users));
                            } else if (aes.decrypt(obj.getJSONObject(user).getString("password"), true).equals(pass)) {
                                UserDetails details = new UserDetails();
                                details.setUsername(user);
                                details.setPassword(pass);

                                preferences.save(Preferences.IS_LOGGED_IN, Preferences.TRUE);
                                preferences.save(Preferences.USERNAME, details.getUsername());
                                preferences.save(Preferences.PASSWORD, details.getPassword());

                                loginUserSuccess(false, details);
                            } else {
                                util.showLongToast(getResources().getString(R.string.incorrect_password));
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

            RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
            rQueue.add(request);
        }
    }

    private void loginUserSuccess(boolean isFromPreference, UserDetails userDetails) {
        UserDetails details = userDetails;
        if (isFromPreference) {
            details = new UserDetails();
            details.setUsername(preferences.get(Preferences.USERNAME));
            details.setPassword(preferences.get(Preferences.PASSWORD));
        }

        Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
        intent.putExtra("userData", details);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        login.setOnClickListener(null);
        register.setOnClickListener(null);
    }
}
