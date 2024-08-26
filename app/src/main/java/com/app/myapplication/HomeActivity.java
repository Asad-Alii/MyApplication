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

import com.app.myapplication.models.User;
import com.app.myapplication.utils.PrefUtils;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    User user;
    String activityName;

    MaterialButton logoutBtn;

    PrefUtils pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        activityName = getIntent().getStringExtra("activity");
        setContentView(R.layout.activity_home);

        pref = PrefUtils.getInstance(this);

        logoutBtn = this.findViewById(R.id.logout_btn);

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
    }
}