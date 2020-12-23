package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doandidong.HistoryDetail;
import com.example.doandidong.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import model.DetailProduct;

public class DetailHistoryDataAdapter extends RecyclerView.Adapter<DetailHistoryDataAdapter.DataViewHolder>{
    RequestOptions options ;
    private List<DetailProduct> detailProducts;
    private HistoryDetail context;

    public DetailHistoryDataAdapter(List<DetailProduct> detailProducts, HistoryDetail context) {
        this.detailProducts = detailProducts;
        this.context = context;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground);
    }


    @Override
    public int getItemCount() {
        return detailProducts == null ? 0 : detailProducts.size();
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_histoty_row, parent, false);

        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        final DetailProduct detailProduct=detailProducts.get(position);
        holder.tvName.setText(detailProduct.getNameProduct());
        holder.tvPrice.setText(currencyFormatter.format(detailProduct.getPrice()));
        holder.tvCount.setText(detailProduct.getCount().toString());
        holder.tvSize.setText("Size: "+detailProduct.getSize()+", Color: "+detailProduct.getColor());
        Glide.with(context).load(detailProduct.getImg()).apply(options).into(holder.imgthumbnail);
    }



    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrice;
        TextView tvCount;
        TextView tvSize;
        ImageView imgthumbnail;

        public DataViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvSize = itemView.findViewById(R.id.tv_size_color);
            imgthumbnail = itemView.findViewById(R.id.img);
        }
    }
}

