package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapplication.adapters.UserListAdapter;
import com.app.myapplication.models.Channel;
import com.app.myapplication.models.User;
import com.app.myapplication.utils.Constants;
import com.app.myapplication.utils.PrefUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersActivity extends AppCompatActivity {

    RecyclerView userListView;
    private UserListAdapter adapter;
    ArrayList<User> users;

    FirebaseFirestore firestore;

    PrefUtils pref;

    ImageButton backBtn;

    RelativeLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        pref = PrefUtils.getInstance(this);

        userListView = findViewById(R.id.user_list);
        backBtn = findViewById(R.id.back_btn);
        loader = findViewById(R.id.loader);

        firestore = FirebaseFirestore.getInstance();

        users = new ArrayList<>();

        userListView.setLayoutManager(new LinearLayoutManager(this));

        loader.setVisibility(View.VISIBLE);

        backBtn.setOnClickListener(view -> finish());

        fetchUsers();
    }

    private void fetchUsersNew() {
        firestore.collection(Constants.usersCollection)
                .whereNotEqualTo("id", pref.getUser().getId())
                .addSnapshotListener((value, error) -> {

                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        users.add(snapshot.toObject(User.class));
                    }

                    loader.setVisibility(View.GONE);
                    adapter = new UserListAdapter(this, users);
                    userListView.setAdapter(adapter);

                    adapter.setOnChatClickListener(position -> {
                        loader.setVisibility(View.VISIBLE);
                        checkAndCreateChannel(position);
                    });
                });
    }

    private void fetchUsers() {
        firestore.collection(Constants.usersCollection)
                .whereNotEqualTo("id", pref.getUser().getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        users.add(snapshot.toObject(User.class));
                    }

                    loader.setVisibility(View.GONE);
                    adapter = new UserListAdapter(this, users);
                    userListView.setAdapter(adapter);

                    adapter.setOnChatClickListener(position -> {
                        loader.setVisibility(View.VISIBLE);
                        checkAndCreateChannel(position);
                    });

                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    Channel channel;

    private void checkAndCreateChannel(int position) {
        String myId = pref.getUser().getId();
        String otherUserId = users.get(position).getId();

        firestore.collection(Constants.channelsCollection)
                .whereArrayContains("userIds", myId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                        // Channel already exists

                        boolean channelExist = false;

                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            List<String> usersIds = (List<String>) document.get("userIds");

                            if(usersIds.contains(otherUserId)){
                                channelExist = true;
                                break;
                            }

                        }

                        if(channelExist){
                            loader.setVisibility(View.GONE);
                            Toast.makeText(this, "Channel already exists!", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(UsersActivity.this, ChatsActivity.class);
//                            intent.putExtra("channel", channel);
//                            startActivity(intent);
                        }
                        else{
                            // Channel does not exist, proceed to create a new one
                            createNewChannel(position);
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Log.e("Error", e.getMessage().toString());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createNewChannel(int position) {
        String channelId = firestore.collection(Constants.channelsCollection).document().getId();

        Map<String, Object> channelMap = new HashMap<>();
        channelMap.put("channelId", channelId);
        channelMap.put("createdAt", FieldValue.serverTimestamp());
        channelMap.put("updatedAt", FieldValue.serverTimestamp());
        channelMap.put("createdBy", pref.getUser().getId());

        ArrayList<String> userIds = new ArrayList<>();
        userIds.add(pref.getUser().getId());
        userIds.add(users.get(position).getId());

        channelMap.put("userIds", userIds);

        firestore.collection(Constants.channelsCollection).document(channelId).set(channelMap)
                .addOnSuccessListener(unused -> {
                    String chatId = firestore.collection(Constants.chatsCollection).document().getId();

                    Map<String, Object> chatMap = new HashMap<>();
                    chatMap.put("chatId", chatId);
                    chatMap.put("message", "Hello");
                    chatMap.put("createdAt", FieldValue.serverTimestamp());
                    chatMap.put("updatedAt", FieldValue.serverTimestamp());
                    chatMap.put("isRead", false);
                    chatMap.put("type", "text");
                    chatMap.put("authorId", pref.getUser().getId());

                    firestore.collection(Constants.channelsCollection).document(channelId)
                            .collection(Constants.chatsCollection).document(chatId).set(chatMap)
                            .addOnSuccessListener(unused1 -> {
                                loader.setVisibility(View.GONE);
                                Toast.makeText(this, "Message Sent Successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                loader.setVisibility(View.GONE);
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
