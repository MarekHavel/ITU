package eu.havy.canteen.ui.order_food;

import static java.security.AccessController.getContext;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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

import eu.havy.canteen.api.Api;
import eu.havy.canteen.model.Dish;

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
            try {
                jsonArray = new JSONArray((String) Objects.requireNonNull(((Bundle) (msg.obj)).get("response")));
            } catch (JSONException e) {
                throw new RuntimeException(e);
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

        new Api(handler).getDishes();
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
