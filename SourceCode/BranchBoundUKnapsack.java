// The Exact Logic Of Code Was Taken From The Paper:
// An improved branch and bound algorithm for a strongly correlated unbounded knapsack problem
// Y-J Seong, Y-G G, M-K Kang & C-W Kang

package SourceCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BranchBoundUKnapsack {
    private int W;
    private List<Item> items;
    private int n;
    private int[][] M;
    private int[] arrayConfiguration;
    int answer;

    static class Item {
        int value;
        int weight;

        public Item(int value, int weight) {
            this.value = value;
            this.weight = weight;
        }
    }

    public int[] getarrayConfiguration() {
        return arrayConfiguration;
    }

    public BranchBoundUKnapsack(int W, List<Item> items) {
        this.W = W;
        this.items = items;
        this.n = items.size();
        this.M = new int[n][W + 1];
        this.arrayConfiguration = new int[n];
        this.answer = 0;
    }

    private void eliminateDominatedItems() {
        ArrayList<Integer> N = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            N.add(i);
        }
    
        int j = 0;
        while (j < N.size() - 1) {
            int k = j + 1;
            while (k < N.size()) {
                Item itemJ = items.get(N.get(j));
                Item itemK = items.get(N.get(k));
                int wj = itemJ.weight, vj = itemJ.value;
                int wk = itemK.weight, vk = itemK.value;
    
                if ((wk / wj) * vj >= vk) {
                    N.remove(k);
                } else if ((wj / wk) * vk >= vj) {
                    N.remove(j);
                    k = N.size();
                } else {
                    k++;
                }
            }
            j++;
        }
    
        ArrayList<Item> newItems = new ArrayList<>();
        for (Integer index : N) {
            newItems.add(items.get(index));
        }
        items = newItems;
        n = items.size();
    }    

    private int calculateUpperBound(int W, int V, int i) {
        if (i + 1 < n) {
            Item nextItem = items.get(i + 1);
            int z = V + (W / nextItem.weight) * nextItem.value;
    
            if (i + 2 < n) {
                Item nextNextItem = items.get(i + 2);
                int WDouble = W - (W / nextItem.weight) * nextItem.weight;
                int U = z + (WDouble * nextNextItem.value / nextNextItem.weight);
    
                return Math.max(U, z);
            }
            return z;
        }
        return V;
    }

    public void branchAndBound() {
        Object[] result = initialize();
        int[] x = (int[]) result[0];
        int i = (int) result[1];
        int V = (int) result[2];
        int W = (int) result[3];
        int U = (int) result[4];
        int[] m = (int[]) result[5];
    
        String nextStep = "Develop";
        while (!nextStep.equals("Finish")) {
            if (nextStep.equals("Develop")) {
                result = develop(x, i, V, W, U, m);
                x = (int[]) result[0];
                i = (int) result[1];
                V = (int) result[2];
                W = (int) result[3];
                nextStep = (String) result[4];
            } else if (nextStep.equals("Backtrack")) {
                result = backtrack(x, i, V, W, m);
                x = (int[]) result[0];
                i = (int) result[1];
                V = (int) result[2];
                W = (int) result[3];
                nextStep = (String) result[4];
            } else if (nextStep.equals("Replace")) {
                result = replaceitem(x, i, V, W, m);
                x = (int[]) result[0];
                i = (int) result[1];
                V = (int) result[2];
                W = (int) result[3];
                nextStep = (String) result[4];
            }
        }
    }

    private Object[] initialize() {
        eliminateDominatedItems();
        items.sort((item1, item2) -> Double.compare(item2.value / (double) item2.weight, item1.value / (double) item1.weight));
    
        M = new int[n][W + 1];
        arrayConfiguration = new int[n];
        answer = 0;
    
        int[] x = new int[n];
        int i = 0;
        x[0] = W / items.get(0).weight;
        int V = items.get(0).value * x[0];
        int W = this.W - items.get(0).weight * x[0];
        int U = calculateUpperBound(W, V, i);
        answer = V;
        arrayConfiguration = x.clone();
    
        int[] m = new int[n];
        for (int j = 0; j < n; j++) {
            int min_w = Integer.MAX_VALUE;
            for (int k = j + 1; k < n; k++) {
                if (items.get(k).weight < min_w) {
                    min_w = items.get(k).weight;
                }
            }
            m[j] = min_w;
        }
    
        return new Object[] {x, i, V, W, U, m};
    }

    private Object[] develop(int[] x, int i, int V, int W, int U, int[] m) {
        while (true) {
            if (W < m[i]) {
                if (answer < V) {
                    answer = V;
                    arrayConfiguration = x.clone();
                    if (answer == U) {
                        return new Object[] {x, i, V, W, "Finish"};
                    }
                }
                return new Object[] {x, i, V, W, "Backtrack"};
            } else {
                Integer min_j = null;
                for (int j = i + 1; j < n; j++) {
                    if (items.get(j).weight <= W) {
                        min_j = j;
                        break;
                    }
                }
                if (min_j == null || V + calculateUpperBound(W, V, min_j) <= answer) {
                    return new Object[] {x, i, V, W, "Backtrack"};
                }
                if (M[i][W] >= V) {
                    return new Object[] {x, i, V, W, "Backtrack"};
                }
                x[min_j] = W / items.get(min_j).weight;
                V += items.get(min_j).value * x[min_j];
                W -= items.get(min_j).weight * x[min_j];
                M[i][W] = V;
                i = min_j;
            }
        }
    }

    private Object[] backtrack(int[] x, int i, int V, int W, int[] m) {
        while (true) {
            Integer max_j = null;
            for (int j = 0; j <= i; j++) {
                if (x[j] > 0) {
                    max_j = j;
                }
            }
            if (max_j == null) {
                return new Object[] {x, i, V, W, "Finish"};
            }
            i = max_j;
            x[i]--;
            V -= items.get(i).value;
            W += items.get(i).weight;
    
            if (W < m[i]) {
                continue;
            }
            if (V + (int) Math.floor((double) W * items.get(i + 1).value / items.get(i + 1).weight) <= answer) {
                V -= items.get(i).value * x[i];
                W += items.get(i).weight * x[i];
                x[i] = 0;
                continue;
            }
            if (W >= m[i]) {
                return new Object[] {x, i, V, W, "Develop"};
            }
        }
    }    

    private Object[] replaceitem(int[] x, int i, int V, int W, int[] m) {
        int j = i, h = j + 1;
        while (true) {
            if (answer >= V + (int) Math.floor((double) W * items.get(h).value / items.get(h).weight)) {
                return new Object[] {x, i, V, W, "Backtrack"};
            }
            if (items.get(h).weight >= items.get(j).weight) {
                if (items.get(h).weight == items.get(j).weight || items.get(h).weight > W || answer >= V + items.get(h).value) {
                    h++;
                    continue;
                }
                answer = V + items.get(h).value;
                arrayConfiguration = x.clone();
                x[h] = 1;
                if (answer == calculateUpperBound(W, V, h)) {
                    return new Object[] {x, i, V, W, "Finish"};
                }
                j = h;
                h++;
                continue;
            } else {
                if (W - items.get(h).weight < m[h - 1]) {
                    h++;
                    continue;
                }
                i = h;
                x[i] = W / items.get(i).weight;
                V += items.get(i).value * x[i];
                W -= items.get(i).weight * x[i];
                return new Object[] {x, i, V, W, "Develop"};
            }
        }
    }

    public static void main(String[] args) {
        int W = 100;
        List<Item> items = Arrays.asList(
            new Item(10, 5), new Item(30, 10), new Item(20, 15)
        );
    
        BranchBoundUKnapsack test = new BranchBoundUKnapsack(W, items);
        test.branchAndBound();

        System.out.println(test.answer);
    }    
}

// Credits for Discussion in Code Idea: Bryan Tjandra