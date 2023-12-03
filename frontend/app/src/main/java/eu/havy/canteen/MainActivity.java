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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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
        instance.startActivity(intent);
        instance.finish();
        instance = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view ->
                {
                    Snackbar.make(view, "Show orders", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        MenuHeaderBinding headerBinding = MenuHeaderBinding.bind(binding.navView.getHeaderView(0));

        headerBinding.logoutButton.setOnClickListener(view -> {
            logOut();
            User.logout();
        });

        //topbar contains menu button instead of back button for these fragments
        int[] menuItems = {R.id.nav_order_food, R.id.nav_recharge_credit, R.id.nav_order_history};
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(menuItems).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_content);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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
        User.getCurrentUser().updateData();

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
                binding.appBarMain.toolbarCredit.setVisibility(View.VISIBLE);
                binding.appBarMain.fab.setVisibility(View.VISIBLE);
                break;
            case "RechargeCreditFragment":
                binding.appBarMain.toolbarCredit.setVisibility(View.VISIBLE);
                binding.appBarMain.fab.setVisibility(View.GONE);
                break;
            default:
                binding.appBarMain.toolbarCredit.setVisibility(View.GONE);
                binding.appBarMain.fab.setVisibility(View.GONE);
                break;
        }
    }

    public static void updateUserInfo() {
        MenuHeaderBinding headerBinding = MenuHeaderBinding.bind(instance.binding.navView.getHeaderView(0));
        headerBinding.userName.setText(User.getCurrentUser().getUsername());
        headerBinding.userEmail.setText(User.getCurrentUser().getEmail());
        //headerBinding.priceCategory.setText(User.getCurrentUser().getPriceCategory());
    }

    public static void updateCredit() {
        instance.binding.appBarMain.toolbarCredit.setText(User.getCurrentUser().getCredit());
    }
}