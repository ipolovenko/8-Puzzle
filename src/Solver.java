public class Solver {

    private Board previousBrd, previousTwinBrd;
    private boolean isSolvable;
    private Node solutionNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new NullPointerException();
        MinPQ<Node> minPq = new MinPQ<>();
        MinPQ<Node> minPqTwin = new MinPQ<>();
        minPq.insert(new Node(initial));
        minPqTwin.insert(new Node(initial.twin()));
        Node solution;
        do {
            if ((solution = move(minPq, true)) != null) {
                isSolvable = true;
                solutionNode = solution;
            } else solution = move(minPqTwin, false);
        } while (solution == null);
    }

    private Node move(MinPQ<Node> mpq, boolean original) {
        Node minimumNode = mpq.delMin();
        if (minimumNode.board.isGoal()) return minimumNode;
        for (Board neighbor : minimumNode.board.neighbors()) {
            if (original && previousBrd != null && previousBrd.equals(neighbor)) continue;
            else if (!original && previousTwinBrd != null && previousTwinBrd.equals(neighbor)) continue;
            mpq.insert(new Node(minimumNode, neighbor));
        }
        if (original) previousBrd = minimumNode.board;
        else previousTwinBrd = minimumNode.board;
        return null;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return isSolvable ? solutionNode.movesCount : -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable) return null;
        Node prevNode = solutionNode;
        Stack<Board> path = new Stack<>();
        path.push(prevNode.board);
        while (prevNode.previousNode != null) {
            path.push(prevNode.previousNode.board);
            prevNode = prevNode.previousNode;
        }
        return path;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private static class Node implements Comparable {
        private Board board;
        private Node previousNode;
        private int movesCount = 0;

        private Node(Board b) {
            board = b;
        }

        private Node(Node n, Board b) {
            board = b;
            previousNode = n;
            movesCount = n.movesCount;
            movesCount++;
        }

        @Override
        public int compareTo(Object o) {
            Node that = (Node)o;
            int manhattanDiff = (board.manhattan() + movesCount) - (that.board.manhattan() + that.movesCount);
            return manhattanDiff != 0 ? manhattanDiff : (board.hamming() + movesCount) - (that.board.hamming() + that.movesCount);
        }
    }
}