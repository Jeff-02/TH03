package com.example.th03;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private Spinner spCategory;
    private SwitchCompat swActive;
    private EditText etDescription;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

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
            getSupportActionBar().setTitle(R.string.title_add_product);
        }

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        spCategory = findViewById(R.id.spCategory);
        swActive = findViewById(R.id.swActive);
        etDescription = findViewById(R.id.etDescription);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String category = spCategory.getSelectedItem() != null ? spCategory.getSelectedItem().toString() : "";
        boolean active = swActive.isChecked();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.err_name_empty));
            etName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            etPrice.setError(getString(R.string.err_price_empty));
            etPrice.requestFocus();
            return;
        }

        long price;
        try {
            price = Long.parseLong(priceStr);
        } catch (NumberFormatException e) {
            price = -1;
        }

        if (price <= 0) {
            etPrice.setError(getString(R.string.err_price_invalid));
            etPrice.requestFocus();
            return;
        }

        if (qtyStr.isEmpty()) {
            etQuantity.setError(getString(R.string.err_quantity_empty));
            etQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            quantity = -1;
        }

        if (quantity < 0) {
            etQuantity.setError(getString(R.string.err_quantity_invalid));
            etQuantity.requestFocus();
            return;
        }

        Product product = new Product(name, price, quantity, category, active, description);
        long result = dbHelper.insertProduct(product);

        if (result == -1) {
            Toast.makeText(this, R.string.toast_add_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.toast_add_success, Toast.LENGTH_SHORT).show();
            finish();
        }
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
