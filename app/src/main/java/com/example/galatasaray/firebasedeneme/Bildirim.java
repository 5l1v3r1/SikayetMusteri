package com.example.galatasaray.firebasedeneme;

/**
 * Created by Galatasaray on 9.04.2017.
 */

public class Bildirim {
    public String Name;
    public String DaireNo;
    public String Okundu;
    public String SikayetId;
    public String AdminId;
    public String UserId;
    public Bildirim(){}
    public Bildirim(String name,String daireNo,String okundu,String sikayetId,String adminId,String userId){
        Name=name;
        DaireNo=daireNo;
        Okundu=okundu;
        SikayetId=sikayetId;
        AdminId=adminId;
        UserId=userId;
    }
}
