package com.app.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.app.myapplication.utils.PrefUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {

    Channel channel;

    TextView userName;
    ImageButton backBtn;

    RecyclerView chatListView;
    ChatListAdapter adapter;
    ArrayList<Chat> chats;

    FirebaseFirestore firestore;

    PrefUtils pref;

    TextInputEditText messageView;

    FloatingActionButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channel = (Channel) getIntent().getSerializableExtra("channel");
        setContentView(R.layout.activity_chats);

        firestore = FirebaseFirestore.getInstance();
        pref = PrefUtils.getInstance(this);

        chats = new ArrayList<>();

        userName = this.findViewById(R.id.user_name);
        backBtn = this.findViewById(R.id.back_btn);
        chatListView = this.findViewById(R.id.chat_list_view);
        messageView = this.findViewById(R.id.message);
        sendBtn = this.findViewById(R.id.send_btn);

        chatListView.setLayoutManager(new LinearLayoutManager(this));

        userName.setText(channel.getUser().getName());

        getChats();

        backBtn.setOnClickListener(v -> finish());

        sendBtn.setOnClickListener(v -> {

            if(!messageView.getText().toString().isEmpty()){
                sendMessage(messageView.getText().toString());
            }
        });
    }

    public void getChats(){

        firestore.collection(Constants.channelsCollection).document(channel.getChannelId())
                .collection(Constants.chatsCollection)
                .addSnapshotListener((value, error) -> {

                    for(DocumentSnapshot snapshot : value.){
                        Chat chat = snapshot.toObject(Chat.class);
                        if (chat != null && chat.getCreatedAt() != null) {
                            chats.add(chat);
                        } else {
                            // Handle the case where the date or chat is null
                            Log.e("ChatError", "Null chat or date found in Firestore");
                        }

//                        chats.add(snapshot.toObject(Chat.class));
                        //adapter.notifyItemChanged(chats.size() - 1);
                    }

                    adapter = new ChatListAdapter(this, chats);
                    chatListView.setAdapter(adapter);

                });
    }

    public void sendMessage(String message){

        String chatId = firestore.collection(Constants.chatsCollection).document().getId();

        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("chatId", chatId);
        chatMap.put("message", message);
        chatMap.put("createdAt", FieldValue.serverTimestamp());
        chatMap.put("updatedAt", FieldValue.serverTimestamp());
        chatMap.put("isRead", false);
        chatMap.put("type", "text");
        chatMap.put("authorId", pref.getUser().getId());

        firestore.collection(Constants.channelsCollection).document(channel.getChannelId())
                .collection(Constants.chatsCollection).document(chatId).set(chatMap)
                .addOnSuccessListener(unused1 -> {
//                    loader.setVisibility(View.GONE);
                    messageView.setText(null);
                    Toast.makeText(this, "Message Sent Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
//                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}