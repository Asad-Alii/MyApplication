package com.app.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapplication.adapters.ChatListAdapter;
import com.app.myapplication.models.Channel;
import com.app.myapplication.models.Chat;
import com.app.myapplication.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChatsActivity extends AppCompatActivity {

    Channel channel;

    TextView userName;
    ImageButton backBtn;

    RecyclerView chatListView;
    ChatListAdapter adapter;
    ArrayList<Chat> chats;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channel = (Channel) getIntent().getSerializableExtra("channel");
        setContentView(R.layout.activity_chats);

        firestore = FirebaseFirestore.getInstance();

        chats = new ArrayList<>();

        userName = this.findViewById(R.id.user_name);
        backBtn = this.findViewById(R.id.back_btn);
        chatListView = this.findViewById(R.id.chat_list_view);

        chatListView.setLayoutManager(new LinearLayoutManager(this));

        userName.setText(channel.getUser().getName());

        getChats();

        backBtn.setOnClickListener(v -> finish());
    }

    public void getChats(){

        firestore.collection(Constants.channelsCollection).document(channel.getChannelId())
                .collection(Constants.chatsCollection).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        chats.add(snapshot.toObject(Chat.class));
                    }

                    adapter = new ChatListAdapter(this, chats);
                    chatListView.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                });
    }
}