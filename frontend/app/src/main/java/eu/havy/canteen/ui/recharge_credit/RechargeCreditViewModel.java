// author: Marek Gergel <xgerge01@vutbr.cz>
package eu.havy.canteen.ui.recharge_credit;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RechargeCreditViewModel extends ViewModel {

    private final MutableLiveData<Integer> rechargeAmount;
    private final MutableLiveData<Integer> rechargeProgressVisibility;
    private final MutableLiveData<Integer> rechargeButtonVisibility;


    public RechargeCreditViewModel() {
        rechargeAmount = new MutableLiveData<>();
        rechargeAmount.setValue(0);

        rechargeProgressVisibility = new MutableLiveData<>();
        rechargeProgressVisibility.setValue(View.GONE); // Set initial visibility to GONE

        rechargeButtonVisibility = new MutableLiveData<>();
        rechargeButtonVisibility.setValue(View.VISIBLE); // Set initial visibility to VISIBLE

    }

    public LiveData<Integer> getRechargeProgressVisibility() {
        return rechargeProgressVisibility;
    }

    public LiveData<Integer> getRechargeButtonVisibility() {
        return rechargeButtonVisibility;
    }

    public LiveData<Integer> getRechargeAmount() {
        return rechargeAmount;
    }

    public Integer getRechargeAmountValue() {
        return rechargeAmount.getValue() == null ? 0 : rechargeAmount.getValue();
    }


    public void hideProgress() {
        rechargeProgressVisibility.setValue(View.GONE);
        rechargeButtonVisibility.setValue(View.VISIBLE);
    }

    public void showProgress() {
        rechargeProgressVisibility.setValue(View.VISIBLE);
        rechargeButtonVisibility.setValue(View.GONE);
    }

    public void setRechargeAmountValue(Integer rechargeAmountText) {
        this.rechargeAmount.setValue(rechargeAmountText);
    }

    public void addToRechargeAmountValue(Integer rechargeAmountToAdd) {
        this.rechargeAmount.setValue(getRechargeAmountValue() + rechargeAmountToAdd);
    }
}