package com.example.th03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final Context context;
    private final ArrayList<Product> productList;
    private final OnItemClickListener listener;

    public ProductAdapter(Context context, ArrayList<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(context.getString(R.string.fmt_price, FormatUtils.money(p.getPrice())));
        holder.tvCategory.setText(p.getCategory());
        holder.tvQuantity.setText(context.getString(R.string.fmt_quantity, p.getQuantity()));

        if (p.isLowStock()) {
            holder.tvLowStock.setVisibility(View.VISIBLE);
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.red_600));
        } else {
            holder.tvLowStock.setVisibility(View.GONE);
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.grey_800));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCategory, tvLowStock, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLowStock = itemView.findViewById(R.id.tvLowStock);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
