package com.example.galatasaray.firebasedeneme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

import static com.example.galatasaray.firebasedeneme.MainActivity.mAuth;

public class SikayetRecView extends AppCompatActivity {

    private RecyclerView recList;
    private DatabaseReference mRef;
    private Query query;
    private FirebaseUser user;
    private Boolean userScrolled=false;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    LinearLayoutManager mLayoutManager;
    private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";
    int itemCount=0;
    private FirebaseRecyclerAdapter<SikayetData,SikayetViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sikayet_rec_view);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recList=(RecyclerView)findViewById(R.id.recList);

        recList.setHasFixedSize(true);

        recList.setLayoutManager(new LinearLayoutManager(this));

        user=mAuth.getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference("Sikayet/"+user.getUid()+"/Sikayetler");


    }



    @Override
    protected void onStart() {
        super.onStart();
        
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               itemCount= (int)dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        query=mRef.limitToFirst(5);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SikayetData, SikayetViewHolder>(
                SikayetData.class,
                R.layout.list_layout,
                SikayetViewHolder.class,
                query
        ) {

            @Override
            protected void populateViewHolder(SikayetViewHolder sikayetViewHolder, SikayetData sikayetData, int i) {

               // sikayetViewHolder.setTarih(sikayetData.Tarih);
              //  sikayetViewHolder.setSikayet(sikayetData.Sikayet,sikayetData.Yapildi);

            }
        };
        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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

                    query=mRef.limitToFirst(totalItemCount+5);
                     firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SikayetData, SikayetViewHolder>(
                            SikayetData.class,
                            R.layout.list_layout,
                            SikayetViewHolder.class,
                            query
                    ) {

                        @Override
                        protected void populateViewHolder(SikayetViewHolder sikayetViewHolder, SikayetData sikayetData, int i) {
                          //  sikayetViewHolder.setTarih(sikayetData.Tarih);
                         //   sikayetViewHolder.setSikayet(sikayetData.Sikayet,sikayetData.Yapildi);
                        }
                    };

                    recList.setAdapter(firebaseRecyclerAdapter);
                     mLayoutManager.scrollToPositionWithOffset(pastVisiblesItems,0);


                    //

                    //firebaseRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        recList.setAdapter(firebaseRecyclerAdapter);
        recList.setLayoutManager(mLayoutManager);

    }

    public static class SikayetViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        public SikayetViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setTarih(String tarih){
            TextView txtTarih=(TextView)mView.findViewById(R.id.txtRecTarih);
            txtTarih.setText(tarih);
        }
        public void setSikayet(String sikayet,String yapildi){
            TextView txtSikayet=(TextView)mView.findViewById(R.id.txtRecSikayet);
            txtSikayet.setTextColor(ContextCompat.getColor(mView.getContext(),R.color.colorWhite));
            txtSikayet.setText(sikayet);
            if(Boolean.valueOf(yapildi))
                txtSikayet.setBackgroundColor(ContextCompat.getColor(mView.getContext(),R.color.colorBlue));
            else
                txtSikayet.setBackgroundColor(ContextCompat.getColor(mView.getContext(),R.color.colorRed));
        }

    }
}
