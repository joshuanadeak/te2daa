package SourceCode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Run {
    public static void main(String[] args) {
        String[] files = {"data_kecil.txt", "data_sedang.txt", "data_besar.txt"};
        try {
            for (String filename : files) {
                // Load the dataset
                List<BranchBoundUKnapsack.Item> items = loadDataset("Dataset/" + filename);

                // Determine W based on the file name
                int W = determineCapacity(filename);

                // Dynamic Programming Approach
                int[] weights = new int[items.size()];
                int[] values = new int[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    weights[i] = items.get(i).weight;
                    values[i] = items.get(i).value;
                }

                long dpStart = System.nanoTime();
                triggerGC();
                long dpStartMemoryUsage = getMemoryUsage();
                int dpResult = DynamicProgrammingUKnapsack.unboundedKnapsack(W, values.length, values, weights);
                long dpEndMemoryUsage = getMemoryUsage();
                long dpEnd = System.nanoTime();

                printResults("Dynamic Programming", filename, dpStart, dpEnd, dpStartMemoryUsage, dpEndMemoryUsage, dpResult);

                // Branch and Bound Approach
                long bnbStart = System.nanoTime();
                triggerGC();
                long bnbStartMemoryUsage = getMemoryUsage();
                BranchBoundUKnapsack knapsack = new BranchBoundUKnapsack(W, items);
                knapsack.branchAndBound();
                long bnbEndMemoryUsage = getMemoryUsage();
                long bnbEnd = System.nanoTime();

                printResults("Branch and Bound", filename, bnbStart, bnbEnd, bnbStartMemoryUsage, bnbEndMemoryUsage, knapsack.answer);

                System.out.println("======================================");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<BranchBoundUKnapsack.Item> loadDataset(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<BranchBoundUKnapsack.Item> items = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int value = Integer.parseInt(parts[0]);
            int weight = Integer.parseInt(parts[1]);
            items.add(new BranchBoundUKnapsack.Item(value, weight));
        }

        reader.close();
        return items;
    }

    private static long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static int determineCapacity(String filename) {
        if (filename.contains("kecil")) {
            return 100;
        }
        else if (filename.contains("sedang")) {
            return 1000;
        }
        else if (filename.contains("besar")) {
            return 10000;
        }
        return 0;
    }

    private static void printResults(String method, String filename, long startTime, long endTime, long startMemoryUsage, long endMemoryUsage, int result) {
        double executionTime = ((double) (endTime - startTime) / 1_000_000); // Convert nanoseconds to milliseconds
        double memoryUsage = (double) ((endMemoryUsage - startMemoryUsage) / 1024); // Convert bytes to kilobytes

        System.out.println(method + " on " + filename);
        System.out.println("Result: " + result);
        System.out.println("Execution Time (ms): " + executionTime);
        System.out.println("Memory Usage (KB): " + memoryUsage);
    }

    private static void triggerGC() {
        System.gc();
        System.gc();
        System.gc();
    }
}
