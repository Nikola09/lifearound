package com.example.lifearound.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.lifearound.data.model.LoggedInUser;
import com.example.lifearound.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.concurrent.Executor;

import static android.support.constraint.Constraints.TAG;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private FirebaseAuth mAuth;
    private LoggedInUser defUser;

    public Result<LoggedInUser> login(String email, String password,FirebaseUser fu) {
        defUser=null;
        try {
            if(fu!=null)
            {
                LoggedInUser user= new LoggedInUser(fu.getUid(),fu.getDisplayName());
                return new Result.Success<>(user);
            }
            /*LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);*/
            /*if(defUser!=null)
                return new Result.Success<>(defUser);*/
            else
                throw new Exception("failed to log in");
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }

    }
//    public void updateDefUser(FirebaseUser fu)
//    {
//        defUser= new LoggedInUser(fu.getUid(),fu.getDisplayName());
//    }

    public void logout() {
        // TODO: revoke authentication
    }
}
