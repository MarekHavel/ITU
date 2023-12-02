package eu.havy.canteen.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.havy.canteen.MainActivity;
import eu.havy.canteen.api.Api;

/**
 * Represents a user of the application and provides methods for logging in and out.
 * Only one user can be logged in at a time. (Singleton)
 */
public class User {

    private static User currentUser;
    private String email;
    private String name;
    private String token;
    private String canteenName;
    private String priceCategory;

    public Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(Objects.requireNonNull(((Bundle) (msg.obj)).getString("response")));
                if (jsonObject.has("token")) {
                    token = jsonObject.getString("token");
                    new Api(this).getUserInfo(token);
                } else {
                    canteenName = jsonObject.getString("canteenName");
                    priceCategory = jsonObject.getString("priceCategory");
                    email = jsonObject.getString("email");
                    name = jsonObject.getString("name");
                    MainActivity.updateUserInfo();
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.getContext(), "FAILURE, code: " + msg.what + " message: " + msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Creates a new user with the given email address.
     * This constructor is private, use {@link #login(String, String)} to log in a user.
     * @param email email address of the user
     */
    private User(String email, String password) {
        new Api(handler).authenticateUser(email, password);
    }

    /**
     * Returns the email address of the currently logged in user.
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the name of the currently logged in user.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the token of the currently logged in user.
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the canteen name of the currently logged in user.
     * @return canteen name
     */
    public String getCanteenName() {
        return canteenName;
    }

    /**
     * Returns the price category of the currently logged in user.
     * @return price category
     */
    public String getPriceCategory() {
        return priceCategory;
    }

    /**
     * Logs out the currently logged in user.
     * @return true if the user was logged out, false if no user was logged in
     */
    public static synchronized boolean logout() {
        if (currentUser != null) {
            currentUser = null;
            return true;
        }
        return false;
    }

    /**
     * Logs in a user with the given email address and password.
     * @param email email address of the user
     * @param password password of the user
     * @return true if the user was logged in, false if a user is already logged in
     */
    public static synchronized boolean login(String email, String password) {
        if (currentUser == null) {
            currentUser = new User(email, password);
            return true;
        }
        return false;
    }

    /**
     * Returns the currently logged in user, or null if no user is logged in.
     * @return {@link User} instance or null
     */
    public static synchronized User getCurrentUser()
    {
        return currentUser;
    }

}
