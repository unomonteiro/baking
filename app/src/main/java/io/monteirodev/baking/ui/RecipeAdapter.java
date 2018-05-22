package io.monteirodev.baking.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.R;
import io.monteirodev.baking.database.RecipeColumns;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final RecipeClickListener mClickListener;
    private Cursor mCursor;

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
        if (mCursor == null || !mCursor.moveToPosition(position)) return;

        String name = mCursor.getString(mCursor.getColumnIndex(RecipeColumns.NAME));
        holder.recipeName.setText(name);
        String imageUrl = mCursor.getString(mCursor.getColumnIndex(RecipeColumns.IMAGE));
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
        if (null == mCursor) {
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface RecipeClickListener {
        void onRecipeClick(int recipeId);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_item_name_text_view)
        TextView recipeName;
        @BindView(R.id.recipe_item_image_view)
        ImageView recipeImage;


        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int recipeId = mCursor.getInt(mCursor.getColumnIndex(RecipeColumns.ID));
            mClickListener.onRecipeClick(recipeId);
        }
    }

}
