import java.util.ArrayList;

public class Board {
    private int size;
    private int currentPosOfZero;
    private int[] tiles;
    private String direction;

    public Board(int[] tiles, int currentPosOfZero, String direction) {
        this.size = (int) Math.sqrt(tiles.length);
        this.currentPosOfZero = currentPosOfZero;
        this.tiles = new int[tiles.length];
        System.arraycopy(tiles, 0, this.tiles, 0, tiles.length);
        this.direction = direction;
    }

    public int manhattan() {
        int count = 0;
        int currentIndex;
        int currentTile;
        int tilesBeforeZero = Solver.getZeroIndex();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                currentIndex = i * size + j;
                currentTile = tiles[currentIndex];
                if (currentTile == 0) {
                    continue;
                }
                if (tilesBeforeZero > 0) {
                    count += Math.abs((currentTile - 1) / size - i) + Math.abs((currentTile - 1) % size - j);
                    --tilesBeforeZero;
                } else {
                    count += Math.abs(currentTile / size - i) + Math.abs(currentTile % size - j);
                }
            }
        }
        return count;
    }

    public Iterable<Board> neighbors(){
        if (size < 2) {
            return null;
        }
        ArrayList<Board> boards = new ArrayList<>();

        int x = currentPosOfZero / size;
        int y = currentPosOfZero % size;

        if (x > 0) {
            boards.add(getNeighbor(currentPosOfZero - size, "down"));
        }
        if (x < size - 1) {
            boards.add(getNeighbor(currentPosOfZero + size, "up"));
        }
        if (y > 0) {
            boards.add(getNeighbor(currentPosOfZero - 1, "right"));
        }
        if (y < size - 1) {
            boards.add(getNeighbor(currentPosOfZero + 1, "left"));
        }
        return boards;
    }

    private Board getNeighbor(int newPositionOfZero, String direction) {
        Board newBoard = new Board(tiles, newPositionOfZero, direction);
        newBoard.swapTiles(currentPosOfZero, newPositionOfZero);
        return newBoard;
    }

    private void swapTiles(int pos1, int pos2) {
        int temp = tiles[pos1];
        tiles[pos1] = tiles[pos2];
        tiles[pos2] = temp;
    }

    public boolean isGoal() {
        return manhattan() == 0;
    }

    public boolean isSolvable() {
        int[] tilesWithoutZero = new int[size * size - 1];
        int curr = 0;
        for (int i = 0; i < size * size; i++) {
            if (tiles[i] != 0) {
                tilesWithoutZero[curr] = tiles[i];
                curr++;
            }
        }
        int numInversions = InversionCounter.getNumberInversions(tilesWithoutZero);
        if (size % 2 == 1) {
            return numInversions % 2 == 0;
        } else {
            int rowOfZero = currentPosOfZero / size;
            return (numInversions + rowOfZero) % 2 == 1;
        }
    }

    public String getDirection() {
        return direction;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass()!= this.getClass()) {
            return false;
        }

        Board board = (Board) obj;
        for (int i = 0; i < size * size; i++) {
            if (this.tiles[i] != board.tiles[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board.append(tiles[i * size + j]).append(" ");
            }
            board.append('\n');
        }
        return board.toString();
    }
}
