package eu.havy.canteen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashSet;
import java.util.Set;

import eu.havy.canteen.databinding.ActivityMainBinding;
import eu.havy.canteen.databinding.MenuHeaderBinding;
import eu.havy.canteen.model.User;
import eu.havy.canteen.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static MainActivity getInstance() {
        return instance;
    }

    public static void logOut() {
        SharedPreferences sharedPreferences = instance.getSharedPreferences("eu.havy.canteen", MODE_PRIVATE);

        //remove token from shared preferences, so that user is not automatically logged in next time
        sharedPreferences.edit().remove("token").apply();

        //save email of logged in user for future logins
        Set<String> emailsSaved = sharedPreferences.getStringSet("emails", null);
        Set<String> emails = emailsSaved != null ? new HashSet<>(emailsSaved) : new HashSet<>();
        emails.add(User.getCurrentUser().getEmail());
        sharedPreferences.edit().putStringSet("emails", emails).apply();

        //start login activity
        Intent intent = new Intent(instance, LoginActivity.class);
        instance.finish();
        instance.startActivity(intent);
        instance = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        //set up logout button
        MenuHeaderBinding headerBinding = MenuHeaderBinding.bind(binding.navView.getHeaderView(0));
        headerBinding.logoutButton.setOnClickListener(view -> {
            logOut();
            User.logout();
        });

        //topbar contains menu button instead of back button for these fragments
        int[] menuItems = {R.id.nav_order_food, R.id.nav_recharge_credit, R.id.nav_order_history};
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(menuItems).setOpenableLayout(binding.drawerLayout).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_content);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //credit toolbar click listener
        binding.appBarMain.toolbarCredit.setOnClickListener(view -> {
            if (navController.getCurrentDestination().getId() != R.id.nav_recharge_credit) {
                navController.navigate(R.id.nav_recharge_credit);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //update color scheme
        SettingsFragment.updateColorScheme(getSharedPreferences("eu.havy.canteen_preferences", MODE_PRIVATE));

        //hide fab and credit toolbar when not on order food fragment
        getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main_content).getChildFragmentManager()
                .addOnBackStackChangedListener(this::updateToolbarView);
        updateToolbarView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //load user data
        if (User.getCurrentUser() != null) {
            User.getCurrentUser().updateData();
        }

        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_content);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateToolbarView() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main_content).getChildFragmentManager().getFragments().get(0);
        switch (currentFragment.getClass().getSimpleName()) {
            case "OrderFoodFragment":
            case "RechargeCreditFragment":
                binding.appBarMain.toolbarCredit.setVisibility(View.VISIBLE);
                break;
            default:
                binding.appBarMain.toolbarCredit.setVisibility(View.GONE);
                break;
        }
    }

    public static void updateUserInfo() {
        MenuHeaderBinding headerBinding = MenuHeaderBinding.bind(instance.binding.navView.getHeaderView(0));
        headerBinding.userName.setText(User.getCurrentUser().getUsername());
        headerBinding.userEmail.setText(User.getCurrentUser().getEmail());
        //headerBinding.priceCategory.setText(User.getCurrentUser().getPriceCategory()); //todo fixup
    }

    public static void updateCredit() {
        instance.binding.appBarMain.toolbarCredit.setText(String.format("%s Kč", User.getCurrentUser().getCredit()));
    }

    public static void updateCanteenInfo(String name, String email, String phone, String openingHours, String address) {
        instance.binding.canteenName.setText(name);
        instance.binding.canteenOpeningHours.setText(openingHours);
        instance.binding.canteenPhone.setText(phone);
        instance.binding.canteenEmail.setText(email);
        instance.binding.canteenAddress.setText(address.replaceAll(",", "\n"));
    }
}