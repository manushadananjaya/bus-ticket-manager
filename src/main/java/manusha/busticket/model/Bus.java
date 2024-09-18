package manusha.busticket.model;

import java.sql.Date;

public class Bus {

    private int busId;
    private String busStatus;
    private String busLocation;
    private Date busDate;
    private double busPrice;

    public Bus(int busId, String busStatus, String busLocation, Date busDate, double busPrice) {
        this.busId = busId;
        this.busStatus = busStatus;
        this.busLocation = busLocation;
        this.busDate = busDate;
        this.busPrice = busPrice;

    }
    public int getBusId() {
        return busId;
    }
    public void setBusId(int busId) {
        this.busId = busId;
    }
    public String getBusStatus() {
        return busStatus;
    }
    public void setBusType(String busType) {
        this.busStatus = busType;
    }
    public String getBusLocation() {
        return busLocation;
    }
    public void setBusLocation(String busLocation) {
        this.busLocation = busLocation;
    }
    public Date getBusDate() {
        return busDate;
    }
    public void setBusDate(Date busDate) {
        this.busDate = busDate;
    }
    public double getBusPrice() {
        return busPrice;
    }

    public void setBusPrice(double busPrice) {
        this.busPrice = busPrice;
    }

}
