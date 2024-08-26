package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapplication.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SignupActivity extends AppCompatActivity {

    MaterialButton registerbutton;
    TextInputEditText signupemail, signuppass, name, phone;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    CircleImageView profilePic;
    ImageView cameraBtn;

    EasyImage easyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        registerbutton=this.findViewById(R.id.register_btn);
        signupemail=this.findViewById(R.id.email);
        signuppass=this.findViewById(R.id.password);
        name = this.findViewById(R.id.name);
        phone = this.findViewById(R.id.phone);
        profilePic = this.findViewById(R.id.profile_pic);
        cameraBtn = this.findViewById(R.id.camera_btn);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        easyImage = new EasyImage.Builder(this)
                .setChooserTitle("Pick Profile Image")
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .allowMultiple(false)
                .build();

        Glide.with(SignupActivity.this).load("https://img.freepik.com/free-photo/portrait-young-teen-tourist-visiting-great-wall-china_23-2151261879.jpg").into(profilePic);

        cameraBtn.setOnClickListener(v -> {

            easyImage.openGallery(this);
//            Toast.makeText(this, "Camera button clicked!", Toast.LENGTH_SHORT).show();
        });

    registerbutton.setOnClickListener(v ->{

        signUpUser();
    });
    }
    private void signUpUser(){
        String email = signupemail.getText().toString();
        String password = signuppass.getText().toString();
        String name = this.name.getText().toString();
        String phone = this.phone.getText().toString();

        if (email.isEmpty())
        {
            Toast.makeText(SignupActivity.this, "Email is empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( password.isEmpty() || password.length() < 8)
        {
            Toast.makeText(SignupActivity.this, "Either password is empty or less than 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", authResult.getUser().getUid());
                map.put("name", name);
                map.put("phone", phone);
                map.put("email", email);
                firestore.collection(Constants.usersCollection).document(authResult.getUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(SignupActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                Intent in = new Intent(SignupActivity.this, HomeActivity.class);
                                startActivity(in);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        });



    }
}