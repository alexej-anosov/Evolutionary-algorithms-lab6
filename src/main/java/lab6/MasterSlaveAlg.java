package lab6;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

public class MasterSlaveAlg {

    private double globalBestFitness;
    private long runTime;

    public static void main(String[] args) {
        MasterSlaveAlg algorithm = new MasterSlaveAlg();
        algorithm.execute();
    }

    public void execute() {
        for (int complexity = 1; complexity <= 5; complexity++) {
            int iterations = 101;
            double[] fitnessResults = new double[iterations];
            long[] executionTimes = new long[iterations];

            for (int i = 0; i < iterations; i++) {
                resetMetrics();
                runEvolution(complexity);
                fitnessResults[i] = globalBestFitness;
                executionTimes[i] = runTime;
            }

            double avgTimeSeconds = LongStream.of(executionTimes).average().orElse(0) / 1000.0;
            double avgFitness = DoubleStream.of(fitnessResults).average().orElse(0);

            System.out.printf("Complexity: %d%n", complexity);
            System.out.printf("Average execution time (s): %f, Average fitness: %f%n", avgTimeSeconds, avgFitness);
            System.out.println("fitnessResults: " + Arrays.toString(fitnessResults));
            System.out.println("executionTimes: " + Arrays.toString(executionTimes) + "\n");
        }
    }

    private void resetMetrics() {
        globalBestFitness = 0;
        runTime = 0;
    }

    public void runEvolution(int complexity) {
        int dimension = 50;
        int populationSize = 100;
        int generations = 1000;

        Random random = new Random();
        CandidateFactory<double[]> factory = new MyFactory(dimension);

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<>();
        operators.add(new MyCrossover());
        operators.add(new MyMutation());
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection();
        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity);

        AbstractEvolutionEngine<double[]> algorithm = new SteadyStateEvolutionEngine<>(
                factory, pipeline, evaluator, selection, populationSize, false, random);

        algorithm.setSingleThreaded(true);

        algorithm.addEvolutionObserver(new EvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                if (bestFit > globalBestFitness)
                    globalBestFitness = bestFit;
//                System.out.println("Epoch " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tEpoch best solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }

        });

        TerminationCondition terminate = new GenerationCount(generations);
        long startTime = System.currentTimeMillis();
        algorithm.evolve(populationSize, 1, terminate);
        long endTime = System.currentTimeMillis();
        runTime = endTime - startTime;
    }
}