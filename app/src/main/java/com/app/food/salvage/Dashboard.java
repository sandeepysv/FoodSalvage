package com.app.food.salvage;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    public static final int REQUEST_LOCATION = 1;
    EditText phone, fresh, region;
    String ph, re, fre;
    Button make, loc;
    Geocoder geocoder;
    List<Address> addresses;
    Double lat = 17.4722205, lon = 78.5632572;
    private ProgressDialog pDialog;
    private LocationManager locationManager;
    private LocationListener locationListener;
    ConnectivityDetector connectivityDetector;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        phone = (EditText) findViewById(R.id.etPhone);
        region = (EditText) findViewById(R.id.etRegion);
        fresh = (EditText) findViewById(R.id.etFresh);
        make = (Button) findViewById(R.id.btnPost);
        loc = (Button) findViewById(R.id.btnLocation);

        geocoder = new Geocoder(this, Locale.getDefault());

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        make.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.getBackground().setAlpha(150);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.getBackground().setAlpha(255);
                }
                return false;
            }
        });

        make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ph = phone.getText().toString();
                re = region.getText().toString();
                fre = fresh.getText().toString();

                //Login for validation
                if(ph.isEmpty()) {
                    Toast.makeText(Dashboard.this, "Enter Phone No.", Toast.LENGTH_SHORT).show();
                }
                else if(re.isEmpty()) {
                    Toast.makeText(Dashboard.this, "Enter Food Available Region", Toast.LENGTH_SHORT).show();
                }
                else if(fre.isEmpty()) {
                    Toast.makeText(Dashboard.this, "Enter Freshness of Food", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    connectivityDetector = new ConnectivityDetector(getBaseContext());

                    if(connectivityDetector.checkConnectivityStatus()){
                        Toast.makeText(Dashboard.this, "Food Posts Added!", Toast.LENGTH_SHORT).show();
                        String serverAddress = "http://192.168.43.214/foodsalvage/food_post/index.php";

                        pDialog.setMessage("Please Wait....");
                        pDialog.setTitle("Processing");
                        pDialog.setCancelable(false);
                        showDialog();

                        StringRequest checkRiderRequest = new StringRequest(Request.Method.POST,
                                serverAddress, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                hideDialog();

                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    if(code.equals("post_failed")) {
                                        Toast.makeText(Dashboard.this, "Adding Food Posts Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(Dashboard.this, "Food Posts Added!", Toast.LENGTH_SHORT).show();
                                        phone.setText("");
                                        region.setText("");
                                        fresh.setText("");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),
                                        " Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                                hideDialog();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("phone",ph);
                                params.put("region",re);
                                params.put("fresh",fre);
                                params.put("status","0");
                                return params;
                            }
                        };
                        MySingleton.getInstance(Dashboard.this).addToRequestque(checkRiderRequest);
                    }
                    else{
                        connectivityDetector.showAlertDialog(Dashboard.this, "Food Posts adding Failed","No Internet Connection");
                    }

                }

            }// End of onClick
        });

        loc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.getBackground().setAlpha(150);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.getBackground().setAlpha(255);
                }
                return false;
            }
        });

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(Dashboard.this, "Turn on your GPS!", Toast.LENGTH_SHORT).show();
                }
                else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation();
                }
                try {
                    addresses = geocoder.getFromLocation(lat,lon,1);
                    String address = addresses.get(0).getAddressLine(0);
                    region.setText(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
            else {
                Toast.makeText(Dashboard.this, "Unable to Trace your Location!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //Show Dialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //Hide Dialog
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
