import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Benchmark {

    static final String ALPHABET = "abcd";
    static final long SEED = 42;
    static final int RUNS = 5;
    static final int PATTERN_LENGTH = 20;
    static int sink = 0;

    public static void main(String[] args) throws IOException {
        runDynamicProgramming();
        runStringMatching();
        runKasai();
    }

    static String randomString(Random random, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    static double averageMillis(Runnable task) {
        task.run();
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            task.run();
            total += System.nanoTime() - start;
        }
        return total / (double) RUNS / 1_000_000.0;
    }

    static void save(String file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    static void runDynamicProgramming() throws IOException {
        int[] sizes = {100, 500, 1000};
        Random random = new Random(SEED);
        StringBuilder csv = new StringBuilder("size,lcs_ms,edit_ms\n");

        System.out.println("LCS and Edit Distance");
        for (int size : sizes) {
            String a = randomString(random, size);
            String b = randomString(random, size);

            double lcsTime = averageMillis(() -> sink += LCS.length(a, b));
            double editTime = averageMillis(() -> sink += EditDistance.distance(a, b));

            System.out.printf("n=%d  LCS=%.3f ms  Edit=%.3f ms%n", size, lcsTime, editTime);
            csv.append(size).append(",").append(lcsTime).append(",").append(editTime).append("\n");
        }
        save("results/dp.csv", csv.toString());
    }

    static void runStringMatching() throws IOException {
        int[] sizes = {10000, 50000, 100000};
        Random random = new Random(SEED);
        StringBuilder csv = new StringBuilder(
                "scenario,text_length,algorithm,time_ms,comparisons,matches\n");

        System.out.println();
        System.out.println("String matching");
        for (int size : sizes) {
            String text = randomString(random, size);
            String present = text.substring(size / 2, size / 2 + PATTERN_LENGTH);
            String absent = randomString(random, PATTERN_LENGTH);
            while (!NaiveMatcher.search(text, absent).isEmpty()) {
                absent = randomString(random, PATTERN_LENGTH);
            }
            String worstText = "a".repeat(size);
            String worstPattern = "a".repeat(PATTERN_LENGTH) + "b";

            matchScenario(csv, "present", text, present);
            matchScenario(csv, "absent", text, absent);
            matchScenario(csv, "worst", worstText, worstPattern);
        }
        save("results/matching.csv", csv.toString());
    }

    static void matchScenario(StringBuilder csv, String scenario, String text, String pattern) {
        double naiveTime = averageMillis(() -> sink += NaiveMatcher.search(text, pattern).size());
        double kmpTime = averageMillis(() -> sink += KMP.search(text, pattern).size());
        double rkTime = averageMillis(() -> sink += RabinKarp.search(text, pattern).size());

        NaiveMatcher.comparisons = 0;
        List<Integer> naiveResult = NaiveMatcher.search(text, pattern);
        KMP.comparisons = 0;
        List<Integer> kmpResult = KMP.search(text, pattern);
        RabinKarp.comparisons = 0;
        List<Integer> rkResult = RabinKarp.search(text, pattern);

        if (!naiveResult.equals(kmpResult) || !naiveResult.equals(rkResult)) {
            throw new IllegalStateException("Algorithms disagree on " + scenario);
        }

        int n = text.length();
        int matches = naiveResult.size();
        add(csv, scenario, n, "Naive", naiveTime, NaiveMatcher.comparisons, matches);
        add(csv, scenario, n, "KMP", kmpTime, KMP.comparisons, matches);
        add(csv, scenario, n, "RabinKarp", rkTime, RabinKarp.comparisons, matches);
    }

    static void add(StringBuilder csv, String scenario, int n, String name,
                    double time, long comparisons, int matches) {
        System.out.printf("%-8s n=%-7d %-10s %9.4f ms  comparisons=%-9d matches=%d%n",
                scenario, n, name, time, comparisons, matches);
        csv.append(scenario).append(",").append(n).append(",").append(name).append(",")
                .append(time).append(",").append(comparisons).append(",").append(matches).append("\n");
    }

    static void runKasai() throws IOException {
        int[] sizes = {1000, 5000, 10000};
        Random random = new Random(SEED);
        StringBuilder csv = new StringBuilder("size,kasai_ms\n");

        System.out.println();
        System.out.println("Kasai");
        for (int size : sizes) {
            String s = randomString(random, size);
            int[] sa = SuffixArray.build(s);

            double time = averageMillis(() -> sink += Kasai.buildLcp(s, sa)[size / 2]);

            System.out.printf("n=%d  Kasai=%.4f ms%n", size, time);
            csv.append(size).append(",").append(time).append("\n");
        }
        save("results/kasai.csv", csv.toString());
    }
}
