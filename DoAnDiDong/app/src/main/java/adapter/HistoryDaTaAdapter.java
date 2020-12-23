package adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.doandidong.HistoryActivity;
import com.example.doandidong.HistoryDetail;
import com.example.doandidong.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import model.Cart;
import model.DetailProduct;
import model.History;

public class HistoryDaTaAdapter extends RecyclerView.Adapter<HistoryDaTaAdapter.DataViewHolder>{
    private List<History> histories;
    private HistoryActivity context;
    private HistoryDaTaAdapter.OnItemClickListener mListener;
    public HistoryDaTaAdapter(List<History> histories, HistoryActivity context) {
        this.histories = histories;
        this.context = context;
    }
    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }

    public interface OnItemClickListener{
        void onClick(int postion);
    }

    public void setOnItemClickListener(HistoryDaTaAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row, parent, false);

        return new DataViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, final int position) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        final History detailHistory=histories.get(position);
        holder.tv_create.setText(detailHistory.getCreateDate());
        holder.tv_money.setText(currencyFormatter.format(detailHistory.getTotalMoney()));
        holder.tv_number.setText(detailHistory.getTotalNumber().toString());

        holder.btn_datlai.setOnClickListener(v -> {
            context.preOrder(detailHistory.getId());

        });
    }


    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tv_create;
        TextView tv_number;
        TextView tv_money;
        Button btn_datlai;

        public DataViewHolder(View itemView, HistoryDaTaAdapter.OnItemClickListener listener) {
            super(itemView);

            tv_create=itemView.findViewById(R.id.tv_create);
            tv_number=itemView.findViewById(R.id.tv_number);
            tv_money=itemView.findViewById(R.id.tv_money);
            btn_datlai=itemView.findViewById(R.id.btnDatLai);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onClick(position);
                        }
                    }
                }
            });
        }
    }
}


