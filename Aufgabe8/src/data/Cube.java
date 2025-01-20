package src.data;

import java.util.Objects;

/**
 * Represents a single Cube, a unit of the Structure in 3D space.
 * <p>
 * Each Cube is defined by its unique coordinates (x, y, z) in the structure.
 * This record provides utility methods for determining spatial relationships between cubes,
 * such as adjacency or positional checks in specific directions.
 * </p>
 * @author JBuchinger
 */

public class Cube {
    private final int x, y, z;
    private final int hashCode; // Precomputed final hash code based on final coords x,y,z

    /**
     * Constructs a Cube with the given coordinates.
     *
     * @param x the x-coordinate of the cube
     * @param y the y-coordinate of the cube
     * @param z the z-coordinate of the cube
     */
    public Cube(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hashCode = computeHashCode();
    }

    /**
     * Gets the x-coordinate of the cube.
     *
     * @return the x-coordinate
     */
    public int x() {
        return x;
    }

    /**
     * Gets the y-coordinate of the cube.
     *
     * @return the y-coordinate
     */
    public int y() {
        return y;
    }

    /**
     * Gets the z-coordinate of the cube.
     *
     * @return the z-coordinate
     */
    public int z() {
        return z;
    }

    /**
     * Checks if this cube is north of the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube is north of the other cube, false otherwise
     */
    public boolean isNorthOf(Cube other) {
        return y > other.y;
    }

    /**
     * Checks if this cube is south of the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube is south of the other cube, false otherwise
     */
    public boolean isSouthOf(Cube other) {
        return y < other.y;
    }

    /**
     * Checks if this cube is east of the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube is east of the other cube, false otherwise
     */
    public boolean isEastOf(Cube other) {
        return x > other.x;
    }

    /**
     * Checks if this cube is west of the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube is west of the other cube, false otherwise
     */
    public boolean isWestOf(Cube other) {
        return x < other.x;
    }

    /**
     * Checks if this cube is below the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube is below the other cube, false otherwise
     */
    public boolean isBelow(Cube other) {
        return z < other.z;
    }

    /**
     * Checks if this cube is above the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube is above the other cube, false otherwise
     */
    public boolean isAbove(Cube other) {
        return z > other.z;
    }

    /**
     * Checks if this cube has the same x-coordinate as the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube has the same x-coordinate as the other cube, false otherwise
     */
    public boolean isSameX(Cube other) {
        return x == other.x;
    }

    /**
     * Checks if this cube has the same y-coordinate as the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube has the same y-coordinate as the other cube, false otherwise
     */
    public boolean isSameY(Cube other) {
        return y == other.y;
    }

    /**
     * Checks if this cube has the same z-coordinate as the specified other cube.
     *
     * @param other the cube to compare against
     * @return true if this cube has the same z-coordinate as the other cube, false otherwise
     */
    public boolean isSameZ(Cube other) {
        return z == other.z;
    }

    /**
     * Computes the hash code for this cube based on its coordinates.
     *
     * @return the computed hash code
     */
    private int computeHashCode() {
        return Objects.hash(x, y, z);
    }

    /**
     * Returns the precomputed hash code for this cube.
     *
     * @return the precomputed hash code
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Determines if this cube is equal to another object.
     *
     * @param o the object to compare
     * @return true if the other object is a cube with the same coordinates, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cube that = (Cube) o;
        return x == that.x && y == that.y && z == that.z;
    }

    /**
     * Provides a string representation of this cube.
     *
     * @return a string in the format "[x, y, z]"
     */
    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}

