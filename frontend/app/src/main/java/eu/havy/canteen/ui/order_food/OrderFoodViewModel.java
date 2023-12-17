package eu.havy.canteen.ui.order_food;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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

import eu.havy.canteen.MainActivity;
import eu.havy.canteen.api.Api;
import eu.havy.canteen.model.Dish;
import eu.havy.canteen.model.User;

public class OrderFoodViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Dish>> dishes;
    private final MutableLiveData<List<Dish>> orders;

    private Date lastRefresh;

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
                    Log.e("User", "FAILURE, request: " + Api.Request.toString(msg.what) + " code: " + msg.arg1 + " message: " + error);
                    return;
                }
            } catch (JSONException e) {
                Log.e("JSON", "Invalid JSON response");
                return;
            }

            switch (Api.Request.getEnum(msg.what)) {
                case GET_MENU:
                    try {
                        jsonArray = new JSONArray(jsonObject.getString("dishes"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Dish dish = new Dish(obj.getInt("id"),obj.getString("name"),
                                    obj.getString("category"),obj.getString("allergens"),
                                    obj.getInt("price"), obj.getInt("itemsLeft"),
                                    obj.getInt("weight"), null, -1,null);
                            dishList.add(dish);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    dishes.setValue(dishList);
                    break;
                case GET_ORDERS_FOR_DAY:
                    try {
                        jsonArray = new JSONArray(jsonObject.getString("orders"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("Canteen", "Received order list: " + jsonArray);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Dish dish = new Dish(obj.getInt("dishId"),obj.getString("name"),
                                    obj.getString("category"),obj.getString("allergens"),
                                    obj.getInt("price"), -1,
                                    obj.getInt("weight"), obj.getString("orderDate"), -1,obj.getString("orderId"));
                            dishList.add(dish);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    orders.setValue(dishList);
                    break;
                case SELL_DISH:
                    Log.d("Canteen", "Received response for deletion request: " + Api.Request.toString(msg.what));
                    refresh();
                    break;
                default:
                    Log.d("Canteen", "Received response for request: " + Api.Request.toString(msg.what));
                    break;
            }
        }
    };


    public OrderFoodViewModel(@NonNull Application application) {
        super(application);

        orders = new MutableLiveData<>();
        orders.setValue(null);

        dishes = new MutableLiveData<>();
        dishes.setValue(null);

    }

    public LiveData<List<Dish>> getAllDishes() {
        return dishes;
    }

    public LiveData<List<Dish>> getAllOrders() {
        return orders;
    }

    public long getDishCount() {
        try {
            return Objects.requireNonNull(dishes.getValue()).size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public long getOrderCount() {
        try {
            return Objects.requireNonNull(orders.getValue()).size();
        } catch (NullPointerException e) {
            return 0;
        }
    }
    public void refresh(Date date) {
        lastRefresh = date;
        Log.d("Canteen", "Refreshing dish list for date: " + date.toString());
        new Api(handler).getOrdersForDay(User.getCurrentUser().getToken(), date);
        new Api(handler).getMenu(User.getCurrentUser().getToken(), date);
    }

    public void refresh() {
        if (lastRefresh != null) {
            refresh(lastRefresh);
        } else {
            refresh(new Date());
        }
    }

    public void deleteOrder(String orderId) {
        new Api(handler).deleteOrder(User.getCurrentUser().getToken(), orderId);
    }
}
