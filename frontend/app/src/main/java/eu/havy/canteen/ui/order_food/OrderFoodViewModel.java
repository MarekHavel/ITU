package eu.havy.canteen.ui.order_food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderFoodViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OrderFoodViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}