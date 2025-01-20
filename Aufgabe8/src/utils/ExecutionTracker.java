package src.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionTracker {

    /**
     * Executes a task while showing a spinner and measuring the execution time.
     *
     * @param task      the task to execute (as a Callable returning a result)
     * @param message   the message to display with the spinner
     * @param <T>       the return type of the task
     * @return the result of the task
     * @throws Exception if the task throws an exception
     */
    public static <T> ExecutionResult<T>  executeWithSpinner(Callable<T> task, String message) throws Exception {
        // Spinner logic in a separate thread
        Thread spinnerThread = new Thread(() -> {
            String[] spinner = {"|", "/", "-", "\\"};
            int index = 0;
            while (!Thread.currentThread().isInterrupted()) {
                System.out.print("\r" + message + " " + spinner[index++ % spinner.length]);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        long startTime = System.currentTimeMillis();
        T result;

        try {
            spinnerThread.start(); // Start the spinner
            result = task.call(); // Execute the task
        } finally {
            spinnerThread.interrupt(); // Stop the spinner
            spinnerThread.join(); // Ensure the thread terminates cleanly
            System.out.print("\r" + " ".repeat(50) + "\r"); // Clear the spinner line
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        return new ExecutionResult<>(result, executionTime);
    }
    /**
     * Encapsulates the result of a task and its execution time.
     */
    public static class ExecutionResult<T> {
        private final T result;
        private final long executionTime;

        public ExecutionResult(T result, long executionTime) {
            this.result = result;
            this.executionTime = executionTime;
        }

        public T getResult() {
            return result;
        }

        public long getExecutionTime() {
            return executionTime;
        }
    }

    /**
     * Tracks the progress of a task by displaying the number of evaluated structures dynamically.
     *
     * @param task             The task to execute.
     * @param structureCounter A thread-safe counter for the evaluated structures.
     * @param message          The message to display while tracking progress.
     * @param <T>              The type of the result produced by the task.
     * @return The result of the task along with its execution time.
     * @throws Exception If the task throws an exception.
     */
    public static <T> ExecutionResult<T> executeWithTracking(Callable<T> task, AtomicInteger structureCounter, String message) throws Exception {
        Thread progressThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.print("\r" + message + " " + structureCounter.get());
                try {
                    Thread.sleep(250); // Update every 250ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        long startTime = System.currentTimeMillis();
        T result;

        try {
            progressThread.start(); // Start the progress tracker
            result = task.call(); // Execute the task
        } finally {
            progressThread.interrupt(); // Stop the progress tracker
            progressThread.join(); // Ensure the thread terminates cleanly
            System.out.print("\r" + " ".repeat(50) + "\r"); // Clear the progress line
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        return new ExecutionResult<>(result, executionTime);
    }
}
