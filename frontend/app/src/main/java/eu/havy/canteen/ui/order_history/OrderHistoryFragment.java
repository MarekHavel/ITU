package eu.havy.canteen.ui.order_history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

import eu.havy.canteen.databinding.CardDishHistoryBinding;
import eu.havy.canteen.databinding.FragmentOrderHistoryBinding;
import eu.havy.canteen.model.Dish;

public class OrderHistoryFragment extends Fragment {

    private FragmentOrderHistoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        OrderHistoryViewModel orderHistoryViewModel =
                new ViewModelProvider(this).get(OrderHistoryViewModel.class);

        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false);

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
        dishAdapter adapter = new dishAdapter(orderHistoryViewModel);
        binding.foodCardRecycler.setAdapter(adapter);

        orderHistoryViewModel.getAllDishes().observe(this.getViewLifecycleOwner(), new Observer<List<Dish>>() {
            @Override
            public void onChanged(List<Dish> dishes) {
                adapter.setDishes(dishes);
            }
        });

        return binding.getRoot();
    }

    private class dishAdapter extends RecyclerView.Adapter<dishAdapter.MyViewHolder>{

        //private List<String> items;
        OrderHistoryViewModel src;

        private class MyViewHolder extends RecyclerView.ViewHolder{

            CardDishHistoryBinding binding;//Name of the test_list_item.xml in camel case + "Binding"

            public MyViewHolder(CardDishHistoryBinding b){
                super(b.getRoot());
                binding = b;
            }
        }

        public dishAdapter(OrderHistoryViewModel data){
            this.src = data;
        }

        @NonNull
        @Override
        public dishAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){ //todo handle categories
            return new dishAdapter.MyViewHolder(CardDishHistoryBinding.inflate(getLayoutInflater()));
        }

        @Override
        public void onBindViewHolder(@NonNull dishAdapter.MyViewHolder holder, int position){
            if(src.getDishCount() > 0) {
                Dish current = src.getAllDishes().getValue().get(position);
                if(current != null) {
                    holder.binding.textViewName.setText(current.getName());
                    holder.binding.textViewExtraInfo.setText(current.getExtraInfo());
                    holder.binding.textViewPrice.setText(current.getPrice());

                    String[] dateSplit = current.getPurchaseDate().split("T");
                    holder.binding.textViewDate.setText(dateSplit[0]);
                    holder.binding.getRoot().setOnClickListener(view -> {
                        Log.d("test", "onBindViewHolder: dish id " + current.getId());
                    });
                }
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}