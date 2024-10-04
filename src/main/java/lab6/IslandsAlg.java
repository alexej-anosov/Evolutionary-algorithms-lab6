package lab6;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.Migration;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

public class IslandsAlg {

    private double globalBestFitness;
    private long runTime;

    public static void main(String[] args) {
        IslandsAlg algorithm = new IslandsAlg();
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

    private void runEvolution(int complexity) {
        final int dimension = 50;
        final int numberOfIslands = 5;
        final int islandPopulationSize = 20;
        final int epochLength = 50;
        final int maxGenerations = 1000 / epochLength;

        Random random = new Random();
        CandidateFactory<double[]> candidateFactory = new MyFactory(dimension);

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover());
        operators.add(new MyMutation());
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection();
        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity);

        Migration migration = new RingMigration();
        IslandEvolution<double[]> islandEvolution = new IslandEvolution<>(numberOfIslands, migration, candidateFactory, pipeline, evaluator, selection, random);

        islandEvolution.addEvolutionObserver(new IslandEvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
//                System.out.println("Epoch " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tEpoch best solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }

            public void islandPopulationUpdate(int i, PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                if (bestFit > globalBestFitness)
                    globalBestFitness = bestFit;
//                System.out.println("Island " + i);
//                System.out.println("\tGeneration " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tBest solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }
        });


        TerminationCondition terminationCondition = new GenerationCount(maxGenerations);
        long startTime = System.currentTimeMillis();
        islandEvolution.evolve(islandPopulationSize, 1, epochLength, 2, terminationCondition);
        runTime = System.currentTimeMillis() - startTime;
    }
}