import java.util.*;

public class DataSet {
    List<String[]> values;
    private List<Set<String>> attributeValues;

    private ArrayList<Map<String, Integer>> recurrenceTable;
    private ArrayList<Map<String, Integer>> noRecurrenceTable;

    private int valuesCount;
    int noRecurrenceCount = 0;
    int recurrenceCount = 0;

    DataSet(List<String[]> rows) {
        this.values = rows;
        this.attributeValues = new ArrayList<>();
        this.recurrenceTable = new ArrayList<>(10);
        this.noRecurrenceTable = new ArrayList<>(10);
        for(int i = 0; i < 10; i++) {
            attributeValues.add(new HashSet<>());
            recurrenceTable.add(new HashMap<String, Integer>());
            noRecurrenceTable.add(new HashMap<String, Integer>());
        }
        getAttributeValuesFromValues();
        updateTables();
    }

    void getAttributeValuesFromValues() {
        for (String[] data : values) {
            for (int i = 1; i < data.length; i++) {
                attributeValues.get(i).add(data[i]);
            }
        }
    }

    void updateTables() {
        for(String[] data : values) {
            boolean isRecurrence = data[0].equals("recurrence-events");
            if (isRecurrence) {
                recurrenceCount++;
            } else {
                noRecurrenceCount++;
            }

            for (int i = 1; i < data.length; i++) {
                if (isRecurrence) {
                    if (recurrenceTable.get(i).containsKey(data[i])) {
                        int oldValue = recurrenceTable.get(i).get(data[i]);
                        recurrenceTable.get(i).replace(data[i], oldValue + 1);
                    } else {
                        recurrenceTable.get(i).put(data[i], 1);
                    }
                } else {
                    if (noRecurrenceTable.get(i).containsKey(data[i])) {
                        int oldValue = noRecurrenceTable.get(i).get(data[i]);
                        noRecurrenceTable.get(i).replace(data[i], oldValue + 1);
                    } else {
                        noRecurrenceTable.get(i).put(data[i], 1);
                    }
                }
            }
        }

        valuesCount = recurrenceCount + noRecurrenceCount;
    }

    double entropy(double a, double b) {
        if (a == 0 || b == 0) {
            return 0;
        }

        double pa = a / (a + b);
        double pb = b / (a + b);

        return - (pa) * Math.log(pa) - (pb) * Math.log(pb);
    }

    double entropyByAttribute(int attributeIndex) {
        double entropy = 0;

        for (String attributeValue : recurrenceTable.get(attributeIndex).keySet()) {
            double recurrence = recurrenceTable.get(attributeIndex).getOrDefault(attributeValue, 0);
            double noRecurrence = noRecurrenceTable.get(attributeIndex).getOrDefault(attributeValue, 0);

            entropy += ((recurrence+noRecurrence)/ valuesCount) * entropy (recurrence, noRecurrence);
        }

        return entropy;
    }

    double informationGain(int attrIndex) {
        return entropy(noRecurrenceCount, recurrenceCount) - entropyByAttribute(attrIndex);
    }

    int findBestAttribute(HashSet<Integer> prevAttributes) {
        double maxInformationGain = Integer.MIN_VALUE;
        double currentInformationGain;
        int bestAttributeIndex = 1;

        for (int currentAttributeIndex = 1; currentAttributeIndex < 10 ; currentAttributeIndex++) {
            if (!prevAttributes.contains(currentAttributeIndex)) {
                currentInformationGain = informationGain(currentAttributeIndex);
                if (currentInformationGain > maxInformationGain) {
                    maxInformationGain = currentInformationGain;
                    bestAttributeIndex = currentAttributeIndex;
                }
            }
        }

        return bestAttributeIndex;
    }

    DataSet filter(int attributeIndex, String value) {
        List<String[]> newValues = new ArrayList<>();

        for (String[] row : values) {
            if(value.equals(row[attributeIndex])) {
                newValues.add(row);
            }
        }

        DataSet newDataSet = new DataSet(newValues);

        return newDataSet;
    }

    boolean canClassifyAsRecurrence(Node node) {
        if (recurrenceCount == noRecurrenceCount) {
            return node.parent.isRecurrence;
        }
        return recurrenceCount > noRecurrenceCount;
    }

    void printValues() {
        for (int i = 1; i < attributeValues.size(); i++) {
            for (String x: attributeValues.get(i)) {
                System.out.print(x + ": " + recurrenceTable.get(i).getOrDefault(x, 0) + " | " +
                        noRecurrenceTable.get(i).getOrDefault(x, 0) + " ... ");
            }
            System.out.println();
        }
    }
}
