package com.example.galatasaray.firebasedeneme;

import android.support.annotation.NonNull;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Galatasaray on 8.04.2017.
 */

public class SikayetData implements Comparable<SikayetData> {
    public String Sikayet;
    public  Long Tarih;
    public String Yapildi;

    public SikayetData(){

    }
    public SikayetData(String sikayet,Long tarih,String yapildi ){
        this.Sikayet=sikayet;
        this.Tarih=tarih;
        this.Yapildi=yapildi;
    }
    @Override
    public int hashCode()
    {
        return Sikayet.hashCode();
    }


    @Override
    public boolean equals(Object o)
    {
        return this.Sikayet.equals(((SikayetData)o).Sikayet);
    }

    @Override
    public int compareTo(@NonNull SikayetData o) {
        Timestamp date=new Timestamp(this.Tarih);
        Timestamp date2=new Timestamp(o.Tarih);
        if(date.compareTo(date2)==-1)
            return 1;
        else if(date.compareTo(date2)==1)
            return -1;
        else
            return 0;
    }
}
