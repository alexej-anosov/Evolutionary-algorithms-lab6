package lab6;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.List;
import java.util.Random;

public class MyMutation implements EvolutionaryOperator<double[]> {

    double mutationRate = 0.01;

    @Override
    public List<double[]> apply(List<double[]> population, Random random) {
        for (double[] individual : population) {
            for (int i = 0; i < individual.length; i++) {
                if (random.nextDouble() < mutationRate) {

                    individual[i] += random.nextGaussian();

                    individual[i] = clip(individual[i]);
                }
            }

        }
        return population;
    }

    private double clip(double geneValue) {
        return Math.max(-5.0, Math.min(5.0, geneValue));
    }
}