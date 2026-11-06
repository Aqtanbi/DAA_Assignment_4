import java.util.Arrays;
import java.util.List;

public class Tests {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        testLcs();
        testEditDistance();
        testMatching();
        testKasai();

        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            failed++;
            System.out.println("FAIL " + name + " expected=" + expected + " actual=" + actual);
        }
    }

    static void testLcs() {
        check("LCS identical", 3, LCS.length("abc", "abc"));
        check("LCS different", 0, LCS.length("abc", "xyz"));
        check("LCS empty first", 0, LCS.length("", "abc"));
        check("LCS empty second", 0, LCS.length("abc", ""));
        check("LCS classic", 4, LCS.length("ABCBDAB", "BDCABA"));
    }

    static void testEditDistance() {
        check("Edit identical", 0, EditDistance.distance("hello", "hello"));
        check("Edit empty first", 3, EditDistance.distance("", "abc"));
        check("Edit empty second", 3, EditDistance.distance("abc", ""));
        check("Edit substitution", 1, EditDistance.distance("cat", "cut"));
        check("Edit insertion", 1, EditDistance.distance("cat", "cats"));
        check("Edit deletion", 1, EditDistance.distance("cats", "cat"));
        check("Edit kitten sitting", 3, EditDistance.distance("kitten", "sitting"));
    }

    static void checkMatch(String name, String text, String pattern, List<Integer> expected) {
        check(name + " naive", expected, NaiveMatcher.search(text, pattern));
        check(name + " kmp", expected, KMP.search(text, pattern));
        check(name + " rabin-karp", expected, RabinKarp.search(text, pattern));
    }

    static void testMatching() {
        checkMatch("Match beginning", "abcdef", "abc", Arrays.asList(0));
        checkMatch("Match middle", "abcdef", "cd", Arrays.asList(2));
        checkMatch("Match end", "abcdef", "ef", Arrays.asList(4));
        checkMatch("Match multiple", "abababa", "aba", Arrays.asList(0, 2, 4));
        checkMatch("Match all same", "aaaaa", "aa", Arrays.asList(0, 1, 2, 3));
        checkMatch("Match absent", "abcdef", "xyz", Arrays.asList());
        checkMatch("Match longer pattern", "abc", "abcdef", Arrays.asList());
        checkMatch("Match empty pattern", "abc", "", Arrays.asList());
        checkMatch("Match whole text", "abc", "abc", Arrays.asList(0));
    }

    static void testKasai() {
        String s = "banana";
        int[] expectedSa = {5, 3, 1, 0, 4, 2};
        int[] expectedLcp = {0, 1, 3, 0, 0, 2};

        int[] sa = SuffixArray.build(s);
        check("Suffix array banana", Arrays.toString(expectedSa), Arrays.toString(sa));
        check("Kasai LCP banana", Arrays.toString(expectedLcp), Arrays.toString(Kasai.buildLcp(s, expectedSa)));

        String t = "abab";
        int[] expectedSa2 = {2, 0, 3, 1};
        int[] expectedLcp2 = {0, 2, 0, 1};
        check("Kasai LCP abab", Arrays.toString(expectedLcp2), Arrays.toString(Kasai.buildLcp(t, expectedSa2)));
    }
}
