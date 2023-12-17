package eu.havy.canteen.ui.order_food;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.havy.canteen.MainActivity;
import eu.havy.canteen.R;
import eu.havy.canteen.databinding.CardDishBinding;
import eu.havy.canteen.databinding.FragmentOrderFoodBinding;
import eu.havy.canteen.model.Dish;

public class OrderFoodFragment extends Fragment {

    private FragmentOrderFoodBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        OrderFoodViewModel homeViewModel = new ViewModelProvider(this).get(OrderFoodViewModel.class);

        binding = FragmentOrderFoodBinding.inflate(inflater, container, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext()){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                lp.width = getWidth();
                lp.topMargin = 16;
                lp.bottomMargin = 16;
                return true;
            }
        };
        binding.foodCardRecycler.setLayoutManager(layoutManager);
        binding.foodCardRecycler.setHasFixedSize(true);
        dishAdapter adapter = new dishAdapter(homeViewModel);
        binding.foodCardRecycler.setAdapter(adapter);

        homeViewModel.getAllDishes().observe(this.getViewLifecycleOwner(), new Observer<List<Dish>>() {
            @Override
            public void onChanged(List<Dish> dishes) {
                adapter.setDishes(dishes);
            }
        });

        return binding.getRoot();
    }

    private class dishAdapter extends RecyclerView.Adapter<dishAdapter.MyViewHolder>{

        //private List<String> items;
        OrderFoodViewModel src;

        private class MyViewHolder extends RecyclerView.ViewHolder{

            CardDishBinding binding; //Name of the test_list_item.xml in camel case + "Binding"

            public MyViewHolder(CardDishBinding b){
                super(b.getRoot());
                binding = b;
            }
        }

        public dishAdapter(OrderFoodViewModel data){
            this.src = data;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            //todo handle categories
            //todo handle ordered food
            return new MyViewHolder(CardDishBinding.inflate(getLayoutInflater()));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position){

            if(src.getDishCount() > 0) {
                Dish current = src.getAllDishes().getValue().get(position);
                holder.binding.textViewName.setText(current.getName());
                holder.binding.textViewExtraInfo.setText(current.getExtraInfo());
                holder.binding.textViewPrice.setText(current.getPrice());
                holder.binding.textViewCount.setText(current.getRemainingAmount());
                holder.binding.purchaseButton.setOnClickListener(view->{
                    Log.d("clickListener","Kliknuto na nákup obědu " + current.getId());
                    Bundle bundle = new Bundle();
                    bundle.putInt("dishId", current.getId());
                    Navigation.findNavController(MainActivity.getInstance(),
                                    R.id.nav_host_fragment_activity_main_content)
                            .navigate(R.id.action_nav_order_food_to_purchaseFoodFragment,bundle);
                });
                holder.binding.getRoot().setOnClickListener(view->{
                    Log.d("clickListener","Kliknuto na detail obědu " + current.getId());
                });
            }
        }

        public void setDishes(List<Dish> dishes){
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount(){
            return (int) src.getDishCount();
        }
    }

    /*
    //todo make use of
    private class orderAdapter extends RecyclerView.Adapter<dishAdapter.MyViewHolder> {

        //private List<String> items;
        OrderFoodViewModel src;

        private class MyViewHolder extends RecyclerView.ViewHolder {

            CardDishBinding binding; //Name of the test_list_item.xml in camel case + "Binding"

            public MyViewHolder(CardDishBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

        public orderAdapter(OrderFoodViewModel data) {
            this.src = data;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //todo handle categories
            //todo handle ordered food
            return new MyViewHolder(CardDishBinding.inflate(getLayoutInflater()));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            if (src.getDishCount() > 0) {
                Dish current = src.getAllDishes().getValue().get(position);
                holder.binding.textViewName.setText(current.getName());
                holder.binding.textViewExtraInfo.setText(current.getExtraInfo());
                holder.binding.textViewPrice.setText(current.getPrice());
                holder.binding.textViewCount.setText(current.getRemainingAmount());
                holder.binding.purchaseButton.setOnClickListener(view -> {
                    Log.d("clickListener", "Kliknuto na nákup obědu " + current.getId());
                    Bundle bundle = new Bundle();
                    bundle.putInt("dishId", current.getId());
                    Navigation.findNavController(MainActivity.getInstance(),
                                    R.id.nav_host_fragment_activity_main_content)
                            .navigate(R.id.action_nav_order_food_to_purchaseFoodFragment, bundle);
                });
                holder.binding.getRoot().setOnClickListener(view -> {
                    Log.d("clickListener", "Kliknuto na detail obědu " + current.getId());
                });
            }
        }
    }
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}