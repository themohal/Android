package com.farjad.phoenixchatmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    MaterialEditText sendEmail;
    Button resetBtn;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Reset Password");
        getSupportActionBar().setHomeButtonEnabled(true);
        sendEmail =  findViewById(R.id.send_email);
        resetBtn= findViewById(R.id.btn_reset);
        firebaseAuth = FirebaseAuth.getInstance();
        resetBtn.setOnClickListener(v -> {
            String email =  Objects.requireNonNull(sendEmail.getText()).toString();
            if(email.equals("")){
                Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            }else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Pleas check your email inbox", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }else {
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(ResetPasswordActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
