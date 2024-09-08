package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapplication.models.User;
import com.app.myapplication.utils.Constants;
import com.app.myapplication.utils.PrefUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextView regBtn;
    TextInputEditText email;
    TextInputEditText password;
    MaterialButton button;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    PrefUtils pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        regBtn = this.findViewById(R.id.reg_btn);
        email = this.findViewById(R.id.email);
        password = this.findViewById(R.id.password);
        button = this.findViewById(R.id.button);

        pref = PrefUtils.getInstance(LoginActivity.this);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        button.setOnClickListener(view -> {

            signinUser();

        });

        regBtn.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);

//            Toast.makeText(this, "Button is clicked", Toast.LENGTH_SHORT).show();
        });

    }

    private void signinUser(){

        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                firestore.collection(Constants.usersCollection).document(authResult.getUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        User user = documentSnapshot.toObject(User.class);

                        if(pref.setUser(user)){
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("user", user);
                            intent.putExtra("activity", "Login Activity");
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }
}