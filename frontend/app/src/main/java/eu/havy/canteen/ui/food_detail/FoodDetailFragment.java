// author: Marek Gergel <xgerge01@vutbr.cz>
package eu.havy.canteen.ui.food_detail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eu.havy.canteen.MainActivity;
import eu.havy.canteen.R;
import eu.havy.canteen.api.Api;
import eu.havy.canteen.databinding.CardFoodReviewBinding;
import eu.havy.canteen.databinding.FragmentFoodDetailBinding;
import eu.havy.canteen.model.Review;
import eu.havy.canteen.model.User;

public class FoodDetailFragment extends Fragment {

    private FragmentFoodDetailBinding binding;
    private static FoodDetailViewModel viewModel;
    private static final Handler viewHandler =  new Handler(Objects.requireNonNull(Looper.myLooper()));
    private static final Runnable showProgress = () -> {
        viewModel.showProgress();
        visibleFrom = System.currentTimeMillis();
    };
    private static long visibleFrom = 0;
    private static final long MINIMUM_VISIBLE_TIME = 500;
    private static final long WAIT_BEFORE_SHOWING_PROGRESS = 0;

    Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject;
            String error = "";
            try {
                jsonObject = (JSONObject) msg.obj;
                error = jsonObject.has("exception") ? jsonObject.getString("exception") : (jsonObject.has("message") ? jsonObject.getString("message") : "");
                if (error.isEmpty() ) {
                    switch (Api.Request.getEnum(msg.what)) {
                        case GET_DISH:
                            binding.foodDetail.foodName.setText(jsonObject.getString("name"));
                            binding.foodDetail.foodPrice.setText(String.format("%s Kč", jsonObject.getString("price")));
                            binding.foodDetail.foodIngridients.setText(jsonObject.getString("ingredients").replace(", ", "\n"));
                            JSONArray allergens = new JSONArray(jsonObject.getString("allergens"));
                            StringBuilder allergensString = new StringBuilder();
                            for (int i = 0; i < allergens.length(); i++) {
                                allergensString.append(allergens.getJSONObject(i).getString("code"));
                                allergensString.append(" ");
                                allergensString.append(allergens.getJSONObject(i).getString("name"));
                                allergensString.append("\n");
                            }
                            binding.foodDetail.foodAllergens.setText(allergensString);
                            //binding.foodWeight.setText(String.format("%s g", jsonObject.getString("weight")));
                            //TODO set image
                            break;
                        case ADD_REVIEW:
                            new Api(this).getGeneralRating(User.getCurrentUser().getToken(), getArguments().getInt("dishId"));
                            break;
                        case GET_GENERAL_RATING:
                            binding.foodDetail.foodRating.setText(String.format("%s/5", jsonObject.getString("averageRating")));
                            List<Review> reviews = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("reviews"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Review review = new Review(obj.getString("username"), obj.getString("reviewText"), obj.getInt("rating"));
                                reviews.add(review);
                            }
                            viewModel.setReviews(reviews);
                            break;
                    }
                }
            } catch (Exception e) {
                error = msg.obj.toString();
                e.printStackTrace();
            } finally {
                switch (Api.Request.getEnum(msg.what)) {
                    case ADD_REVIEW:
                        if (msg.arg1 == HttpURLConnection.HTTP_BAD_REQUEST) {
                            if (MainActivity.getInstance() != null) {
                                MainActivity.logOut();
                            }
                            User.logout();
                        } else {
                            requestFinished();
                        }
                        break;
                }

                if (!error.isEmpty()) {
                    Log.e("User", "FAILURE, request: " + Api.Request.toString(msg.what) +" code: " + msg.arg1 + " message: " + error);
                }
            }
        }
    };

    private class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {
        private class ReviewHolder extends RecyclerView.ViewHolder {
            CardFoodReviewBinding binding;
            public ReviewHolder(CardFoodReviewBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        @NonNull
        @Override
        public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReviewHolder(CardFoodReviewBinding.inflate(getLayoutInflater()));
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
            try {
                Review current = FoodDetailFragment.viewModel.getReviews().getValue().get(position);
                holder.binding.reviewer.setText(current.getUsername());
                holder.binding.rating.setText(String.format("%s/5", current.getRating()));
                if (current.getDetail() != null && !current.getDetail().isEmpty()) {
                    holder.binding.reviewDetail.setVisibility(View.VISIBLE);
                    holder.binding.reviewDetail.setText(current.getDetail());
                } else {
                    holder.binding.reviewDetail.setVisibility(View.GONE);
                }
            } catch (NullPointerException ignored) {}
        }

        public void updateReviews(List<Review> reviews) {
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return (int) FoodDetailFragment.viewModel.getReviewCount();
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(FoodDetailViewModel.class);
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false);

        ReviewsAdapter adapter = new ReviewsAdapter();
        binding.foodReviewList.reviewListRecycler.setAdapter(adapter);
        binding.foodReviewList.reviewListRecycler.setHasFixedSize(true);
        binding.foodReviewList.reviewListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getReviews().observe(getViewLifecycleOwner(), adapter::updateReviews);

        try {
            new Api(handler).getDish(User.getCurrentUser().getToken(), getArguments().getInt("dishId"));
            new Api(handler).getGeneralRating(User.getCurrentUser().getToken(), getArguments().getInt("dishId"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        binding.foodReview.reviewStars.star1.setOnClickListener(l -> updateRating(1));
        binding.foodReview.reviewStars.star2.setOnClickListener(l -> updateRating(2));
        binding.foodReview.reviewStars.star3.setOnClickListener(l -> updateRating(3));
        binding.foodReview.reviewStars.star4.setOnClickListener(l -> updateRating(4));
        binding.foodReview.reviewStars.star5.setOnClickListener(l -> updateRating(5));

        binding.foodReview.reviewSubmit.setOnClickListener(l -> {
            if (viewModel.getReviewRating().getValue() == 0) {
                Toast.makeText(getContext(), "Please select star rating", Toast.LENGTH_SHORT).show();
                return;
            }
            viewHandler.postDelayed(showProgress, WAIT_BEFORE_SHOWING_PROGRESS);

            viewModel.setReviewDetail(binding.foodReview.reviewDetail.getText().toString());
            new Api(handler).addReview(User.getCurrentUser().getToken(), getArguments().getInt("dishId"), viewModel.getReviewRating().getValue(), viewModel.getReviewDetail().getValue());

            viewModel.setReviewDetail("");
            updateRating(0);
        });

        viewModel.getReviewDetail().observe(getViewLifecycleOwner(), value -> binding.foodReview.reviewDetail.setText(value));
        viewModel.getSubmitProgressVisibility().observe(getViewLifecycleOwner(), visibility -> binding.foodReview.progressBar.setVisibility(visibility));
        viewModel.getSubmitButtonVisibility().observe(getViewLifecycleOwner(), visibility -> binding.foodReview.reviewSubmit.setVisibility(visibility));
        viewModel.getSubmitButtonEnabled().observe(getViewLifecycleOwner(), enabled -> binding.foodReview.reviewSubmit.setEnabled(enabled));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void requestFinished() {
        long timePassed = System.currentTimeMillis() - visibleFrom;
        viewHandler.postDelayed(() -> {
            viewHandler.removeCallbacks(showProgress);
            viewModel.hideProgress();
        }, MINIMUM_VISIBLE_TIME - timePassed < 0 ? 0 : MINIMUM_VISIBLE_TIME - timePassed);
    }

    private void updateRating(int rating) {
        binding.foodReview.reviewStars.star1.setImageResource(rating >= 1 ? R.drawable.star_full : R.drawable.star_outline);
        binding.foodReview.reviewStars.star2.setImageResource(rating >= 2 ? R.drawable.star_full : R.drawable.star_outline);
        binding.foodReview.reviewStars.star3.setImageResource(rating >= 3 ? R.drawable.star_full : R.drawable.star_outline);
        binding.foodReview.reviewStars.star4.setImageResource(rating >= 4 ? R.drawable.star_full : R.drawable.star_outline);
        binding.foodReview.reviewStars.star5.setImageResource(rating >= 5 ? R.drawable.star_full : R.drawable.star_outline);
        viewModel.setReviewRating(rating);
    }
}