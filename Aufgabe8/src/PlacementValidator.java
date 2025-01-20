package src;

import src.data.Cube;
import src.data.Structure;

/**
 * Functional interface for validating cube placements in a structure.
 * <p>
 * The `PlacementValidator` ensures that a cube can be placed at a specific position
 * within a structure while adhering to freely predefined constraints, such as adjacency,
 * structural integrity, and maximum height restrictions.
 * </p>
 */
@FunctionalInterface
public interface PlacementValidator {
    /**
     * Checks if a given cube can be placed in a specific structure.
     *
     * @param structure the current structure to validate against
     * @param cube      the cube to validate for placement
     * @return true if the placement is valid, false otherwise
     */
    boolean isValid(Structure structure, Cube cube);
}
