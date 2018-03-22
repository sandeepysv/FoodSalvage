package com.app.food.salvage;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {

    //Declaration
    public SharedPreferences userDB;
    public static final String userDBName = "userData";

    //Constructor
    public  UserLocalStore(Context ctx){
        userDB = ctx.getSharedPreferences(userDBName, 0);
    }

    //Store donor data in shared preference
    public void storeRiderData(Donor donor){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.putString("email", donor.getEmail());
        riderSpEditor.putString("password", donor.getPassword());
        riderSpEditor.putString("phone", donor.getPhone());
        riderSpEditor.putString("address", donor.getAddress());
        riderSpEditor.putString("category", donor.getCategory());
        riderSpEditor.commit();
    }

    //Store charity  data in shared preference
    public void storeClientData(Charity charity){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.putString("email", charity.getEmail());
        clientSpEditor.putString("password", charity.getPassword());
        clientSpEditor.putString("phone", charity.getPhone());
        clientSpEditor.putString("address", charity.getAddress());
        clientSpEditor.putString("category", charity.getCategory());
        clientSpEditor.commit();
    }

    //Get rider data
    public Donor getLoggedInRider () {
        String email = userDB.getString("email", "");
        String password = userDB.getString("password", "");
        String phone = userDB.getString("phone", "");
        String address = userDB.getString("address", "");
        String category = userDB.getString("category", "");

        Donor donor = new Donor(email, password);
        donor.setPhone(phone);
        donor.setAddress(address);
        donor.setCategory(category);

        return donor;
    }

    //Get client data
    public Charity getLoggedInClient (){
        String email = userDB.getString("email", "");
        String password = userDB.getString("password", "");
        String phone = userDB.getString("phone", "");
        String address = userDB.getString("address", "");
        String category = userDB.getString("category", "");

        //Create Charity and put charity data
        Charity charity = new Charity();

        charity.setEmail(email);
        charity.setPassword(password);
        charity.setPhone(phone);
        charity.setAddress(address);
        charity.setCategory(category);

        return charity;
    }

    //Set Login Donor
    public void setRiderLoggedIn(boolean loggedIn){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.putBoolean("loggedIn", loggedIn);
        riderSpEditor.putString("user", "rider");
        riderSpEditor.commit();
    }

    //Set Login Charity
    public void setClientLoggedIn(boolean loggedIn){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.putBoolean("loggedIn", loggedIn);
        clientSpEditor.putString("user", "client");
        clientSpEditor.commit();
    }

    //get rider is logged in or not
    public boolean isRiderLoggedIn(){
        if(userDB.getBoolean("loggedIn", false)){
            return true;
        }else{
            return false;
        }
    }

    //get client is logged in or not
    public boolean isClientLoggedIn(){
        if(userDB.getBoolean("loggedIn", false)){
            return true;
        }else{
            return false;
        }
    }

    //Clear rider data
    public void clearRiderData(){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.clear();
        riderSpEditor.commit();
    }

    //Clear client data
    public void clearClientData(){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.clear();
        clientSpEditor.commit();
    }

    //Set Login Donor
    public String loggedInUser(){
        String user = userDB.getString("user", "");
        return  user;
    }
}
