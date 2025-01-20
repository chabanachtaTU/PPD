package src;

import src.data.*;
import src.utils.Direction;

/**
 * Provides a collection of evaluation functions for scoring structures.
 * <p>
 * Each evaluation function calculates a score for a given structure based on specific criteria,
 * such as thermal surface quality, view quality, maximum height, or width. These scores are used
 * to optimize cube placements in a structure.
 * </p>
 * @author Nikolai Asche
 */
public class EvaluationFunctions {
    /**
     * Default evaluation function that calculates a combined score for thermal surface quality
     * and view quality of a structure.
     *
     * @return a SolutionEvaluator that computes the default score
     */
    public static SolutionEvaluator defaultEvaluation() {
        return new SolutionEvaluator() {
            @Override
            public double evaluate(Structure structure) {
                return structure.stream().mapToDouble(cube ->
                        calculateThermalSurfaceQuality(structure, cube) +
                        calculateViewQuality(structure, cube))
                        .sum();
            }

            @Override
            public String toString() {
                return "Default";
            }
        };
    }

    /**
     * Evaluation function that rewards structures with a layout in a meandering river-like pattern.
     * The score is calculated as the product of the unused x and y dimensions of the structure, therefore
     * rewarding usage of given space, limited by the map width and height provided by the m parameter
     *
     * @param m the maximum allowable offset for cube placement
     * @return a SolutionEvaluator that computes the score based on the river pattern
     */
    public static SolutionEvaluator riverEvaluation(int m) {
        return new SolutionEvaluator() {
            @Override
            public double evaluate(Structure structure) {
                int maxX = structure.stream()
                        .mapToInt(cube -> m/2 - Math.abs(cube.x()))
                        .max()
                        .orElse(0);
                int maxY = structure.stream()
                        .mapToInt(cube -> m/2 - Math.abs(cube.y()))
                        .max()
                        .orElse(0);

                return maxX * maxY;
            }

            @Override
            public String toString() {
                return "River";
            }
        };
    }

    /**
     * Calculates the thermal surface quality of a cube within a structure.
     * Thermal surface quality depends on adjacency and exposure to sunlight
     * based on specific rules for different faces.
     *
     * @param structure the structure containing the cube
     * @param cube      the cube for which to calculate thermal surface quality
     * @return the thermal surface quality score for the cube
     */
    private static double calculateThermalSurfaceQuality(Structure structure, Cube cube) {
        //System.out.println("\nChecking TSF for structure " + structure + " of cube: " + cube);
        double res = Direction.dirStream()
                .mapToDouble(dir -> {
                    //System.out.print("Checking dir " + dir + ": ");
                    if (structure.getAdjacent(cube, dir) != null) {
                        //System.out.println("adjacent cube");
                        return 1;
                    } else if (dir == Direction.EAST) {
                        return isSunnyEast(structure, cube) ? 0.2 : 0.0;
                    } else if (dir == Direction.WEST) {
                        return isSunnyWest(structure, cube) ? 0.1 : 0.0;
                    } else if (dir == Direction.SOUTH) {
                        return isSunnySouth(structure, cube) ? 0.5 : 0.0;
                    } else {
                        //System.out.println("shady");
                        return 0.0;
                    }
                })
                .sum(); // profiling shows that the sum() reduction is quite expensive
        //System.out.println("TSF for Cube: " + cube + ": " + res);
        return Math.round(res * 100.0) / 100.0;
    }

    /**
     * Determines if the east face of a cube is exposed to sunlight.
     *
     * @param structure the structure containing the cube
     * @param cube      the cube to check
     * @return true if the east face is sunny, false otherwise
     */
    private static boolean isSunnyEast(Structure structure, Cube cube) {
        // Given that there is no limit to the distance which causes a Cube to be shaded, the list of candidates is
        // potentially unlimited. Therefore, it seems to be better to filter the structure's cubes.
        boolean res = structure.stream().noneMatch(c -> // complex condition faster than multiple filters
                c.isEastOf(cube) &&
                (c.isSouthOf(cube) || c.isSameY(cube)) &&
                c.isSameZ(cube));
        //System.out.println(res? "sunny" : "shady");
        return res;
    } // There is no cube in the structure with y <= cube.y (südlich) and x > cube.x (östlich) and z == cube.z (gleiche Höhe)


    /**
     * Determines if the west face of a cube is exposed to sunlight.
     *
     * @param structure the structure containing the cube
     * @param cube      the cube to check
     * @return true if the west face is sunny, false otherwise
     */
    private static boolean isSunnyWest(Structure structure, Cube cube) {
        boolean res =  structure.stream().noneMatch(c ->
                c.isWestOf(cube) &&
                (c.isSouthOf(cube) || c.isSameY(cube)) &&
                c.isSameZ(cube));
        //System.out.println(res? "sunny" : "shady");
        return res;
    } // There is no cube in the structure with y <= cube.y (südlich) and x < cube.x (westlich) and z == cube.z (gleiche Höhe)


    /**
     * Determines if the south face of a cube is exposed to sunlight.
     *
     * @param structure the structure containing the cube
     * @param cube      the cube to check
     * @return true if the south face is sunny, false otherwise
     */
    private static boolean isSunnySouth(Structure structure, Cube cube) {
        boolean res = structure.stream()
            .noneMatch(c ->
                c.isSouthOf(cube) &&
                (c.isSameZ(cube) || c.isAbove(cube)) &&  // Cube is at the same or higher level
                Math.abs(c.y() - cube.y()) < 5 * (c.z() - cube.z() + 1) // within the n-s range determined by the height difference
        );
        //System.out.println(res? "sunny" : "shady");
        return res;
    }

    /**
     * Calculates the view quality of a cube within a structure.
     * View quality depends on directional and edge factors for each face of the cube.
     *
     * @param structure the structure containing the cube
     * @param cube      the cube for which to calculate view quality
     * @return the view quality score for the cube
     */
    private static double calculateViewQuality(Structure structure, Cube cube) {
        double res = Direction.dirStream()
                .filter(dir -> dir != Direction.UP && dir != Direction.DOWN) // We only check side faces for view quality
                .mapToDouble(dir ->
                        structure.getAdjacent(cube, dir) != null ?
                            0.0 :  // zero if adjacent
                            calculateDirectionalFactor(structure, cube, dir) * // calculate if not adjacent
                            calculateEdgeFactor(structure, cube, dir)
                        )
                .sum(); // seems to be the most expensive call here.
        //System.out.println("VQ of Cube "+ cube + " in "+ structure + ": " + res + "\n");
        return Math.round(res * 100.0) / 100.0;
    }

    /**
     * Calculates the directional view quality factor for a cube in a given direction.
     * Directional factor depends on distance to the nearest obstruction in the given direction.
     *
     * @param structure the structure containing the cube
     * @param cube      the cube to evaluate
     * @param dir       the direction to consider
     * @return the normalized directional factor for the cube
     */
    private static double calculateDirectionalFactor(Structure structure, Cube cube, Direction dir) {
        //System.out.print("DirFactor for "+ structure + ", Cube "+ cube +", " + dir + ": ");
        int step = 1;
        int maxDistance = 25;
        int distance = 0;

        while (step <= maxDistance) {
            Cube candidate = new Cube(
                    cube.x() + dir.dx * step,
                    cube.y() + dir.dy * step,
                    cube.z()
            );
            if (structure.contains(candidate)) { //O(1) loopup in HashSet
                distance = step;
                break;
            }
            step++;
        }

        double result = Math.round((distance == 0 ?
                1.0 :
                Math.min(1, (double) distance / maxDistance)) * 100.0
        ) / 100.0;
        //System.out.println("DirFactor for "+ structure + ", Cube "+ cube +", " + dir + ": " + result);
        return result;
//Using a funcional approach by filtering the cubes here is much slower and gets progressively  slower. Therefore, we
// sticked to a procedural loop.
/*        double res = structure.getCubes().stream()
                .filter(c -> // complex condition in filter faster than multiple filters
                        !c.equals(cube) &&
                        c.z() == cube.z() &&
                        (dir.dx != 0 && c.isSameY(cube) && (c.x() - cube.x()) * dir.dx > 0) ||
                        (dir.dy != 0 && c.isSameX(cube) && (c.y() - cube.y()) * dir.dy > 0))
                .mapToInt(c -> Math.abs(dir.dx != 0 ? c.x() - cube.x() : c.y() - cube.y()))
                .min()
                .orElse(25) / 25.0;
        //System.out.println(res);
        return Math.round(res * 100.0) / 100.0;*/
    }

    /**
     * Calculates the edge view quality factor for a cube's face in a given direction.
     * Edge factor depends on the number of restricted edges based on cubes adjacent to side faces or ground level.
     *
     * @param structure      the structure containing the cube
     * @param cube           the cube to evaluate
     * @param faceDirection  the direction of the cube's face
     * @return the edge factor for the face
     */
    private static double calculateEdgeFactor(Structure structure, Cube cube, Direction faceDirection) {
        //System.out.print("EdgeFactor for "+ structure + ", Cube "+ cube +", " + faceDirection + ": ");
        int restrictedEdges = 0;

        // Check bottom edge (ground level)
        if (cube.z() == 0 || structure
                .contains(
                new Cube(cube.x() + faceDirection.dx,
                        cube.y() + faceDirection.dy,
                        cube.z() - 1))

/*              .stream().anyMatch(c -> // This is very inefficient, better query HashMap directly
                c.x() == cube.x() + faceDirection.dx &&
                c.y() == cube.y() + faceDirection.dy &&
                c.z() == cube.z() - 1)*/
        ){
            restrictedEdges++;
        }

        // Determine left and right edges dynamically based on face direction
        int[][] offsets = switch (faceDirection) {
            case NORTH -> new int[][]{{-1, 1}, {1, 1}};  // Left = NW, Right = NE
            case SOUTH -> new int[][]{{1, -1}, {-1, -1}};  // Left = SE, Right = SW
            case EAST -> new int[][]{{1, 1}, {1, -1}};   // Left = NE, Right = SE
            case WEST -> new int[][]{{-1, -1}, {-1, 1}};   // Left = SW, Right = NW
            default -> throw new IllegalArgumentException("Invalid face direction");
        };

        // Check left edge
        if (structure.contains(new Cube(
                        cube.x() + offsets[0][0],
                        cube.y() + offsets[0][1],
                        cube.z()
                ))
/*                .stream().anyMatch(c -> // This is very inefficient, better query HashMap directly
                    c.x() == cube.x() + offsets[0][0] &&
                    c.y() == cube.y() + offsets[0][1] &&
                   c.isSameZ(cube))*/

        ) {
            restrictedEdges++;
        }

        // Check right edge
        if (structure.contains(new Cube(
                        cube.x() + offsets[1][0],
                        cube.y() + offsets[1][1],
                        cube.z()
                ))
/*                .stream().anyMatch(c -> // This is very inefficient, better query HashMap directly
                    c.x() == cube.x() + offsets[1][0] &&
                    c.y() == cube.y() + offsets[1][1] &&
                    c.isSameZ(cube))) {*/
        ) {
            restrictedEdges++;
    }

        // Return the factor based on restricted edges
        double res = switch (restrictedEdges) {
            case 1 -> 0.5;  // 1 restricted edge
            case 2 -> 0.25; // 2 restricted edges
            case 3 -> 0.125; // 3 restricted edges
            default -> 1.0; // No restricted edges
        };
        //System.out.println(res);
        return res;
    }
}
