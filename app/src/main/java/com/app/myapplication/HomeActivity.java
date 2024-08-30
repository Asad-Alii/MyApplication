package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.QuickContactBadge;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    User user;
    String activityName;

    MaterialButton logoutBtn;

    PrefUtils pref;

    RecyclerView userListView;
    private UserListAdapter adapter;
    ArrayList<User> users;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        activityName = getIntent().getStringExtra("activity");
        setContentView(R.layout.activity_home);

        pref = PrefUtils.getInstance(this);

        logoutBtn = this.findViewById(R.id.logout_btn);
        userListView = this.findViewById(R.id.user_list);

        firestore = FirebaseFirestore.getInstance();

        users = new ArrayList<>();

        userListView.setLayoutManager(new LinearLayoutManager(this));

//        Toast.makeText(this, user.getName(), Toast.LENGTH_SHORT).show();

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

        firestore.collection(Constants.usersCollection).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

//                    int count = queryDocumentSnapshots.getDocuments().size();
//                    Toast.makeText(this, "Count: " + count, Toast.LENGTH_SHORT).show();

                    //for(int i = 0; i < 10; i++){}

                    for(DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()){
                        users.add(snapshot.toObject(User.class));
                    }

                    adapter = new UserListAdapter(this, users);
                    userListView.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                });
    }
}