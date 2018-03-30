package com.app.food.salvage;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

    TextView email;
    EditText phone, fresh, region;
    String ph, re, fre;
    Button make, loc;
    Geocoder geocoder;
    List<Address> addresses;
    Double lat = 18.984920,lon = 72.822278;
    private ProgressDialog pDialog;
    private LocationManager locationManager;
    private LocationListener locationListener;
    ConnectivityDetector connectivityDetector;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        email = (TextView) findViewById(R.id.tvProfileName);
        phone = (EditText) findViewById(R.id.etPhone);
        region = (EditText) findViewById(R.id.etRegion);
        fresh = (EditText) findViewById(R.id.etFresh);
        make = (Button) findViewById(R.id.btnPost);
        loc = (Button) findViewById(R.id.btnLocation);

        geocoder = new Geocoder(this, Locale.getDefault());
        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        email.setText("Donor");

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
                        Toast.makeText(Dashboard.this, "Food Post Added!", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(Dashboard.this, "Adding Food Post Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(Dashboard.this, "Food Post Added!", Toast.LENGTH_SHORT).show();
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
                        connectivityDetector.showAlertDialog(Dashboard.this, "Food Post adding Failed","No Internet Connection");
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
                Toast.makeText(Dashboard.this, "Pressed!", Toast.LENGTH_SHORT).show();
                try {
                    addresses = geocoder.getFromLocation(lat,lon,1);
                    String address = addresses.get(0).getAddressLine(0);
                    String area = addresses.get(0).getLocality();
                    String city = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();

                    String fulladdress = address+", "+area+", "+city+", "+country;
                    region.setText(fulladdress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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
