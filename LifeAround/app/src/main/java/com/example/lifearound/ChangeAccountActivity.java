package com.example.lifearound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lifearound.data.UsersData;
import com.google.firebase.auth.FirebaseUser;

public class ChangeAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);

        final EditText etstatus=(EditText) findViewById(R.id.newstatus);

        etstatus.setText(UsersData.getInstance().getMe().getStatus());
        Button changeStatus = (Button) findViewById(R.id.btn_newstatus);
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status=etstatus.getText().toString();
                if (!UsersData.getInstance().getMe().getStatus().equals(status))
                UsersData.getInstance().updateStatus(status);
                finish();
            }
        });

        Button cancel = (Button) findViewById(R.id.btn_cancel_change);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }
}

/*
//final EditText etpassword=(EditText) findViewById(R.id.newpassword);
Button changePassword = (Button) findViewById(R.id.btn_newpassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {

        String password=etpassword.getText().toString();

        //if (!UsersData.getInstance().getMe().getStatus().equals(status))
        //firebase updatePassword
        finish();
        }
        });*/
