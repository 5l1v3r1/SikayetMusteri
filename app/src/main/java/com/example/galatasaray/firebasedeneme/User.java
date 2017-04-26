package com.example.galatasaray.firebasedeneme;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Galatasaray on 8.04.2017.
 */

public class User {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    TextView text;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String Name;
    public String Daire;
    public String Telefon;
    public String[] Sikayetler;
    private String TAG ="EmailPassword";
    public User(String email,String password){

    }

}
