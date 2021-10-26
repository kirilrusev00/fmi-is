import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Board {
    private static int RANDOM_RESTART = 150;

    private boolean hasConflicts;
    private int size;
    private int[] queens;
    private int[] columnConflicts;
    private int[] diagonal1Conflicts;
    private int[] diagonal2Conflicts;
    private Random random = new Random();

    public Board(int size) {
        this.size = size;
        hasConflicts = true;
        queens = new int[size];
        columnConflicts = new int[size];
        diagonal1Conflicts = new int[2 * size - 1];
        diagonal2Conflicts = new int[2 * size - 1];

        Arrays.fill(queens, -1);
        Arrays.fill(columnConflicts, 0);
        Arrays.fill(diagonal1Conflicts, 0);
        Arrays.fill(diagonal2Conflicts, 0);
    }

    private void putQueens() {
        for (int row = 0; row < size; row++) {
            int lowestConflictIndex = findLowestConflicts(row);

            queens[row] = lowestConflictIndex;
            updateConflicts(row, lowestConflictIndex, 1);
        }
    }

    private void updateConflicts(int row, int col, int conflictChange) {
        this.columnConflicts[col] += conflictChange;
        this.diagonal1Conflicts[col - row + size - 1] += conflictChange;
        this.diagonal2Conflicts[col + row] += conflictChange;
    }

    private int findLowestConflicts(int row) {
        int minConflicts = size + 1;
        ArrayList<Integer> minConflictColumns = new ArrayList<>();

        for (int col = 0; col < size; col++) {
            int currentConflicts = columnConflicts[col] + diagonal1Conflicts[col - row + size - 1]
                                 + diagonal2Conflicts[row + col];

            if (queens[row] == col) {
                currentConflicts -= 3;
            }

            if (currentConflicts == minConflicts) {
                minConflictColumns.add(col);
            } else if (currentConflicts < minConflicts) {
                minConflicts = currentConflicts;
                minConflictColumns.clear();
                minConflictColumns.add(col);
            }
        }

        int randomIndex = random.nextInt(minConflictColumns.size());
        return minConflictColumns.get(randomIndex);
    }

    private int findQueenWithMostConflicts() {
        int maxConflicts = -1;
        ArrayList<Integer> queensWithMostConflicts = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            int col = queens[row];
            int currentConflicts = columnConflicts[col] + diagonal1Conflicts[col - row + size - 1]
                                 + diagonal2Conflicts[row + col] - 3;

            if (currentConflicts == maxConflicts) {
                queensWithMostConflicts.add(row);
            } else if (currentConflicts > maxConflicts) {
                maxConflicts = currentConflicts;
                queensWithMostConflicts.clear();
                queensWithMostConflicts.add(row);
            }
        }

        if (maxConflicts == 0) {
            hasConflicts = false;
        }

        int randomIndex = random.nextInt(queensWithMostConflicts.size());
        return queensWithMostConflicts.get(randomIndex);
    }

    private void solve() {
        int steps = 0;

        while (steps <= RANDOM_RESTART) {
            int row = findQueenWithMostConflicts();
            if (!hasConflicts) {
                System.out.println("Steps: " + steps);
                break;
            }
            int col = findLowestConflicts(row);
            updateConflicts(row, queens[row], -1);
            updateConflicts(row, col, 1);
            queens[row] = col;

            steps++;
        }
    }

    private void print(){
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (queens[row] == col) {
                    System.out.print("* ");
                }
                else {
                    System.out.print("_ ");
                }
            }
            System.out.println();
        }
    }

    private boolean isSolved() {
        return !hasConflicts;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        if (n < 4) {
            System.out.println("Queens should be more than 3");
            return;
        }

        Instant start = Instant.now();

        Board board = new Board(n);
        board.putQueens();
        board.solve();

        while (!board.isSolved()) {
            System.out.println("Random restart!");
            board = new Board(n);
            board.putQueens();
            board.solve();
        }

        Instant end = Instant.now();

        if (n <= 100) {
            board.print();
        }

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " milliseconds");
    }
}
