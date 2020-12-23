package model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

public class History implements Serializable {
   private String id;
   private String userID;
   private String createDate;
   private String phone;
   private String address;
   private Number totalMoney;
   private Number totalNumber;
   private List<DetailProduct>detailProductList;
    public History(){}

    public History(String id, String userID, String createDate, String phone, String address, Number totalMoney, Number totalNumber, List<DetailProduct> detailProductList) {
        this.id = id;
        this.userID = userID;
        this.createDate = createDate;
        this.phone = phone;
        this.address = address;
        this.totalMoney = totalMoney;
        this.totalNumber = totalNumber;
        this.detailProductList = detailProductList;
    }

    public List<DetailProduct> getDetailProductList() {
        return detailProductList;
    }

    public void setDetailProductList(List<DetailProduct> detailProductList) {
        this.detailProductList = detailProductList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Number getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Number totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Number getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Number totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
