import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class Solver {
    private static int zeroIndex;
    private static int minMoves = -1;
    private static BoardNode bestNode;
    private static int threshold;

    public Solver(Board initial) {
        if (initial == null) {
            throw new NullPointerException("No initial board given!");
        }
        PriorityQueue<BoardNode> queue = new PriorityQueue<>();
        queue.add(new BoardNode(initial, 0, null));
        while (!queue.isEmpty()) {
            BoardNode current = queue.remove();
            if (current.getMoves() + current.getManhattanDistance() > threshold) {
                break;
            }
            if (current.getBoard().isGoal()) {
                BoardNode root = current.root();
                if (!root.getBoard().equals(initial)) {
                    break;
                }
                if (minMoves == -1 || current.getMoves() < minMoves) {
                    minMoves = current.getMoves();
                    bestNode = current;
                }
            }
            if (minMoves == -1 || current.getMoves() + current.getManhattanDistance() < minMoves) {
                Iterable<Board> it = current.getBoard().neighbors();
                for (Board b : it) {
                    if (current.getPreviousBoard() == null || !b.equals(current.getPreviousBoard().getBoard())) {
                        queue.add(new BoardNode(b, current.getMoves() + 1, current));
                    }
                }
            } else {
                break;
            }
        }
    }

    private static void printSolution() {
        if (minMoves != -1) {
            Stack<String> sol = new Stack<>();
            BoardNode current = bestNode;
            while (current != null) {
                sol.push(current.getBoard().getDirection());
                current = current.getPreviousBoard();
            }
            sol.pop();
            while (!sol.empty()) {
                System.out.println(sol.pop());
            }
        }
    }

    public static int getZeroIndex() {
        return zeroIndex;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numTiles = Integer.parseInt(scanner.nextLine().trim());
        int size = (int) Math.sqrt(numTiles + 1);
        if (size < 2 || size > 32768) {
            throw new IllegalArgumentException("Size of the board must be between 2 and 32768");
        }

        zeroIndex = Integer.parseInt(scanner.nextLine().trim());
        if (zeroIndex == -1) {
            zeroIndex = size * size - 1;
        }

        int initialZero = 0;
        int[] temp = new int[numTiles + 1];
        for (int i = 0; i < size; i++) {
            String line = scanner.nextLine().trim();
            String[] numbersAsString = line.split(" ");
            if (numbersAsString.length != size) {
                throw new IllegalArgumentException("All rows must have " + size + " elements");
            }
            for (int j = 0; j < size; j++) {
                int el = Integer.parseInt(numbersAsString[j]);
                if (el == 0) {
                    initialZero = i * size + j;
                }
                temp[i * size + j] = el;
            }
        }

        Board initial = new Board(temp, initialZero, "root");

        if (initial.isSolvable()) {
            threshold = size * size;
            while (minMoves == -1) {
                Solver solver = new Solver(initial);
                threshold += size;
            }

            System.out.println(minMoves);
            printSolution();
        } else {
            System.out.println("Board is not solvable!");
        }
    }
}