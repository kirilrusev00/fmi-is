import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class KMeans {
    private static final int RANDOM_RESTART = 50;
    private static final boolean ASSIGN_CENTROIDS_RANDOMLY = true;
    private static final boolean ASSIGN_CENTROIDS_PLUS_PLUS = true;

    private List<Point> currentPoints;
    private Point[] currentCentroids;

    private List<Point> bestPoints;
    private Point[] bestCentroids;

    private Set<Integer> centroidPointsIndexes;
    private double[] pointDistances;

    public KMeans() {
        currentPoints = new ArrayList<>(PointsInput.points);
        currentCentroids = new Point[PointsInput.k];

        bestPoints = new ArrayList<>();
        bestCentroids = new Point[PointsInput.k];

        centroidPointsIndexes = new HashSet<>();
        pointDistances = new double[currentPoints.size()];
    }

    private void assignPoints() {
        Collections.shuffle(currentPoints);
        int clusterSize = currentPoints.size() / PointsInput.k;
        int clustersWithMorePoints = currentPoints.size() % PointsInput.k;

        int currentCluster = 0;
        int pointsInCluster = 0;
        double currentCentroidSumX = 0;
        double currentCentroidSumY = 0;

        for (Point point : currentPoints) {
            if ((pointsInCluster >= clusterSize && currentCluster >= clustersWithMorePoints) || pointsInCluster > clusterSize) {
                if (!ASSIGN_CENTROIDS_RANDOMLY) {
                    double currentCentroidX = currentCentroidSumX / pointsInCluster;
                    double currentCentroidY = currentCentroidSumY / pointsInCluster;
                    Point centroid = new Point(currentCentroidX, currentCentroidY, currentCluster);
                    currentCentroids[currentCluster] = centroid;
                }
                currentCluster++;
                pointsInCluster = 0;
                if (!ASSIGN_CENTROIDS_RANDOMLY) {
                    currentCentroidSumX = 0;
                    currentCentroidSumY = 0;
                }
            }

            point.setCluster(currentCluster);
            pointsInCluster++;
            if (!ASSIGN_CENTROIDS_RANDOMLY) {
                currentCentroidSumX += point.getX();
                currentCentroidSumY += point.getY();
            }
        }

        if (!ASSIGN_CENTROIDS_RANDOMLY) {
            double currentCentroidX = currentCentroidSumX / pointsInCluster;
            double currentCentroidY = currentCentroidSumY / pointsInCluster;
            Point centroid = new Point(currentCentroidX, currentCentroidY, currentCluster);
            currentCentroids[currentCluster] = centroid;
        }
    }

    private void assignCentroids() {
        Random random = new Random();
        for (int i = 0; i < PointsInput.k; i++) {
            double x = random.nextDouble() * (PointsInput.maxX - PointsInput.minX) + PointsInput.minX;
            double y = random.nextDouble() * (PointsInput.maxY - PointsInput.minY) + PointsInput.minY;

            Point centroid = new Point(x, y, i);
            currentCentroids[i] = centroid;
        }
    }

    private int findNearestCentroid(Point p) {
        double minDistance = Point.calculateDistance(p, currentCentroids[0]);
        int nearestCentroid = 0;

        for (int i = 1; i < PointsInput.k; i++) {
            double currentDistance = Point.calculateDistance(p, currentCentroids[i]);

            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                nearestCentroid = i;
            }
        }

        return nearestCentroid;
    }

    private boolean moveCentroid(int index) {
        double sumX = 0;
        double sumY = 0;
        int pointsCounter = 0;

        for (Point point : currentPoints) {
            if (point.getCluster() == index) {
                sumX += point.getX();
                sumY += point.getY();
                pointsCounter++;
            }
        }

        if (pointsCounter == 0) {
            return false;
        }

        Point newCentroid = new Point(sumX / pointsCounter, sumY / pointsCounter, index);
        Point oldCentroid = currentCentroids[index];

        if (oldCentroid.getX() == newCentroid.getX() && oldCentroid.getY() == newCentroid.getY()) {
            return false;
        }

        currentCentroids[index] = newCentroid;

        return true;
    }

    private double findMinimalDistanceFromPointToCentroid(int pointIndex) {
        double minDistance = Point.calculateDistance(currentPoints.get(pointIndex), currentPoints.get(0));
        double currentDistance;

        for (int centroidIndex : centroidPointsIndexes) {
            currentDistance = Point.calculateDistance(currentPoints.get(pointIndex), currentPoints.get(centroidIndex));

            if (currentDistance < minDistance) {
                minDistance = currentDistance;
            }
        }

        return minDistance;
    }

    private int choosePointForNewCentroid() {
        double randomValue = new Random().nextDouble();
        double currentSum = 0;
        double sumAllPointDistances = 0;
        for (double distance : pointDistances) {
            sumAllPointDistances += distance;
        }

        for (int i = 0; i < pointDistances.length; i++) {
            currentSum += pointDistances[i];
            if (randomValue < currentSum / sumAllPointDistances) {
                return i;
            }
        }

        return pointDistances.length - 1;
    }

    private void assignPointsPlusPlus() {
        Collections.shuffle(currentPoints);

        centroidPointsIndexes.clear();
        centroidPointsIndexes.add(0);

        int centroidsCount = 0;
        int newCentroidIndex;

        while (centroidsCount < PointsInput.k - 1) {
            for (int i = 0; i < currentPoints.size(); i++) {
                if (!centroidPointsIndexes.contains(i)) {
                    pointDistances[i] = findMinimalDistanceFromPointToCentroid(i);
                }
            }

            do {
                newCentroidIndex = choosePointForNewCentroid();
            } while (centroidPointsIndexes.contains(newCentroidIndex));

            centroidPointsIndexes.add(newCentroidIndex);
            centroidsCount++;
        }

        int currentCentroid = 0;
        for (int pointIndex : centroidPointsIndexes) {
            Point centroid = new Point(currentPoints.get(pointIndex).getX(), currentPoints.get(pointIndex).getY(), currentCentroid);
            currentCentroids[currentCentroid] = centroid;
            currentCentroid++;
        }
    }

    private void formClusters() {
        if (ASSIGN_CENTROIDS_PLUS_PLUS) {
            assignPointsPlusPlus();
        }
        else {
            assignPoints();
            if (ASSIGN_CENTROIDS_RANDOMLY) {
                assignCentroids();
            }
        }

        boolean shouldContinue = true;
        boolean areClustersChanged;

        while (shouldContinue) {
            shouldContinue = false;

            for (Point point : currentPoints) {
                point.setCluster(findNearestCentroid(point));
            }

            for (int i = 0; i < PointsInput.k; i++) {
                areClustersChanged = moveCentroid(i);
                shouldContinue = areClustersChanged || shouldContinue;
            }
        }
    }

    private double evaluateClusters() {
        double evaluation = 0;
        for (Point point : currentPoints) {
            evaluation += Point.calculateDistance(point, currentCentroids[point.getCluster()]);
        }
        return evaluation / currentPoints.size();
    }

    public void findBestClusters() {
        double bestEvaluation = Integer.MAX_VALUE;

        for (int i = 0; i < RANDOM_RESTART; i++) {
            formClusters();

            double evaluation = evaluateClusters();

            if (evaluation < bestEvaluation) {
                bestPoints.clear();
                for (Point point : currentPoints) {
                    bestPoints.add(new Point(point));
                }

                bestCentroids = Arrays.copyOf(currentCentroids, currentCentroids.length);
                bestEvaluation = evaluation;
            }
        }
    }

    List<Point> getBestPoints() {
        return bestPoints;
    }

    Point[] getBestCentroids() {
        return bestCentroids;
    }
}