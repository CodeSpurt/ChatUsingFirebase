package com.codespurt.chatusingfirebase.chatModule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codespurt.chatusingfirebase.R;
import com.codespurt.chatusingfirebase.chatModule.pojo.UserDetails;
import com.codespurt.chatusingfirebase.chatModule.security.AES;
import com.codespurt.chatusingfirebase.chatModule.utils.Urls;
import com.codespurt.chatusingfirebase.chatModule.utils.Util;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Code Spurt on 12-05-18.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ScrollView scrollView;
    private RelativeLayout layout_2;
    private LinearLayout layout;
    private EditText messageArea;
    private ImageView sendButton;
    private UserDetails details;
    private Firebase reference1, reference2;
    private Util util;
    private AES aes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();

        if (getIntent() != null) {
            if (getIntent().hasExtra("userData")) {
                details = new UserDetails();
                details = (UserDetails) getIntent().getSerializableExtra("userData");
            }
        }

        util = new Util(this);
        aes = new AES();

        Firebase.setAndroidContext(this);
        reference1 = new Firebase(Urls.FIREBASE_DATABASE_URL + Urls.FIREBASE_TABLE_NAME_MESSAGES + details.getUsername() + " -> " + details.getChatWith());
        reference2 = new Firebase(Urls.FIREBASE_DATABASE_URL + Urls.FIREBASE_TABLE_NAME_MESSAGES + details.getChatWith() + " -> " + details.getUsername());

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString().trim();
                String userName = map.get("user").toString().trim();
                String isMessageEncrypted = map.get("isMessageEncrypted").toString().trim();

                if (userName.equals(details.getUsername())) {
                    if (isMessageEncrypted.equals("true")) {
                        addMessageBox(aes.decrypt(message, true), 1);
                    } else {
                        addMessageBox(message, 1);
                    }
                } else {
                    if (isMessageEncrypted.equals("true")) {
                        addMessageBox(aes.decrypt(message, true), 2);
                    } else {
                        addMessageBox(message, 2);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void initViews() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        messageArea = (EditText) findViewById(R.id.messageArea);
        sendButton = (ImageView) findViewById(R.id.sendButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendButton.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                String messageText = messageArea.getText().toString();
                if (!messageText.trim().equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", aes.encrypt(messageText, false));
                    map.put("user", details.getUsername());
                    map.put("isMessageEncrypted", aes.isMessageEncrypted());

                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                } else {
                    util.showToast(getResources().getString(R.string.blank_error));
                }
                break;
            case R.id.layout1:
                util.hideKeyboard(this);
                break;
        }
    }

    private void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.setMargins(0, 15, 5, 0);
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            lp2.setMargins(5, 15, 0, 0);
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendButton.setOnClickListener(null);
        layout.setOnClickListener(null);
    }
}