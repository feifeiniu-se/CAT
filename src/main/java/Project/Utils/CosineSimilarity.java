package Project.Utils;

import java.util.HashMap;
import java.util.Map;

public class CosineSimilarity {
    public static double cosineSimilarity(String text1, String text2) {
        Map<String, Integer> vector1 = getWordFrequency(text1);
        Map<String, Integer> vector2 = getWordFrequency(text2);

        return calculate(vector1, vector2);
    }

    private static Map<String, Integer> getWordFrequency(String text) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = text.split("[\"\\s;{}(),./*+-]|(?<!\\[)]|\\[(?!])");
        for (String word : words) {
            if (!word.isEmpty()){
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }
        return wordFrequency;
    }

    public static double cosineRenameSimilarity(String oldLine, String newLine, String oldName, String newName) {
        Map<String, Integer> vector1 = getRenameWordFrequency(oldLine, oldName, newName);
        Map<String, Integer> vector2 = getWordFrequency(newLine);
        return calculate(vector1, vector2);
    }

    private static double calculate( Map<String, Integer> vector1, Map<String, Integer> vector2){
        for (String word : vector1.keySet()){
            if (!vector2.containsKey(word)){
                vector2.put(word, 0);
            }
        }

        for (String word : vector2.keySet()){
            if (!vector1.containsKey(word)){
                vector1.put(word, 0);
            }
        }

        double dotProduct = 0;
        for (String word : vector1.keySet()) {
            if (vector2.containsKey(word)) {
                dotProduct += vector1.get(word) * vector2.get(word);
            }
        }

        double norm1 = 0;
        for (int value : vector1.values()) {
            norm1 += value * value;
        }
        norm1 = Math.sqrt(norm1);

        double norm2 = 0;
        for (int value : vector2.values()) {
            norm2 += value * value;
        }
        norm2 = Math.sqrt(norm2);

        return dotProduct / (norm1 * norm2);
    }

    private static Map<String, Integer> getRenameWordFrequency(String text, String oldName, String newName) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = text.split("[\\s;{}(),./*+-]|(?<!\\[)]|\\[(?!])");
        for (String word : words) {
            if (!word.isEmpty()){
                if (word.equals(oldName)){
                    wordFrequency.put(newName, wordFrequency.getOrDefault(word, 0) + 1);
                } else {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }
        }
        return wordFrequency;
    }
}
