package com.uj.mybook.login_signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uj.mybook.R;
import com.uj.mybook.main.MainActivity;
import com.uj.mybook.main.User;

public class Signup extends AppCompatActivity {
    private EditText etFirstName, etLastName, etPassword, etEmail, etPhone;
    private String stFirstName, stLastName, stPassword, stEmail, stPhone, stCollege;
    private Spinner spnCollege;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private Button btnSignup;
    private FirebaseAuth auth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etFirstName = findViewById(R.id.firstName);
        etLastName = findViewById(R.id.lastName);
        etPassword = findViewById(R.id.password);
        etEmail = findViewById(R.id.editEmail);
        etPhone = findViewById(R.id.phone);
        spnCollege = findViewById(R.id.college_spinner);
        btnSignup = findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference("Users");

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.colleges, R.layout.signup_spinner_style);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCollege.setAdapter(spinnerAdapter);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(Signup.this);
                dialog.setMessage("Signup...");
                stFirstName = etFirstName.getText().toString();
                stLastName = etLastName.getText().toString();
                stPassword = etPassword.getText().toString();
                stEmail = etEmail.getText().toString();
                stPhone = etPhone.getText().toString();
                stCollege = spnCollege.getSelectedItem().toString();

                if (!checkUserInformation()) return;
                dialog.show();
                auth.createUserWithEmailAndPassword(stEmail, stPassword)
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = auth.getCurrentUser();
                                    String userId = currentUser.getUid();

                                    User user = new User(userId, stFirstName, stLastName, stPhone, stCollege);
                                    dbReference.child(userId).setValue(user);
                                    saveUserInformation(user);
                                    dialog.dismiss();
                                    startActivity(new Intent(Signup.this, MainActivity.class));
                                    finish();

                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(Signup.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
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

    public Boolean checkUserInformation() {
        Boolean x = true;
        if (stFirstName.isEmpty()) {
            etFirstName.setError("Please enter a valid name");
            x = false;
        }
        if (stLastName.isEmpty()) {
            etLastName.setError("Please enter a valid name");
            x = false;
        }
        if (stPassword.isEmpty()) {
            etPassword.setError("Please enter a valid password");
            x = false;
        } else if (stPassword.length() <= 6) {
            etPassword.setError("Password length must be larger than 6");
            x = false;
        }
        if (stPhone.isEmpty() || stPhone.length() != 10) {
            etPhone.setError("Please enter a valid phone number");
            x = false;
        }
        if (stEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(stEmail).matches()) {
            etEmail.setError("Please enter a valid email");
            x = false;
        }
        return x;
    }
}
