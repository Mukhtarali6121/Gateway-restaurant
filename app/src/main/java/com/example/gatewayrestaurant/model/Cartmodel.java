package com.example.gatewayrestaurant.model;

public class Cartmodel {

    String image;
    String name;
    String price;
    String quantity;
    String quantityprice;

    public Cartmodel()
    {

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantityprice() {
        return quantityprice;
    }


    public void setQuantityprice(String quantityprice) {
        this.quantityprice = quantityprice;
    }


    public Cartmodel(String image, String name, String price, String quantity, String quantityprice, String totalAmount) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.quantityprice = quantityprice;
    }


}
