package eu.havy.canteen.ui.recharge_credit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import eu.havy.canteen.databinding.FragmentRechargeCreditBinding;

public class RechargeCreditFragment extends Fragment {

    private FragmentRechargeCreditBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RechargeCreditViewModel galleryViewModel =
                new ViewModelProvider(this).get(RechargeCreditViewModel.class);

        binding = FragmentRechargeCreditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textRechargeCredit;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}