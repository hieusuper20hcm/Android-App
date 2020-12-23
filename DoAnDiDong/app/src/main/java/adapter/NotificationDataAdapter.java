package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doandidong.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import model.Product;

public class NotificationDataAdapter extends RecyclerView.Adapter<NotificationDataAdapter.DataViewHolder>{
    RequestOptions options ;
    private List<Product> product;
    private Context context;
    private OnItemClickListener mListener;

    public NotificationDataAdapter(List<Product> product, Context context) {
        this.product = product;
        this.context = context;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground);
    }

    @Override
    public int getItemCount() {
        return product == null ? 0 : product.size();
    }

    public void clear() {
        int size = product.size();
        product.clear();
        notifyItemRangeRemoved(0, size);
    }

    public interface OnItemClickListener{
        void onClick(int postion);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
    @Override
    public NotificationDataAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        return new NotificationDataAdapter.DataViewHolder(itemView, mListener);
    }


    @Override
    public void onBindViewHolder(NotificationDataAdapter.DataViewHolder holder, int position) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        final Product detailProduct=product.get(position);
        holder.tvName.setText(detailProduct.getName());
        holder.tvPrice.setText(currencyFormatter.format(detailProduct.getPrice()));
        holder.tvDesc.setText(detailProduct.getDescription());
        Glide.with(context).load(detailProduct.getImg()).apply(options).into(holder.imgthumbnail);
    }

    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvPrice,tvDesc;
        ImageView imgthumbnail;

        public DataViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDesc=itemView.findViewById(R.id.tv_desc);
            imgthumbnail = itemView.findViewById(R.id.img);

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
