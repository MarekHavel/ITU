package eu.havy.canteen.ui.recharge_credit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RechargeCreditViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RechargeCreditViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}