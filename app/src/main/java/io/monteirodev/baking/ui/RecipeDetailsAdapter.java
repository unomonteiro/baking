package io.monteirodev.baking.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Ingredient;
import io.monteirodev.baking.models.Step;

public class RecipeDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int INGREDIENTS_VIEW_TYPE = 0;
    private static final int STEP_VIEW_TYPE = 1;

    final private StepClickListener mStepClickListener;
    private List<Ingredient> mIngredients;
    private List<Step> mSteps;

    public RecipeDetailsAdapter(StepClickListener stepClickListener) {
        mStepClickListener = stepClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;

        switch (viewType) {

            case INGREDIENTS_VIEW_TYPE:
                layoutId = R.layout.recipe_detail_ingredients_item;
                break;

            case STEP_VIEW_TYPE:
                layoutId = R.layout.recipe_detail_step_item;
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);

        switch (viewType) {
            case INGREDIENTS_VIEW_TYPE: {
                return new IngredientsViewHolder(view);
            }
            case STEP_VIEW_TYPE: {
                return new StepViewHolder(view);
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        int viewType = getItemViewType(position);
        switch (viewType) {
            case INGREDIENTS_VIEW_TYPE: {
                IngredientsViewHolder ingredientsViewHolder = (IngredientsViewHolder) holder;
                List<String> ingredientList = getIngredientsString(context);
                ingredientsViewHolder.mIngredientListTextView.setText(fromHtml(
                        TextUtils.join("<br>", ingredientList)), TextView.BufferType.SPANNABLE);
                break;
            }
            case STEP_VIEW_TYPE: {
                StepViewHolder stepViewHolder = (StepViewHolder) holder;
                int adjustedPosition = position + (hasIngredientList() ? -1 : 0);
                Step step = mSteps.get(adjustedPosition);

                int stepOrder = step.getId();
                stepViewHolder.itemView.setTag(stepOrder);
                String shortDescription = step.getShortDescription();
                String stepText = shortDescription;
                if (stepOrder > 0) {
                    stepText = context.getString(R.string.number_step, stepOrder, shortDescription);
                }
                stepViewHolder.mStepTextView.setText(stepText);

                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @NonNull
    private List<String> getIngredientsString(Context context) {
        List<String> ingredientsString = new ArrayList<>();
        if (hasIngredientList() && context != null) {
            for (Ingredient ingredientObj : mIngredients) {
                DecimalFormat df = new DecimalFormat("0.##");
                String quantity = df.format((float) ingredientObj.getQuantity());
                String measure = ingredientObj.getMeasure();
                String ingredient = ingredientObj.getIngredient();
                ingredientsString.add(context.getString(
                        R.string.quantity_measure_ingredient, quantity, measure, ingredient));
            }
        }
        return ingredientsString;
    }

    /**
     * Html.fromHtml deprecated in Android N
     * https://stackoverflow.com/a/37905107/6997703
     */
    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    @Override
    public int getItemCount() {
        int totalItems = 0;
        if (hasIngredientList()) {
            totalItems++;
        }
        totalItems += getStepsCount();
        return totalItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasIngredientList()) {
            return INGREDIENTS_VIEW_TYPE;
        } else {
            return STEP_VIEW_TYPE;
        }
    }

    public void setIngredients(List<Ingredient> newIngredients) {
        mIngredients = newIngredients;
        notifyDataSetChanged();
    }

    public void setSteps(List<Step> newSteps) {
        mSteps = newSteps;
        notifyDataSetChanged();
    }

    private boolean hasIngredientList() {
        return mIngredients != null && mIngredients.size() > 0;
    }

    private int getStepsCount() {
        if (mSteps == null) {
            return 0;
        }
        return mSteps.size();
    }

    public interface StepClickListener {
        void onStepClick(int stepIndex);
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredients_title_text_view)
        TextView mIngredientsTitleTextView;

        @BindView(R.id.ingredient_list_text_view)
        TextView mIngredientListTextView;

        IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_step_text_view)
        TextView mStepTextView;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int stepIndex = (int) v.getTag();
            mStepClickListener.onStepClick(stepIndex);
        }
    }
}
