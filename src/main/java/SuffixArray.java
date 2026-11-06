import java.util.Arrays;

public class SuffixArray {

    public static int[] build(String s) {
        int n = s.length();
        Integer[] boxed = new Integer[n];
        for (int i = 0; i < n; i++) {
            boxed[i] = i;
        }

        Arrays.sort(boxed, (a, b) -> compareSuffixes(s, a, b));

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = boxed[i];
        }
        return sa;
    }

    private static int compareSuffixes(String s, int a, int b) {
        int n = s.length();
        while (a < n && b < n) {
            if (s.charAt(a) != s.charAt(b)) {
                return s.charAt(a) - s.charAt(b);
            }
            a++;
            b++;
        }
        return (n - a) - (n - b);
    }
}
