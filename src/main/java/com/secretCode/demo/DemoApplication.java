// package com.secretCode.demo;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// import java.io.File;
// import java.math.BigInteger;
// import java.util.*;

// @SpringBootApplication
// public class DemoApplication {

//     public static void main(String[] args) throws Exception {
//         SpringApplication.run(DemoApplication.class, args);

//         ObjectMapper mapper = new ObjectMapper();
//         JsonNode root = mapper.readTree(new File("input.json"));

//         int n = root.get("keys").get("n").asInt();
//         int k = root.get("keys").get("k").asInt();

//         List<Share> shares = new ArrayList<>();

//         for (int i = 1; i <= n; i++) {
//             JsonNode node = root.get(String.valueOf(i));
//             int base = node.get("base").asInt();
//             String valueStr = node.get("value").asText();
//             BigInteger x = BigInteger.valueOf(i);
//             BigInteger y = new BigInteger(valueStr, base);
//             shares.add(new Share(x, y));
//         }

//         Set<BigInteger> possibleSecrets = new HashSet<>();
//         List<List<Share>> combinations = getCombinations(shares, k);

//         for (List<Share> subset : combinations) {
//             BigInteger secret = lagrangeInterpolation(subset);
//             possibleSecrets.add(secret);
//         }

//         BigInteger correctSecret = findMostFrequent(possibleSecrets, shares, k);
//         System.out.println("Reconstructed Secret: " + correctSecret);
//     }

//     record Share(BigInteger x, BigInteger y) {}

//     static BigInteger lagrangeInterpolation(List<Share> shares) {
//         BigInteger result = BigInteger.ZERO;
//         for (int i = 0; i < shares.size(); i++) {
//             BigInteger xi = shares.get(i).x();
//             BigInteger yi = shares.get(i).y();
//             BigInteger term = yi;

//             for (int j = 0; j < shares.size(); j++) {
//                 if (i != j) {
//                     BigInteger xj = shares.get(j).x();
//                     BigInteger numerator = xj.negate();
//                     BigInteger denominator = xi.subtract(xj);
//                     term = term.multiply(numerator).divide(denominator);
//                 }
//             }
//             result = result.add(term);
//         }
//         return result;
//     }

//     static List<List<Share>> getCombinations(List<Share> shares, int k) {
//         List<List<Share>> result = new ArrayList<>();
//         combine(shares, 0, k, new ArrayList<>(), result);
//         return result;
//     }

//     static void combine(List<Share> arr, int index, int k, List<Share> current, List<List<Share>> result) {
//         if (current.size() == k) {
//             result.add(new ArrayList<>(current));
//             return;
//         }
//         for (int i = index; i < arr.size(); i++) {
//             current.add(arr.get(i));
//             combine(arr, i + 1, k, current, result);
//             current.remove(current.size() - 1);
//         }
//     }

//     static BigInteger findMostFrequent(Set<BigInteger> secrets, List<Share> shares, int k) {
//         Map<BigInteger, Integer> frequency = new HashMap<>();
//         List<List<Share>> combinations = getCombinations(shares, k);

//         for (List<Share> subset : combinations) {
//             BigInteger secret = lagrangeInterpolation(subset);
//             frequency.put(secret, frequency.getOrDefault(secret, 0) + 1);
//         }

//         return frequency.entrySet().stream()
//                 .max(Map.Entry.comparingByValue())
//                 .get().getKey();
//     }

//     // Additional math functions
//     static BigInteger lcm(BigInteger a, BigInteger b) {
//         return a.multiply(b).divide(a.gcd(b));
//     }

//     static BigInteger hcf(BigInteger a, BigInteger b) {
//         return a.gcd(b);
//     }

//     static BigInteger sum(BigInteger a, BigInteger b) {
//         return a.add(b);
//     }

//     static BigInteger multiply(BigInteger a, BigInteger b) {
//         return a.multiply(b);
//     }
// }




package com.secretCode.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);

        // Read JSON file
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("input.json");
        JsonNode root = mapper.readTree(file);

        int n = root.get("keys").get("n").asInt();
        int k = root.get("keys").get("k").asInt();

        Map<Integer, BigInteger> x = new HashMap<>();
        Map<Integer, BigInteger> y = new HashMap<>();

        int count = 0;
        for (int i = 1; i <= n && count < k; i++) {
            String key = String.valueOf(i);
            if (!root.has(key)) continue;

            JsonNode node = root.get(key);
            int base = Integer.parseInt(node.get("base").asText());
            String value = node.get("value").asText();

            BigInteger yVal = new BigInteger(value, base);
            x.put(i, BigInteger.valueOf(i));
            y.put(i, yVal);
            count++;
        }

        System.out.println("Reconstructed Secret: " + lagrangeInterpolation(x, y));
    }

    static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> x, Map<Integer, BigInteger> y) {
        BigInteger secret = BigInteger.ZERO;

        for (int i : x.keySet()) {
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j : x.keySet()) {
                if (i != j) {
                    num = num.multiply(BigInteger.valueOf(-j));
                    den = den.multiply(BigInteger.valueOf(i - j));
                }
            }

            BigInteger term = y.get(i).multiply(num).divide(den);
            secret = secret.add(term);
        }

        return secret;
    }

    // Add utility functions for future enhancements (like LCM, HCF, etc.)
    public static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    public static int lcm(int a, int b) {
        return a * (b / gcd(a, b));
    }
}
