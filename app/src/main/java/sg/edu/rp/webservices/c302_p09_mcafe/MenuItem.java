package sg.edu.rp.webservices.c302_p09_mcafe;

import java.io.Serializable;

public class MenuItem implements Serializable {

    private String itemId;
    private String catId;
    private String description;
    private double price;

    public MenuItem(String itemId, String catId, String description, double price) {
        this.itemId = itemId;
        this.catId = catId;
        this.description = description;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return description;
    }
}
