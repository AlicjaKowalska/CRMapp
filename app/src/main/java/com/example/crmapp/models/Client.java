package com.example.crmapp.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Client implements Serializable {

    private int _ID;
    private String Name;
    private String LastName;
    private int Building;
    private String Street;
    private String City;
    private String PostalCode;
    private float GeoLat; //szerokość geograficzna
    private float GeoLon; //długość geograficzna
    private Bitmap Image;
    private int Activity;

    public Client(){}

    public Client(String Name, String LastName, int Building, String Street, String City, String PostalCode, float GeoLat, float GeoLon, Bitmap Image, int Activity){
        this.Name=Name;
        this.LastName=LastName;
        this.Building=Building;
        this.Street=Street;
        this.City=City;
        this.PostalCode=PostalCode;
        this.GeoLat=GeoLat;
        this.GeoLon=GeoLon;
        this.Image=Image;
        this.Activity=Activity;
    }

    public int get_ID(){ return _ID; }
    public void set_ID(int _ID) { this._ID = _ID; }

    public String getName(){ return Name; }
    public void setName(String Name) { this.Name = Name; }

    public String getLastName(){ return LastName; }
    public void setLastName(String LastName) { this.LastName = LastName; }

    public int getBuilding(){ return Building; }
    public void setBuilding(int Building){ this.Building = Building; }

    public String getStreet(){ return Street; }
    public void setStreet(String Street){ this.Street=Street; }

    public String getCity(){ return City; }
    public void setCity(String City){ this.City = City; }

    public String getPostalCode(){ return PostalCode; }
    public void setPostalCode(String PostalCode){ this.PostalCode = PostalCode; }

    public float getGeoLat(){ return GeoLat; }
    public void setGeoLat(float GeoLat){ this.GeoLat = GeoLat; }

    public float getGeoLon(){ return GeoLon; }
    public void setGeoLon(float GeoLon){ this.GeoLon = GeoLon; }

    public Bitmap getImage(){ return Image; }
    public void setImage(Bitmap Image){ this.Image = Image; }

    public int getActivity(){ return Activity; }
    public void setActivity(int Activity){ this.Activity = Activity; }
}
