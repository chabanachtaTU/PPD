package src;

import src.data.Structure;
import src.utils.ExecutionTracker;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class generates optimal cube structure solutions. It is used as an entry point to separate
 * the Test class from the application.
 * <p>
 * The SolutionGenerator class serves as an entry point to generate cube structures
 * by utilizing the RecursiveSolver. It allows the user to define the number of cubes
 * to place, the number of solutions to retain, and the specific evaluation and
 * validation functions for scoring and constraints.
 * </p>
 */
public class SolutionGenerator {
    private final int n; // Number of cubes to place
    private final int k; // Maximum number of solutions to retain

    /**
     * Constructs a SolutionGenerator with the specified parameters.
     *
     * @param n the number of cubes to place
     * @param k the maximum number of solutions to retain
     */
    public SolutionGenerator(int n, int k) {
        this.n = n;
        this.k = k;
    }

    /**
     * Processes and visualizes the solutions generated for the given evaluator and validator.
     *
     * @param m          An integer parameter to be passed to the evaluator/validator.
     * @param evaluator  The evaluation function for scoring structures.
     * @param validator  The validation function to enforce placement rules.
     */
    public void processSolutions(int m, SolutionEvaluator evaluator, PlacementValidator validator) {
        System.out.print(SolutionVisualizer.formatHeader(evaluator + " Solutions", n, m, k));

        AtomicInteger structureCounter = new AtomicInteger(0); // Counter for evaluated structures

        try {
            ExecutionTracker.ExecutionResult<List<Structure>> result =
                    ExecutionTracker.executeWithTracking(() ->
                            generateSolutions(evaluator, validator, structureCounter),
                            structureCounter,
                            "Generating and evaluating structures:");

            List<Structure> solutions = result.getResult();
            if (solutions != null && !solutions.isEmpty()) {
                Structure bestSolution = solutions.stream().max(Comparator.comparingDouble(Structure::getScore)).orElse(null);
                Structure worstSolution = solutions.stream().min(Comparator.comparingDouble(Structure::getScore)).orElse(null);

                System.out.println("\nBest Solution (Score: " + bestSolution.getScore() + "):");
                System.out.println(SolutionVisualizer.visualize(bestSolution));

                if (!worstSolution.equals(bestSolution)) {
                    System.out.println("\nWorst Solution (Score: " + worstSolution.getScore() + "):");
                    System.out.println(SolutionVisualizer.visualize(worstSolution));
                }

                System.out.printf("%n%d Structures evaluated in %.3f seconds.%n", structureCounter.intValue(), ((float) (result.getExecutionTime())) / 1000);
            } else {
                System.out.println("\nNo solutions for the given parameters found. \nTry again (solver tries to add cubes in random directions)!");
            }
        } catch (Exception e) {
            System.out.println("\nAn error occurred during solution generation: " + e.getMessage());
        }
        System.out.println(SolutionVisualizer.formatFooter(evaluator + " Solutions"));
    }
    /**
     * Generates a list of optimal solutions for cube placements.
     * <p>
     * This method uses RecursiveSolver to generate solutions based on the provided evaluation and validation
     * functions. A progress indicator is displayed while solutions are being generated.
     * </p>
     *
     * @param evaluator the evaluation function to score the solutions
     * @param validator the validation function to enforce placement constraints
     * @return a list of generated solutions
     */
    public List<Structure> generateSolutions(SolutionEvaluator evaluator, PlacementValidator validator, AtomicInteger structureCounter) {
        RecursiveSolver solver = new RecursiveSolver(n, k, evaluator, validator, structureCounter);
        return solver.solve();
    }
}
