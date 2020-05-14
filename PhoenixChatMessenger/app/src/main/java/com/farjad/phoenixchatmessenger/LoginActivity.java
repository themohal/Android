package com.farjad.phoenixchatmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    MaterialEditText email,password;
    Button btn_login;
    FirebaseAuth auth;
    DatabaseReference reference;
    TextView forgot_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setHomeButtonEnabled(true);

        email =findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login=findViewById(R.id.btn_login);
        forgot_password =findViewById(R.id.forgot_password);
        auth = FirebaseAuth.getInstance();
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));

            }
        });
    }

    public void loginUser(View view) {
        String txt_email = Objects.requireNonNull(email.getText()).toString();
        String txt_password = Objects.requireNonNull(password.getText()).toString();
        if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)){
            Toast.makeText(this, "Email or Password field is empty", Toast.LENGTH_SHORT).show();
        }else {
            auth.signInWithEmailAndPassword(txt_email,txt_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
