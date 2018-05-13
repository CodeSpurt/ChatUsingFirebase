package com.codespurt.chatusingfirebase.chatModule.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codespurt.chatusingfirebase.R;
import com.codespurt.chatusingfirebase.chatModule.ChatActivity;
import com.codespurt.chatusingfirebase.chatModule.pojo.UserDetails;
import com.codespurt.chatusingfirebase.chatModule.utils.Preferences;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private Context context;
    private List<UserDetails> list;
    private Preferences preferences;

    public UsersAdapter(Context context, List<UserDetails> list) {
        this.context = context;
        this.list = list;
        preferences = new Preferences(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        UserDetails details = list.get(position);
        if (details.getUserImage() != null) {
            if (details.getUserImage().trim().equals("")) {
                if (details.getUsername().length() > 0 && details.getUsername().length() > 2) {
                    holder.initialLetter.setText(details.getUsername().substring(0, 1).toUpperCase());

                    holder.image.setVisibility(View.GONE);
                    holder.initialLetter.setVisibility(View.VISIBLE);
                } else {
                    holder.initialLetter.setVisibility(View.GONE);
                    holder.image.setVisibility(View.VISIBLE);
                }
            } else {
                holder.initialLetter.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(details.getUserImage().trim())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.image);
            }
        } else {
            if (details.getUsername().length() > 0 && details.getUsername().length() > 2) {
                holder.initialLetter.setText(details.getUsername().substring(0, 1).toUpperCase());

                holder.image.setVisibility(View.GONE);
                holder.initialLetter.setVisibility(View.VISIBLE);
            } else {
                holder.initialLetter.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
            }
        }
        if (details.getUsername() != null) {
            holder.username.setText(details.getUsername());
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetails item = new UserDetails();
                item.setUsername(preferences.get(Preferences.USERNAME));
                item.setPassword(preferences.get(Preferences.PASSWORD));
                item.setChatWith(list.get(position).getUsername());

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userData", item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout;
        private ImageView image;
        private TextView initialLetter, username;

        public MyViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.layout);
            image = (ImageView) view.findViewById(R.id.img_user);
            initialLetter = (TextView) view.findViewById(R.id.tv_initial_letter);
            username = (TextView) view.findViewById(R.id.tv_username);
        }
    }
}
