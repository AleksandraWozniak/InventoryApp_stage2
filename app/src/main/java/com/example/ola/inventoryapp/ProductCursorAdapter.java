package com.example.ola.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ola.inventoryapp.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        String currentId = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry._ID));
        final Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Long.parseLong(currentId));

        TextView nameTextView = view.findViewById(R.id.name);
        TextView authorTextView = view.findViewById(R.id.author);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sell_button);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int authorColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productAuthor = cursor.getString(authorColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        final int itemQuantity = Integer.parseInt(productQuantity);

        if (TextUtils.isEmpty(productQuantity)) {
            productQuantity = context.getString(R.string.default_quantity);
        }

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        authorTextView.setText(productAuthor);
        quantityTextView.setText(productQuantity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemQuantity > 0) {
                    ContentValues values = new ContentValues();

                    int newItemQuantity = itemQuantity - 1;

                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newItemQuantity);

                    int newQuantity = context.getContentResolver().update(currentUri, values, null, null);

                    if (newQuantity == 0)
                        Toast.makeText(context, R.string.quantity_error, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, R.string.quantity_below, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
