package eu.havy.canteen.ui.order_food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import eu.havy.canteen.databinding.FragmentOrderFoodBinding;

public class OrderFoodFragment extends Fragment {

    private FragmentOrderFoodBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrderFoodViewModel homeViewModel =
                new ViewModelProvider(this).get(OrderFoodViewModel.class);

        binding = FragmentOrderFoodBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textOrderFood;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}