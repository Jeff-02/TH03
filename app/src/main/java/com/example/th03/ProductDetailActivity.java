package com.example.th03;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    private TextView tvName;
    private TextView tvCategory;
    private TextView tvPrice;
    private TextView tvQuantity;
    private TextView tvStockValue;
    private TextView tvLowStock;
    private TextView tvDescription;

    private Button btnImport;
    private Button btnSell;
    private Button btnDelete;

    private DatabaseHelper dbHelper;
    private int productId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        View root = findViewById(R.id.main);
        if (root != null) {
            int l = root.getPaddingLeft(), t = root.getPaddingTop(),
                r = root.getPaddingRight(), b = root.getPaddingBottom();
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                        | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(l + bars.left, t + bars.top, r + bars.right, b + bars.bottom);
                return insets;
            });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_product_detail);
        }

        dbHelper = new DatabaseHelper(this);
        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);

        if (productId == -1) {
            Toast.makeText(this, R.string.toast_product_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvStockValue = findViewById(R.id.tvStockValue);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvDescription = findViewById(R.id.tvDescription);

        btnImport = findViewById(R.id.btnImport);
        btnSell = findViewById(R.id.btnSell);
        Button btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        btnImport.setOnClickListener(v -> importOne());
        btnSell.setOnClickListener(v -> sellOne());
        btnBack.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> deleteProduct());

        loadProduct();
    }

    private void loadProduct() {
        Product p = dbHelper.getProductById(productId);
        if (p == null) {
            Toast.makeText(this, R.string.toast_product_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(p.getName());
        tvCategory.setText(getString(R.string.fmt_category, p.getCategory()));
        tvPrice.setText(getString(R.string.fmt_price, FormatUtils.money(p.getPrice())));
        tvQuantity.setText(getString(R.string.fmt_quantity_detail, p.getQuantity()));
        tvStockValue.setText(getString(R.string.fmt_stock_value, FormatUtils.money(p.getStockValue())));

        if (p.isLowStock()) {
            tvLowStock.setVisibility(View.VISIBLE);
        } else {
            tvLowStock.setVisibility(View.GONE);
        }

        if (p.getDescription() == null || p.getDescription().trim().isEmpty()) {
            tvDescription.setText(R.string.no_description);
        } else {
            tvDescription.setText(p.getDescription());
        }
    }

    private void importOne() {
        Product p = dbHelper.getProductById(productId);
        if (p == null) {
            finish();
            return;
        }
        dbHelper.updateQuantity(productId, p.getQuantity() + 1);
        loadProduct();
    }

    private void sellOne() {
        Product p = dbHelper.getProductById(productId);
        if (p == null) {
            finish();
            return;
        }
        if (p.getQuantity() <= 0) {
            Toast.makeText(this, R.string.toast_out_of_stock, Toast.LENGTH_SHORT).show();
            return;
        }
        dbHelper.updateQuantity(productId, p.getQuantity() - 1);
        loadProduct();
    }

    private void deleteProduct() {
        dbHelper.deleteProduct(productId);
        Toast.makeText(this, R.string.toast_delete_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
