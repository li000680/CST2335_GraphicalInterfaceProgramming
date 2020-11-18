package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;

public class SearchResult extends AppCompatActivity {

    private String country;
    private String province;
    private int caseNumber;
    private String date;
    private long id;

    public SearchResult(){}
    public SearchResult( String c,String p,int ca,String d)
    {
        country=c;
        province=p;
        caseNumber=ca;
        date=d;
    }

    public String getCountry() {
        return country;
    }
    public String getProvince() {
        return province;
    }
    public int getCase() {
        return caseNumber;
    }
    public String getDate() {
        return date;
    }
    public long getId() {return id; }
}
