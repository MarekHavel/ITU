package eu.havy.canteen.ui.order_food;

import android.annotation.SuppressLint;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import eu.havy.canteen.LoginActivity;
import eu.havy.canteen.MainActivity;
import eu.havy.canteen.api.Api;
import eu.havy.canteen.model.Dish;
import eu.havy.canteen.model.User;

public class OrderFoodViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Dish>> dishes;

    Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            List<Dish> dishList = new ArrayList<>();
            JSONArray jsonArray;
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) msg.obj;
                String error = jsonObject.has("exception") ? jsonObject.getString("exception") : (jsonObject.has("message") ? jsonObject.getString("message") : "");
                if (!error.isEmpty()) {
                    Toast.makeText(MainActivity.getInstance() != null ? MainActivity.getInstance() : LoginActivity.getInstance(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    Log.e("User", "FAILURE, request: " + Api.Request.toString(msg.what) + " code: " + msg.arg1 + " message: " + error);
                    return;
                }
                jsonArray = new JSONArray(jsonObject.getString("dishes"));
            } catch (JSONException e) {
                Log.e("JSON", "Invalid JSON response");
                return;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Dish dish = new Dish(obj.getInt("id"),obj.getString("name"),
                            obj.getString("category"),obj.getString("allergens"),
                            obj.getInt("price"), obj.getInt("itemsLeft"),
                            obj.getInt("weight"), null,-1);
                    dishList.add(dish);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            dishes.setValue(dishList);
        }
    };


    public OrderFoodViewModel(@NonNull Application application) {
        super(application);

        dishes = new MutableLiveData<>();
        dishes.setValue(null);

        refresh();
    }

    public LiveData<List<Dish>> getAllDishes() {
        return dishes;
    }

    public long getDishCount() {
        try {
            return Objects.requireNonNull(dishes.getValue()).size();
        } catch (NullPointerException e) {
            return 0;
        }
    }
    public void refresh(){
        Date date = MainActivity.getInstance().getSelectedDate().getValue();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // API has specific date format
        String dateText = sdf.format(Objects.requireNonNull(date));
        Log.d("Canteen", "Refreshing dish list for date: " + dateText);
        new Api(handler).getMenu(User.getCurrentUser().getToken(), dateText);

    }
}
