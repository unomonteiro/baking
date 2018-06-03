package io.monteirodev.baking.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import net.simonvt.schematic.Cursors;

import io.monteirodev.baking.database.IngredientColumns;

public class Ingredient implements Parcelable {

    //@SerializedName("quantity")
    @Expose
    private Float quantity;
    //@SerializedName("measure")
    @Expose
    private String measure;
    //@SerializedName("ingredient")
    @Expose
    private String ingredient;

    public Ingredient() {
    }

    public Ingredient(Cursor data) {
        this.quantity = Cursors.getFloat(data, IngredientColumns.QUANTITY);
        this.measure = Cursors.getString(data, IngredientColumns.MEASURE);
        this.ingredient = Cursors.getString(data, IngredientColumns.INGREDIENT);
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(quantity);
        }
        dest.writeString(measure);
        dest.writeString(ingredient);
    }

    protected Ingredient(Parcel in) {
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readFloat();
        }
        measure = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
