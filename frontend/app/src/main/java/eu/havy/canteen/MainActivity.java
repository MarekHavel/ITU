package eu.havy.canteen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.havy.canteen.api.Api;
import eu.havy.canteen.databinding.ActivityMainBinding;
import eu.havy.canteen.ui.order_food.OrderFoodFragment;
import eu.havy.canteen.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(Objects.requireNonNull(((Bundle) (msg.obj)).getString("response")));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String str;
            if (msg.what == 200) {
                str = jsonObject.toString();
            } else {
                try {
                    str = jsonObject.getString("errorMessage");
                } catch (JSONException e) {
                    str = "Invalid JSON response";
                }
            }
            Snackbar.make(binding.getRoot(), str, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view ->
                {
                    new Api(handler).authenticateUser();
                });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_content);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateToolbarView() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main_content).getChildFragmentManager().getFragments().get(0);
        if (currentFragment instanceof OrderFoodFragment) {
            binding.appBarMain.toolbarCredit.setVisibility(View.VISIBLE);
            binding.appBarMain.fab.setVisibility(View.VISIBLE);
        } else {
            binding.appBarMain.toolbarCredit.setVisibility(View.GONE);
            binding.appBarMain.fab.setVisibility(View.GONE);
        }
    }
}