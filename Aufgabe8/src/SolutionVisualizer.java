package src;

import src.data.Structure;

import java.util.Map;
import java.util.TreeMap;
import src.data.Cube;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for visualizing cube structures and formatting output.
 * <p>
 * The `SolutionVisualizer` class provides methods to generate a textual
 * representation of a cube structure in a 2D grid format. It also includes
 * methods for formatting headers and footers for better display in the console.
 * </p>
 */
public class SolutionVisualizer {

    /**
     * Visualizes a cube Structure as a 2D grid.
     * <p>
     * Each position in the grid corresponds to the x and y coordinates of a cube, with the value at that
     * position representing the height (z-coordinate + 1) of the cube. Empty positions are represented as spaces,
     * numbers from 1-9 are shown as-is, and values greater than 9 are represented as letters (A, B, ...).
     * </p>
     *
     * @author Oliver Hagenauer
     *
     * @param structure the structure to visualize
     * @return a string representation of the structure in a 2D grid format
     */
    public static String visualize(Structure structure) {
        // make a grid with rows for y coordinate and elements mapping x coordinate to height
        Map<Integer, Map<Integer, Integer>> grid = structure.stream()
                .collect(Collectors.groupingBy(
                        Cube::y,
                        TreeMap::new,
                        Collectors.toMap(
                                Cube::x,
                                cube -> cube.z() + 1,
                                Math::max,
                                TreeMap::new
                        )
                ));

        int minX = grid.values().stream()
                .flatMap(map -> map.keySet().stream())
                .min(Integer::compare)
                .orElse(0);

        int maxX = grid.values().stream()
                .flatMap(map -> map.keySet().stream())
                .max(Integer::compare)
                .orElse(0);

        return grid.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(row -> IntStream.rangeClosed(minX, maxX)
                        .mapToObj(x -> {
                            int height = row.getValue().getOrDefault(x, 0);
                            if (height == 0) return "  ";
                            return height <= 9 ? height + " " : (char) ('A' + height - 10) + " ";
                        })
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n", "\n", ""));
    }

    /**
     * Formats a header string for an optimization task.
     * <p>
     * The header includes the title of the task, the parameters `n`, `m`, and `k`, and is centered within a
     * fixed-width box with borders.
     * </p>
     *
     * @param title the title of the solution
     * @param n     the number of cubes in the solution
     * @param m     the maximum height of the structure
     * @param k     the maximum number of solutions to retain
     * @return a formatted header string
     */
    public static String formatHeader(String title, int n, int m, int k) {
        String paddedTitle = String.format(" %s using n=%d m=%d k=%d ", title, n, m, k);
        int totalWidth = 50;
        int padding = (totalWidth - paddedTitle.length()) / 2;
        int extraPadding = (totalWidth - paddedTitle.length()) % 2; // Add 1 space if odd-length
        String border = "═".repeat(totalWidth);

        return String.format(
                """
                ╔%s╗
                ║%s%s%s%s║
                ╚%s╝
                """,
                border,
                " ".repeat(padding), paddedTitle, " ".repeat(padding), " ".repeat(extraPadding),
                border);
    }

    /**
     * Formats a footer string for an optimization task.
     * <p>
     * The footer includes the title of the solution and is centered within a fixed-width box with borders.
     * </p>
     *
     * @param title the title of the solution
     * @return a formatted footer string
     */
    public static String formatFooter(String title) {
        String paddedTitle = String.format(" End %s ", title);
        int totalWidth = 50;
        int padding = (totalWidth - paddedTitle.length()) / 2;
        int extraPadding = (totalWidth - paddedTitle.length()) % 2; // Add 1 space if odd-length
        String border = "═".repeat(totalWidth);

        return String.format(
                """
                
                ╔%s╗
                ║%s%s%s%s║
                ╚%s╝
                """,
                border,
                " ".repeat(padding), paddedTitle, " ".repeat(padding), " ".repeat(extraPadding),
                border);
    }
}

