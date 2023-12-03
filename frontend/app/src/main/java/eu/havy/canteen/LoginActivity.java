package eu.havy.canteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Objects;

import eu.havy.canteen.databinding.ActivityLoginBinding;
import eu.havy.canteen.model.User;

public class LoginActivity extends AppCompatActivity {

    private static ActivityLoginBinding binding;
    private static LoginActivity instance;
    private static final Handler viewHandler =  new Handler(Objects.requireNonNull(Looper.myLooper()));
    private static final Runnable showProgress = () -> {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginButton.setVisibility(View.GONE);
        visibleFrom = System.currentTimeMillis();
    };

    private static long visibleFrom = 0;
    private static final long MINIMUM_VISIBLE_TIME = 500;
    private static final long WAIT_BEFORE_SHOWING_PROGRESS = 0;

    public static LoginActivity getInstance() {
        return instance;
    }

    public static void logIn() {
        SharedPreferences sharedPreferences = instance.getSharedPreferences("eu.havy.canteen", MODE_PRIVATE);

        //save token of logged in user for automatic login
        sharedPreferences.edit().putString("token", User.getCurrentUser().getToken()).apply();

        //start main activity
        Intent intent = new Intent(instance, MainActivity.class);
        instance.startActivity(intent);
        instance.finish();
        instance = null;
    }

    public void logInFailed() {
        long timePassed = System.currentTimeMillis() - visibleFrom;
        viewHandler.postDelayed(() -> {
            viewHandler.removeCallbacks(showProgress);
            binding.progressBar.setVisibility(View.GONE);
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.loginError.setText("Invalid email or password");
        }, MINIMUM_VISIBLE_TIME - timePassed < 0 ? 0 : MINIMUM_VISIBLE_TIME - timePassed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        //if user is already logged in, skip login activity
        String previousToken = getSharedPreferences("eu.havy.canteen", MODE_PRIVATE).getString("token", null);
        if (previousToken != null) {
            User.login(previousToken);
            logIn();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set previous logged email values
        String[] emails = getSharedPreferences("eu.havy.canteen", MODE_PRIVATE).getStringSet("emails", new HashSet<>()).toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, emails);
        binding.loginEmail.setAdapter(adapter);

        binding.loginButton.setOnClickListener(view -> {
            if (binding.loginEmail.getText().toString().isEmpty() || binding.loginPassword.getText().toString().isEmpty()) {
                binding.loginError.setText("Login and password must not be empty");
                return;
            }
            viewHandler.postDelayed(showProgress, WAIT_BEFORE_SHOWING_PROGRESS);
            binding.loginError.setText("");
            User.login(binding.loginEmail.getText().toString(), binding.loginPassword.getText().toString());
        });
    }
}
