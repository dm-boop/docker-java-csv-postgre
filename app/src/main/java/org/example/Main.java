package org.example;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Main {

    private static final String CSV_FILE_PATH = "src/main/resources/data.csv";

    // Database credentials and connection string
    //private static final String DB_URL = "jdbc:postgresql://localhost:5432/productdb";
    private static final String DB_URL = "jdbc:postgresql://postgres:5432/productdb";
    private static final String USER = "productuser";
    private static final String PASSWORD = "productpassword";

    public static void main(String[] args) {
        List<Product> products = loadCSVData(CSV_FILE_PATH);
        products.forEach(product -> System.out.println("Loaded Product: " + product));

//       products.forEach(product -> {
//           try {
//               insertProduct(
//                       product.getVariantId(),
//                       product.getProductId(),
//                       product.getSizeLabel(),
//                       product.getProductName(),
//                       product.getBrand(),
//                       product.getColor(),
//                       product.getAgeGroup(),
//                       product.getGender(),
//                       product.getSizeType(),
//                       product.getProductType()
//               );
//           } catch (SQLException e) {
//               System.err.println("Error inserting product: " + e.getMessage());
//           }
//       });
    }



    private static List<Product> loadCSVData(String filePath) {
        List<Product> products = new ArrayList<>();

        try (CSVParser csvParser = new CSVParser(
                new FileReader(filePath),
                CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                try {
                    // Sanitize and validate each field
                    String variantId = sanitizeString(record.get("variant_id"));
                    int productId = sanitizeInteger(record.get("product_id"), -1); // Default to -1 if invalid
                    String sizeLabel = sanitizeString(record.get("size_label"));
                    String productName = sanitizeString(record.get("product_name"));
                    String brand = sanitizeAndStandardizeBrand(record.get("brand"));
                    String color = sanitizeString(record.get("color"));
                    String ageGroup = sanitizeString(record.get("age_group"));
                    String gender = sanitizeString(record.get("gender"));
                    String sizeType = sanitizeString(record.get("size_type"));
                    String productType = sanitizeString(record.get("product_type"));

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

    private static String sanitizeString(String value) {
        return value == null ? "" : value.trim().replaceAll("[^\\p{Print}]", ""); // Remove non-printable chars
    }


    private static int sanitizeInteger(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue; // Return default value if parsing fails
        }
    }

    private static String sanitizeAndStandardizeBrand(String brand) {
        if (brand == null) return "Unknown";

        String sanitizedBrand = sanitizeString(brand).toLowerCase();

        // Example of standardizing common brand variations
        switch (sanitizedBrand) {
            case "adidas":
            case "adidas originals":
                return "Adidas";
            case "nike":
            case "nike inc.":
                return "Nike";
            default:
                return capitalizeFirstLetter(sanitizedBrand);
        }
    }

    private static String capitalizeFirstLetter(String value) {
        if (value.isEmpty()) return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }



    private static void insertProduct(String variantId, int productId, String sizeLabel, String productName,
           String brand, String color, String ageGroup, String gender,
           String sizeType, String productType) throws SQLException {

       String insertQuery = "INSERT INTO products (variant_id, product_id, size_label, product_name, " +
               "brand, color, age_group, gender, size_type, product_type) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

       // Establish database connection and execute insert - TODO This is definitely need to move from here
       try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

           pstmt.setString(1, variantId);
           pstmt.setInt(2, productId);
           pstmt.setString(3, sizeLabel);
           pstmt.setString(4, productName);
           pstmt.setString(5, brand);
           pstmt.setString(6, color);
           pstmt.setString(7, ageGroup);
           pstmt.setString(8, gender);
           pstmt.setString(9, sizeType);
           pstmt.setString(10, productType);

           pstmt.executeUpdate();
           System.out.println("Product inserted successfully.");

       } catch (SQLException e) {
           System.err.println("Error inserting product: " + e.getMessage());
           throw e;
       }
   }

}


