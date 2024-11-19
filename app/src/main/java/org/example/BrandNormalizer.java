package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class BrandNormalizer {

    // Method to canonical brand names, returns a list of brands with original name -> canonical normalized name mapping
    public static List<Brand> canonicalBrands(List<Brand> brands) {
        // Map to store the canonical name for each group
        List<String> canonicalBrands = new ArrayList<>();

        for (Brand brand : brands) {
            String originalBrand = brand.getOriginalBrand();
            String normalizedBrand = normalizeBrand(originalBrand) == null ? "" : normalizeBrand(originalBrand);

            // Split the brand into words
            List<String> words = Arrays.asList(normalizedBrand.split("\\s+"));

            // Attempt to match this brand to an existing canonical brand
            boolean matched = false;
            String newCanonicalBrandName = "";
            for (String canonicalBrand : canonicalBrands) {

                // Check how many words the current brand has in common with the canonical one
                List<String> canonicalWords = Arrays.asList(canonicalBrand.split("\\s+"));
                int commonStartingWords = 0;
                for (int i = 0; i < Math.min(words.size(), canonicalWords.size()); i++) {
                    if (!words.get(i).equalsIgnoreCase(canonicalWords.get(i))) {
                        break; // Stop counting on first mismatch
                    }
                    commonStartingWords++;
                }


                if (canonicalWords.size() == 1 && commonStartingWords == 1) //This brand name must be canonical and already in the list of canonical brands
                {
                    matched = true;
                    brand.setNormalizedBrand(canonicalBrand);
                    brand.setSubBrand("");
                } else if (commonStartingWords >= 2)  // Let's assume that at least 2 words need to match at the beginning of the brand name, hopefully there are no separate brands that match this criteria
                {
                    matched = true;
                    // Update canonical name if the new brand is shorter
                    if (normalizedBrand.length() < canonicalBrand.length()) {
                        canonicalBrands.remove(canonicalBrand);
                        canonicalBrands.add(canonicalBrand);

                    } else {
                    }
                } else {
                    newCanonicalBrandName = normalizedBrand;
                }
            }

            // If no match was found, add this as a new canonical brand
            if (!matched) {
                canonicalBrands.add(newCanonicalBrandName);
            }
        }

        // When we are here, we have a canonical brand name list
        // Now we need to iterate over the brands again and set the canonical, normalized name and the subbrand

        for (Brand brand : brands) {
            String originalBrand = brand.getOriginalBrand();
            String normalizedBrand = normalizeBrand(originalBrand);

            // Split the brand into words
            List<String> words = Arrays.asList(normalizedBrand.split("\\s+"));

            // Attempt to match this brand to an existing canonical brand
            boolean matched = false;
            String canonicalBrandName = "";
            for (String canonicalBrand : canonicalBrands) {

                // Check how many words the current brand has in common with the canonical one
                List<String> canonicalWords = Arrays.asList(canonicalBrand.split("\\s+"));
                int commonStartingWords = 0;
                for (int i = 0; i < Math.min(words.size(), canonicalWords.size()); i++) {
                    if (!words.get(i).equalsIgnoreCase(canonicalWords.get(i))) {
                        break; // Stop counting on first mismatch
                    }
                    commonStartingWords++;
                }


                if (canonicalWords.size() == 1 && commonStartingWords == 1) //This brand name must be canonical and already in the list of canonical brands
                {
                    brand.setNormalizedBrand(canonicalBrand);
                    brand.setSubBrand("");
                } else if (commonStartingWords >= 2)  // Let's assume that at least 2 words need to match at the beginning of the brand name, hopefully there are no separate brands that match this criteria
                {
                    matched = true;
                    brand.setNormalizedBrand(canonicalBrand);
                    brand.setSubBrand(normalizedBrand.replaceFirst(Pattern.quote(canonicalBrand), "").trim());
                }
            }
        }


        return brands;
    }

    // Method to sanitize and normalize brand names
    private static String normalizeBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return ""; // Return empty string if input is null or empty
        }

        return Arrays.stream(brand.trim().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    // Method to find the normalized brand name for an original name
    public static String getNormalizedBrandNameForOriginalName(List<Brand> brands, String originalName) {
        if (brands == null || originalName == null || originalName.isEmpty()) {
            return null; // Return null if the list or original name is invalid
        }

        // Iterate through the list to find the matching originalBrand
        for (Brand brand : brands) {
            if (originalName.equals(brand.getOriginalBrand())) {
                return brand.getNormalizedBrand(); // Return the normalized name if found
            }
        }

        return ""; // Return empty string, but we shall never come here
    }
}