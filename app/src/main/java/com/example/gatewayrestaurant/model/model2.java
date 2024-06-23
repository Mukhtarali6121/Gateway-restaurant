package com.example.gatewayrestaurant.model;

public class model2 {

    String image, name, realprice, discountprice;

    public model2() {

    }


    public model2 (String image, String name, String realprice, String discountprice) {
        this.image = image;
        this.name = name;
        this.realprice = realprice;
        this.discountprice = discountprice;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealprice() {
        return realprice;
    }

    public void setRealprice(String realprice) {
        this.realprice = realprice;
    }

    public String getDiscountprice() {
        return discountprice;
    }

    public void setDiscountprice(String discountprice) {
        this.discountprice = discountprice;
    }


}
