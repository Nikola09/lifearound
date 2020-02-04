package com.example.lifearound.ui.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lifearound.R;
import com.example.lifearound.data.UsersData;
import com.example.lifearound.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.support.constraint.Constraints.TAG;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String password_confirm;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email="";
        password="";
        password_confirm="";
        name="";
        mAuth = FirebaseAuth.getInstance();
        Button reg_btn = (Button) findViewById(R.id.register_button);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText passwordCEditText = findViewById(R.id.password_confirm);
        final EditText nameEditText = findViewById(R.id.name);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email=emailEditText.getText().toString();
                password=passwordEditText.getText().toString();
                password_confirm=passwordCEditText.getText().toString();
                name=nameEditText.getText().toString();
                if(email.contains("@") && email.length()>5 && password.length()>5 && password.equals(password_confirm)
                        && name.length()>2 && email.contains("."))
                {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        createUser(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Registration failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void createUser(FirebaseUser fu)
    {
        LoggedInUser uu=new LoggedInUser(fu.getUid(),name);
        UsersData.getInstance().addNewUser(uu);
        finish();
    }


}
