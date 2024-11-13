package org.example;

public class Product {
    private String variantId;
    private int productId;
    private String sizeLabel;
    private String productName;
    private String brand;
    private String color;
    private String ageGroup;
    private String gender;
    private String sizeType;
    private String productType;

    public Product(String variantId, int productId, String sizeLabel, String productName,
                   String brand, String color, String ageGroup, String gender, String sizeType, String productType) {
        this.variantId = variantId;
        this.productId = productId;
        this.sizeLabel = sizeLabel;
        this.productName = productName;
        this.brand = brand;
        this.color = color;
        this.ageGroup = ageGroup;
        this.gender = gender;
        this.sizeType = sizeType;
        this.productType = productType;
    }

    @Override
    public String toString() {
        return "Product{" +
                "variantId='" + variantId + '\'' +
                ", productId=" + productId +
                ", sizeLabel='" + sizeLabel + '\'' +
                ", productName='" + productName + '\'' +
                ", brand='" + brand + '\'' +
                ", color='" + color + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", gender='" + gender + '\'' +
                ", sizeType='" + sizeType + '\'' +
                ", productType='" + productType + '\'' +
                '}';
    }
}