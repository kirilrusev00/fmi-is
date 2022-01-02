import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

class Surface extends JPanel implements ActionListener {
    private List<Point> points;
    private Point[] centroids;
    private Color[] clusterColors;
    private int clusters;

    public Surface(List<Point> points, Point[] centroids) {
        this.points = new ArrayList<>(points);
        this.centroids = Arrays.copyOf(centroids, centroids.length);
        this.clusters = centroids.length;
        clusterColors = new Color[clusters];
        generateClusterColors();
    }

    private void generateClusterColors() {
        for (int i = 0; i < clusters; i++) {
            Random rand = new Random();
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            Color randomColor = new Color(r, g, b);
            clusterColors[i] = randomColor;
        }
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(Color.blue);

        int windowWidth = getWidth();
        int windowHeight = getHeight();

        for (Point point : points) {
            int x = (int) point.getWindowX(windowWidth);
            int y = (int) point.getWindowY(windowHeight);
            g2d.setColor(clusterColors[point.getCluster()]);
            g2d.setStroke(new BasicStroke(3, CAP_ROUND,	JOIN_ROUND));
            g2d.drawLine(x, y, x, y);
        }

        for (Point centroid : centroids) {
            int x = (int) centroid.getWindowX(windowWidth);
            int y = (int) centroid.getWindowY(windowHeight);
            g2d.setColor(new Color(0, 0, 0));
            g2d.setStroke(new BasicStroke(10, CAP_ROUND,	JOIN_ROUND));
            g2d.drawLine(x, y, x, y);
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}