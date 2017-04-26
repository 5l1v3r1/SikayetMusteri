package com.example.galatasaray.firebasedeneme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static com.example.galatasaray.firebasedeneme.MainActivity.mAuth;
import static com.example.galatasaray.firebasedeneme.MainActivity.firebaseDatabase;
import static com.example.galatasaray.firebasedeneme.MainActivity.sikayet;

public class YeniSikayet extends AppCompatActivity {
    EditText txtIsim;
    TextView txtApart;
    EditText txtDaireNo;
    EditText txtTelefon;
    EditText txtSikayet;
    Button btnGonder;
    ProgressBar progressBar;
    ArrayList<String> sikayetIdler;
    Map<String,String> apartMap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni_sikayet);
        txtIsim =(EditText)findViewById(R.id.txtIsim);
        txtApart=(TextView)findViewById(R.id.txtApart);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar2);
        DatabaseReference myRef = firebaseDatabase.getReference("ApartListesi");
        final ArrayList<String> apartListesi = new ArrayList<String>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    apartMap.put(snap.child("Apart").getValue().toString(),snap.child("Uid").getValue().toString());
                    apartListesi.add(snap.child("Apart").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        txtApart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopup(apartListesi);
            }
        });
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtDaireNo =(EditText)findViewById(R.id.txtDaireNo);
        txtTelefon =(EditText)findViewById(R.id.txtTelefon);
        txtSikayet =(EditText)findViewById(R.id.txtSikayet);
        btnGonder = (Button)findViewById(R.id.btnGonder);
        txtIsim.setText(sikayet.Name);
        txtDaireNo.setText(sikayet.Daire);
        txtTelefon.setText(sikayet.Telefon);
        txtApart.setText(sikayet.ApartAdi);
        final String clickedSikayet = getIntent().getStringExtra("clickedSikayet");
        final String clickedSikayetId=getIntent().getStringExtra("clickedSikayetId");
        txtSikayet.setText(clickedSikayet);
        txtSikayet.requestFocus();
        if(clickedSikayet!=null)
            btnGonder.setText("Güncelle");
        sikayetIdler= new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseDatabase.getInstance().getReference("Sikayet/"+user.getUid()+"/Sikayetler").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator(); i.hasNext(); ) {
                    DataSnapshot snap=i.next();
                    String sikayet=snap.child("Sikayet").getValue().toString();
                    if(sikayet.equals(clickedSikayet)){
                    String sikayetData= snap.getKey();
                    sikayetIdler.add(sikayetData);
                    break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String isim,apartAdi,daireNo,telefon,sikayet;
                isim=txtIsim.getText().toString();
                apartAdi=txtApart.getText().toString();
                daireNo=txtDaireNo.getText().toString();
                telefon=txtTelefon.getText().toString();
                sikayet=txtSikayet.getText().toString();
                FirebaseUser user = mAuth.getCurrentUser();
        String uId=user.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("Sikayet");
        DatabaseReference childRef1 = myRef.child(uId);
        DatabaseReference childRef2 = childRef1.child("Name");
        childRef2.setValue(isim);
        childRef2 =childRef1.child("Daire");
        childRef2.setValue(daireNo);
        childRef2 =childRef1.child("Telefon");
        childRef2.setValue(telefon);
        childRef2 =childRef1.child("Apart Adi");
        childRef2.setValue(apartAdi);
        childRef2 =childRef1.child("Sikayetler");
                Timestamp time = new Timestamp(System.currentTimeMillis());
                long timeStamp= time.getTime();
                progressBar.setVisibility(View.VISIBLE);
        if(clickedSikayetId!=null)
        {
            childRef2.child(sikayetIdler.get(0)).setValue(new SikayetData(sikayet,timeStamp,"False")).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(YeniSikayet.this, "Şikayet başarıyla güncellendi",
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                      //  startActivity(new Intent(YeniSikayet.this,SikayetRec.class));
                        finish();
                    } else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(YeniSikayet.this, "Şikayet gönderilemedi",
                                Toast.LENGTH_SHORT).show();}
                }
            });
            Bildirim bild=new Bildirim(isim,daireNo,"False",sikayetIdler.get(0),apartMap.get(apartAdi),uId);
            myRef = firebaseDatabase.getReference("Bildirimler/"+bild.AdminId);
            myRef.push().setValue(bild);
        }
        else {
                childRef2.push().setValue(new SikayetData(sikayet,timeStamp,"False")).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(YeniSikayet.this, "Şikayet başarıyla gönderildi",
                                    Toast.LENGTH_SHORT).show();
                           // startActivity(new Intent(YeniSikayet.this,SikayetRec.class));
                            finish();
                        } else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(YeniSikayet.this, "Şikayet gönderilemedi",
                                    Toast.LENGTH_SHORT).show();}
                    }
                });
                String sikayetId=childRef2.getKey();

            Bildirim bild=new Bildirim(isim,daireNo,"False",sikayetId,apartMap.get(apartAdi),uId);
            myRef = firebaseDatabase.getReference("Bildirimler/"+bild.AdminId);
            myRef.push().setValue(bild);
        }
            }
        });
    }
    private void createPopup(final ArrayList<String> list)
    {
        final CharSequence apartListesi[] = new CharSequence[list.size()];
        int counter=0;
        for(String apart : list){
            apartListesi[counter]=apart;
            counter++;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apart Seçimi");
        builder.setItems(apartListesi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtApart.setText(apartListesi[which]);
            }
        });
        builder.show();
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}
