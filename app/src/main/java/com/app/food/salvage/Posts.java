package com.app.food.salvage;

public class Posts {

    private double phone;
    private String region;
    private String fresh;
    private int status;

    public Posts(double phone, String region, String fresh, int status) {
        this.phone = phone;
        this.region = region;
        this.fresh = fresh;
        this.status = status;
    }

    public double getPhone() {
        return phone;
    }

    public String getRegion() {
        return region;
    }

    public String getFresh() {
        return fresh;
    }

    public int getStatus() {
        return status;
    }

}
