package src.data;

import src.utils.Direction;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a 3D structure composed of multiple Cubes.
 * <p>
 * The Structure class provides methods to manage and manipulate cubes within the structure,
 * ensuring validity of placements and maintaining the overall structure state.
 * It supports operations such as adding cubes, checking adjacency, and calculating scores
 * based on evaluation functions.
 * </p>
 * @author JBuchinger
 */
public class Structure {
    /**
     * A map to store cubes in the structure
     */
    private final Set<Cube> cubes; // Stores all cubes in the structure

    /**
     * The current score of the structure, calculated using an evaluation function.
     */
    private final double score;

    /**
     * Constructs a new Structure with the given collection of cubes and an initial score.
     *
     * @param cubes the collection of cubes to initialize the structure
     * @param score the initial score of the structure
     */
    public Structure(Set<Cube> cubes, double score) {
        this.cubes = new HashSet<>(cubes); // Ensure immutability
        this.score = score;
    }

    /**
     * Retrieves all cubes in the structure as a collection.
     *
     * @return a collection of all cubes in the structure
     */
    public Set<Cube> getCubes() {
        return cubes;
    }

    /**
     * Returns all cubes in the Structure as a Stream
     *
     * @return a Stream of cubes
     */
    public Stream<Cube> stream() {
        return cubes.stream();
    }

    /**
     * checks if the given cube is contained in this structure
     *
     * @return true if contained, false if not
     */
    public boolean contains(Cube cube) {
        return cubes.contains(cube);
    }

    /**
     * Checks if the Structure is empty, i.e. does not contain cubes
     *
     * @return true if the Structure is empty, false if not
     */
    public boolean isEmpty() {
        return cubes.isEmpty();
    }

    /**
     * Retrieves the current score of the structure. The score is rounded to 2 decimal places to avoid floating point
     * errors, which have significant adverse consequences due to the score being used in equality checks.
     *
     * @return the rounded score of the structure
     */
    public double getScore() {
        return Math.round(score * 100.0) / 100.0;
    }

    /**
     * Generates and returns a new Structure by adding a new cube to the structure.
     *
     * @param cube the cube to add to the structure
     * @return a new Structure with the added cube
     */
    public Structure addCube(Cube cube) {
        Set<Cube> newCubes = new HashSet<>(cubes);
        newCubes.add(cube); // Add the new cube
        return new Structure(newCubes, score); // Return a new structure
    }

    /**
     * Checks if the given cube has an adjacent cube in the specified direction.
     *
     * @param cube      the cube to check adjacency for
     * @param direction the direction to check for adjacency
     * @return the adjacent cube if present, or null if none exists
     */
    public Cube getAdjacent(Cube cube, Direction direction) {
        Cube adjacent = new Cube(
                cube.x() + direction.dx,
                cube.y() + direction.dy,
                cube.z() + direction.dz
        );
        return cubes.contains(adjacent) ? adjacent : null; // Check for adjacency
    }

    /**
     * Checks if the given cube is supported (by the ground or a cube below it).
     *
     * @param cube the cube to check
     * @return true if the cube is supported, false otherwise
     */
    public boolean cubeIsSupported(Cube cube) {
        return cube.z() == 0 || getAdjacent(cube, Direction.DOWN) != null;
    }

    /**
     * Checks if the given cube is connected to another cube, i.e. has at least one adjacent face.
     *
     * @param cube the cube to check
     * @return true if the cube is connected, false otherwise
     */
    public boolean cubeIsConnected(Cube cube) {
        return Direction.dirStream().anyMatch(direction -> getAdjacent(cube, direction) != null);
    }

    /**
     * Checks if the given cube has at least one side (N/E/W/S) face that is non-adjacent.
     *
     * @param cube the cube to check
     * @return true if the cube has a free side face, false otherwise
     */
    public boolean cubeHasFreeSideFace(Cube cube) {
        return Direction.dirStream()
                .filter(dir -> dir != Direction.UP && dir != Direction.DOWN)
                .anyMatch(dir -> getAdjacent(cube, dir) == null);
    }

    /**
     * Equals compares Structures based on the cubeMap and its score.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Structure that = (Structure) o;
        return (cubes.equals(that.cubes) && score == that.score);
    }

    @Override
    public int hashCode() {
        return (int) score;
    }

    @Override
    public String toString() {
        return String.format("Structure(cubes=%s, score=%f)", cubes, score);
    }
}


