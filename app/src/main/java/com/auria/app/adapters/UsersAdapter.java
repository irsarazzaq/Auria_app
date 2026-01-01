package com.auria.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auria.app.R;
import com.auria.app.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;

    public UsersAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameText.setText(user.getName());
        holder.emailText.setText(user.getEmail());

        // Optional: click listener for user item
        holder.itemView.setOnClickListener(v -> {
            // Open user profile or details if needed
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ✅ Update Users method
    public void updateUsers(List<User> newUsers) {
        this.userList = newUsers;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.userNameText);
            emailText = itemView.findViewById(R.id.userEmailText);
        }
    }
}
