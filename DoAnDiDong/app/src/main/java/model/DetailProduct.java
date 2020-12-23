package model;

import java.io.Serializable;

public class DetailProduct implements Serializable {
    String id;
    String img;
    String productID;
    String size;
    String color;
    Number price;
    String nameProduct;
    Number count;
    public DetailProduct(){}

    public DetailProduct(String id, String img, String productID, String size, String color, Number price, String nameProduct, Number count) {
        this.id = id;
        this.img = img;
        this.productID = productID;
        this.size = size;
        this.color = color;
        this.price = price;
        this.nameProduct = nameProduct;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public Number getCount() {
        return count;
    }

    public void setCount(Number count) {
        this.count = count;
    }
}
