package com.example.ola.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    private ProductContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.ola.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME = "productName";

        public final static String COLUMN_PRODUCT_AUTHOR = "productAuthor";

        public final static String COLUMN_PRODUCT_PRICE = "productPrice";

        public final static String COLUMN_PRODUCT_QUANTITY = "productQuantity";

        public final static String COLUMN_SUPPLIER_NAME = "supplierNAME";

        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierPhoneNumber";

        public final static String COLUMN_PRODUCT_COVER = "cover";

        public static final int E_BOOK = 0;
        public static final int HARD_COVER = 1;
        public static final int SOFT_COVER = 2;

        public static boolean isValidCover(int cover) {
            if (cover == E_BOOK || cover == HARD_COVER || cover == SOFT_COVER) {
                return true;
            }
            return false;
        }


    }



}
