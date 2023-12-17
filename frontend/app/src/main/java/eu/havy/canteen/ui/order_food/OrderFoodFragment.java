package eu.havy.canteen.ui.order_food;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eu.havy.canteen.R;
import eu.havy.canteen.api.Api;
import eu.havy.canteen.databinding.CardDishBinding;
import eu.havy.canteen.databinding.FragmentOrderFoodBinding;
import eu.havy.canteen.model.Dish;
import eu.havy.canteen.model.User;

public class OrderFoodFragment extends Fragment {

    private FragmentOrderFoodBinding binding;
    public MutableLiveData<Date> selectedDate;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOrderFoodBinding.inflate(inflater, container, false);
        if(User.getCurrentUser() == null) return binding.getRoot();

        selectedDate = new MutableLiveData<>();

        selectedDate.setValue(Calendar.getInstance().getTime());

        /* // use me if you need to set date manually for testing
        String dateString = "2023-11-13";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateString);
            selectedDate.setValue(date);
            Log.d("Canteen", "Selected date: " + sdf.format(Objects.requireNonNull(selectedDate.getValue())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        OrderFoodViewModel orderFoodViewModel = new ViewModelProvider(this).get(OrderFoodViewModel.class);
        orderFoodViewModel.refresh(selectedDate.getValue());

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

        //todo move dates around

        binding.foodCardRecycler.setLayoutManager(layoutManager);
        binding.foodCardRecycler.setHasFixedSize(true);
        dishAdapter adapter = new dishAdapter(orderFoodViewModel);
        binding.foodCardRecycler.setAdapter(adapter);

        orderAdapter orders = new orderAdapter(orderFoodViewModel);
        binding.orderCardRecycler.setAdapter(orders);

        orderFoodViewModel.getAllDishes().observe(this.getViewLifecycleOwner(), new Observer<List<Dish>>() {
            @Override
            public void onChanged(List<Dish> dishes) {
                adapter.setDishes(dishes);
            }
        });

        selectedDate.observe(this.getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                DateFormat sdf = DateFormat.getDateInstance();
                String dateText = sdf.format(date);
                Log.d("Canteen", "Selected date: " + dateText);
                binding.Date.setText(dateText);
                orderFoodViewModel.refresh(date);
            }
        });

        binding.dateOneDown.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate.getValue());
            cal.add(Calendar.DATE, -1);
            selectedDate.setValue(cal.getTime());
        });
        binding.dateOneUp.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate.getValue());
            cal.add(Calendar.DATE, 1);
            selectedDate.setValue(cal.getTime());
        });

        return binding.getRoot();
    }

    public LiveData<Date> getSelectedDate() {
        return selectedDate;
    }

    public void selectDate(Date date) {
        selectedDate.setValue(date);
    }

    //todo fixup - update of dataset does nothing
    private class dishAdapter extends RecyclerView.Adapter<dishAdapter.MyViewHolder> {
        OrderFoodViewModel src;
        private class MyViewHolder extends RecyclerView.ViewHolder {
            CardDishBinding binding;
            public MyViewHolder(CardDishBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

        public dishAdapter(OrderFoodViewModel data) {
            this.src = data;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(CardDishBinding.inflate(getLayoutInflater()));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if(src.getDishCount() > 0) {
                Dish current = src.getAllDishes().getValue().get(position);
                holder.binding.textViewName.setText(current.getName());
                holder.binding.textViewExtraInfo.setText(current.getExtraInfo());
                holder.binding.textViewPrice.setText(current.getPrice());
                holder.binding.textViewCount.setText(current.getRemainingAmount());
                Bundle bundle = new Bundle();
                bundle.putInt("dishId", current.getId());
                bundle.putString("date", Api.DateToApiDate(selectedDate.getValue()));
                holder.binding.purchaseButton.setOnClickListener(view->{
                    Log.d("clickListener","Kliknuto na nákup obědu " + current.getId());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_content).navigate(R.id.nav_purchase_food, bundle);
                });
                holder.binding.getRoot().setOnClickListener(view->{
                    Log.d("clickListener","Kliknuto na detail obědu " + current.getId());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_content).navigate(R.id.nav_food_detail, bundle);
                });
            }
        }

        public void setDishes(List<Dish> dishes) {
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return (int) src.getDishCount();
        }

    }

    private class orderAdapter extends RecyclerView.Adapter<orderAdapter.MyViewHolder> {

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
        public orderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(CardDishBinding.inflate(getLayoutInflater()));
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}