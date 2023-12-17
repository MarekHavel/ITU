package eu.havy.canteen.ui.food_detail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.havy.canteen.api.Api;
import eu.havy.canteen.databinding.FragmentFoodDetailBinding;
import eu.havy.canteen.model.Dish;
import eu.havy.canteen.model.User;

public class FoodDetailFragment extends Fragment {

    private FragmentFoodDetailBinding binding;
    private static FoodDetailViewModel viewModel;
    private static final Handler viewHandler =  new Handler(Objects.requireNonNull(Looper.myLooper()));
    private static final Runnable showProgress = () -> {
        //viewModel.showProgress();
        visibleFrom = System.currentTimeMillis();
    };
    private static long visibleFrom = 0;
    private static final long MINIMUM_VISIBLE_TIME = 500;
    private static final long WAIT_BEFORE_SHOWING_PROGRESS = 0;

    private Dish dish;

    Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject;
            String error = "";
            try {
                jsonObject = (JSONObject) msg.obj;
                error = jsonObject.has("exception") ? jsonObject.getString("exception") : (jsonObject.has("message") ? jsonObject.getString("message") : "");
                if (error.isEmpty() ) {
                    switch (Api.Request.getEnum(msg.what)) {
                        case GET_DISH:

                            binding.foodDetail.foodName.setText(jsonObject.getString("name"));
                            binding.foodDetail.foodPrice.setText(String.format("%s Kč", jsonObject.getString("price")));
                            binding.foodDetail.foodRating.setText(String.format("%s/5", jsonObject.getString("averageRating")));
                            binding.foodDetail.foodIngridients.setText(jsonObject.getString("ingredients").replace(",", "\n"));
                            JSONArray allergens = new JSONArray(jsonObject.getString("allergens"));
                            StringBuilder allergensString = new StringBuilder();
                            for (int i = 0; i < allergens.length(); i++) {
                                allergensString.append(allergens.getJSONObject(i).getString("code"));
                                allergensString.append(" ");
                                allergensString.append(allergens.getJSONObject(i).getString("name"));
                                allergensString.append("\n");
                            }
                            binding.foodDetail.foodAllergens.setText(allergensString);
                            //binding.foodWeight.setText(String.format("%s g", jsonObject.getString("weight")));
                            //TODO set image
                            break;
                    }
                }
            } catch (JSONException e) {
                error = msg.obj.toString();
                e.printStackTrace();
            } finally {
                if (!error.isEmpty()) {
                    Log.e("User", "FAILURE, request: " + Api.Request.toString(msg.what) +" code: " + msg.arg1 + " message: " + error);
                }
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(FoodDetailViewModel.class);
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false);

        try {
            new Api(handler).getDish(User.getCurrentUser().getToken(), getArguments().getInt("dishId"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void requestFinished() {
        long timePassed = System.currentTimeMillis() - visibleFrom;
        viewHandler.postDelayed(() -> {
            viewHandler.removeCallbacks(showProgress);
            //viewModel.hideProgress();
        }, MINIMUM_VISIBLE_TIME - timePassed < 0 ? 0 : MINIMUM_VISIBLE_TIME - timePassed);
    }
}