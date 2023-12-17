// author: Marek Gergel <xgerge01@vutbr.cz>
package eu.havy.canteen.model;

public class Review {

    private String username;
    private String detail;
    private int rating;

    public Review(String username, String detail, int rating) {
        this.username = username;
        this.detail = detail;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public String getDetail() {
        return detail;
    }

    public int getRating() {
        return rating;
    }
}
