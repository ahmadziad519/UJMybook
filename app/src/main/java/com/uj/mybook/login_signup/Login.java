package com.uj.mybook.login_signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uj.mybook.R;
import com.uj.mybook.main.MainActivity;
import com.uj.mybook.main.User;


public class Login extends AppCompatActivity {
    private TextView tvToSignUp;
    private EditText etEmail, etPassword;
    private String stEmail, stPassword;
    private Button btnLogin;
    private FirebaseAuth auth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvToSignUp = findViewById(R.id.toSignUp);
        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        auth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

        tvToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(Login.this);
                dialog.setMessage("Login...");
                stEmail = etEmail.getText().toString();
                stPassword = etPassword.getText().toString();

                //check if email and password follow the rules
                if (!checkEmailAndPassword()) {
                    return;
                }
                dialog.show();

                auth.signInWithEmailAndPassword(stEmail, stPassword).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_LONG).show();

                        } else {

                            dbReference = FirebaseDatabase.getInstance().getReference("Users");
                            String userID = FirebaseAuth.getInstance().getUid();
                            dbReference.child(userID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    saveUserInformation(user);
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });

            }
        });


    }

    private void saveUserInformation(User user) {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("id", user.getId());
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("number", user.getNumber());
        editor.putString("college", user.getCollege());
        editor.putString("imageUrl", user.getImageUrl());
        editor.commit();

    }

    private boolean checkEmailAndPassword() {
        boolean x = true;
        if (stEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(stEmail).matches()) {
            etEmail.setError("Please enter a valid email");
            x = false;
        }
        if (TextUtils.isEmpty(stPassword) || stPassword.length() < 6) {
            etPassword.setError("Please enter a valid password");
            x = false;
        }
        return x;
    }
}
