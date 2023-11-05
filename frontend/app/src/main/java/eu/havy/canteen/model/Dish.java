package eu.havy.canteen.model;

import java.util.Objects;

public class Dish {
    private final String name, category, allergensLite;
    private final int id, weight, price;

    private int remainingAmount;

    public Dish(int id, String name, String category, String allergensLite, int price, int remainingAmount, int weight){
        this.id = id;
        this.name = name;
        this.category = category;
        this.allergensLite = allergensLite;
        this.price = price;
        this.remainingAmount = remainingAmount;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getPrice() {
        return price+" Kč";
    }

    public String getRemainingAmount() {
        return remainingAmount + " ks";
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
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
