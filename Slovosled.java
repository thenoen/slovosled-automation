import java.io.FileWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Slovosled {

    public static void main(String[] args) {
        System.out.println("Slovosled");

        String hashesCacheLocation = "/tmp/slovosled-hashes-cache.txt";

        List<String> hashes = getHashes();
        System.out.println("Number of hashes: " + hashes.size());
    }

    private static List<String> getHashes() {
        String hashesCacheLocation = "/tmp/slovosled-hashes-cache.txt";

        var file = new File(hashesCacheLocation);

        if (file.exists()) {
            System.out.println("Hashes cache file exists - loading...");
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                var hashes = reader.lines().toList();
                return hashes;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        } else {

            var hashes = downloadHashes();
            try (FileWriter fileWriter = new FileWriter(hashesCacheLocation, false)) {
                for (String hash : hashes) {
                    fileWriter.write(hash.toString() + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            return Arrays.asList(hashes);
        }
        throw new RuntimeException("Unable to load hashes");
    }

    private static String[] downloadHashes() {
        System.out.println("Downloading hashes from slovosled.dennikn.sk ...");
        String content = null;
        URLConnection connection = null;
        try {
            // connection = new URL("https://slovosled.dennikn.sk/").openConnection();
            connection = URL.of(URI.create("https://slovosled.dennikn.sk/"), null).openConnection();

            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // System.out.println(content);

        Pattern pattern = Pattern.compile(".*?window.validWords = \"\\[(.*?)\\]\";.*?");
        var matcher = pattern.matcher(content);
        matcher.find();
        String words = matcher.group(1);
        var hashes = words.replaceAll("\\\\u0022", "").split(", ");
        System.out.println("Cashing downloaded hashes...");
        for (String hash : hashes) {
            System.out.println(hash);
        }
        return hashes;

    }

}
