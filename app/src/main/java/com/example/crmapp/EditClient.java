package com.example.crmapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.crmapp.databases.DbHandler;
import com.example.crmapp.interfaces.OnGeocoderTaskCompleted;
import com.example.crmapp.models.Client;

import java.lang.reflect.Field;


public class EditClient extends AppCompatActivity implements OnGeocoderTaskCompleted {

    private EditText Name, LastName, Building, Street, City, PostalCode;
    TextView n, ln, b, s, c, pc;
    private ImageView Image;
    private int editID;
    private DbHandler DB;
    private Client client;

    public Uri imageFilePath;
    public Bitmap imageToStore;
    private static final int PICK_IMAGE_REQUEST=100;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editclient);

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 50 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap img = BitmapFactory.decodeResource(this.getResources(), R.drawable.client_image);
        ImageView deleteImage = findViewById(R.id.deleteImage);

        Toolbar toolbar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Name = findViewById(R.id.ClientName);
        LastName = findViewById(R.id.ClientLastName);
        Building = findViewById(R.id.ClientBuilding);
        Street = findViewById(R.id.ClientStreet);
        City = findViewById(R.id.ClientCity);
        PostalCode = findViewById(R.id.ClientPostalCode);
        Image = findViewById(R.id.ClientImagePlaceHolder);

        editID = getIntent().getIntExtra("editID", 0);
        DB = new DbHandler(EditClient.this);
        client = DB.getClient(editID);

        Name.setText(client.getName());
        LastName.setText(client.getLastName());
        Building.setText(String.valueOf(client.getBuilding()));
        Street.setText(client.getStreet());
        City.setText(client.getCity());
        PostalCode.setText(client.getPostalCode());

        if(img.sameAs(client.getImage())){
            Image.setImageDrawable(getDrawable(R.drawable.ic_image_placeholder));

            Image.setOnClickListener( view -> {
                try {
                    Intent objectIntent = new Intent();
                    objectIntent.setType("image/*");

                    objectIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(objectIntent, PICK_IMAGE_REQUEST);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            deleteImage.setOnClickListener(v -> {
                Image.setImageDrawable(getDrawable(R.drawable.ic_image_placeholder));
                client.setImage(img);
            });
        }
        else Image.setImageBitmap(client.getImage());

        n = findViewById(R.id.Name); ln = findViewById(R.id.LastName);
        s = findViewById(R.id.Street); b = findViewById(R.id.Building);
        pc = findViewById(R.id.PostalCode); c = findViewById(R.id.City);

        ImageView clientEditButton = findViewById(R.id.ClientEditButton);
        clientEditButton.setOnClickListener(v -> {
            String name = Name.getText().toString();
            String lastname = LastName.getText().toString();
            String building = Building.getText().toString();
            String street = Street.getText().toString();
            String city = City.getText().toString();
            String postalcode = PostalCode.getText().toString();

            if(name.length()==0) {
                Name.requestFocus();
                Name.setError("FIELD CANNOT BE EMPTY");
                Name.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                n.setTextColor(getColor(R.color.red));
            }
            else if(lastname.length()==0) {
                LastName.requestFocus();
                LastName.setError("FIELD CANNOT BE EMPTY");
                LastName.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                ln.setTextColor(getColor(R.color.red));
            }
            else if(building.length()==0) {
                Building.requestFocus();
                Building.setError("FIELD CANNOT BE EMPTY");
                Building.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                b.setTextColor(getColor(R.color.red));
            }
            else if(!building.matches("[0-9]{1,2}")) {
                Building.requestFocus();
                Building.setError("INSERT VALID BUILDING");
                Building.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                b.setTextColor(getColor(R.color.red));
            }
            else if(street.length()==0) {
                Street.requestFocus();
                Street.setError("FIELD CANNOT BE EMPTY");
                Street.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                s.setTextColor(getColor(R.color.red));
            }
            else if(city.length()==0) {
                City.requestFocus();
                City.setError("FIELD CANNOT BE EMPTY");
                City.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                c.setTextColor(getColor(R.color.red));
            }
            else if(postalcode.length()==0) {
                PostalCode.requestFocus();
                PostalCode.setError("FIELD CANNOT BE EMPTY");
                PostalCode.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                pc.setTextColor(getColor(R.color.red));
            }
            else if(!postalcode.matches("[0-9]{2}-[0-9]{3}")) {
                PostalCode.requestFocus();
                PostalCode.setError("INSERT VALID POSTAL CODE");
                PostalCode.setBackground(getDrawable(R.drawable.edit_text_shape_red));
                pc.setTextColor(getColor(R.color.red));
            }
            else {
                GeocoderTask geocodertask = new GeocoderTask(EditClient.this, this);
                geocodertask.execute(street, city, postalcode, name, lastname, building);
            }
        });


        deleteImage.setOnClickListener(v -> {
            Image.setImageDrawable(getDrawable(R.drawable.ic_image_placeholder));
            client.setImage(img);

            Image.setOnClickListener( view -> {
                try {
                    Intent objectIntent=new Intent();
                    objectIntent.setType("image/*");

                    objectIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(objectIntent, PICK_IMAGE_REQUEST);
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        Name.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                Name.setBackground(getDrawable(R.drawable.edit_text_shape));
                n.setTextColor(getColor(R.color.blue2));
            } else {
                Name.setBackground(getDrawable(R.drawable.edit_text_shape_grey));
                n.setTextColor(R.color.textColor);
            }
        });

        LastName.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                LastName.setBackground(getDrawable(R.drawable.edit_text_shape));
                ln.setTextColor(getColor(R.color.blue2));
            } else {
                LastName.setBackground(getDrawable(R.drawable.edit_text_shape_grey));
                ln.setTextColor(R.color.textColor);
            }
        });

        Street.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                Street.setBackground(getDrawable(R.drawable.edit_text_shape));
                s.setTextColor(getColor(R.color.blue2));
            } else {
                Street.setBackground(getDrawable(R.drawable.edit_text_shape_grey));
                s.setTextColor(R.color.textColor);
            }
        });

        Building.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                Building.setBackground(getDrawable(R.drawable.edit_text_shape));
                b.setTextColor(getColor(R.color.blue2));
            } else {
                Building.setBackground(getDrawable(R.drawable.edit_text_shape_grey));
                b.setTextColor(R.color.textColor);
            }
        });

        PostalCode.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                PostalCode.setBackground(getDrawable(R.drawable.edit_text_shape));
                pc.setTextColor(getColor(R.color.blue2));
            } else {
                PostalCode.setBackground(getDrawable(R.drawable.edit_text_shape_grey));
                pc.setTextColor(R.color.textColor);
            }
        });

        City.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                City.setBackground(getDrawable(R.drawable.edit_text_shape));
                c.setTextColor(getColor(R.color.blue2));
            } else {
                City.setBackground(getDrawable(R.drawable.edit_text_shape_grey));
                c.setTextColor(R.color.textColor);
            }
        });
    }

    @Override
    public void onGeocoderTaskCompleted(Client c) {
        DB = new DbHandler(EditClient.this);
        Client editClient = DB.getClient(editID);
        editClient.setName(c.getName());
        editClient.setLastName(c.getLastName());
        editClient.setBuilding(c.getBuilding());
        editClient.setStreet(c.getStreet());
        editClient.setCity(c.getCity());
        editClient.setPostalCode(c.getPostalCode());
        editClient.setGeoLat(c.getGeoLat());
        editClient.setGeoLon(c.getGeoLon());
        if(imageToStore!=null) editClient.setImage(imageToStore);
        else editClient.setImage(client.getImage());
        DB.updateClient(editClient);

        Intent intent = new Intent(EditClient.this, ClientDetails.class);
        intent.putExtra("clientID",editID);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageFilePath = data.getData();
                imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);

                Image.setImageBitmap(imageToStore);
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
