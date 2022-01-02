import java.util.List;
import java.util.Set;

public class DecisionTree {
    private static final int MIN_SAMPLE_SIZE = 50;

    private List<Set<String>> allAttributeValues;
    private Node root;

    DecisionTree(DataSet dataset, List<Set<String>> allAttributeValues) {
        this.allAttributeValues = allAttributeValues;
        buildAttributeNode(dataset, null);
    }

    private void buildAttributeNode(DataSet dataset, Node parentNode) {
        Node childNode;

        if (parentNode == null) {
            root = new Node();
            childNode = root;
        } else {
            childNode = new Node();
            childNode.prevAttributes = parentNode.prevAttributes;
            parentNode.children.add(childNode);
        }

        int bestAttr = dataset.findBestAttribute(childNode.prevAttributes);

        childNode.attribute = bestAttr;
        childNode.prevAttributes.add(bestAttr);
        childNode.parent = parentNode;
        childNode.isRecurrence = dataset.canClassifyAsRecurrence(childNode);

        Set<String> attrValues = allAttributeValues.get(bestAttr);

        for (String value: attrValues) {
            buildValueNode(bestAttr, value, childNode, dataset);
        }
    }

    private void buildValueNode(int attr, String value, Node parentNode, DataSet dataset) {
        if (isEntropyZero(dataset) || parentNode.prevAttributes.size() == 9 || dataset.values.size() < MIN_SAMPLE_SIZE) {
            Node leaf = new Node();
            leaf.value = value;
            leaf.isLeaf = true;
            leaf.parent = parentNode;
            leaf.isRecurrence = dataset.canClassifyAsRecurrence(leaf);
            parentNode.children.add(leaf);
            return;
        }

        Node childNode = new Node();
        childNode.attribute = attr;
        childNode.value = value;
        childNode.prevAttributes = parentNode.prevAttributes;
        childNode.parent = parentNode;
        childNode.isRecurrence = dataset.canClassifyAsRecurrence(childNode);
        parentNode.children.add(childNode);
        DataSet subset = dataset.filter(attr, value);
        buildAttributeNode(subset, childNode);
    };

    boolean classify(String[] row) {
        int attr;
        String value;
        Node node = root;

        while(true) {
            attr = node.attribute;
            value = row[attr];

            for (int i = 0; i < node.children.size(); i++) {
                if(value.equals(node.children.get(i).value)) {
                    if (node.children.get(i).isLeaf) {
                        return (node.children.get(i).isRecurrence) ?
                        row[0].equals("recurrence-events") :
                        row[0].equals("no-recurrence-events");
                    }

                    node = node.children.get(i).children.get(0);
                    break;
                }
            }
        }
    }

    private boolean isEntropyZero(DataSet dataset) {
        return dataset.entropy(dataset.recurrenceCount, dataset.noRecurrenceCount) == 0;
    }
}
