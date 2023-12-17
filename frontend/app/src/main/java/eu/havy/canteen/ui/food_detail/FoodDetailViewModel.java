package eu.havy.canteen.ui.food_detail;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Objects;

import eu.havy.canteen.model.Review;

public class FoodDetailViewModel extends ViewModel {

    private final MutableLiveData<String> reviewDetail;
    private final MutableLiveData<Integer> reviewRating;
    private final MutableLiveData<Integer> submitProgressVisibility;
    private final MutableLiveData<Integer> submitButtonVisibility;
    private final MutableLiveData<Boolean> submitButtonEnabled;
    private final MutableLiveData<List<Review>> reviews;

    public FoodDetailViewModel() {
        reviewDetail = new MutableLiveData<>();
        reviewDetail.setValue("");

        reviewRating = new MutableLiveData<>();
        reviewRating.setValue(0);

        submitProgressVisibility = new MutableLiveData<>();
        submitProgressVisibility.setValue(View.GONE); // Set initial visibility to GONE

        submitButtonVisibility = new MutableLiveData<>();
        submitButtonVisibility.setValue(View.VISIBLE); // Set initial visibility to VISIBLE

        submitButtonEnabled = new MutableLiveData<>();
        submitButtonEnabled.setValue(true); // Set initial enabled to true

        reviews = new MutableLiveData<>();
        reviews.setValue(null);
    }

    public LiveData<String> getReviewDetail() {
        return reviewDetail;
    }

    public LiveData<Integer> getReviewRating() {
        return reviewRating;
    }

    public LiveData<Integer> getSubmitProgressVisibility() {
        return submitProgressVisibility;
    }

    public LiveData<Integer> getSubmitButtonVisibility() {
        return submitButtonVisibility;
    }

    public LiveData<Boolean> getSubmitButtonEnabled() {
        return submitButtonEnabled;
    }

    public void setReviewDetail(String reviewDetail) {
        this.reviewDetail.setValue(reviewDetail);
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating.setValue(reviewRating);
    }

    public void hideProgress() {
        submitProgressVisibility.setValue(View.GONE);
        submitButtonVisibility.setValue(View.VISIBLE);
        submitButtonEnabled.setValue(true);
    }

    public void showProgress() {
        submitProgressVisibility.setValue(View.VISIBLE);
        submitButtonVisibility.setValue(View.INVISIBLE);
        submitButtonEnabled.setValue(false);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews.setValue(reviews);
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public long getReviewCount() {
        try {
            return Objects.requireNonNull(reviews.getValue()).size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

}