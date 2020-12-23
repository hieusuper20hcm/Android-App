package model;

import java.io.Serializable;

public class Cart implements Serializable {
    private  String id;
    private String productID;
    private String name;
    private Integer price;
    private String img;
    private Integer count;
    private String size;
    private String color;


    public Cart(){}

    public Cart(String id, String productID, String name, Integer price, String img, Integer count, String size, String color) {
        this.id = id;
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.img = img;
        this.count = count;
        this.size = size;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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


}
