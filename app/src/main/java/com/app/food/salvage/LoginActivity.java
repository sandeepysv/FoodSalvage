package com.app.food.salvage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.view.View.*;


public class LoginActivity extends AppCompatActivity {

    //Declare variable and object
    EditText etUserEmail;
    EditText etPassword;
    RadioGroup rbgUserType;
    ImageView ivLogo;
    Drawable resizedImage;
    Button btnLoginSubmit,btnSignUp;
    private ProgressDialog pDialog;
    String userEmail, userPassword, userCategory;
    int checkedUserId;
    ConnectivityDetector connectivityDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialize variable and object
        etUserEmail = (EditText) findViewById(R.id.etLoginUserEmail);
        etPassword = (EditText) findViewById(R.id.etLoginPassword);
        ivLogo = (ImageView) findViewById(R.id.logoLogin);
        btnLoginSubmit = (Button) findViewById(R.id.btnLoginSubmit);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        rbgUserType = (RadioGroup) findViewById(R.id.rbgLoginUserType);

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Check Already Logged in User
        UserLocalStore sessionManager = new UserLocalStore(this);
        if(sessionManager.loggedInUser() == "donor" && sessionManager.isRiderLoggedIn()) {
            Intent goRiderProfileActivity = new Intent(LoginActivity.this, RiderDashboardActivity.class);
            startActivity(goRiderProfileActivity);
            finish();
        }
        else if(sessionManager.loggedInUser() == "charity" && sessionManager.isClientLoggedIn()) {
            Intent goClientProfileActivity = new Intent(LoginActivity.this, ClientDashboardActivity.class);
            startActivity(goClientProfileActivity);
            finish();
        }

        //Change button color when click
        btnLoginSubmit.setOnTouchListener(new OnTouchListener() {
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

        //Submit login form
        btnLoginSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = etUserEmail.getText().toString();
                userPassword = etPassword.getText().toString();

                //Login for validation
                if(userEmail.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter E-Mail Address", Toast.LENGTH_SHORT).show();
                }
                else if(userPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())) {
                    Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Check user type
                    checkedUserId = rbgUserType.getCheckedRadioButtonId();
                    connectivityDetector = new ConnectivityDetector(getBaseContext());

                    if(checkedUserId == R.id.rbDonor) {
                        if(connectivityDetector.checkConnectivityStatus()){
                            Toast.makeText(LoginActivity.this, "Donor", Toast.LENGTH_SHORT).show();
                            userCategory = "Donor";
                            checkRiderLogin(userEmail, userPassword);
                        }
                        else{
                            connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed","No Internet Connection");
                        }

                    }
                    else
                        if(checkedUserId == R.id.rbCharity) {
                            if(connectivityDetector.checkConnectivityStatus()) {
                                Toast.makeText(LoginActivity.this, "Charity", Toast.LENGTH_SHORT).show();
                                userCategory = "Charity";
                                checkRiderLogin(userEmail, userPassword);
                            }
                            else {
                                connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed","No Internet Connection");
                            }
                        }
                } //End of else

            }// End of onClick
        });

        btnSignUp.setOnTouchListener(new OnTouchListener() {
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

        //Submit login form
        btnSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }// End of onClick
        });

    } //End of onCreate

    //Check Login
    private void checkRiderLogin(final String email, final String password) {

        String serverAddress = "http://192.168.43.214/foodsalvage/log_in/index.php";

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Processing");
        pDialog.setCancelable(false);
        showDialog();

        StringRequest checkRiderRequest = new StringRequest(Request.Method.POST,
                serverAddress, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
               // Log.d("Login Response", "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");
                    if(code.equals("login_failed")) {
                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Charity loggedInCharity = new Charity();
                        loggedInCharity.setEmail(jsonObject.getString("email"));
                        loggedInCharity.setPassword(jsonObject.getString("password"));
                        loggedInCharity.setPhone(jsonObject.getString("phone"));
                        loggedInCharity.setAddress(jsonObject.getString("address"));
                        loggedInCharity.setCategory("Donor");

                        UserLocalStore userLocalStore = new UserLocalStore(LoginActivity.this);
                        userLocalStore.storeClientData(loggedInCharity);
                        userLocalStore.setClientLoggedIn(true);

                        if(checkedUserId == R.id.rbDonor) {
                            Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                        else if(checkedUserId == R.id.rbCharity) {
                            Intent intent = new Intent(LoginActivity.this, charityDashboard.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                       " Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",userEmail);
                params.put("password",userPassword);
                params.put("category",userCategory);
                return params;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestque(checkRiderRequest);
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

} //End of activity