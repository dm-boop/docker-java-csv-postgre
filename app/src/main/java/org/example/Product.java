package org.example;

import java.util.UUID;

public class Product {
    private String uuid;         // Unique identifier (generated)
    private String variantId;    // Original variant ID
    private int productId;       // Product ID
    private String sizeLabel;    // Size label
    private String productName;  // Product name
    private String brand;        // Brand name
    private String color;        // Product color
    private String ageGroup;     // Age group
    private String gender;       // Gender
    private String sizeType;     // Size type
    private String productType;  // Product taxonomy
    private boolean isDuplicate; // Flag for duplicate variant IDs

    // Updated constructor
    public Product(String uuid, String variantId, int productId, String sizeLabel, String productName,
                   String brand, String color, String ageGroup, String gender, String sizeType,
                   String productType, boolean isDuplicate) {
        this.uuid = uuid;
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
        this.isDuplicate = isDuplicate;
    }

    // Additional constructor for backward compatibility
    public Product(String variantId, int productId, String sizeLabel, String productName,
                   String brand, String color, String ageGroup, String gender, String sizeType,
                   String productType) {
        this(UUID.randomUUID().toString(), variantId, productId, sizeLabel, productName,
                brand, color, ageGroup, gender, sizeType, productType, false);
    }

    // Getters for new fields
    public String getUuid() {
        return uuid;
    }

    // Other getters
    public String getVariantId() {
        return variantId;
    }

    public int getProductId() {
        return productId;
    }

    public String getSizeLabel() {
        return sizeLabel;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public String getGender() {
        return gender;
    }

    public String getSizeType() {
        return sizeType;
    }

    public String getProductType() {
        return productType;
    }

    public Boolean getIsDuplicate() {
        return isDuplicate;
    }

    // Setter for variantId
    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    // Setter for productId
    public void setProductId(int productId) {
        this.productId = productId;
    }

    // Setter for sizeLabel
    public void setSizeLabel(String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

    // Setter for productName
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Setter for brand
    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Setter for color
    public void setColor(String color) {
        this.color = color;
    }

    // Setter for ageGroup
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    // Setter for gender
    public void setGender(String gender) {
        this.gender = gender;
    }

    // Setter for sizeType
    public void setSizeType(String sizeType) {
        this.sizeType = sizeType;
    }

    // Setter for productType
    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setIsDuplicate(Boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    // toString() method updated with new fields
    @Override
    public String toString() {
        return "Product{" +
                "uuid='" + uuid + '\'' +
                ", variantId='" + variantId + '\'' +
                ", productId=" + productId +
                ", sizeLabel='" + sizeLabel + '\'' +
                ", productName='" + productName + '\'' +
                ", brand='" + brand + '\'' +
                ", color='" + color + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", gender='" + gender + '\'' +
                ", sizeType='" + sizeType + '\'' +
                ", productType='" + productType + '\'' +
                ", isDuplicate=" + isDuplicate +
                '}';
    }
}