package org.example;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


import java.util.UUID;

import static org.example.BrandNormalizer.canonicalBrands;
import static org.example.BrandNormalizer.getNormalizedBrandNameForOriginalName;


public class Main {

    //private static final String CSV_FILE_PATH = "src/main/resources/smalldata-UTF8-mergetest.csv"; // For testing locally
    private static final String CSV_FILE_PATH = "src/main/resources/data.csv";

    // Database credentials and connection string
    //private static final String DB_URL = "jdbc:postgresql://localhost:5432/productdb?characterEncoding=UTF8"; // For testing locally
    private static final String DB_URL = "jdbc:postgresql://postgres:5432/productdb?characterEncoding=UTF8";
    private static final String USER = "productuser";
    private static final String PASSWORD = "productpassword";

    public static void main(String[] args) {

        List<Brand> brands = loadBrands(CSV_FILE_PATH);
        List<Brand> canonicalBrands;

        brands.sort((b1, b2) -> b1.getOriginalBrand().toLowerCase().compareTo(b2.getOriginalBrand().toLowerCase()));
        brands.forEach(brand -> {
            System.out.println(brand.getOriginalBrand());
        });

        // let's get a list of original brands with their mapping to their canonical brand
        canonicalBrands = canonicalBrands(brands);


        List<Product> products = loadCSVData(CSV_FILE_PATH);

        for (Product product : products) {
            try {
                List<Product> productAddedWithSameVariantId = selectProductsByVariantId(product.getVariantId());

                // Update the brand before we try to insert
                String normalizedBrand = getNormalizedBrandNameForOriginalName(canonicalBrands, product.getBrand());
                product.setBrand(normalizedBrand);

                if (productAddedWithSameVariantId.isEmpty()) {
                    insertProduct(
                            // uuid is generated automatically, no need to pass
                            product.getVariantId(),
                            product.getProductId(),
                            product.getSizeLabel(),
                            product.getProductName(),
                            product.getBrand(),
                            product.getColor(),
                            product.getAgeGroup(),
                            product.getGender(),
                            product.getSizeType(),
                            product.getProductType(),
                            product.getIsDuplicate()
                    );
                } else if (productAddedWithSameVariantId.size() == 1) {
                    //Instead of adding it again, we merge the data
                    Product mergedProduct = mergeProducts(productAddedWithSameVariantId.getFirst(), product);

                    updateProductByVariantId(
                            mergedProduct.getVariantId(),
                            mergedProduct.getProductId(),
                            mergedProduct.getSizeLabel(),
                            mergedProduct.getProductName(),
                            mergedProduct.getBrand(),
                            mergedProduct.getColor(),
                            mergedProduct.getAgeGroup(),
                            mergedProduct.getGender(),
                            mergedProduct.getSizeType(),
                            mergedProduct.getProductType(),
                            mergedProduct.getIsDuplicate()
                    );

                } else {
                    System.err.println("Somehow we already inserted twice, err");
                }
            } catch (SQLException e) {
                System.err.println("Error inserting product: " + e.getMessage());
            }
        }
        ;
    }


    private static List<Product> loadCSVData(String filePath) {
        List<Product> products = new ArrayList<>();

        try (CSVParser csvParser = new CSVParser(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8), // Use UTF-8 encoding
                CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                try {
                    // Sanitize and validate each field
                    String variantId = record.get("variant_id");
                    int productId = sanitizeInteger(record.get("product_id"), -1); // Default to -1 if invalid
                    String sizeLabel = record.get("size_label");
                    String productName = record.get("product_name");
                    String brand = record.get("brand");
                    String color = record.get("color");
                    String ageGroup = record.get("age_group");
                    String gender = record.get("gender");
                    String sizeType = record.get("size_type");
                    String productType = record.get("product_type");

                    // Validate critical fields (e.g., variantId, productId)
                    if (variantId.isEmpty() || productId == -1) {
                        System.out.printf("Skipping record due to missing or invalid critical fields: %s%n", record);
                        continue;
                    }

                    // Create and add product
                    Product product = new Product(
                            variantId, productId, sizeLabel, productName, brand, color, ageGroup, gender, sizeType, productType
                    );
                    products.add(product);
                } catch (Exception e) {
                    System.out.printf("Error processing record: %s. Skipping record.%n", record, e);
                }
            }
        } catch (IOException e) {
            System.out.printf("Error reading CSV file: %s%n", e.getMessage());
        }
        return products;
    }

    private static List<Brand> loadBrands(String filePath) {
        List<Brand> brands = new ArrayList<>();

        try (CSVParser csvParser = new CSVParser(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8), // Use UTF-8 encoding
                CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                try {
                    // Sanitize and validate each field
                    String variantId = record.get("variant_id");
                    int productId = sanitizeInteger(record.get("product_id"), -1); // Default to -1 if invalid
                    String brand = record.get("brand") == null ? "" : record.get("brand");

                    // Validate critical fields (e.g., variantId, productId)
                    if (variantId.isEmpty() || productId == -1) {
                        System.out.printf("Skipping record due to missing or invalid critical fields: %s%n", record);
                        continue;
                    }

                    // Create and add product
                    Brand newbrand = new Brand(brand);
                    brands.add(newbrand);
                } catch (Exception e) {
                    System.out.printf("Error processing record: %s. Skipping record.%n", record, e);
                }
            }
        } catch (IOException e) {
            System.out.printf("Error reading CSV file: %s%n", e.getMessage());
        }
        return brands;
    }

    private static int sanitizeInteger(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue; // Return default value if parsing fails
        }
    }

    private static void insertProduct(String variantId, int productId, String sizeLabel, String productName,
                                      String brand, String color, String ageGroup, String gender,
                                      String sizeType, String productType, Boolean isDuplicate) throws SQLException {

        String insertQuery = "INSERT INTO products (uuid, variant_id, product_id, size_label, product_name, " +
                "brand, color, age_group, gender, size_type, product_type, is_duplicate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Establish database connection and execute insert - TODO This is definitely need to move from here
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            String uuid = UUID.randomUUID().toString();
            pstmt.setString(1, uuid);
            pstmt.setString(2, variantId);
            pstmt.setInt(3, productId);
            pstmt.setString(4, sizeLabel);
            pstmt.setString(5, productName);
            pstmt.setString(6, brand);
            pstmt.setString(7, color);
            pstmt.setString(8, ageGroup);
            pstmt.setString(9, gender);
            pstmt.setString(10, sizeType);
            pstmt.setString(11, productType);
            pstmt.setBoolean(12, isDuplicate);

            pstmt.executeUpdate();
            System.out.println("Product inserted successfully.");

        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
            throw e;
        }
    }


    private static void updateProductByVariantId(String variantId, int productId, String sizeLabel, String productName,
                                                 String brand, String color, String ageGroup, String gender,
                                                 String sizeType, String productType, Boolean isDuplicate) throws SQLException {

        String updateQuery = "UPDATE products SET product_id = ?, size_label = ?, product_name = ?, brand = ?, " +
                "color = ?, age_group = ?, gender = ?, size_type = ?, product_type = ?, is_duplicate = ? " +
                "WHERE variant_id = ?";

        // Establish database connection and execute update
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setInt(1, productId);
            pstmt.setString(2, sizeLabel);
            pstmt.setString(3, productName);
            pstmt.setString(4, brand);
            pstmt.setString(5, color);
            pstmt.setString(6, ageGroup);
            pstmt.setString(7, gender);
            pstmt.setString(8, sizeType);
            pstmt.setString(9, productType);
            pstmt.setBoolean(10, isDuplicate);
            pstmt.setString(11, variantId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Product updated successfully.");
            } else {
                System.out.println("No product found with the given variant_id.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            throw e;
        }
    }

    private static List<Product> selectProductsByVariantId(String variantId) throws SQLException {
        String selectQuery = "SELECT * FROM products WHERE variant_id = ?";

        List<Product> products = new ArrayList<>();

        // Establish database connection and execute query
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {

            pstmt.setString(1, variantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Create a Product instance for each row in the ResultSet
                    Product product = new Product(
                            rs.getString("uuid"),
                            rs.getString("variant_id"),
                            rs.getInt("product_id"),
                            rs.getString("size_label"),
                            rs.getString("product_name"),
                            rs.getString("brand"),
                            rs.getString("color"),
                            rs.getString("age_group"),
                            rs.getString("gender"),
                            rs.getString("size_type"),
                            rs.getString("product_type"),
                            rs.getBoolean("is_duplicate")
                    );
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
            throw e;
        }

        return products;
    }

    public static Product mergeProducts(Product existingProduct, Product newProduct) {
        if (!existingProduct.getVariantId().equalsIgnoreCase(newProduct.getVariantId())) {
            throw new IllegalArgumentException("Products must have the same variant_id to be merged.");
        }

        String uuid = existingProduct.getUuid(); // Keep the UUID from the existing product
        String variantId = existingProduct.getVariantId(); // Both should have the same variant ID

        int productId = existingProduct.getProductId() != 0 ? existingProduct.getProductId() : newProduct.getProductId();

        String sizeLabel = mergeStrings(existingProduct.getSizeLabel(), newProduct.getSizeLabel());
        String productName = mergeStrings(existingProduct.getProductName(), newProduct.getProductName());
        String brand = mergeBrandNames(existingProduct.getBrand(), newProduct.getBrand());
        String color = mergeStrings(existingProduct.getColor(), newProduct.getColor());
        String ageGroup = mergeStrings(existingProduct.getAgeGroup(), newProduct.getAgeGroup());
        String gender = mergeGender(existingProduct.getGender(), newProduct.getGender());
        String sizeType = mergeStrings(existingProduct.getSizeType(), newProduct.getSizeType());
        String productType = mergeStrings(existingProduct.getProductType(), newProduct.getProductType());

        boolean isDuplicate = true;

        return new Product(uuid, variantId, productId, sizeLabel, productName, brand, color, ageGroup, gender, sizeType, productType, isDuplicate);
    }

    private static String mergeStrings(String existingValue, String newValue) {
        if (existingValue == null || existingValue.isEmpty()) {
            return newValue;
        }
        if (newValue == null || newValue.isEmpty()) {
            return existingValue;
        }

        // Capitalize as needed (capitalize the first letter of each word)
        String formattedExistingValue = capitalizeWords(existingValue);
        String formattedNewValue = capitalizeWords(newValue);

        // Prefer the longer value if one is a substring of the other
        if (formattedExistingValue.contains(formattedNewValue)) {
            return formattedExistingValue;
        }
        if (formattedNewValue.contains(formattedExistingValue)) {
            return formattedNewValue;
        }

        // Default: return the most sensible one (alphabetically longer as a heuristic for importance)
        return formattedExistingValue.length() >= formattedNewValue.length() ? formattedExistingValue : formattedNewValue;
    }

    private static String mergeBrandNames(String existingBrand, String newBrand) {

        if ((existingBrand == null || existingBrand.isEmpty()) && (newBrand == null || newBrand.isEmpty())) {
            return "";
        }

        if (existingBrand == null || existingBrand.isEmpty()) {
            return capitalizeWords(newBrand);
        }
        if (newBrand == null || newBrand.isEmpty()) {
            return capitalizeWords(existingBrand);
        }

        // Ensure consistent capitalization
        String formattedExistingBrand = capitalizeWords(existingBrand);
        String formattedNewBrand = capitalizeWords(newBrand);

        // Prefer the longer one if one is a substring of the other
        if (formattedExistingBrand.contains(formattedNewBrand)) {
            return formattedExistingBrand;
        }
        if (formattedNewBrand.contains(formattedExistingBrand)) {
            return formattedNewBrand;
        }

        // Default: Concatenate them with a separator if they are completely different, we shall never reach this point
        return formattedExistingBrand + "/" + formattedNewBrand;
    }

    private static String mergeGender(String existingGender, String newGender) {
        // Normalize the gender strings to lowercase for comparison
        String normalizedExisting = existingGender == null ? "" : existingGender.trim().toLowerCase();
        String normalizedNew = newGender == null ? "" : newGender.trim().toLowerCase();

        // Define priority order
        String UNISEX = "unisex";
        String OTHER = "other";
        String MALE = "male";
        String FEMALE = "female";

        // If either value is unisex or other, prioritize it
        if (UNISEX.equals(normalizedExisting) || OTHER.equals(normalizedExisting)) {
            return capitalizeWords(normalizedExisting);
        }
        if (UNISEX.equals(normalizedNew) || OTHER.equals(normalizedNew)) {
            return capitalizeWords(normalizedNew);
        }

        // If one is male and the other is female, return unisex
        if ((MALE.equals(normalizedExisting) && FEMALE.equals(normalizedNew)) ||
                (FEMALE.equals(normalizedExisting) && MALE.equals(normalizedNew))) {
            return capitalizeWords(UNISEX);
        }

        // Default: Prefer non-empty value
        if (!normalizedExisting.isEmpty()) {
            return capitalizeWords(normalizedExisting);
        }
        if (!normalizedNew.isEmpty()) {
            return capitalizeWords(normalizedNew);
        }

        // If both are empty or null, return an empty string
        return "";
    }

    private static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (word.length() > 1) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            } else {
                capitalized.append(word.toUpperCase());
            }
            capitalized.append(" ");
        }
        return capitalized.toString().trim();
    }
}


