package com.mccarl.es;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("input.csv")) {
            if (inputStream == null) {
                throw new FileNotFoundException("File not found in resources: input.csv");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                solution(br);
            }
        }
    }

    private static void solution(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            List<Integer> numbers = new ArrayList<>();

            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    try {
                        numbers.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException e) {
                        // Skip invalid numbers
                    }
                }
            }

            if (numbers.size() < 2) {
                continue;
            }

            int target = numbers.removeFirst();

            Result result = findOptimalSubset(target, numbers);

            System.out.println("Target: " + target + " → Selected: " + result.numbers + ", Sum: " + result.sum +
                    ", Difference: " + (target - result.sum));
        }
    }

    private static Result findOptimalSubset(int target, List<Integer> candidates) {
        List<Integer> validNumbers = new ArrayList<>();
        for (int num : candidates) {
            if (num > 0) {
                validNumbers.add(num);
            }
        }

        if (validNumbers.isEmpty()) {
            return new Result(new ArrayList<>(), 0);
        }

        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        Map<Integer, int[]> parent = new HashMap<>();

        for (int num : validNumbers) {
            for (int i = target - num; i >= 0; i--) {
                if (dp[i] && i + num <= target && !dp[i + num]) {
                    dp[i + num] = true;
                    parent.put(i + num, new int[]{i, num});
                }
            }
        }

        int bestSum = target;
        while (bestSum > 0 && !dp[bestSum]) {
            bestSum--;
        }

        List<Integer> selectedNumbers = new ArrayList<>();
        int currentSum = bestSum;

        while (currentSum > 0 && parent.containsKey(currentSum)) {
            int[] parentInfo = parent.get(currentSum);
            selectedNumbers.add(parentInfo[1]);
            currentSum = parentInfo[0];
        }

        Collections.sort(selectedNumbers);

        return new Result(selectedNumbers, bestSum);
    }



    static class Result {
        List<Integer> numbers;
        int sum;

        Result(List<Integer> numbers, int sum) {
            this.numbers = numbers;
            this.sum = sum;
        }
    }
}