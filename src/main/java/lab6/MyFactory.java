package lab6;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.Random;

public class MyFactory extends AbstractCandidateFactory<double[]> {

    private int dimension;

    public MyFactory(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public double[] generateRandomCandidate(Random random) {
        double[] solution = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            solution[i] = Math.max(-5.0, Math.min(-0.5 + 0.1*random.nextGaussian(), 5));
        }

        return solution;
    }
}

