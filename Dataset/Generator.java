package Dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    private static void createDataset(int itemCount, String sizeLabel) {
        int[] itemWeights = new int[itemCount];
        int[] itemValues = new int[itemCount];
        populateRandomValues(itemWeights, itemValues);

        String outputFilePath = "data_" + sizeLabel + ".txt";
        saveToFile(itemWeights, itemValues, outputFilePath);
    }

    private static void populateRandomValues(int[] weights, int[] values) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = ThreadLocalRandom.current().nextInt(1, 51); // Weights in range [1, 50]
            values[i] = ThreadLocalRandom.current().nextInt(1, 101); // Values in range [1, 100]
        }
    }

    private static void saveToFile(int[] weights, int[] values, String filePath) {
        File file = new File(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < weights.length; i++) {
                bw.write(weights[i] + " " + values[i] + System.lineSeparator());
            }
            System.out.println("Data saved to " + file.getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Error writing to file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        createDataset(100, "kecil");
        createDataset(1000, "sedang");
        createDataset(10000, "besar");
    }
}
