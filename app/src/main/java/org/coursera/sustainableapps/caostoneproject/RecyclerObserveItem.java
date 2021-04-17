package org.coursera.sustainableapps.caostoneproject;

public class RecyclerObserveItem {

    private  int image;
    private int idCurrent;
    private double lan;
    private double lng;
    private String description;
    private String meters;

    public RecyclerObserveItem(int image, int id, double lan, double lng,
                               String description, String meters) {
        this.image = image;
        this.idCurrent = id;
        this.lan = lan;
        this.lng = lng;
        this.description = description;
        this.meters = meters;
    }

    public int getImage() {
        return image;
    }
    public int getIdCurrent() {
        return idCurrent;
    }
    public double getLat() {
        return lan;
    }

    public double getLng() {
        return lng;
    }

    public String getDescription() {
        return description;
    }

    public String getMeters() {
        return meters;
    }

    public void setMeters(String meters) {
        this.meters = meters;
    }
}
