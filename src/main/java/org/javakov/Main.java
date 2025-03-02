package org.javakov;

import org.javakov.algorithm.PorterStemmerRu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String filePath = "src\\main\\resources\\testRu.txt";

        // PorterStemmer stemmer = new PorterStemmer();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[\\s\\p{Punct}]+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        String stemmedWord = PorterStemmerRu.stem(word);
                        System.out.println(word + " -> " + stemmedWord);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error via reading file: " + e.getMessage());
        }
    }
}