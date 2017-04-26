package com.example.galatasaray.firebasedeneme;

import java.util.Date;
import java.util.Map;

/**
 * Created by Galatasaray on 7.04.2017.
 */

public class Sikayetler {
    public String ApartAdi;
    public String Name;
    public  String Daire;
    public String Telefon;
    public Map<SikayetData,String> Sikayetler;
    public String UserId;
    public Date date;

    public Sikayetler(){

    }
    public Sikayetler(String daire,String name,Map<SikayetData,String> sikayetler,String telefon){
        this.Name=name;
        this.Daire=daire;
        this.Telefon=telefon;
        this.Sikayetler=sikayetler;

    }

}
