import java.util.Scanner;

public class TicTacToe {
    private static char EMPTY_PLACE_SYMBOL = '_';
    private static char PLAYER_SYMBOL = 'o';
    private static char COMPUTER_SYMBOL = 'x';
    private static int MAX_SCORE = 10;
    private static int MIN_SCORE = -10;

    private char[][] board;
    private boolean isPlayerTurn;

    public TicTacToe(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
        board = new char[][]{{EMPTY_PLACE_SYMBOL, EMPTY_PLACE_SYMBOL, EMPTY_PLACE_SYMBOL},
                             {EMPTY_PLACE_SYMBOL, EMPTY_PLACE_SYMBOL, EMPTY_PLACE_SYMBOL},
                             {EMPTY_PLACE_SYMBOL, EMPTY_PLACE_SYMBOL, EMPTY_PLACE_SYMBOL}};
    }

    private void print() {
        System.out.println("==========");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(" ");
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    private int evaluate(int takenTurnsCount) {
        int currentValue = checkRows(takenTurnsCount);
        if (currentValue == 0) {
            currentValue = checkColumns(takenTurnsCount);
        }
        if (currentValue == 0) {
            currentValue = checkMainDiagonal(takenTurnsCount);
        }
        if (currentValue == 0) {
            currentValue = checkSecondDiagonal(takenTurnsCount);
        }

        return currentValue;
    }

    private int checkRows(int takenTurnsCount) {
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                if (board[row][0] == COMPUTER_SYMBOL) {
                    return MAX_SCORE - takenTurnsCount;
                } else if (board[row][0] == PLAYER_SYMBOL) {
                    return MIN_SCORE + takenTurnsCount;
                }
            }
        }
        return 0;
    }

    private int checkColumns(int takenTurnsCount) {
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                if (board[0][col] == COMPUTER_SYMBOL) {
                    return MAX_SCORE - takenTurnsCount;
                } else if (board[0][col] == PLAYER_SYMBOL) {
                    return MIN_SCORE + takenTurnsCount;
                }
            }
        }
        return 0;
    }

    private int checkMainDiagonal(int takenTurnsCount) {
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == COMPUTER_SYMBOL) {
                return MAX_SCORE - takenTurnsCount;
            } else if (board[0][0] == PLAYER_SYMBOL) {
                return MIN_SCORE + takenTurnsCount;
            }
        }
        return 0;
    }

    private int checkSecondDiagonal(int takenTurnsCount) {
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == COMPUTER_SYMBOL) {
                return MAX_SCORE - takenTurnsCount;
            } else if (board[0][2] == PLAYER_SYMBOL) {
                return MIN_SCORE + takenTurnsCount;
            }
        }
        return 0;
    }

    private boolean areThereMoreTurns() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY_PLACE_SYMBOL) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isThereWinner()
    {
        return evaluate(0) != 0;
    }

    private int maximizer(int alpha, int beta, int takenTurnsCount) {
        int currentScore = evaluate(takenTurnsCount);

        if (currentScore != 0) {
            return currentScore;
        }

        if (!areThereMoreTurns()) {
            return 0;
        }

        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY_PLACE_SYMBOL) {
                    board[i][j] = COMPUTER_SYMBOL;

                    bestScore = Integer.max(bestScore, minimizer(alpha, beta, takenTurnsCount + 1));

                    board[i][j] = EMPTY_PLACE_SYMBOL;

                    if (bestScore >= beta) {
                        return bestScore;
                    }
                    alpha = Integer.max(alpha, bestScore);
                }
            }
        }
        return bestScore;
    }

    private int minimizer(int alpha, int beta, int takenTurnsCount) {
        int currentScore = evaluate(takenTurnsCount);

        if (currentScore != 0) {
            return currentScore;
        }

        if (!areThereMoreTurns()) {
            return 0;
        }

        int bestScore = Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY_PLACE_SYMBOL) {
                    board[i][j] = PLAYER_SYMBOL;

                    bestScore = Integer.min(bestScore, maximizer(alpha, beta, takenTurnsCount + 1));

                    board[i][j] = EMPTY_PLACE_SYMBOL;

                    if (bestScore <= alpha) {
                        return bestScore;
                    }
                    beta = Integer.min(beta, bestScore);
                }
            }
        }
        return bestScore;
    }

    private int[] findBestTurnForComputer() {
        int bestValue = Integer.MIN_VALUE;
        int[] bestNextTurn = new int[]{-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY_PLACE_SYMBOL) {
                    board[i][j] = COMPUTER_SYMBOL;

                    int curTurnValue = minimizer(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);

                    board[i][j] = EMPTY_PLACE_SYMBOL;

                    if (curTurnValue > bestValue) {
                        bestNextTurn[0] = i;
                        bestNextTurn[1] = j;
                        bestValue = curTurnValue;
                    }
                }
            }
        }
        return bestNextTurn;
    }

    private boolean makeTurn(int i, int j) {
        if (board[i][j] == EMPTY_PLACE_SYMBOL) {
            if (isPlayerTurn) {
                board[i][j] = PLAYER_SYMBOL;
            } else {
                board[i][j] = COMPUTER_SYMBOL;
            }
            print();
            return true;
        }

        System.out.println("This place is not empty! Choose again.");
        return false;
    }

    private void play() {
        Scanner scanner = new Scanner(System.in);
        while (areThereMoreTurns() && !isThereWinner()) {
            int i, j;
            if (this.isPlayerTurn) {
                do {
                    System.out.println("Input the place of your turn:");
                    System.out.println("Row:");
                    i = scanner.nextInt();
                    System.out.println("Column:");
                    j = scanner.nextInt();
                } while (!makeTurn(i - 1, j - 1));
                this.isPlayerTurn = !this.isPlayerTurn;
                continue;
            }
            int[] bestTurn = findBestTurnForComputer();
            makeTurn(bestTurn[0], bestTurn[1]);
            this.isPlayerTurn = !this.isPlayerTurn;
        }
        System.out.println("The game has ended!");
        if (isThereWinner()) {
            if (!isPlayerTurn) {
                System.out.println("You won :)");
            } else {
                System.out.println("You lost :(");
            }
        } else {
            System.out.println("No winner :/");
        }
    }

    public static void main(String[] args) {
        System.out.println("Do you want to play first? yes/no");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();

        boolean isPlayerFirst = response.equals("yes");

        TicTacToe board = new TicTacToe(isPlayerFirst);
        board.play();
    }
}
