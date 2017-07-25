package com.example.alessio.project101;

public class Item {

    private final String prodName;
    private final String price;
    private final int quant;
    private final String vendorName;
    private final String vendorPhone;
    private final String vendorEmail;
    private final String img;

    public Item(String prodName, String price, int quant, String vendorName, String vendorPhone, String vendorEmail, String img) {
        this.prodName = prodName;
        this.price = price;
        this.quant = quant;
        this.vendorName = vendorName;
        this.vendorPhone = vendorPhone;
        this.vendorEmail = vendorEmail;
        this.img = img;
    }

    public String getProdName() {
        return prodName;
    }
    public String getPrice() {
        return price;
    }
    public int getQuant() {
        return quant;
    }
    public String getVendorName() {
        return vendorName;
    }
    public String getVendorPhone() {
        return vendorPhone;
    }
    public String getVendorEmail() {
        return vendorEmail;
    }
    public String getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "Item{" +
                "prodName='" + prodName + '\'' +
                ", price='" + price + '\'' +
                ", quant=" + quant +
                ", vendorName='" + vendorName + '\'' +
                ", vendorPhone='" + vendorPhone + '\'' +
                ", vendorEmail='" + vendorEmail + '\'' +
                '}';
    }

}
