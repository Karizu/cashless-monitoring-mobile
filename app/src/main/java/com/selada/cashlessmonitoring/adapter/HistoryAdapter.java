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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.selada.cashlessmonitoring.DetailTransactionActivity;
import com.selada.cashlessmonitoring.R;
import com.selada.cashlessmonitoring.network.Response.TransactionResponse;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<TransactionResponse> transactionModels;
    private Context context;
    private Dialog dialog;

    public HistoryAdapter(List<TransactionResponse> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_transaction, parent, false);

        return new HistoryAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position){
        final TransactionResponse transactionModel = transactionModels.get(position);
        final String trxCode = transactionModel.getTransactionCode();
        final String amount = String.valueOf(transactionModel.getAmount());
        final String order_date = transactionModel.getCreatedAt();
        final int status = transactionModel.getStatus();

        Date d = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            d = sdf.parse(order_date);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        sdf.applyPattern("dd MMM yyyy");
        String orderDate = sdf.format(d);
        sdf.applyPattern("HH:mm");
        String orderTime = sdf.format(d);

        holder.tvStatusOrder.setText("BERHASIL");
        holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));

//        switch (status){
//            case "1":
//                holder.tvStatusOrder.setText("PENDING");
//                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
//                break;
//            case "2":
//                holder.tvStatusOrder.setText("BERHASIL");
//                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
//                break;
//            case "3":
//                holder.tvStatusOrder.setText("DIBATALKAN");
//                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
//                break;
//        }

        int mHarga = Integer.parseInt(amount);
        holder.tvOrderNo.setText(trxCode);
        holder.tvOrderDate.setText(orderDate);
        holder.tvOrderTime.setText(orderTime);
        holder.tvAmount.setText("-Rp " + NumberFormat.getNumberInstance(Locale.US).format(mHarga));

        Date finalD = d;
        holder.layoutTransaction.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailTransactionActivity.class);
            intent.putExtra("order_no", trxCode);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusOrder;
        TextView tvOrderNo;
        TextView tvOrderTime;
        TextView tvAmount;
        TextView tvOrderDate;
        LinearLayout layoutTransaction;

        ViewHolder(View v){
            super(v);

            layoutTransaction = v.findViewById(R.id.layoutTransaction);
            tvStatusOrder = v.findViewById(R.id.status_transaction);
            tvOrderNo = v.findViewById(R.id.transaction_id);
            tvAmount = v.findViewById(R.id.amount_transaction);
            tvOrderDate = v.findViewById(R.id.tvDate);
            tvOrderTime = v.findViewById(R.id.tvTime);
        }
    }

    private void showDialog(int layout, Context context) {
        dialog = new Dialog(Objects.requireNonNull(context));
        //set content
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
