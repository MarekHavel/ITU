package eu.havy.canteen.model;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.havy.canteen.LoginActivity;
import eu.havy.canteen.MainActivity;
import eu.havy.canteen.api.Api;

/**
 * Represents a user of the application and provides methods for logging in and out.
 * Only one user can be logged in at a time. (Singleton)
 */
public class User {

    private static User currentUser;
    private String email;
    private String username;
    private String token;
    private String canteenName;
    private String priceCategory;
    private String credit;

    private Handler creditUpdateLoop = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Runnable creditUpdate = new Runnable() {
        @Override
        public void run() {
            new Api(handler).getUserCredit(token);
            creditUpdateLoop.postDelayed(this, 30000);
        }
    };

    public Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) msg.obj;
                String error = jsonObject.has("exception") ? jsonObject.getString("exception") : (jsonObject.has("message") ? jsonObject.getString("message") : "");
                if (!error.isEmpty()) {
                    Log.e("User", "FAILURE, code: " + msg.what + " message: " + error);
                    Toast.makeText(MainActivity.getInstance() != null ? MainActivity.getInstance() : LoginActivity.getInstance(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    if (LoginActivity.getInstance() != null) {
                        LoginActivity.getInstance().logInFailed();
                        User.logout();
                    }
                    return;
                }
                if (jsonObject.has("credit")) {
                    credit = jsonObject.getString("credit");
                    MainActivity.updateCredit();
                    creditUpdateLoop.removeCallbacks(creditUpdate);
                    creditUpdateLoop.post(creditUpdate);
                } else if (jsonObject.has("email")) {
                    canteenName = jsonObject.getString("canteen");
                    priceCategory = jsonObject.getString("priceCategory");
                    email = jsonObject.getString("email");
                    username = jsonObject.getString("username");
                    MainActivity.updateUserInfo();
                } else if (jsonObject.has("token")){
                    token = jsonObject.getString("token");
                    LoginActivity.logIn();
                } else {
                    creditUpdateLoop.removeCallbacks(creditUpdate);
                    creditUpdateLoop.post(creditUpdate);
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.getInstance() != null ? MainActivity.getInstance() : LoginActivity.getInstance(), "Request failed", Toast.LENGTH_SHORT).show();
                Log.e("User", "FAILURE, code: " + msg.what + " message: " + msg.obj);
                if (LoginActivity.getInstance() != null) {
                    LoginActivity.getInstance().logInFailed();
                    User.logout();
                }
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
     * Creates a new user with the given token.
     * @param token token of the user
     */
    private User(String token) {
        this.token = token;
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
    public String getUsername() {
        return username;
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
     * Returns the credit of the currently logged in user.
     * @return credit
     */
    public String getCredit() {
        return credit;
    }

    /**
     * Adds credit to the currently logged in user and updates the credit in the database.
     * @param amount credit amount as string
     */
    public void addCredit(int amount) {
        new Api(handler).addUserCredit(token, amount);
    }

    /**
     * Logs out the currently logged in user.
     * @return true if the user was logged out, false if no user was logged in
     */
    public static synchronized boolean logout() {
        if (currentUser != null) {
            currentUser.creditUpdateLoop.removeCallbacks(currentUser.creditUpdate);
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
     * Logs in a user with the given token.
     * @param token token of the user
     * @return true if the user was logged in, false if a user is already logged in
     */
    public static synchronized boolean login(String token) {
        if (currentUser == null) {
            currentUser = new User(token);
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
