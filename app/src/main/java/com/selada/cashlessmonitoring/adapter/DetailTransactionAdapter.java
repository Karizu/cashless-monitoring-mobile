package com.selada.cashlessmonitoring.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.selada.cashlessmonitoring.DetailTransactionActivity;
import com.selada.cashlessmonitoring.R;
import com.selada.cashlessmonitoring.network.Response.TransactionMember.Detail;
import com.selada.cashlessmonitoring.network.Response.TransactionResponse;
import com.selada.cashlessmonitoring.util.MethodUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailTransactionAdapter extends RecyclerView.Adapter<DetailTransactionAdapter.ViewHolder> {
    private List<Detail> transactionModels;
    private Context context;
    private Dialog dialog;

    public DetailTransactionAdapter(List<Detail> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailTransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_transaction_detail, parent, false);

        return new DetailTransactionAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull DetailTransactionAdapter.ViewHolder holder, int position){
        final Detail transactionModel = transactionModels.get(position);
        final String itemName = transactionModel.getStock().getItem().getName();
        final String amount = transactionModel.getSalesPrice();
        final String qty = String.valueOf(transactionModel.getQty());
        final int total = Integer.parseInt(amount) * Integer.parseInt(qty);

        holder.transaction_item.setText(itemName);
        holder.transaction_price_qty.setText(qty +" x Rp "+MethodUtil.toCurrencyFormat(amount));
        holder.sub_total_transaction.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(total)));
    }

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView transaction_item;
        TextView transaction_price_qty;
        TextView sub_total_transaction;

        ViewHolder(View v){
            super(v);

            transaction_item = v.findViewById(R.id.transaction_item);
            transaction_price_qty = v.findViewById(R.id.transaction_price_qty);
            sub_total_transaction = v.findViewById(R.id.sub_total_transaction);
        }
    }
}
