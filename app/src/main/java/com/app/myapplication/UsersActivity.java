package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapplication.adapters.UserListAdapter;
import com.app.myapplication.models.User;
import com.app.myapplication.utils.Constants;
import com.app.myapplication.utils.PrefUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
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

        userListView = this.findViewById(R.id.user_list);
        backBtn = this.findViewById(R.id.back_btn);
        loader = this.findViewById(R.id.loader);

        firestore = FirebaseFirestore.getInstance();

        users = new ArrayList<>();

        userListView.setLayoutManager(new LinearLayoutManager(this));

        loader.setVisibility(View.VISIBLE);

        backBtn.setOnClickListener(view -> {
            finish();
        });

        firestore.collection(Constants.usersCollection).whereNotEqualTo("id", pref.getUser().getId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()){
                        users.add(snapshot.toObject(User.class));
                    }

                    loader.setVisibility(View.GONE);
                    adapter = new UserListAdapter(this, users);
                    userListView.setAdapter(adapter);

                    adapter.setOnChatClickListener(position -> {
//                        Toast.makeText(this, users.get(position).getName(), Toast.LENGTH_SHORT).show();

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

//                                    Toast.makeText(this, "Channel created Successfully!", Toast.LENGTH_SHORT).show();

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

                                                Toast.makeText(this, "Message Sent Successfully!", Toast.LENGTH_SHORT).show();

                                            })
                                            .addOnFailureListener(e -> {
                                                loader.setVisibility(View.GONE);
                                                Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            });

                                })
                                .addOnFailureListener(e -> {
                                    loader.setVisibility(View.GONE);
                                    Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                });

                    });


                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                });
    }
}