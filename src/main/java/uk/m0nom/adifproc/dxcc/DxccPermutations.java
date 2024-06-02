package uk.m0nom.adifproc.dxcc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class DxccPermutations {

    public static List<String> generate(String begin, String end) {
        if (begin.charAt(begin.length()-1) != end.charAt(end.length()-1)) {
            return generateInternal(begin, end);
        }

        int i = begin.length()-1;
        while (begin.charAt(i) == end.charAt(i)) {
            i--;
        }
        int suffixStart = i+1;
        String suffix = begin.substring(suffixStart);

        // String suffix off begin and end and generate variants
        List<String> variants = generateInternal(begin.substring(0, suffixStart), end.substring(0, suffixStart));
        // Add suffix back on
        variants = variants.stream().map(variant -> variant.concat(suffix)).collect(Collectors.toList());
        return variants;
    }

    private static List<String> generateInternal(String begin, String end) {
        List<String> result = new ArrayList<>();

        String current = begin;
        while (true) {
            result.add(current);
            if (current.equals(end))
                break;
            current = getNextPermutation(current, end);
        }

        return result;
    }

    private static String getNextPermutation(String current, String end) {
        char[] candidate = current.toCharArray();
        createNextPermutation(candidate, current.length()-1, end);
        return String.valueOf(candidate);
    }

    private static void createNextPermutation(char[] candidate, int index, String end) {
        char c = getNextChar(candidate[index]);
        if (c > end.charAt(index)) {
            candidate[index] = 'a';
            createNextPermutation(candidate, index-1, end);
        }
        else {
            candidate[index] = c;
        }
    }

    private static char getNextChar(char c) {
        return (char)(c + 1);
    }
}