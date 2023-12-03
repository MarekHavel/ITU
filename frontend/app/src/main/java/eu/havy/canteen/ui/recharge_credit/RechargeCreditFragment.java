package eu.havy.canteen.ui.recharge_credit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import eu.havy.canteen.databinding.FragmentRechargeCreditBinding;
import eu.havy.canteen.model.User;

public class RechargeCreditFragment extends Fragment {

    private FragmentRechargeCreditBinding binding;

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 200) {
                Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "FAILURE, code: " + msg.what, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RechargeCreditViewModel rechargeCreditViewModel =
                new ViewModelProvider(this).get(RechargeCreditViewModel.class);

        binding = FragmentRechargeCreditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText creditField = binding.rechargeAmount;
        final Button submit = binding.rechargeButton;

        submit.setOnClickListener(l -> {
            if (getIntegerFromString(creditField.getText().toString()) == 0) {
                Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }
            User.getCurrentUser().addCredit(Integer.parseInt(String.valueOf(creditField.getText())));
            creditField.setText("");
        });

        binding.plus100.setOnClickListener(l -> {
            creditField.setText(String.valueOf(getIntegerFromString(creditField.getText().toString()) + 100));
        });

        binding.plus200.setOnClickListener(l -> {
            creditField.setText(String.valueOf(getIntegerFromString(creditField.getText().toString()) + 200));
        });

        binding.plus500.setOnClickListener(l -> {
            creditField.setText(String.valueOf(getIntegerFromString(creditField.getText().toString()) + 500));
        });

        binding.clear.setOnClickListener(l -> {
            creditField.setText("");
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static Integer getIntegerFromString(String string) {
        try {
            return Integer.parseInt(String.valueOf(string));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}