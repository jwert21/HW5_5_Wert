import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HW5 {

    private static final char[] ARABIC_ALPHABET = {
            'ا','ب','ت','ث','ج','ح','خ','د','ذ','ر','ز','س','ش','ص',
            'ض','ط','ظ','ع','غ','ف','ق','ك','ل','م','ن','ه','و','ي'
    };

    private static final double[] ARABIC_FREQUENCIES = {
            11.6,4.8,3.7,1.1,2.8,2.6,1.1,3.5,1.0,4.7,0.9,6.5,3.0,2.9,
            1.5,1.7,0.7,3.9,1.0,3.0,2.7,3.6,5.3,3.1,7.2,2.5,6.0,6.7
    };

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

        try {

            System.out.print("Enter the API Key: ");
            String apiKey = sc.nextLine();

            System.out.print("Enter the plaintext message: ");
            String plaintext = sc.nextLine();

            System.out.print("Enter the Caesar shift key: ");
            int shift = Integer.parseInt(sc.nextLine());

            String translatedText = translateText(apiKey, plaintext, "ar");

            System.out.println("\nTranslated Arabic text:");
            System.out.println(translatedText);

            String encryptedText = caesarCipherArabic(translatedText, shift);

            System.out.println("\nEncrypted Arabic text:");
            System.out.println(encryptedText);

            System.out.println("\nArabic Frequency Analysis:");
            performArabicFrequencyAnalysis(encryptedText);

        } catch (Exception e) {

            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    public static String translateText(String apiKey, String text, String targetLang) throws Exception {

        String urlStr = "https://translation.googleapis.com/language/translate/v2?key=" + apiKey;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        String safeText = text.replace("\"", "\\\"");

        String jsonInput = "{ \"q\": \"" + safeText + "\", \"target\": \"" + targetLang + "\" }";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
        }

        BufferedReader br;

        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }

        br.close();

        String jsonResponse = response.toString();

        if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
            throw new RuntimeException("API request failed: " + jsonResponse);
        }

        String translated = extractTranslatedText(jsonResponse);

        if (translated.isEmpty()) {
            throw new RuntimeException("Could not extract translated text.");
        }

        return decodeBasicHtmlEntities(translated);
    }

    public static String extractTranslatedText(String json) {
        String marker = "\"translatedText\":\"";
        int start = json.indexOf(marker);

        if (start == -1) return "";

        start += marker.length();
        int end = json.indexOf("\"", start);

        if (end == -1) return "";

        return json.substring(start, end);
    }

    public static String decodeBasicHtmlEntities(String text) {
        return text.replace("&#39;", "'")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    public static String caesarCipherArabic(String input, int shift) {
        StringBuilder result = new StringBuilder();
        int length = ARABIC_ALPHABET.length;

        shift = shift % length;
        if (shift < 0) shift += length;

        for (char c : input.toCharArray()) {
            int index = indexOfArabicChar(c);

            if (index != -1) {
                int newIndex = (index + shift) % length;
                result.append(ARABIC_ALPHABET[newIndex]);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static int indexOfArabicChar(char c) {
        for (int i = 0; i < ARABIC_ALPHABET.length; i++) {
            if (ARABIC_ALPHABET[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public static void performArabicFrequencyAnalysis(String text) {
        int[] counts = new int[ARABIC_ALPHABET.length];
        int total = 0;

        for (char c : text.toCharArray()) {
            int idx = indexOfArabicChar(c);
            if (idx != -1) {
                counts[idx]++;
                total++;
            }
        }

        System.out.printf("%-5s %-10s %-12s %-12s%n", "Char", "Count", "Observed%", "Expected%");
        System.out.println("------------------------------------------------");

        for (int i = 0; i < ARABIC_ALPHABET.length; i++) {
            double observed = (total == 0) ? 0 : (counts[i] * 100.0 / total);

            System.out.printf("%-5s %-10d %-12.2f %-12.2f%n",
                    ARABIC_ALPHABET[i],
                    counts[i],
                    observed,
                    ARABIC_FREQUENCIES[i]);
        }

        System.out.println("\nTotal Arabic letters counted: " + total);

        System.out.println("Note: Arabic is right-to-left visually, but Java still processes characters normally.");
    }
}