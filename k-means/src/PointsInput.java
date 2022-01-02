import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class PointsInput {
    static List<Point> points;
    static int k;
    static double minX, maxX, minY, maxY;

    static void readInput() {
        readClusters();
        readFromFile();

        calculateMinAndMaxCoordinates();
    }

    private static void readClusters() {
        System.out.println("Enter number of clusters:");
        Scanner scanner = new Scanner(System.in);
        k = Integer.parseInt(scanner.nextLine());
    }

    private static void readFromFile() {
        File file = new File("Datasets\\unbalance\\unbalance.txt");
        points = new ArrayList<>();
        try (BufferedReader br
                     = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("\\s+");

                double x = Double.parseDouble(splitLine[0]);
                double y = Double.parseDouble(splitLine[1]);

                points.add(new Point(x, y));
            }
        } catch (IOException e) {
            System.err.println("Cannot read from file" + file.getPath());
        }
    }

    private static void calculateMinAndMaxCoordinates() {
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;

        for (Point point : points) {
            double currentX = point.getX();
            double currentY = point.getY();
            if (currentX < minX) {
                minX = currentX;
            }
            if (currentX > maxX) {
                maxX = currentX;
            }

            if (currentY < minY) {
                minY = currentY;
            }
            if (currentY > maxY) {
                maxY = currentY;
            }
        }
    }
}
