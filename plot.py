import csv
import matplotlib.pyplot as plt


def read(path):
    with open(path) as f:
        return list(csv.DictReader(f))


dp = read("results/dp.csv")
sizes = [int(r["size"]) for r in dp]
plt.figure()
plt.plot(sizes, [float(r["lcs_ms"]) for r in dp], marker="o", label="LCS")
plt.plot(sizes, [float(r["edit_ms"]) for r in dp], marker="o", label="Edit Distance")
plt.xlabel("String length")
plt.ylabel("Time (ms)")
plt.title("LCS and Edit Distance")
plt.legend()
plt.grid(True)
plt.savefig("results/plot_dp.png")

rows = read("results/matching.csv")
for scenario in ["present", "absent", "worst"]:
    plt.figure()
    for name in ["Naive", "KMP", "RabinKarp"]:
        part = [r for r in rows if r["scenario"] == scenario and r["algorithm"] == name]
        plt.plot([int(r["text_length"]) for r in part],
                 [float(r["time_ms"]) for r in part], marker="o", label=name)
    plt.xlabel("Text length")
    plt.ylabel("Time (ms)")
    plt.title("String matching, pattern " + scenario)
    plt.legend()
    plt.grid(True)
    plt.savefig("results/plot_matching_" + scenario + ".png")

kasai = read("results/kasai.csv")
plt.figure()
plt.plot([int(r["size"]) for r in kasai], [float(r["kasai_ms"]) for r in kasai], marker="o")
plt.xlabel("String length")
plt.ylabel("Time (ms)")
plt.title("Kasai LCP construction")
plt.grid(True)
plt.savefig("results/plot_kasai.png")
