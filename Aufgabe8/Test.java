import src.*;

/**
 * NOTE: Work log
 * <p>
 * Our group ppB5 consists of
 * Nikolai Asche, e12318680@student.tuwien.ac.at
 * Johannes Buchinger, e09001734@student.tuwien.ac.at
 * Oliver Hagenauer, e12231467@student.tuwien.ac.at
 * <p>
 * Aufgabe 7: Division of tasks between Team members
 * <p>
 * Given the holiday season, the team did not meet in person but held several Discord meetings during which the
 * task and implementation was discussed and carried out. Given that Johannes' workload was somewhat lower in the last
 * task, this time, he took the role of maintaining the code base for this task and pushing the input provided by the
 * other team members to the repository.
 * <p>
 * Code structure and type hierarchy was suggested by Oliver, who also took responsibility for the recursive solver and the
 * presentation logic. Nikolai concentrated on the functional interfaces for validation and evaluation. Johannes worked
 * on the cube and structure calculations within the 3D grid.
 * <p>
 * In terms of test cases, Nikolai and Oliver focused on the default implementation and its evaluator/validator, while Johannes took care of
 * the custom river implementation and its evaluator/validator.
 */
public class Test {
    public static void main(String[] args) {
        int n = 30;// number of cubes
        int m = 5; // evaluation / validation parameter. In the default solution, it is maxHeight, in the custom solution,
                   // it is the map height/width
        int k = 20; // number of solutions to be kept during each recursion

        generateDefaultSolutions(n, m, k);
        generateDefaultSolutions(250, 10, 10);
        generateSolutions(200, 100, 10, EvaluationFunctions.riverEvaluation(100), ValidationFunctions.riverValidation(100));
    }

    // shortcut to generateSolutions for the default solution
    static void generateDefaultSolutions(int n, int m, int k) {
        generateSolutions(n, m, k, EvaluationFunctions.defaultEvaluation(), ValidationFunctions.defaultValidation(m));
    }

    /**
     * Invokes the `generateSolutions` method in `SolutionGenerator`.
     *
     * @param n          Number of cubes to be used.
     * @param m          Integer parameter passed to the evaluator/validator.
     * @param k          Number of solutions to be kept during each recursion.
     * @param evaluator  The evaluation function for scoring structures.
     * @param validator  The validation function to enforce placement rules.
     */
    static void generateSolutions(int n, int m, int k, SolutionEvaluator evaluator, PlacementValidator validator) {
        SolutionGenerator generator = new SolutionGenerator(n, k);
        generator.processSolutions(m, evaluator, validator);
    }
}
