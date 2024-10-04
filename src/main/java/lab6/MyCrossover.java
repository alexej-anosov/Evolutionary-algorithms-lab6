package lab6;

import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyCrossover extends AbstractCrossover<double[]> {
    protected MyCrossover() {
        super(1);
    }

    @Override
    protected List<double[]> mate(double[] p1, double[] p2, int i, Random random) {
        ArrayList<double[]> children = new ArrayList<>();

        double[] child1 = p1.clone();
        double[] child2 = p2.clone();
        double alpha = 0.2;

        double minGene  = Math.min(p1[i], p2[i]);
        double maxGene  = Math.max(p1[i], p2[i]);
        double range = maxGene  - minGene;

        double lowerBound = minGene - range * alpha;
        double upperBound = maxGene + range * alpha;

        child1[i] = lowerBound + random.nextDouble() * (upperBound - lowerBound);
        child2[i] = lowerBound + random.nextDouble() * (upperBound - lowerBound);

        children.add(child1);
        children.add(child2);

        return children;
    }
}