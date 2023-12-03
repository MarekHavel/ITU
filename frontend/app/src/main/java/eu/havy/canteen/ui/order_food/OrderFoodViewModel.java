package eu.havy.canteen.ui.order_food;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eu.havy.canteen.LoginActivity;
import eu.havy.canteen.MainActivity;
import eu.havy.canteen.api.Api;
import eu.havy.canteen.model.Dish;
import eu.havy.canteen.model.User;

public class OrderFoodViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Dish>> dishes; //todo implement
    private final MutableLiveData<String> mText;
    private final MutableLiveData<Integer> mCount;


    //todo pull out of viewmodel, this has no place here
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
                            obj.getInt("price"), obj.getInt("itemsLeft"),obj.getInt("weight"));
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
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        mCount = new MutableLiveData<>();
        mCount.setValue(3);

        dishes = new MutableLiveData<>();
        dishes.setValue(null);

        new Api(handler).getMenu(User.getCurrentUser().getToken(), "2023-11-13");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public Integer getCount() {
        return mCount.getValue();
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
}
