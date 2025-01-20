package src;

import src.data.Cube;
import src.data.Structure;
import src.utils.Direction;

import java.util.*;

/**
 * Provides a collection of placement validators to enforce placement constraints on cube structures.
 * <p>
 * Each placement validator defines specific rules that cubes must satisfy to be validly
 * placed in a structure. These validators can be freely customized to support different
 * structural and aesthetic requirements.
 * </p>
 */
public class ValidationFunctions {

    /**
     * Provides the default validation rules for cube placement in accordancce with the task description.
     * <p>
     * The default validation ensures:
     * <ul>
     *     <li>The cube does not exceed the maximum height.</li>
     *     <li>The cube is either on the ground or supported by another cube.</li>
     *     <li>The cube is connected to at least one other cube.</li>
     *     <li>The cube and all adjacent cubes have at least one free side face after placement.</li>
     * </ul>
     * </p>
     *
     * @param m the maximum height of the structure
     * @return a `PlacementValidator` implementing the default rules
     */
    public static PlacementValidator defaultValidation(int m) {
        return (structure, cube) -> {
            if (structure.isEmpty()) return true; // Skip checks for the first cube (0,0,0) = always valid
            if (cube.z() >= m ||
                    !structure.cubeIsSupported(cube) || // fast check
                    !structure.cubeIsConnected(cube) || //  slower check, needs to iterate.
                    !structure.cubeHasFreeSideFace(cube)) { // slower check, needs to iterate.
                return false;
            }

            Structure tempStructure = structure.addCube(cube);
            return Direction.dirStream()// check for free side face.
                    .map(dir -> tempStructure.getAdjacent(cube, dir))
                    .filter(Objects::nonNull)
                    .allMatch(tempStructure::cubeHasFreeSideFace);
        };
    }

    /**
     * Provides validation rules for the "River" structure.
     * <p>
     * The River validation ensures:
     * <ul>
     *     <li>The cube remains within the specified bounds.</li>
     *     <li>The cube is placed on the ground level only.</li>
     *     <li>The cube, when placed, has exactly one adjacent cube.</li>
     *     <li>A maximum length of 10 of any straight line of cubes is not exceeded.</li>
     *     <li>After placement, all other cubes in the structure have at most two adjacent cubes,
     *     i.e., there are no branches. </li>
     * </ul>
     * </p>
     *
     * @param m the maximum size (side length) of the structure
     * @return a `PlacementValidator` implementing the meandering river rules
     */
    public static PlacementValidator riverValidation(int m) {
        return new PlacementValidator() {
            private final int maxStraightLineLength = 10; // Maximum length of a straight line
            private final int maxSideLength = m; // Maximum side length of the structure

            @Override
            public boolean isValid(Structure structure, Cube cube) {
                if (structure.isEmpty()) return true;

                if (cube.z() > 0 ||
                        Math.abs(cube.x()) > maxSideLength / 2 ||
                        Math.abs(cube.y()) > maxSideLength / 2) {
                    //System.out.println("Cube " + cube + " invalid: maxSideLength / 2 = " + maxSideLength / 2);
                    return false; // Cube must be within bounds
                }

                Structure tempStructure = structure.addCube(cube);

                long adjacentCount = Direction.dirStream()
                        .map(dir -> tempStructure.getAdjacent(cube, dir))
                        .filter(Objects::nonNull)
                        .count();

                if (adjacentCount != 1) {
                    return false; // Ensure the new cube has exactly one adjacent cube when placed
                }

                // Ensure the structure does not exceed the maximum straight line length
                if (exceedsMaxLineLength(tempStructure)) return false;

                // Check that all other cubes in the structure have at most two adjancent cubes (i.e. there are
                // no loops and branches
                return tempStructure.getCubes().parallelStream()
                        .allMatch(existingCube -> {
                            long count = Direction.dirStream()
                                    .map(dir -> tempStructure.getAdjacent(existingCube, dir))
                                    .filter(Objects::nonNull)
                                    .count();
                            return count <= 2; // At most 2 adjacent cubes for each cube
                        });
            }

            /**
             * Checks if the structure exceeds the maximum allowable straight line length.
             *
             * @param structure the structure to validate
             * @return true if the maximum straight line length is exceeded, false otherwise
             */
            private boolean exceedsMaxLineLength(Structure structure) {
                Map<Integer, Set<Integer>> xToYs = new HashMap<>();
                Map<Integer, Set<Integer>> yToXs = new HashMap<>();

                // Group cubes by x and y coordinates
                for (Cube c : structure.getCubes()) {
                    xToYs.computeIfAbsent(c.x(), k -> new HashSet<>()).add(c.y());
                    yToXs.computeIfAbsent(c.y(), k -> new HashSet<>()).add(c.x());
                }

                // Check consecutive counts for rows and columns
                return xToYs.values().stream().anyMatch(this::hasConsecutiveValues) ||
                        yToXs.values().stream().anyMatch(this::hasConsecutiveValues);
            }

            /**
             * Helper function for checking maximum straight line length based on coordinates.
             *
             * @param coordinates the set of coordinates to validate
             * @return true if the consecutive value limit is exceeded, false otherwise
             */
            private boolean hasConsecutiveValues(Set<Integer> coordinates) {
                List<Integer> sorted = coordinates.stream().sorted().toList();
                int consecutiveCount = 1;

                for (int i = 1; i < sorted.size(); i++) {
                    if (sorted.get(i) == sorted.get(i - 1) + 1) {
                        consecutiveCount++;
                        if (consecutiveCount > maxStraightLineLength) {
                            return true;
                        }
                    } else {
                        consecutiveCount = 1;
                    }
                }
                return false;
            }
        };
    }
}

