package com.app.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapplication.R;
import com.app.myapplication.models.Channel;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Channel> channels;
    private OnChannelClickListener onChannelClickListener;


    public interface OnChannelClickListener{
        void channelClickListener(int position);
    }

    public void setOnChannelClickListener(OnChannelClickListener onChannelClickListener){
        this.onChannelClickListener = onChannelClickListener;
    }

    public ChannelListAdapter(Context context, ArrayList<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.channel_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(channels.get(position).getUser().getName());
//
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM 'at' hh:mm a", Locale.getDefault());
        holder.date.setText(simpleDateFormat.format(channels.get(position).getCreatedAt()));

        Glide.with(context)
                .load(channels.get(position).getUser().getImage())
                .into(holder.profilePic);

    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView name, date;
        MaterialCardView root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profile_pic);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            root = itemView.findViewById(R.id.root);

            root.setOnClickListener(v -> onChannelClickListener.channelClickListener(getAdapterPosition()));
        }
    }
}
