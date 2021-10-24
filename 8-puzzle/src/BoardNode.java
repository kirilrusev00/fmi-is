public class BoardNode implements Comparable<BoardNode> {
    private Board board;
    private int moves;
    private int manhattanDistance;
    private BoardNode previousBoard;

    public BoardNode(Board board, int moves, BoardNode previousBoard) {
        this.board = board;
        this.moves = moves;
        this.previousBoard = previousBoard;
        manhattanDistance = board.manhattan();
    }

    public int compareTo(BoardNode that) {
        return this.moves + this.manhattanDistance - that.moves - that.manhattanDistance;
    }

    public Board getBoard() {
        return board;
    }

    public BoardNode getPreviousBoard() {
        return previousBoard;
    }

    public int getMoves() {
        return moves;
    }

    public int getManhattanDistance() {
        return manhattanDistance;
    }


    public BoardNode root() {
        BoardNode current = this;
        while (current.getPreviousBoard() != null) {
            current = current.getPreviousBoard();
        }
        return current;
    }
}
