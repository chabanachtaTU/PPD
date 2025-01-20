package src.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents directions for cube adjacency checks.
 * Each direction is defined by a change in x, y, and z coordinates.
 * @author JBuchinger
 */
public enum Direction {
    NORTH(0, 1, 0),
    SOUTH(0, -1, 0),
    EAST(1, 0, 0),
    WEST(-1, 0, 0),
    UP(0, 0, 1),
    DOWN(0, 0, -1);

    public final int dx, dy, dz;

    Direction(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public static Stream<Direction> dirStream() {
        return Arrays.stream(Direction.values());
    }

    public static Stream<Direction> randomDirStream() {
        List<Direction> result = Arrays.asList(Direction.values());
        Collections.shuffle(result);
        return result.stream();
    }
}
