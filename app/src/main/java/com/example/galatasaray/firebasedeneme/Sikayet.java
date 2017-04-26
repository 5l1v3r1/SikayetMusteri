package com.example.galatasaray.firebasedeneme;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.galatasaray.firebasedeneme.MainActivity.mAuth;
import static com.example.galatasaray.firebasedeneme.MainActivity.sikayet;

public class Sikayet extends AppCompatActivity {
    ListView gridSikayet;
    Button btnCikis;
    Button btnSikayet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sikayet);

        gridSikayet =(ListView)findViewById(R.id.listSikayet);

        btnCikis = (Button)findViewById(R.id.btnCikis);
        btnSikayet =(Button)findViewById(R.id.btnSikayet);
        final ListView listview = (ListView) findViewById(R.id.listSikayet);
        String[] values = getIntent().getStringArrayExtra("sikayetler");
        String[] tarih = getIntent().getStringArrayExtra("tarih");

        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listTarih = new ArrayList<String>();
        if(values!=null){
            for (int i = 0; i < values.length; ++i) {
                list.add(values[i]);
                listTarih.add(tarih[i]);
            }
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);



        btnCikis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mAuth.signOut();
                    finish();
            }
        });
        btnSikayet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sikayet.this,YeniSikayet.class);
                startActivity(intent);
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sikayet1 = list.get((int)id);
                String sikayetId="";
                for (SikayetData value : sikayet.Sikayetler.keySet()
                     ) {
                    if(value.Sikayet.equals(sikayet1))
                        sikayetId=sikayet.Sikayetler.get(value);
                }
                Intent intent = new Intent(Sikayet.this,YeniSikayet.class);
                intent.putExtra("clickedSikayet",sikayet1);
                intent.putExtra("clickedSikayetId",sikayetId);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        mAuth.signOut();
        finish();
    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
