package com.app.food.salvage;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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

public class SignUp extends AppCompatActivity {

    Button btnBack,btnSignUp;
    EditText email,pass,cpass,phone,address;
    String em,pw,cpw,ph,add,ca;
    RadioGroup cat;
    int checkedUserId;
    AlertDialog.Builder builder;
    String reg_url = "http://192.168.43.214/foodsalvage/sign_up/index.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        email = (EditText) findViewById(R.id.etSignUpUserEmail);
        pass = (EditText) findViewById(R.id.etSignUpPassword);
        cpass = (EditText) findViewById(R.id.etSignUpPassword2);
        phone = (EditText) findViewById(R.id.etSignUpPhone);
        address = (EditText) findViewById(R.id.etSignUpAddress);
        cat = (RadioGroup) findViewById(R.id.rbgSignUpUserType);
        builder = new AlertDialog.Builder(SignUp.this);

        btnBack.setOnTouchListener(new View.OnTouchListener() {
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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }// End of onClick
        });

        btnSignUp.setOnTouchListener(new View.OnTouchListener() {
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
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                em = email.getText().toString();
                pw = pass.getText().toString();
                cpw = cpass.getText().toString();
                ph = phone.getText().toString();
                add = address.getText().toString();
                checkedUserId = cat.getCheckedRadioButtonId();

                if(checkedUserId == R.id.rbSignUpDonor) {
                    ca = "Donor";
                    Toast.makeText(SignUp.this, "Category : "+ca, Toast.LENGTH_SHORT).show();
                }
                else if(checkedUserId == R.id.rbSignUpCharity) {
                    ca = "Charity";
                    Toast.makeText(SignUp.this, "Category : "+ca, Toast.LENGTH_SHORT).show();
                }


                //Login for validation
                if (em.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter E-Mail Address", Toast.LENGTH_SHORT).show();
                }
                else if (pw.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (cpw.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                }
                else if (ph.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter Phone No.", Toast.LENGTH_SHORT).show();
                }
                else if (add.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter Address", Toast.LENGTH_SHORT).show();
                }
                else if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches())) {
                    Toast.makeText(SignUp.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
                else if(!(pw.equals(cpw))) {
                    Toast.makeText(SignUp.this, "Passwords doesn't Match", Toast.LENGTH_SHORT).show();
                }
                else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
                                builder.setTitle("Food Salvage");
                                builder.setMessage(message);
                                displayAlert(code);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("email",em);
                            params.put("password",pw);
                            params.put("phone",ph);
                            params.put("address",add);
                            params.put("category",ca);
                            return params;
                        }
                    };
                    MySingleton.getInstance(SignUp.this).addToRequestque(stringRequest);
                }
                // End of onClick
            }
        });

    }

    public void displayAlert(final String code)
    {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("reg_success")) {
                    finish();
                }
                else if(code.equals("reg_failed")) {
                    email.setText("");
                    pass.setText("");
                    cpass.setText("");
                    phone.setText("");
                    address.setText("");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
