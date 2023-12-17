// author: Marek Gergel <xgerge01@vutbr.cz>
package eu.havy.canteen.ui.recharge_credit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import eu.havy.canteen.databinding.FragmentRechargeCreditBinding;
import eu.havy.canteen.model.User;

public class RechargeCreditFragment extends Fragment {

    private FragmentRechargeCreditBinding binding;
    private static RechargeCreditViewModel viewModel;
    private static final Handler viewHandler =  new Handler(Objects.requireNonNull(Looper.myLooper()));
    private static final Runnable showProgress = () -> {
        viewModel.showProgress();
        visibleFrom = System.currentTimeMillis();
    };
    private static long visibleFrom = 0;
    private static final long MINIMUM_VISIBLE_TIME = 500;
    private static final long WAIT_BEFORE_SHOWING_PROGRESS = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RechargeCreditViewModel.class);
        binding = FragmentRechargeCreditBinding.inflate(inflater, container, false);

        binding.rechargeButton.setOnClickListener(l -> {
            viewModel.setRechargeAmountValue(getIntegerFromString(binding.rechargeAmount.getText().toString()));
            if (viewModel.getRechargeAmountValue() == 0) {
                Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }
            viewHandler.postDelayed(showProgress, WAIT_BEFORE_SHOWING_PROGRESS);
            User.getCurrentUser().addCredit(viewModel.getRechargeAmountValue());
            viewModel.setRechargeAmountValue(0);
        });

        binding.plus100.setOnClickListener(l -> viewModel.addToRechargeAmountValue(100));
        binding.plus200.setOnClickListener(l -> viewModel.addToRechargeAmountValue(200));
        binding.plus500.setOnClickListener(l -> viewModel.addToRechargeAmountValue(500));
        binding.clear.setOnClickListener(l -> viewModel.setRechargeAmountValue(0));

        viewModel.getRechargeAmount().observe(getViewLifecycleOwner(), value -> binding.rechargeAmount.setText(getStringFromInteger(value)));
        viewModel.getRechargeProgressVisibility().observe(getViewLifecycleOwner(), visibility -> binding.progressBar.setVisibility(visibility));
        viewModel.getRechargeButtonVisibility().observe(getViewLifecycleOwner(), visibility -> binding.rechargeButton.setVisibility(visibility));

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
            viewModel.hideProgress();
        }, MINIMUM_VISIBLE_TIME - timePassed < 0 ? 0 : MINIMUM_VISIBLE_TIME - timePassed);
    }

    private static String getStringFromInteger(Integer integer) {
        if (integer == null || integer == 0) {
            return "";
        } else {
            return String.valueOf(integer);
        }
    }

    private static int getIntegerFromString(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return 0;
        }
    }
}