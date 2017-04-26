package com.example.galatasaray.firebasedeneme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;

import static com.example.galatasaray.firebasedeneme.MainActivity.firebaseDatabase;
import static com.example.galatasaray.firebasedeneme.MainActivity.mAuth;

/**
 * Created by Galatasaray on 12.04.2017.
 */

public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.SikayetViewHolder> {
    private ArrayList<SikayetData> mSikayet;
    public static Map<String,String> mSikayetAndId ;
    public RecyclerAdapter(ArrayList<SikayetData> sikayet,Map<String,String> sikayetAndId) {
        mSikayet = sikayet;
        mSikayetAndId=sikayetAndId;
    }
    @Override
    public RecyclerAdapter.SikayetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_layout, parent, false);
        return new SikayetViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.SikayetViewHolder holder, int position) {
            final SikayetData itemSikayet = mSikayet.get(position);
            holder.setTarih(itemSikayet.Tarih);
            holder.setSikayet(itemSikayet.Sikayet, itemSikayet.Yapildi);
    }

    @Override
    public int getItemCount() {
        return mSikayet.size();
    }
    public static class SikayetViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        View mView;
        TextView txtSikayet;
        Context context;

        public SikayetViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtSikayet = (TextView) mView.findViewById(R.id.txtRecSikayet);
            txtSikayet.setOnClickListener(this);
            txtSikayet.setOnLongClickListener(this);

        }

        public void setTarih(Long tarih) {
            TextView txtTarih = (TextView) mView.findViewById(R.id.txtRecTarih);
            long timeStamp = tarih;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            txtTarih.setText(format.format(timeStamp));
        }

        public void setSikayet(String sikayet, String yapildi) {

            txtSikayet.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.colorWhite));
            txtSikayet.setText(sikayet);
            if (Boolean.valueOf(yapildi))
                txtSikayet.setBackgroundColor(ContextCompat.getColor(mView.getContext(), R.color.colorBlue));
            else
                txtSikayet.setBackgroundColor(ContextCompat.getColor(mView.getContext(), R.color.colorRed));
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == txtSikayet.getId()) {
                String s = txtSikayet.getText().toString();
                context = itemView.getContext();
                Intent yeniSikayet = new Intent(context, YeniSikayet.class);
                yeniSikayet.putExtra("clickedSikayet", txtSikayet.getText().toString());
                yeniSikayet.putExtra("clickedSikayetId", String.valueOf(getAdapterPosition()));
                context.startActivity(yeniSikayet);
            } else {

            }
         /*  Context context = itemView.getContext();
            Intent showPhotoIntent = new Intent(context, YeniSikayet.class);
            showPhotoIntent.putExtra("clickedSikayet", );
            context.startActivity(showPhotoIntent);*/
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == txtSikayet.getId()) {
                context = itemView.getContext();
                String key = mSikayetAndId.get(txtSikayet.getText().toString());
                ArrayList<String> popup = new ArrayList<String>();
                popup.add("Sil");
                popup.add("Yapıldı olarak işaretle");
                createPopup(popup, key, context);
                return true;
            }
            return false;
        }

        private void createPopup(final ArrayList<String> list, final String key, Context ctx) {
            final Context context = itemView.getContext();
            final CharSequence liste[] = new CharSequence[list.size()];
            int counter = 0;
            for (String item : list) {
                liste[counter] = item;
                counter++;
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("İşlem Seçimi");
            builder.setItems(liste, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Boolean result = false;
                    if (liste[which].equals("Sil")) {
                        SikayetIslemleri islem = new SikayetIslemleri();
                        islem.execute(key);
                    } else if (liste[which].equals("Yapıldı olarak işaretle"))
                        Toast.makeText(context, "Yapıldı işlemi", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Bi bok yok", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }

        private class SikayetIslemleri extends AsyncTask<String, String, String> {
            private String userId;
            private Boolean completed = false;
            ProgressDialog progressDialog;

            public SikayetIslemleri() {
                FirebaseUser user = mAuth.getCurrentUser();
                userId = user.getUid();
            }

            @Override
            protected String doInBackground(String... strings) {
                firebaseDatabase.getInstance().getReference("Sikayet/" + userId + "/Sikayetler/" + strings[0]).removeValue();
                publishProgress("Lütfen bekleyin");
                return "";
            }

            @Override
            protected void onPostExecute(String result) {
                // execution of result of Long time consuming operation
                Toast.makeText(context, "Silme başarılı", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }


            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(context,
                        "ProgressDialog",
                        "Bekleyiniz");
            }
        }
    }
}

