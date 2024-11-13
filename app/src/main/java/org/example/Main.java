package org.example;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.*;

public class Main {

    private static final String CSV_FILE_PATH = "src/main/resources/smalldata.csv";

    public static void main(String[] args) {
        List<Product> products = loadCSVData(CSV_FILE_PATH);
        products.forEach(product ->   System.out.printf("Loaded Product: " + product + "\n"));
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
}


