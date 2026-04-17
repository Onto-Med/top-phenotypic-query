package care.smith.top.top_phenotypic_query.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombiIndices {

  public static List<int[]> get(int loop, List<Integer> counts) {
    return get(loop, counts.stream().mapToInt(i -> i).toArray());
  }

  public static List<int[]> get(int loop, int... counts) {
    List<int[]> combiIndices = new ArrayList<>();
    calculate(loop, counts, combiIndices);
    return combiIndices;
  }

  private static void calculate(int loop, int[] counts, List<int[]> combiIndices) {
    if (loop == 0) {
      for (int i = counts[0]; i >= 0; i--) {
        int[] combi = Arrays.copyOf(counts, counts.length);
        combi[0] = i;
        combiIndices.add(combi);
      }
      return;
    }

    for (int i = counts[loop]; i >= 0; i--) {
      int[] newCounts = Arrays.copyOf(counts, counts.length);
      newCounts[loop] = i;
      calculate(loop - 1, newCounts, combiIndices);
    }
  }
}
