package com.example.gatewayrestaurant.model;

public class model1 {

    String image, name, price, availableFrom, availableTo, nextAvailableFrom, nextAvailableTo,nameLowerCase;


    public model1() {

    }

    public model1(String image, String name, String price, String availableFrom, String availableTo, String nextAvailableFrom, String nextAvailableTo, String nameLowerCase) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.nextAvailableFrom = nextAvailableFrom;
        this.nextAvailableTo = nextAvailableTo;
        this.nameLowerCase = nameLowerCase;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    public void setNameLowerCase(String nameLowerCase) {
        this.nameLowerCase = nameLowerCase;
    }

    public String getNextAvailableFrom() {
        return nextAvailableFrom;
    }

    public void setNextAvailableFrom(String nextAvailableFrom) {
        this.nextAvailableFrom = nextAvailableFrom;
    }

    public String getNextAvailableTo() {
        return nextAvailableTo;
    }

    public void setNextAvailableTo(String nextAvailableTo) {
        this.nextAvailableTo = nextAvailableTo;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(String availableTo) {
        this.availableTo = availableTo;
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

}
