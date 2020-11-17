package com.example.cst2335_graphicalinterfaceprogramming;

public class SearchResult {

    private String country;
    private String province;
    private int caseNumber;
    private String date;
    private String fromDate;
    private String endDate;
    private long id;

//    public SearchResult(String c, String p,String ca,String d,long i)
//    {
//        country =c;
//        province=p;
//        caseNumber=ca;
//        date=d;
//        id = i;
//    }

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
    public String getFromDate() {
        return fromDate;
    }
    public String getEndDate() {
        return endDate;
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
