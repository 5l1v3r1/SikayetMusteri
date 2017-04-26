package com.example.galatasaray.firebasedeneme;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.galatasaray.firebasedeneme.MainActivity.firebaseDatabase;
import static com.example.galatasaray.firebasedeneme.MainActivity.mAuth;

public class SikayetRec extends AppCompatActivity {
    private RecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressbar;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mRef;
    private Query query;
    private FirebaseUser user;
    private Boolean userScrolled=false;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private  int itemCount=0;

    ArrayList<SikayetData> mSikayetList;
    Map<String,String> mSikayetAndId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sikayet_rec);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recList2);
        mProgressbar= (ProgressBar)findViewById(R.id.progress_bar);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSikayetList=new ArrayList<SikayetData>();
        mSikayetAndId=new HashMap<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SikayetRec.this,YeniSikayet.class));
                //finish();
            }
        });
        user=mAuth.getCurrentUser();
        mRef =firebaseDatabase.getInstance().getReference("Sikayet/"+user.getUid()+"/Sikayetler");
        query=firebaseDatabase.getInstance().getReference("Sikayet/"+user.getUid()+"/Sikayetler");


        mAdapter=new RecyclerAdapter(mSikayetList,mSikayetAndId);
        mRecyclerView.setAdapter(mAdapter);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemCount= (int)dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        query.orderByChild("Tarih").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSikayetList.clear();
                mSikayetAndId.clear();
                for (Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator(); i.hasNext(); ) {
                    DataSnapshot snap=i.next();
                    SikayetData sikayetData= snap.getValue(SikayetData.class);
                    mSikayetList.add(sikayetData);
                    mSikayetAndId.put(sikayetData.Sikayet,snap.getKey());
                }
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        itemCount= (int)dataSnapshot.getChildrenCount();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Collections.sort(mSikayetList);
                mAdapter=new RecyclerAdapter(mSikayetList,mSikayetAndId);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressbar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*query.orderByChild("Tarih").limitToLast(5).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                SikayetData sikayetData= dataSnapshot.getValue(SikayetData.class);
                int sa=mSikayetList.indexOf(sikayetData);
                if(sa==-1){
                    mSikayetList.add(sikayetData);
                Collections.sort(mSikayetList);
                mSikayetAndId.put(dataSnapshot.child("Sikayet").getValue().toString(),dataSnapshot.getKey().toString());
                mAdapter.notifyItemInserted(1);

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                SikayetData sikayetData= dataSnapshot.getValue(SikayetData.class);
                int position = mSikayetList.indexOf(sikayetData);
                mSikayetList.remove(sikayetData);
                Collections.sort(mSikayetList);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSikayetList.size() == 0) {
            requestPhoto();
        }
    }
    @Override
    public void onBackPressed() {
        mAuth.signOut();
        finish();
    }
    private void requestPhoto() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager
                        .findFirstVisibleItemPosition();



                // Now check if userScrolled is true and also check if
                // the item is end then update recycler view and set
                // userScrolled to false
                if (userScrolled
                        && (visibleItemCount + pastVisiblesItems) == totalItemCount && totalItemCount<itemCount) {
                    userScrolled = false;
                    mProgressbar.setVisibility(View.VISIBLE);
                    query.orderByChild("Tarih").limitToLast(totalItemCount+5).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mSikayetList.clear();
                            for (Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator(); i.hasNext(); ) {
                                DataSnapshot snap=i.next();
                                mSikayetList.add(snap.getValue(SikayetData.class));
                            }
                            Collections.sort(mSikayetList);
                            mAdapter.notifyItemInserted(mSikayetList.size());
                            mProgressbar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

}
