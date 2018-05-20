package com.example.ola.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ola.inventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mPriceEditText;
    private EditText mQuantitytEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;

    private Spinner mCoverSpinner;
    private int mCover = ProductEntry.E_BOOK;

    private Button mQuantityDecrement;
    private Button mQuantityIncrement;
    private Button mOrderItem;

    private boolean mProductHasChanged = false;
    private boolean mProductCanBeSaved = false;
    private int quantity;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get the associated URI
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_product));

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.edit_product_name);
        mAuthorEditText = findViewById(R.id.edit_product_author);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantitytEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);
        mCoverSpinner = findViewById(R.id.spinner_cover);
        mQuantityDecrement = findViewById(R.id.quantity_decrement);
        mQuantityIncrement = findViewById(R.id.quantity_increment);
        mOrderItem = findViewById(R.id.order_product_button);

        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantitytEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mCoverSpinner.setOnTouchListener(mTouchListener);
        mQuantityDecrement.setOnTouchListener(mTouchListener);
        mQuantityIncrement.setOnTouchListener(mTouchListener);

        setupSpinner();
        decrementQuantity();
        incrementQuantity();
        orderProduct();
    }

    private void orderProduct() {
        mOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productNameMail = mNameEditText.getText().toString().trim();
                String productNameAuthor = mAuthorEditText.getText().toString().trim();
                String productQuantityMail = mQuantitytEditText.getText().toString().trim();
                String productPriceMail = mPriceEditText.getText().toString().trim();

                String message = getString(R.string.email_message) +
                        "\n\nBook name: " + productNameMail +
                        "\nAuthor: " + productNameAuthor +
                        "\nQuantity: " + productQuantityMail +
                        "\nPrice per item (in EUR): " + productPriceMail +
                        "\n\nThank you," +
                        "\n\nBest regards.";

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, "dummy@email.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(Intent.createChooser(intent, "Send email"));
            }
        });
    }

    private void incrementQuantity() {
        mQuantityIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(mQuantitytEditText.getText().toString())) {
                    quantity = Integer.parseInt(mQuantitytEditText.getText().toString());
                }
                quantity++;
                mQuantitytEditText.setText("" + quantity);

            }
        });
    }


    private void decrementQuantity() {
        mQuantityDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(mQuantitytEditText.getText().toString())) {
                    quantity = Integer.parseInt(mQuantitytEditText.getText().toString());
                }
                if (quantity > 0) {
                    quantity--;
                }
                mQuantitytEditText.setText("" + quantity);
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter coverSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_cover_options, android.R.layout.simple_spinner_item);

        coverSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mCoverSpinner.setAdapter(coverSpinnerAdapter);

        mCoverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.hard_cover))) {
                        mCover = ProductEntry.HARD_COVER; // hard
                    } else if (selection.equals(getString(R.string.soft_cover))) {
                        mCover = ProductEntry.SOFT_COVER; // soft
                    } else {
                        mCover = ProductEntry.E_BOOK; // e-book
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCover = ProductEntry.E_BOOK; // e-book
            }
        });
    }

    private boolean saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantitytEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();


        if (mCurrentProductUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneNumberString) &&
                mCover == ProductEntry.E_BOOK) {
            mProductCanBeSaved = false;
            return mProductCanBeSaved;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.error_require_name),
                    Toast.LENGTH_SHORT).show();
            mProductCanBeSaved = false;
            return mProductCanBeSaved;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);

        if (TextUtils.isEmpty(authorString)) {
            Toast.makeText(this, getString(R.string.error_require_author),
                    Toast.LENGTH_SHORT).show();
            mProductCanBeSaved = false;
            return mProductCanBeSaved;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_AUTHOR, authorString);

        double price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.error_require_supplier_name),
                    Toast.LENGTH_SHORT).show();
            mProductCanBeSaved = false;
            return mProductCanBeSaved;
        }
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);

        if (TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, getString(R.string.error_require_supplier_phone),
                    Toast.LENGTH_SHORT).show();
            mProductCanBeSaved = false;
            return mProductCanBeSaved;
        }
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        values.put(ProductEntry.COLUMN_PRODUCT_COVER, mCover);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        mProductCanBeSaved = true;
        return mProductCanBeSaved;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (saveProduct()) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_AUTHOR,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                ProductEntry.COLUMN_PRODUCT_COVER};

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int authorColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int coverColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_COVER);

            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierNumberColumnIndex);
            int cover = cursor.getInt(coverColumnIndex);

            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(Double.toString(price));
            mQuantitytEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);

            switch (cover) {
                case ProductEntry.HARD_COVER:
                    mCoverSpinner.setSelection(1);
                    break;
                case ProductEntry.SOFT_COVER:
                    mCoverSpinner.setSelection(2);
                    break;
                default:
                    mCoverSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mCoverSpinner.setSelection(0);
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mQuantitytEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
