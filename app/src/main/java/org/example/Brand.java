package org.example;

import java.util.List;

public class Brand {
    private String normalizedBrand;   // A standardized version of the brand (e.g., lowercase)
    private String subBrand;          // The sub-brand if applicable (e.g., "Kids" in Adidas Kids)
    private String originalBrand;     // The original brand name as it appears in the CSV


    public Brand(String normalizedBrand, String subBrand, String originalBrand ) {
        this.normalizedBrand = normalizedBrand;
        this.subBrand = subBrand;
        this.originalBrand = originalBrand;
    }

    public Brand(String originalBrand) {
        this.originalBrand = originalBrand;
    }

    // Getter for normalizedBrand
    public String getNormalizedBrand() {
        return normalizedBrand;
    }

    // Setter for normalizedBrand
    public void setNormalizedBrand(String normalizedBrand) {
        this.normalizedBrand = normalizedBrand;
    }

    // Getter for subBrand
    public String getSubBrand() {
        return subBrand;
    }

    // Setter for subBrand
    public void setSubBrand(String subBrand) {
        this.subBrand = subBrand;
    }

    // Getter for originalBrand
    public String getOriginalBrand() {
        return originalBrand;
    }

    // Setter for originalBrand
    public void setOriginalBrand(String originalBrand) {
        this.originalBrand = originalBrand;
    }


    // Override toString to display brand information nicely
    @Override
    public String toString() {
        return "Brand{" +
                "normalizedBrand='" + normalizedBrand + '\'' +
                ", subBrand='" + subBrand + '\'' +
                ", originalBrand='" + originalBrand + '\'' +
                '}';
    }
}