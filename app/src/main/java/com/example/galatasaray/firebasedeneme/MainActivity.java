package com.example.galatasaray.firebasedeneme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    CheckBox chkRemember;
    Button btnKayit;
    EditText txtEmail;
    EditText txtPassword;
    private static final String TAG = "EmailPassword";
    public static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    TextView text;
    ProgressDialog progress;
    public static final FirebaseAuth mAuth =FirebaseAuth.getInstance();;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static Sikayetler sikayet = new Sikayetler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        long time2= time.getTime();
        Date date= new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateFormat=format.format(time2);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if(progress==null)
                        progress = ProgressDialog.show(MainActivity.this, "Giriş",
                                "Giriş yapılıyor", true);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
              //  getDataFromDatabase(user);

                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
        chkRemember=(CheckBox)findViewById(R.id.chkRemember);
        btnKayit=(Button)findViewById(R.id.btnKayit);
        txtEmail=(EditText)findViewById(R.id.txtEmail);
        txtPassword=(EditText)findViewById(R.id.txtPassword);

        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        final String username = pref.getString(PREF_USERNAME, null);
        final String password = pref.getString(PREF_PASSWORD, null);
        if (username == null || password == null) {
            chkRemember.setChecked(false);
        }
        else{
            chkRemember.setChecked(true);
            txtEmail.setText(username);
            txtPassword.setText(password);
        }
        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=txtEmail.getText().toString();
                String password=txtPassword.getText().toString();
                if(email==null||password==null||email.equals("")||password.equals("")){
                    Toast.makeText(MainActivity.this, "Email ve password alanları boş bırakılamaz",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!email.contains("@"))
                {
                    Toast.makeText(MainActivity.this, "Hatalı email adresi",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(password.length()<8)
                {
                    Toast.makeText(MainActivity.this, "Şifre 8 karakterden küçük olamaz",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(progress==null)
                    progress = ProgressDialog.show(MainActivity.this, "Kayıt",
                            "Kayıt yapılıyor", true);

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                            Toast.makeText(MainActivity.this, "Kayıt başarısız",
                                    Toast.LENGTH_SHORT).show();
                        else if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Kayıt başarılı",
                                    Toast.LENGTH_SHORT).show();
                            if(chkRemember.isChecked()) {
                                String username = txtEmail.getText().toString();
                                String password = txtPassword.getText().toString();
                                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                        .edit()
                                        .putString(PREF_USERNAME, username)
                                        .putString(PREF_PASSWORD, password)
                                        .apply();
                            }
                        }
                    }
                });
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void login(View view)
    {
        String email=txtEmail.getText().toString();
        String password=txtPassword.getText().toString();
        if(email==null||password ==null||email.equals("")||password.equals(""))
            return;
        if(chkRemember.isChecked()) {
             email = txtEmail.getText().toString();
             password = txtPassword.getText().toString();
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(PREF_USERNAME, email)
                    .putString(PREF_PASSWORD, password)
                    .apply();
        }
        else
        {
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(PREF_USERNAME, null)
                    .putString(PREF_PASSWORD, null)
                    .apply();
        }
        signIn(email,password);
    }

    private void userPage()
    {
        if(progress!=null){
                progress.dismiss();
           startActivity(new Intent(this, SikayetRec.class));
        }
    }
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
       /* if (!validateForm()) {
            return;
        }*/

        progress = ProgressDialog.show(this, "Giriş",
                "Giriş yapılıyor", true);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Auth failed",
                                    Toast.LENGTH_SHORT).show();
                        }


                        // [START_EXCLUDE]
                        /*if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }*/


                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }
    public void signOut(View view)
    {
        signOut();
    }
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
        sikayet=null;
    }
    private void updateUI(FirebaseUser user) {

        if (user != null) {
            DatabaseReference ref =firebaseDatabase.getInstance().getReference("Sikayet/"+user.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    sikayet.Name=dataSnapshot.child("Name").getValue(String.class);
                    sikayet.ApartAdi=dataSnapshot.child("Apart Adi").getValue(String.class);
                    sikayet.Daire=dataSnapshot.child("Daire").getValue(String.class);
                    sikayet.Telefon=dataSnapshot.child("Telefon").getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            findViewById(R.id.btnLogin).setVisibility(View.GONE);
            findViewById(R.id.btnKayit).setVisibility(View.GONE);
            userPage();
        } else {
            findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
            findViewById(R.id.btnKayit).setVisibility(View.VISIBLE);
        }
    }
}
