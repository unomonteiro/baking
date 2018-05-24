package io.monteirodev.baking.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import io.monteirodev.baking.database.IngredientColumns;
import io.monteirodev.baking.database.StepColumns;

public class RecipeDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int INGREDIENTS_VIEW_TYPE = 0;
    private static final int STEP_VIEW_TYPE = 1;

    final private StepClickListener mStepClickListener;
    private Cursor mIngredientsCursor;
    private Cursor mStepsCursor;

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
                List<String> ingredientList = getIngredientList(context);
                ingredientsViewHolder.mIngredientListTextView.setText(Html.fromHtml(
                        TextUtils.join("<br>", ingredientList)), TextView.BufferType.SPANNABLE);
                break;
            }
            case STEP_VIEW_TYPE: {
                StepViewHolder stepViewHolder = (StepViewHolder) holder;
                int adjustedPosition = position + (hasIngredientList() ? -1 : 0);
                mStepsCursor.moveToPosition(adjustedPosition);
                int stepNumber = mStepsCursor.getInt(mStepsCursor.getColumnIndex(
                        StepColumns.STEP));
                String shortDescription = mStepsCursor.getString(mStepsCursor.getColumnIndex(StepColumns.SHORT_DESCRIPTION));
                if (stepNumber == 0) {
                    stepViewHolder.mStepTextView.setText(shortDescription);
                } else {
                    String numberShortDescription = context.getString(
                            R.string.number_step, stepNumber, shortDescription);
                    stepViewHolder.mStepTextView.setText(numberShortDescription);
                    stepViewHolder.itemView.setTag(mStepsCursor.getInt(mStepsCursor.getColumnIndex(StepColumns.ID)));
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @NonNull
    private List<String> getIngredientList(Context context) {
        List<String> ingredientList = new ArrayList<>();
        if (hasIngredientList() && context != null) {
            while (mIngredientsCursor.moveToNext()) {
                float quantityFloat = mIngredientsCursor.getFloat(
                        mIngredientsCursor.getColumnIndex(IngredientColumns.QUANTITY));
                DecimalFormat df = new DecimalFormat("0.##");
                String quantity = df.format(quantityFloat);
                String measure = mIngredientsCursor.getString(
                        mIngredientsCursor.getColumnIndex(IngredientColumns.MEASURE));
                String ingredient = mIngredientsCursor.getString(
                        mIngredientsCursor.getColumnIndex(IngredientColumns.INGREDIENT));
                ingredientList.add(context.getString(
                        R.string.quantity_measure_ingredient, quantity, measure, ingredient));
            }
        }
        return ingredientList;
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

    public void swapIngredientsCursor(Cursor newIngredientsCursor) {
        mIngredientsCursor = newIngredientsCursor;
        notifyDataSetChanged();
    }

    public void swapStepsCursor(Cursor newStepsCursor) {
        mStepsCursor = newStepsCursor;
        notifyDataSetChanged();
    }

    private boolean hasIngredientList() {
        return mIngredientsCursor != null && mIngredientsCursor.getCount() > 0;
    }

    private int getStepsCount() {
        if (mStepsCursor == null) {
            return 0;
        }
        return mStepsCursor.getCount();
    }

    public interface StepClickListener {
        void onStepClick(int stepId);
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
            int stepId = (int) v.getTag();
            mStepClickListener.onStepClick(stepId);
        }
    }
}
