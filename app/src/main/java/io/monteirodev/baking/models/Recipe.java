package io.monteirodev.baking.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import net.simonvt.schematic.Cursors;

import java.util.ArrayList;

import io.monteirodev.baking.database.RecipeColumns;

public class Recipe implements Parcelable {

    //@SerializedName("id")
    @Expose
    private Integer id;
    //@SerializedName("name")
    @Expose
    private String name;
    //@SerializedName("ingredients")
    @Expose
    private ArrayList<Ingredient> ingredients = null;
    //@SerializedName("steps")
    @Expose
    private ArrayList<Step> steps = null;
    //@SerializedName("servings")
    @Expose
    private Integer servings;
    //@SerializedName("image")
    @Expose
    private String image;

    public Recipe() {
    }

    public Recipe(Cursor data) {
        this.id = Cursors.getInt(data, RecipeColumns.ID);
        this.name = Cursors.getString(data, RecipeColumns.NAME);
        this.servings = Cursors.getInt(data, RecipeColumns.SERVINGS);
        this.image = Cursors.getString(data, RecipeColumns.IMAGE);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
        if (servings == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(servings);
        }
        dest.writeString(image);
    }

    protected Recipe(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
        if (in.readByte() == 0) {
            servings = null;
        } else {
            servings = in.readInt();
        }
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}