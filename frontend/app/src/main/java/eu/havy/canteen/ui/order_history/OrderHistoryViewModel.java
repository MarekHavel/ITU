package eu.havy.canteen.ui.order_history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderHistoryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OrderHistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}