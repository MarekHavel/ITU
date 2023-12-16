package eu.havy.canteen.ui.purchase_food;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import eu.havy.canteen.MainActivity;
import eu.havy.canteen.R;

public class PurchaseFoodFragment extends Fragment {

    private PurchaseFoodViewModel mViewModel;
    public static PurchaseFoodFragment newInstance() {
        return new PurchaseFoodFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int dishId = this.getArguments().getInt("dishId");
        Log.w("Canteen","create, dishId: "+dishId);

        mViewModel = new ViewModelProvider(this).get(PurchaseFoodViewModel.class);

        mViewModel.setDishId(dishId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View binding = inflater.inflate(R.layout.fragment_purchase_food, container, false);

        mViewModel.getDish().observe(this.getViewLifecycleOwner(), dish -> {
            if (dish != null) {
                Log.d("Canteen", "Picked dish changed: " + dish.getName());
                TextView text = binding.findViewById(R.id.buyFoodName);
                text.setText(dish.getName());
                text = binding.findViewById(R.id.buyFoodRating);
                text.setText("0.0"); //todo fixup data source
                text = binding.findViewById(R.id.buyFoodAllergyHeader);
                text.setText(getString(R.string.allergies));
                text = binding.findViewById(R.id.buyFoodRating);
                text.setText(dish.getAllergensLite()); //todo fixup data source
                Button button = binding.findViewById(R.id.buyFoodButton);
                button.setOnClickListener(view->{
                    Log.e("Canteen","Order submission not supported at the moment.");
                    Navigation.findNavController(MainActivity.getInstance(), R.id.nav_host_fragment_activity_main_content).popBackStack();

                });
            } else {
                Log.d("Canteen","Picked dish changed: null");
            }
        });
        return binding;
    }

}