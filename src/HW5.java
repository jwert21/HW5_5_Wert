import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLCOnnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HW5 {

    private static final double[] ARABIC_FREQUENCIES = {
            11.6, 4.8, 3.7, 1.1, 2.8, 2.6, 1.1, 3.5,
            1.0, 4.7, 0.9, 6.5, 3.0, 2.9, 1.5, 1.7,
            0.7, 3.9, 1.0, 3.0, 2.7, 3.6, 5.3, 3.1,
            7.2, 2.5, 6.0, 6.7
    };

    public static void main(String[] args) {
        // Example Caesar cipher text (shifted version of a known sentence)
        String ciphertext = "Aol xbpjr iyvdu mve qbtwz vcly aol shgf kvn.";

        // Attempt to decrypt using frequency analysis
        String bestGuess = decryptUsingFrequencyAnalysis(ciphertext);

        // Output the most likely plaintext
        System.out.println("Decrypted text (best guess): " + bestGuess);
    }

    /**
     * Tries all 26 possible Caesar shifts and selects the one that produces
     * text closest to standard English letter frequencies.
     */
    public static String decryptUsingFrequencyAnalysis(String ciphertext) {
        String bestDecryption = "";
        double lowestChiSquare = Double.MAX_VALUE; // Lower is better match

        // Try all possible shifts (0–25)
        for (int shift = 0; shift < 26; shift++) {

            // Decrypt using the current shift
            String decryptedText = decryptWithShift(ciphertext, shift);

            // Measure how "English-like" the result is
            double chiSquare = calculateChiSquare(decryptedText);

            // Keep track of the best (lowest chi-square score)
            if (chiSquare < lowestChiSquare) {
                lowestChiSquare = chiSquare;
                bestDecryption = decryptedText;
            }
        }

        return bestDecryption;
    }

    /**
     * Decrypts a Caesar cipher using a given shift value.
     * Handles both uppercase and lowercase letters.
     */
    public static String decryptWithShift(String text, int shift) {
        StringBuilder decryptedText = new StringBuilder();

        for (char c : text.toCharArray()) {

            // Only shift alphabetic characters
            if (Character.isLetter(c)) {

                // Determine base ASCII value ('A' or 'a')
                char base = Character.isUpperCase(c) ? 'A' : 'a';

                // Apply reverse shift with wrap-around using modulo
                decryptedText.append(
                        (char) ((c - base - shift + 26) % 26 + base)
                );

            } else {
                // Preserve spaces, punctuation, etc.
                decryptedText.append(c);
            }
        }

        return decryptedText.toString();
    }

    /**
     * Computes the Chi-Square statistic:
     * Measures how closely the letter frequency of the given text
     * matches expected English letter frequencies.
     *
     * Lower value = closer match to English = more likely correct decryption
     */
    public static double calculateChiSquare(String text) {

        int[] letterCounts = new int[26]; // Frequency of A–Z
        int totalLetters = 0;

        // Count occurrences of each letter
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char lowerCaseChar = Character.toLowerCase(c);
                letterCounts[lowerCaseChar - 'a']++;
                totalLetters++;
            }
        }

        double chiSquare = 0.0;

        // Compare observed vs expected frequencies
        for (int i = 0; i < 26; i++) {
            double observed = letterCounts[i];

            // Expected count based on English frequency distribution
            double expected = totalLetters * ARABIC_FREQUENCIES[i] / 100;

            // Chi-square formula: Σ (O - E)^2 / E
            chiSquare += Math.pow(observed - expected, 2) / expected;
        }

        return chiSquare;
    }
}