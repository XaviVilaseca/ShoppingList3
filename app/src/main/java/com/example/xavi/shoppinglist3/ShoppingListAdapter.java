package com.example.xavi.shoppinglist3;

/**
 * Created by Xavi on 27/10/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class ShoppingListAdapter extends ArrayAdapter<ShoppingItem> {

    public ShoppingListAdapter(@NonNull Context context, @NonNull int resource, @NonNull List objects) {
        super(context, resource, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result= convertView;
        if (result==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.shopping_item, null);
        }
        CheckBox checkbox = (CheckBox) result.findViewById(R.id.shopping_item);
        ShoppingItem item_text = getItem(position);
        checkbox.setText(item_text.getText());
        checkbox.setChecked(item_text.isCheck());
        return result;
    }
}