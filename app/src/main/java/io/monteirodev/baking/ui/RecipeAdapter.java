package io.monteirodev.baking.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.R;
import io.monteirodev.baking.models.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final RecipeClickListener mClickListener;
    private List<Recipe> mRecipes;

    public RecipeAdapter(RecipeClickListener clickListener) {
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);

        holder.itemView.setTag(position);
        holder.recipeName.setText(recipe.getName());
        String imageUrl = recipe.getImage();
        // for image testing
//        if (position == 1) {
//            //imageUrl = "http://www.brickcitybears.com/wp-content/uploads/2018/04/2527-best-squirrels-images-on-pinterest.jpg";
//            imageUrl = "https://images.pexels.com/photos/248797/pexels-photo-248797.jpeg";
//        }
        if (imageUrl.isEmpty()) {
            holder.recipeImage.setVisibility(View.GONE);
        } else {
            Picasso.with(holder.recipeImage.getContext())
                    .load(imageUrl)
                    .into(holder.recipeImage);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mRecipes) {
            return 0;
        }
        return mRecipes.size();
    }

    public void setRecipes(List<Recipe> newRecipes) {
        mRecipes = newRecipes;
        notifyDataSetChanged();
    }

    public interface RecipeClickListener {
        void onRecipeClick(int recipeIndex);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_item_name_text_view)
        TextView recipeName;
        @BindView(R.id.recipe_item_image_view)
        ImageView recipeImage;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int recipeIndex = (int) v.getTag();
            mClickListener.onRecipeClick(recipeIndex);
        }
    }

}
