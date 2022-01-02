import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NaiveBayesClassifier {
    private List<String[]> dataSet;

    private int republicanCount;
    private int democratCount;
    private int[][] republicanProbabilityTable;
    private int[][] democratProbabilityTable;

    private NaiveBayesClassifier() {
        dataSet = new ArrayList<>();
        readFromDataFile();

        republicanCount = 0;
        democratCount = 0;

        republicanProbabilityTable = new int[3][16];
        democratProbabilityTable = new int[3][16];
    }

    private void readFromDataFile() {
        File file = new File("src\\house-votes-84.data");
        try (BufferedReader br
                     = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                if (splitLine.length != 17) {
                    continue;
                }
                dataSet.add(splitLine);
            }
        } catch (IOException e) {
            System.err.println("Cannot read from file" + file.getPath());
        }
    }

    private void train(int iteration, int testSetSize) {
        for (int i = 0; i < testSetSize*iteration; i++) {
            trainElement(dataSet.get(i));
        }

        for (int i = testSetSize*(iteration + 1); i < dataSet.size(); i++) {
            trainElement(dataSet.get(i));
        }
    }

    private void trainElement(String[] line) {
        boolean isRepublican = line[0].equals("republican");

        if(isRepublican) {
            trainRepublican(line);
        } else {
            trainDemocrat(line);
        }
    }

    private void trainRepublican(String[] line) {
        republicanCount++;

        for(int i = 1; i < line.length; i++) {
            switch(line[i]) {
                case "y":
                    republicanProbabilityTable[0][i-1]++;
                    break;
                case "n":
                    republicanProbabilityTable[1][i-1]++;
                    break;
                case "?":
                    republicanProbabilityTable[2][i-1]++;
                    break;
            }
        }
    }

    private void trainDemocrat(String[] line) {
        democratCount++;

        for(int i = 1; i < line.length; i++) {
            switch(line[i]) {
                case "y":
                    democratProbabilityTable[0][i-1]++;
                    break;
                case "n":
                    democratProbabilityTable[1][i-1]++;
                    break;
                case "?":
                    democratProbabilityTable[2][i-1]++;
                    break;
            }
        }
    }

    private boolean testElement(String[] line) {
        boolean isRepublican = line[0].equals("republican");

        double republicanProbability = Math.log((republicanCount + 1) / (double)(republicanCount + democratCount + 2));
        double democratProbability = Math.log((democratCount + 1) / (double)(republicanCount + democratCount + 2));

        for (int i = 1; i < line.length; i++) {
            switch(line[i]) {
                case "y":
                    republicanProbability += (republicanProbabilityTable[0][i-1] + 1) / (double)(republicanCount + 2);
                    democratProbability += (democratProbabilityTable[0][i-1] + 1) / (double)(democratCount + 2);
                case "n":
                    republicanProbability += (republicanProbabilityTable[1][i-1] + 1) / (double)(republicanCount + 2);
                    democratProbability += (democratProbabilityTable[1][i-1] + 1) / (double)(democratCount + 2);
                case "?":
                    republicanProbability += (republicanProbabilityTable[2][i-1] + 1) / (double)(republicanCount + 2);
                    democratProbability += (democratProbabilityTable[2][i-1] + 1) / (double)(democratCount + 2);
            }
        }

        return isRepublican ? republicanProbability >= democratProbability : democratProbability >= republicanProbability;
    }

    void printProbabilityTables() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 16; j++) {
                System.out.println("Democrat " + i + " " + j + " " + democratProbabilityTable[i][j]);
                System.out.println("Republican " + i + " " + j + " " + republicanProbabilityTable[i][j]);
            }
        }
    }

    private double accuracy(int iteration, int testSetSize) {
        double rightPredictions = 0;

        for (int i = testSetSize*iteration; i < testSetSize*(iteration + 1); i++) {
            rightPredictions += testElement(dataSet.get(i)) ? 1 : 0;
        }

        return rightPredictions * 100/testSetSize;
    }

    public static void main(String[] args) {
        NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();

        int testSetSize = naiveBayesClassifier.dataSet.size() / 10;
        double accuracySum = 0;
        for (int iteration = 0; iteration < 10; iteration++) {
            Collections.shuffle(naiveBayesClassifier.dataSet);
            naiveBayesClassifier.train(iteration, testSetSize);
            double accuracy = naiveBayesClassifier.accuracy(iteration, testSetSize);
            System.out.println("Iteration " + iteration + ": " + accuracy + "%");
            accuracySum += accuracy;
        }

        System.out.println("Average accuracy: " + accuracySum / 10);
    }
}
