package com.app.food.salvage;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    TextView email;
    EditText phone,region,fresh;
    String ph,re,fre;
    Button make;
    private ProgressDialog pDialog;
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
                                        phone.setFocusable(true);
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
