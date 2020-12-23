package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Product implements Serializable, Parcelable {
    private String id;
    private String name;
    private Integer price;
    private String img;
    private String categories;
    private String description;
    private Boolean status;
    public  Product(){
    }

    public Product(String id, String name, Integer price, String img, String categories, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
        this.categories = categories;
        this.description = description;
    }

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readInt();
        }
        img = in.readString();
        categories = in.readString();
        description = in.readString();
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(price);
        }
        dest.writeString(img);
        dest.writeString(categories);
        dest.writeString(description);
        dest.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
    }
}
