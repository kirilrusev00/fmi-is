import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Algorithm {
    private List<String[]> values;
    private List<Set<String>> allAttributeValues;

    Algorithm() {
        values = new ArrayList<>();
        allAttributeValues = new ArrayList<>(10);
        for(int i = 0; i < 10; i++) {
            allAttributeValues.add(new HashSet<>());
        }
    }

    private void readFromDataFile() {
        File file = new File("src\\breast-cancer.data");
        try (BufferedReader br
                     = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                if (splitLine.length != 10) {
                    continue;
                }
                for (int i = 0; i < 10; i++) {
                    allAttributeValues.get(i).add(splitLine[i]);
                }
                values.add(splitLine);
            }
            Collections.shuffle(values);
        } catch (IOException e) {
            System.err.println("Cannot read from file" + file.getPath());
        }
    }

    private double calculateAccuracy(List<String[]> trainSet, List<String[]> testSet) {
        DataSet dataSet = new DataSet(trainSet);
        dataSet.printValues();

        DecisionTree tree = new DecisionTree(dataSet, allAttributeValues);

        double sum = 0;

        for (String[] row : testSet) {
            if (tree.classify(row)) {
                sum += 1;
            }
        }

        return (sum * 100) / testSet.size();
    }

    private void tenFoldCrossValidate() {
        int testSetSize = values.size()/10;

        double accuracySum = 0;

        List<String[]> trainSet = new ArrayList<>();
        List<String[]> testSet = new ArrayList<>();

        for (int j = 0; j < 10; j++)
        {
            for (int i = 0; i < values.size(); i++)
            {
                if (i >= j * testSetSize && i < j * testSetSize + testSetSize)
                {
                    testSet.add(values.get(i));
                }
                else
                {
                    trainSet.add(values.get(i));
                }
            }

            double accuracy = calculateAccuracy(trainSet, testSet);
            System.out.println("Iteration " + j + ": " + accuracy + "%");
            accuracySum += accuracy;

            trainSet.clear();
            testSet.clear();
        }

        System.out.println("Average accuracy: " + accuracySum / 10);
    }

    public static void main(String[] args) {
        Algorithm algorithm = new Algorithm();
        algorithm.readFromDataFile();
        algorithm.tenFoldCrossValidate();
    }
}
