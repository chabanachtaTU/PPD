package src;

import src.data.Structure;

/**
 * Functional interface for evaluating the quality of a structure.
 * <p>
 * The `SolutionEvaluator` assigns a score to a given structure based on specific criteria.
 * Higher scores indicate better structures. Evaluation functions can be customized
 * to prioritize different objectives, such as maximizing height, minimizing surface area,
 * or optimizing thermal and view qualities.
 * </p>
 */
@FunctionalInterface
public interface SolutionEvaluator {
    /**
     * Evaluates the quality of a structure and assigns it a score.
     *
     * @param structure the structure to evaluate
     * @return the score assigned to the structure
     */
    double evaluate(Structure structure);
}

