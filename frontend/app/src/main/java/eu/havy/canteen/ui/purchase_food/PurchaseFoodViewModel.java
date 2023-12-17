package eu.havy.canteen.ui.purchase_food;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.havy.canteen.LoginActivity;
import eu.havy.canteen.MainActivity;
import eu.havy.canteen.api.Api;
import eu.havy.canteen.model.Dish;
import eu.havy.canteen.model.User;
import eu.havy.canteen.ui.order_food.OrderFoodViewModel;
import eu.havy.canteen.ui.order_history.OrderHistoryViewModel;

public class PurchaseFoodViewModel extends AndroidViewModel {

    private final MutableLiveData<Dish> dish;
    private final MutableLiveData<Integer> dishId;

    Handler getDishHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) msg.obj;
                String error = jsonObject.has("exception") ? jsonObject.getString("exception") : (jsonObject.has("message") ? jsonObject.getString("message") : "");
                if (!error.isEmpty()) {
                    Toast.makeText(MainActivity.getInstance() != null ? MainActivity.getInstance() : LoginActivity.getInstance(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    Log.e("User", "FAILURE, request: " + Api.Request.toString(msg.what) + " code: " + msg.arg1 + " message: " + error);
                    return;
                }
            } catch (JSONException e) {
                Log.e("JSON", "Invalid JSON response");
                return;
            }
            try {
                String allergensString = "";
                JSONArray allergens = jsonObject.getJSONArray("allergens");
                for (int i = 0; i < allergens.length(); i++) {
                    allergensString += allergens.getJSONObject(i).getString("name");
                    if (i != allergens.length() - 1) {
                        allergensString += "\n";
                    }
                }
                dish.setValue(new Dish(Objects.requireNonNull(dishId.getValue()),jsonObject.getString("name"),jsonObject.getString("category"),
                        allergensString,jsonObject.getInt("price"),-1,jsonObject.getInt("weight"),null,jsonObject.getInt("averageRating"),null));
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    };

    Handler orderDishHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) msg.obj;
                String error = jsonObject.has("exception") ? jsonObject.getString("exception") : (jsonObject.has("message") ? jsonObject.getString("message") : "");
                if (!error.isEmpty()) {
                    Toast.makeText(MainActivity.getInstance() != null ? MainActivity.getInstance() : LoginActivity.getInstance(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    Log.e("Canteen", "FAILURE, request: " + Api.Request.toString(msg.what) + " code: " + msg.arg1 + " message: " + error);
                    return;
                } else {
                    Log.d("Canteen", "SUCCESS, request: " + Api.Request.toString(msg.what) + " code: " + msg.arg1 + " message: " + error);
                    // refresh credits
                    User.getCurrentUser().updateData();
                }
            } catch (JSONException e) {
                Log.e("JSON", "Invalid JSON response");
                return;
            }

        }
    };

    public PurchaseFoodViewModel(@NonNull Application application) {
        super(application);

        dish = new MutableLiveData<>();
        dish.setValue(null);

        dishId = new MutableLiveData<>();
        dishId.setValue(null);

    }

    public LiveData<Dish> getDish() {
        return dish;
    }

    public void setDishId(int dishn){
        dishId.setValue(dishn);
        new Api(getDishHandler).getDish(User.getCurrentUser().getToken(),dishn);
    }

    public void orderDish(String date){
        new Api(orderDishHandler).orderDish(User.getCurrentUser().getToken(), Objects.requireNonNull(dishId.getValue()), date);
    }
}