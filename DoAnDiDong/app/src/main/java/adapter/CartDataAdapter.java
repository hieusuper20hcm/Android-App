package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doandidong.CartActivity;
import com.example.doandidong.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import model.Cart;

public class CartDataAdapter extends RecyclerView.Adapter<CartDataAdapter.DataViewHolder>{
    RequestOptions options ;
    private List<Cart> cart;
    private CartActivity context;
    private CartDataAdapter.OnItemClickListener mListener;

    public CartDataAdapter(List<Cart> cart, CartActivity context) {
        this.cart = cart;
        this.context = context;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground);
    }


    @Override
    public int getItemCount() {
        return cart == null ? 0 : cart.size();
    }

    public interface OnItemClickListener{
        void onClick(int postion);
    }
    public void setOnItemClickListener(CartDataAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row, parent, false);

        return new CartDataAdapter.DataViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
            Locale locale = new Locale("vi", "VN");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            final Cart detailCart=cart.get(position);
            holder.tvName.setText(detailCart.getName());
            holder.tvPrice.setText(currencyFormatter.format(detailCart.getPrice()));
            holder.tvCount.setText(detailCart.getCount().toString());
            holder.tvSize.setText("Size: "+detailCart.getSize()+", Color: "+detailCart.getColor());
            Glide.with(context).load(detailCart.getImg()).apply(options).into(holder.imgthumbnail);
            holder.btnXoa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.DeleteCart(detailCart.getId());
                }
            });
    }

    public void clear() {
        int size = cart.size();
        cart.clear();
        notifyItemRangeRemoved(0, size);
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
        Button btnXoa;

        public DataViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvSize = itemView.findViewById(R.id.tv_size_color);
            imgthumbnail = itemView.findViewById(R.id.img);
            btnXoa=itemView.findViewById(R.id.btnXoa);

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

