package com.example.crmapp.databases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.crmapp.models.Client;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 12;
    private static final String DB_NAME = "clientDB";
    private static final String TABLE_Clients = "clients";

    private static final String KEY_ID = "_ID";
    private static final String KEY_NAME = "Name";
    private static final String KEY_LASTNAME = "LastName";
    private static final String KEY_AGE = "Age";
    private static final String KEY_STREET = "Street";
    private static final String KEY_CITY = "City";
    private static final String KEY_POSTALCODE = "PostalCode";
    private static final String KEY_GEOLAT = "GeoLat";
    private static final String KEY_GEOLON = "GeoLon";
    private static final String KEY_IMAGE = "Image";
    private static final String KEY_ACTIVITY = "Activity";

    private ByteArrayOutputStream objectByteArrayOutputStream = new ByteArrayOutputStream();
    private byte[] imageInBytes;

    public DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Clients + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_AGE + " INTEGER,"
                + KEY_STREET + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_POSTALCODE + " TEXT,"
                + KEY_GEOLAT + " REAL,"
                + KEY_GEOLON+ " REAL,"
                + KEY_IMAGE + " BLOB,"
                + KEY_ACTIVITY + " INTEGER"+")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Clients);
        onCreate(db);
    }

    public void insertClient(Client client) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Bitmap image = client.getImage();
        objectByteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,objectByteArrayOutputStream);
        imageInBytes=objectByteArrayOutputStream.toByteArray();
        image.recycle();

        ContentValues cValues = new ContentValues();
        cValues.put(KEY_NAME, client.getName());
        cValues.put(KEY_LASTNAME, client.getLastName());
        cValues.put(KEY_AGE, client.getBuilding());
        cValues.put(KEY_STREET, client.getStreet());
        cValues.put(KEY_CITY, client.getCity());
        cValues.put(KEY_POSTALCODE, client.getPostalCode());
        cValues.put(KEY_GEOLAT, client.getGeoLat());
        cValues.put(KEY_GEOLON, client.getGeoLon());
        cValues.put(KEY_IMAGE, imageInBytes);
        cValues.put(KEY_ACTIVITY, client.getActivity());

        DB.insert(TABLE_Clients, null, cValues);
    }

    public void updateClient(Client client){
        SQLiteDatabase DB = this.getWritableDatabase();

        Bitmap image = client.getImage();
        objectByteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,objectByteArrayOutputStream);
        imageInBytes=objectByteArrayOutputStream.toByteArray();
        //image.recycle();

        ContentValues cValues = new ContentValues();
        cValues.put(KEY_NAME, client.getName());
        cValues.put(KEY_LASTNAME, client.getLastName());
        cValues.put(KEY_AGE, client.getBuilding());
        cValues.put(KEY_STREET, client.getStreet());
        cValues.put(KEY_CITY, client.getCity());
        cValues.put(KEY_POSTALCODE, client.getPostalCode());
        cValues.put(KEY_GEOLAT, client.getGeoLat());
        cValues.put(KEY_GEOLON, client.getGeoLon());
        cValues.put(KEY_IMAGE, imageInBytes);
        cValues.put(KEY_ACTIVITY, client.getActivity());

        DB.update(TABLE_Clients,cValues,KEY_ID+" = ?", new String[]{String.valueOf(client.get_ID())});
    }

    public void deleteClient(int id){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.delete(TABLE_Clients, KEY_ID+" = ?",new String[]{String.valueOf(id)});
    }

    public void deleteAll(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.delete(TABLE_Clients,null, null);
    }

    public ArrayList<Client> getAllClientsData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        ArrayList<Client> clientArrayList=new ArrayList<>();
        Cursor objectCursor=DB.rawQuery("select * from "+TABLE_Clients+"", null);
        if(objectCursor.getCount()!= -1){
            while(objectCursor.moveToNext()){
                Client client = new Client();
                client.set_ID(objectCursor.getInt(0));
                client.setName(objectCursor.getString(1));
                client.setLastName(objectCursor.getString(2));
                client.setBuilding(objectCursor.getInt(3));
                client.setStreet(objectCursor.getString(4));
                client.setCity(objectCursor.getString(5));
                client.setPostalCode(objectCursor.getString(6));
                client.setGeoLat(objectCursor.getFloat(7));
                client.setGeoLon(objectCursor.getFloat(8));

                byte [] imageBytes = objectCursor.getBlob(9);
                Bitmap objectBitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                client.setImage(objectBitmap);
                client.setActivity(objectCursor.getInt(10));

                clientArrayList.add(client);
            }
            objectCursor.close();
            return clientArrayList;
        }
        else{
            return null;
        }
    }

    public Client getClient(int id){
            SQLiteDatabase DB = this.getWritableDatabase();
            Client client = new Client();
            @SuppressLint("Recycle")
            Cursor objectCursor = DB.rawQuery("select * from " + TABLE_Clients + " where " + KEY_ID + " = ?", new String[]{String.valueOf(id)});
            if(objectCursor.getCount()!= -1) {
                while (objectCursor.moveToNext()) {
                    client.set_ID(objectCursor.getInt(0));
                    client.setName(objectCursor.getString(1));
                    client.setLastName(objectCursor.getString(2));
                    client.setBuilding(objectCursor.getInt(3));
                    client.setStreet(objectCursor.getString(4));
                    client.setCity(objectCursor.getString(5));
                    client.setPostalCode(objectCursor.getString(6));
                    client.setGeoLat(objectCursor.getFloat(7));
                    client.setGeoLon(objectCursor.getFloat(8));

                    byte [] imageBytes = objectCursor.getBlob(9);
                    Bitmap objectBitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                    client.setImage(objectBitmap);
                    client.setActivity(objectCursor.getInt(10));
                }
                return client;
            }
            else{
                return null;
            }
    }

    @SuppressLint("Recycle")
    public List<Client> filterClients(CharSequence constraint){

        SQLiteDatabase DB = this.getWritableDatabase();
        List<Client> filteredList = new ArrayList<>();
        Cursor objectCursor;

        String[] splitString = constraint.toString().split(" ");

        if(splitString.length==5){
            objectCursor = DB.rawQuery("select * from " + TABLE_Clients + " where (" +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? )"
                    , new String[]{"%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%",
                            "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%",
                            "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%",
                            "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%",
                            "%" + splitString[4].trim() + "%", "%" + splitString[4].trim() + "%", "%" + splitString[4].trim() + "%", "%" + splitString[4].trim() + "%", "%" + splitString[4].trim() + "%"});

        }
        else if (splitString.length == 4) {
            objectCursor = DB.rawQuery("select * from " + TABLE_Clients + " where (" +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? )"
                    , new String[]{"%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%",
                            "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%",
                            "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%",
                            "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%", "%" + splitString[3].trim() + "%"});
        }
        else if(splitString.length==3){
            objectCursor = DB.rawQuery("select * from " + TABLE_Clients + " where (" +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? )"
                    , new String[]{"%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%",
                                   "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%",
                                   "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%", "%" + splitString[2].trim() + "%"});
        }
        else if(splitString.length==2) {
            objectCursor = DB.rawQuery("select * from " + TABLE_Clients + " where (" +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? ) AND ( " +
                            KEY_NAME + " LIKE ? OR " + KEY_LASTNAME + " LIKE ? OR "+KEY_CITY+" LIKE ? OR "+KEY_STREET+" LIKE ? OR "+KEY_POSTALCODE+" LIKE ? )"
                    , new String[]{"%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%",
                                   "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%", "%" + splitString[1].trim() + "%"});
        }
        else if(splitString.length==1) {
            objectCursor = DB.rawQuery("select * from " + TABLE_Clients + " where " +
                            KEY_NAME + " LIKE ? OR " +
                            KEY_LASTNAME + " LIKE ? OR " +
                            KEY_STREET + " LIKE ? OR " +
                            KEY_POSTALCODE + " LIKE ? OR " +
                            KEY_CITY + " LIKE ?"
                    , new String[]{"%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%", "%" + splitString[0].trim() + "%"});
        }
        else{
            objectCursor = DB.rawQuery("select * from " + TABLE_Clients + " WHERE "+KEY_NAME+" LIKE ? ", new String[]{"%" +constraint+ "%"});
        }

        if(objectCursor.getCount()!= -1) {
            while (objectCursor.moveToNext()) {
                Client client = new Client();
                client.set_ID(objectCursor.getInt(0));
                client.setName(objectCursor.getString(1));
                client.setLastName(objectCursor.getString(2));
                client.setBuilding(objectCursor.getInt(3));
                client.setStreet(objectCursor.getString(4));
                client.setCity(objectCursor.getString(5));
                client.setPostalCode(objectCursor.getString(6));
                client.setGeoLat(objectCursor.getFloat(7));
                client.setGeoLon(objectCursor.getFloat(8));

                byte [] imageBytes = objectCursor.getBlob(9);
                Bitmap objectBitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                client.setImage(objectBitmap);

                filteredList.add(client);
            }
        }

        return filteredList;
    }
}
