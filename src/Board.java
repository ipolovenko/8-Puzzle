public class Board {

    private int N, length, zeroPos, hamming = -1, manhattan = -1;
    private int[] board;

    private Board() {}

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        int index = 0;
        N = blocks.length;
        length = N * N;
        board = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[index] = blocks[i][j];
                if (board[index] == 0) zeroPos = index;
                index++;
            }
        }
    }

    // board dimension N
    public int dimension() {
        return N;
    }

    // number of blocks out of place
    public int hamming() {
        if (hamming == -1) {
            hamming = 0;
            for (int i = 0; i < length - 1; i++) {
                if (i + 1 != board[i]) hamming++;
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        if (manhattan == -1) {
            manhattan = 0;
            for (int i = 0; i < length; i++) {
                if (board[i] == 0) continue;
                int a = board[i], pos = i + 1;
                if (a != pos) {
                    int rows = ceiling(a, N) - ceiling(pos, N);
                    if (rows < 0) rows = -rows;
                    int cols = (a - 1) % N - i % N;
                    if (cols < 0) cols = -cols;
                    manhattan += rows;
                    manhattan += cols;
                }
            }
        }
        return manhattan;
    }

    private int ceiling(int a, int b) {
        int ceil = a / b;
        return a % b != 0 ? ++ceil : ceil;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < length - 1; i++) {
            if (i + 1 != board[i])
                return false;
        }
        return true;
    }

    // a board that is obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        Board twin = new Board();
        twin.board = new int[length];
        for (int i = 0; i < length; i++) {
            twin.board[i] = board[i];
            if (twin.board[i] == 0) twin.zeroPos = i;
        }
        if (twin.zeroPos < 2) {
            int exch = twin.board[N];
            twin.board[N] = twin.board[N + 1];
            twin.board[N + 1] = exch;
        } else {
            int exch = twin.board[0];
            twin.board[0] = twin.board[1];
            twin.board[1] = exch;
        }
        twin.N = N;
        twin.length = length;
        return twin;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (!(y instanceof Board)) return false;
        Board that = (Board) y;
        if (length != that.length) return false;
        for (int i = 0; i < length; i++) {
            if (board[i] != that.board[i]) return false;
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> neighbors = new Stack<>();
        if (zeroPos + 1 < length && (zeroPos + 1) / N == zeroPos / N) neighbors.push(makeNeighbor(zeroPos + 1));
        if (zeroPos + N < length) neighbors.push(makeNeighbor(zeroPos + N));
        if (zeroPos - 1 >= 0 && (zeroPos - 1) / N == zeroPos / N) neighbors.push(makeNeighbor(zeroPos - 1));
        if (zeroPos - N >= 0) neighbors.push(makeNeighbor(zeroPos - N));
        return neighbors;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        int index = 0;
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", board[index++]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    private Board makeNeighbor(int newZeroPos) {
        Board neighbor = new Board();
        neighbor.zeroPos = newZeroPos;
        neighbor.length = length;
        neighbor.N = N;
        neighbor.board = new int[length];
        for (int i = 0; i < length; i++) {
            if (i == zeroPos) neighbor.board[i] = board[newZeroPos];
            else if (i == newZeroPos) neighbor.board[i] = board[zeroPos];
            else neighbor.board[i] = board[i];
        }
        return neighbor;
    }
}