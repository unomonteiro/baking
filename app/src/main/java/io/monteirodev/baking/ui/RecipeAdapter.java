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
    public void onBindViewHolder(@NonNull final RecipeViewHolder holder, int position) {
        holder.recipe = mRecipes.get(position);
        holder.itemView.setTag(holder.recipe.getId());
        holder.recipeName.setText(holder.recipe.getName());
        String imageUrl = holder.recipe.getImage();
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onRecipeClick(holder.recipe);
            }
        });
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
        void onRecipeClick(Recipe recipe);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_item_name_text_view)
        TextView recipeName;
        @BindView(R.id.recipe_item_image_view)
        ImageView recipeImage;
        Recipe recipe;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
