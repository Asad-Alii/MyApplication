package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapplication.adapters.ChannelListAdapter;
import com.app.myapplication.adapters.UserListAdapter;
import com.app.myapplication.models.Channel;
import com.app.myapplication.models.User;
import com.app.myapplication.utils.Constants;
import com.app.myapplication.utils.PrefUtils;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    User user;
    String activityName;

    ImageButton logoutBtn;

    PrefUtils pref;

    FloatingActionButton fab;

    RecyclerView channelListView;

    ChannelListAdapter adapter;

    ArrayList<Channel> channels;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        activityName = getIntent().getStringExtra("activity");
        setContentView(R.layout.activity_home);

        firestore = FirebaseFirestore.getInstance();

        pref = PrefUtils.getInstance(this);

        channels = new ArrayList<>();

        logoutBtn = this.findViewById(R.id.logout_btn);
        fab = this.findViewById(R.id.fab);
        channelListView = this.findViewById(R.id.channel_list_view);

        channelListView.setLayoutManager(new LinearLayoutManager(this));

        getChannels();

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, UsersActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(view -> {
            if(pref.clearPrefs()){
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Unable to logout. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getChannels(){

        firestore.collection(Constants.channelsCollection).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        ArrayList<String> userIds = (ArrayList<String>) snapshot.get("userIds");

                        String otherId = userIds.stream().filter(id -> !id.equals(pref.getUser().getId())).findFirst().get();

//                        for(String id : userIds){
//                            if(!id.equals(pref.getUser().getId())){
//                                otherId = id;
//                            }
//                        }

                        firestore.collection(Constants.usersCollection).document(otherId).get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    User user = documentSnapshot.toObject(User.class);
                                    Channel channel = snapshot.toObject(Channel.class);
                                    channel.setUser(user);

                                    channels.add(channel);
                                    adapter.notifyItemChanged(channels.size() - 1);
                                    channels.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                });

                    }

                    adapter = new ChannelListAdapter(this, channels);
                    channelListView.setAdapter(adapter);
                    adapter.setOnChannelClickListener(position -> {

                        Intent intent = new Intent(HomeActivity.this, ChatsActivity.class);
                        intent.putExtra("channel", channels.get(position));
                        startActivity(intent);
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                });

    }
}