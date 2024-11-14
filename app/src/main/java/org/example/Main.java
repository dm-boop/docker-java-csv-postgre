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

    private static final String CSV_FILE_PATH = "src/main/resources/smalldata.csv";

    // Database credentials and connection string
    //private static final String DB_URL = "jdbc:postgresql://localhost:5432/productdb";
    private static final String DB_URL = "jdbc:postgresql://postgres:5432/productdb";
    private static final String USER = "productuser";
    private static final String PASSWORD = "productpassword";

    public static void main(String[] args) {
        List<Product> products = loadCSVData(CSV_FILE_PATH);
        products.forEach(product -> System.out.printf("Loaded Product: " + product + "\n"));

        products.forEach(product -> {
            try {
                insertProduct(
                        product.getVariantId(),
                        product.getProductId(),
                        product.getSizeLabel(),
                        product.getProductName(),
                        product.getBrand(),
                        product.getColor(),
                        product.getAgeGroup(),
                        product.getGender(),
                        product.getSizeType(),
                        product.getProductType()
                );
            } catch (SQLException e) {
                System.err.println("Error inserting product: " + e.getMessage());
            }
        });
    }



    private static List<Product> loadCSVData(String filePath) {
        List<Product> products = new ArrayList<>();

        try (CSVParser csvParser = new CSVParser(
                new FileReader(filePath),
                CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                Product product = new Product(
                        record.get("variant_id"),
                        Integer.parseInt(record.get("product_id")),
                        record.get("size_label"),
                        record.get("product_name"),
                        record.get("brand"),
                        record.get("color"),
                        record.get("age_group"),
                        record.get("gender"),
                        record.get("size_type"),
                        record.get("product_type")
                );
                products.add(product);
            }
        } catch (IOException e) {
            System.out.printf("Error reading CSV file", e);
        } catch (NumberFormatException e) {
            System.out.printf("Invalid number format in CSV data", e);
        }
        return products;
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


