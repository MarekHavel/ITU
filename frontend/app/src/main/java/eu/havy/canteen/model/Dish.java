// author: Marek Havel <xhavel46@vutbr.cz>
package eu.havy.canteen.model;

import java.util.Objects;

public class Dish {
    private final String name, category, allergensLite, purchaseDate, orderId;
    private final int id, weight, price;

    private final float rating;

    private int remainingAmount;

    public Dish(int id, String name, String category, String allergensLite, int price, int remainingAmount, int weight, String purchaseDate, float rating, String orderId){
        this.id = id;
        this.name = name;
        this.category = category;
        this.allergensLite = allergensLite;
        this.price = price;
        this.remainingAmount = remainingAmount;
        this.weight = weight;
        this.purchaseDate = purchaseDate;
        this.rating = rating;
        this.orderId = orderId;
    }

    public int getId() {
        return id;
    }

    public String getPrice() {
        return price+" Kč";
    }

    public int getRemainingAmount() {
        return remainingAmount;
    }

    public String getName() {
        return name;
    }

    public String getPurchaseDate(){return purchaseDate;}

    public String getOrderId(){return orderId;}

    public int getWeight() {
        return weight;
    }

    public float getRating() {
        return rating;
    }

    public String getAllergensLite() {
        return allergensLite;
    }

    public String getExtraInfo() {
        if(Objects.equals(allergensLite, "")){
            return weight + "g";
        } else {
            return + weight + "g; " + allergensLite;
        }
    }
    public String getCategory() {
        return category;
    }

    public void setRemainingAmount(int remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}
