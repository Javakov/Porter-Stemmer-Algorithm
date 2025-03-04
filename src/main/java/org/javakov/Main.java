package org.javakov;

import ca.rmen.porterstemmer.PorterStemmer;
import org.javakov.algorithm.PorterStemmerEn;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        String filePath = "src\\main\\resources\\testEn.txt";
        PorterStemmerEn stemmer = new PorterStemmerEn();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[\\s\\p{Punct}]+");

                for (String word : words) {
                    if (!word.isEmpty()) {
                        stemmer.add(word.toLowerCase().toCharArray(), word.length());
                        stemmer.stem();
                        System.out.print(stemmer + "\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
        }
    }

    public static void main1(String[] args) {
        PorterStemmer porterStemmer = new PorterStemmer();
        String stem = porterStemmer.stemWord("conflated");
        System.out.println(stem);
    }
}