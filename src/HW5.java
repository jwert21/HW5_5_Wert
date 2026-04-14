public class HW5 {

    private static final double[] ARABIC_FREQUENCIES = {
            11.6, 4.8, 3.7, 1.1, 2.8, 2.6, 1.1, 3.5,
            1.0, 4.7, 0.9, 6.5, 3.0, 2.9, 1.5, 1.7,
            0.7, 3.9, 1.0, 3.0, 2.7, 3.6, 5.3, 3.1,
            7.2, 2.5, 6.0, 6.7
    };

    public static void main(String[] args) {

        String ciphertext = "Aol xbpjr iyvdu mve qbtwz vcly aol shgf kvn.";

        String bestGuess = decryptUsingFrequencyAnalysis(ciphertext);

        System.out.println("Enter the API Key: ");

        System.out.println("Enter the plaintext: ");
    }

    public static String decryptUsingFrequencyAnalysis(String ciphertext) {

        String bestDecryption = "";
        double lowestChiSquare = Double.MAX_VALUE;

        for (int shift = 0; shift < 26; shift++) {

            String decryptedText = decryptWithShift(ciphertext, shift);
            double chiSquare = calculateChiSquare(decryptedText);

            if (chiSquare < lowestChiSquare) {
                lowestChiSquare = chiSquare;
                bestDecryption = decryptedText;
            }
        }

        return bestDecryption;
    }

    public static String decryptWithShift(String text, int shift) {
        StringBuilder decryptedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                decryptedText.append((char) ((c - base - shift + 26) % 26 + base));
            } else {
                decryptedText.append(c);
            }
        }

        return decryptedText.toString();
    }

    public static double calculateChiSquare(String text) {

        int[] letterCounts = new int[26];
        int totalLetters = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCounts[Character.toLowerCase(c) - 'a']++;
                totalLetters++;
            }
        }

        double chiSquare = 0.0;

        for (int i = 0; i < 26; i++) {
            double observed = letterCounts[i];
            double expected = totalLetters * ARABIC_FREQUENCIES[i] / 100;
            chiSquare += Math.pow(observed - expected, 2) / expected;
        }

        return chiSquare;
    }
}