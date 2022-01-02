import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.List;

public class PointDrawer extends JFrame {
    public void initUI(List<Point> points, Point[] centroids) {
        final Surface surface = new Surface(points, centroids);
        add(surface);

        setTitle("Points");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PointsInput.readInput();
                PointDrawer pointDrawer = new PointDrawer();

                KMeans kMeans = new KMeans();
                kMeans.findBestClusters();

                pointDrawer.setVisible(true);
                pointDrawer.initUI(kMeans.getBestPoints(), kMeans.getBestCentroids());
            }
        });
    }
}
