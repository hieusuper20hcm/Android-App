package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doandidong.DetailActivity;
import com.example.doandidong.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import model.Product;

public class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.DataViewHolder>{
    public RecyclerDataAdapter list;
    RequestOptions options ;
    private List<Product> product;
    private Context context;
    private  String userID;

    public RecyclerDataAdapter(List<Product> product,String userID, Context context) {
        this.product = product;
        this.context = context;
        this.userID=userID;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground);

    }


    @Override
    public int getItemCount() {
        return product == null ? 0 : product.size();
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, parent, false);
        return new DataViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        final Product detailProduct=product.get(position);
        holder.tvName.setText(detailProduct.getName());
        holder.tvPrice.setText(currencyFormatter.format(detailProduct.getPrice()));
        Glide.with(context).load(detailProduct.getImg()).apply(options).into(holder.imgthumbnail);
        holder.btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(context, DetailActivity.class);
               intent.putExtra("userID",userID);
               intent.putExtra("detail", (Serializable) detailProduct);
               context.startActivity(intent);
            }
        });
    }

    public void clear() {
        int size = product.size();
        product.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void filterList(ArrayList<Product> tmp) {
        product=tmp;
        notifyDataSetChanged();
    }

    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrice;
        ImageView imgthumbnail;
        TextView btnDatHang;
        //LinearLayout layout;

        public DataViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            imgthumbnail = itemView.findViewById(R.id.img);
            btnDatHang=itemView.findViewById(R.id.btnDatHang);
        }
    }
}

