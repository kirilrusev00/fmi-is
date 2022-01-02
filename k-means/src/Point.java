public class Point {
    private double x;
    private double y;
    private int cluster;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
        cluster = -1;
    }

    Point(double x, double y, int cluster) {
        this.x = x;
        this.y = y;
        this.cluster = cluster;
    }

    Point(Point point) {
        this.x = point.x;
        this.y = point.y;
        this.cluster = point.cluster;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public int getCluster() {
        return cluster;
    }

    public double getWindowX(int windowWidth) {
        return (x - PointsInput.minX) * windowWidth / (PointsInput.maxX - PointsInput.minX);
    }

    public double getWindowY(int windowHeight) {
        return (PointsInput.maxY - y) * windowHeight / (PointsInput.maxY - PointsInput.minY);
    }

    public static double calculateDistance(Point a, Point b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    @Override
    public String toString() {
        return "Point coordinates: (" + x + "; " + y + ")";
    }
}
