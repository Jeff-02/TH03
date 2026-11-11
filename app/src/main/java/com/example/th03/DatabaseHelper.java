package com.example.th03;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Table users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Table products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";
    public static final String COLUMN_PRODUCT_ACTIVE = "active";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL" +
                ");";

        String createTableProducts = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_ACTIVE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_DESCRIPTION + " TEXT" +
                ");";

        db.execSQL(createTableUsers);
        db.execSQL(createTableProducts);

        // Insert default user
        insertUserInternal(db, "admin", "123456");

        // Insert sample products
        insertProductInternal(db, new Product("Loa mini Bluetooth", 280000, 15, "Điện tử", true, "Loa nhỏ gọn, pin dùng khoảng 8 giờ"));
        insertProductInternal(db, new Product("Cà phê hòa tan hộp 20 gói", 65000, 2, "Thực phẩm", true, ""));
        insertProductInternal(db, new Product("Nồi cơm điện 1.8L", 890000, 8, "Gia dụng", true, "Lòng nồi chống dính, bảo hành 12 tháng"));
        insertProductInternal(db, new Product("Bút bi hộp 20 cây", 48000, 60, "Văn phòng phẩm", true, ""));
        insertProductInternal(db, new Product("Pin AA vỉ 4 viên", 35000, 25, "Khác", true, "Pin kiềm dùng cho điều khiển, đồng hồ"));
        insertProductInternal(db, new Product("Sạc dự phòng 10.000mAh", 420000, 0, "Điện tử", true, "Hai cổng USB, sạc nhanh"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_NAME + " = ?", new String[]{"Loa mini Bluetooth"})) {
            if (c.moveToFirst() && c.getInt(0) == 0) {
                try (Cursor cAll = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null)) {
                    if (cAll.moveToFirst() && cAll.getInt(0) == 3) {
                        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
                        onCreate(db);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private long insertUserInternal(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    private long insertProductInternal(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, p.getName());
        values.put(COLUMN_PRODUCT_PRICE, p.getPrice());
        values.put(COLUMN_PRODUCT_QUANTITY, p.getQuantity());
        values.put(COLUMN_PRODUCT_CATEGORY, p.getCategory());
        values.put(COLUMN_PRODUCT_ACTIVE, p.isActive() ? 1 : 0);
        values.put(COLUMN_PRODUCT_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?",
                new String[]{username, password})) {
            return c.moveToFirst();
        }
    }

    public long insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        return insertUserInternal(db, username, password);
    }

    public long insertProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        return insertProductInternal(db, p);
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COLUMN_PRODUCT_ID + " ASC";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME);
                int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE);
                int qtyIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY);
                int catIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY);
                int activeIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE);
                int descIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION);

                do {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    long price = cursor.getLong(priceIndex);
                    int quantity = cursor.getInt(qtyIndex);
                    String category = cursor.getString(catIndex);
                    boolean active = cursor.getInt(activeIndex) == 1;
                    String description = cursor.getString(descIndex);

                    list.add(new Product(id, name, price, quantity, category, active, description));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_ID + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME);
                int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE);
                int qtyIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY);
                int catIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY);
                int activeIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE);
                int descIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION);

                String name = cursor.getString(nameIndex);
                long price = cursor.getLong(priceIndex);
                int quantity = cursor.getInt(qtyIndex);
                String category = cursor.getString(catIndex);
                boolean active = cursor.getInt(activeIndex) == 1;
                String description = cursor.getString(descIndex);

                return new Product(id, name, price, quantity, category, active, description);
            }
        }
        return null;
    }

    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COALESCE(SUM(" + COLUMN_PRODUCT_PRICE + " * " + COLUMN_PRODUCT_QUANTITY + "), 0) FROM " + TABLE_PRODUCTS, null)) {
            return c.moveToFirst() ? c.getLong(0) : 0;
        }
    }

    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_QUANTITY + " < ?",
                new String[]{String.valueOf(Product.LOW_STOCK_THRESHOLD)})) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public int countProducts() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }
}
