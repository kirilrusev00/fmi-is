import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class Population {
    private static final int POPULATION_SIZE = 500;
    private static final int MIN_CITY_X = -50;
    private static final int MAX_CITY_X = 50;
    private static final int AGES_COUNT = 50000;
    private static final double MUTATION_RATE = 0.3;

    private double[] x;
    private double[] y;
    private int citiesCount;
    private PriorityQueue<State> states;
    private Random rand = new Random(1000);

    private class State implements Comparable<State> {
        private int[] path;
        double distance = 0;

        public State() {
            generateRandomPath();
            calculateDistance();
//            System.out.println(Arrays.toString(path));
//            System.out.println(distance);
        }

        private void generateRandomPath() {
            path = new int[citiesCount];
            for (int i = 0; i < citiesCount; i++) {
                path[i] = i;
            }
            for (int i = 0; i < citiesCount; i++) {
                int randomIndexToSwap = rand.nextInt(path.length);
                int temp = path[randomIndexToSwap];
                path[randomIndexToSwap] = path[i];
                path[i] = temp;
            }
        }

        private void calculateDistance() {
            for (int i = 0; i < path.length - 1; i++) {
                int a = path[i];
                int b = path[i + 1];
                distance += distance(x[a], y[a], x[b], y[b]);
            }
        }

        private double distance(double x1, double y1, double x2, double y2) {
            return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        }

        public int compareTo(State other) {
            return (int) (this.distance - other.distance);
        }

        public void mutate() {
            int randCity1 = rand.nextInt(citiesCount);
            int randCity2 = rand.nextInt(citiesCount);

            int temp = path[randCity1];
            path[randCity1] = path[randCity2];
            path[randCity2] = temp;

//            System.out.println(Arrays.toString(path));
//            System.out.println();
        }
    }

    public Population(int citiesCount) {
        this.citiesCount = citiesCount;
        x = new double[citiesCount];
        y = new double[citiesCount];
        for (int i = 0; i < citiesCount; i ++) {
            x[i] = getRandomValue();
            y[i] = getRandomValue();
        }

//        this.citiesCount = 12;
//
//        x = new double[]{1.90E-04, 383.458, -27.0206, 335.751, 69.4331, 168.521, 320.35, 179.933, 492.671, 112.198, 306.32, 217.343};
//        y = new double[]{-2.86E-04, -6.09E-04, -282.758, -269.577, -246.78, 31.4012, -160.9, -318.031, -131.563, -110.561, -108.09, -447.089};

        states = new PriorityQueue<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            State newState = new State();
            states.add(newState);
        }

        System.out.println(Arrays.toString(x));
        System.out.println(Arrays.toString(y));
    }

    private double getRandomValue() {
        return MIN_CITY_X + (MAX_CITY_X - MIN_CITY_X) * rand.nextDouble();
    }

    private State crossover(State parent1, State parent2) {
        State child = new State();

        int stopIndex = rand.nextInt(citiesCount);
        System.arraycopy(parent1.path, 0, child.path, 0, stopIndex);

        boolean alreadyIncluded;
        int indexParent2 = 0;
        int currentValueParent2;

        for (int i = stopIndex; i < citiesCount; i++) {
            alreadyIncluded = true;
            currentValueParent2 = parent2.path[indexParent2];
            while (alreadyIncluded) {
                currentValueParent2 = parent2.path[indexParent2];
                alreadyIncluded = false;
                for (int j = 0; j < i; j++) {
                    if (child.path[j] == currentValueParent2) {
                        alreadyIncluded = true;
                        indexParent2++;
                        break;
                    }
                }
            }
            child.path[i] = currentValueParent2;
        }

        if (rand.nextDouble() < MUTATION_RATE) {
            child.mutate();
        }

        return child;
    }

    public double getBest() {
        return states.peek().distance;
    }

    public void reproduce() {
        State state1, state2, child1, child2;

        PriorityQueue<State> newStates = new PriorityQueue<>();

        for (int i = 0; i < POPULATION_SIZE / 2; i++) {
            state1 = states.poll();
            state2 = states.poll();
            child1 = crossover(state1, state2);
            child2 = crossover(state2, state1);

            newStates.add(state1);
            newStates.add(state2);
            newStates.add(child1);
            newStates.add(child2);
        }

        for (int i = 0; i < POPULATION_SIZE; i++) {
            states.add(newStates.poll());
        }

//        states = newStates;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int citiesCount = scanner.nextInt();
        Population population = new Population(citiesCount);

        System.out.println(population.getBest());

        for (int age = 1; age <= AGES_COUNT; age++) {
            if (age == 1 || age == 100 || age == 1000 || age == 10000 || age == AGES_COUNT) {
                System.out.println(age + " : " + population.getBest());
            }

            population.reproduce();
        }
    }
}
