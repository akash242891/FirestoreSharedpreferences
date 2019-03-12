package com.example.akash.firestoresharedpreferences;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText username, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String name = username.getText().toString();
                    String pass = password.getText().toString();
                    if (name.isEmpty() && pass.isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please enter username and password!", Toast.LENGTH_SHORT).show();
                    }
                    else if (name.isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please enter username!", Toast.LENGTH_SHORT).show();
                    }
                    else if (pass.isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mAuth.signInWithEmailAndPassword(name,pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent questionintent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(questionintent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please enter correct email and password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.signOut();
    }
}
