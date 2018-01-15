import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * @author Dima Pasieka
 */
public class SAP {
    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (isInValid(v) || isInValid(w)) {
            throw new IllegalArgumentException();
        }

        int ancestor = ancestor(v, w);
        if (ancestor == -1) {
            return -1;
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);

        return bfsv.distTo(ancestor) + bfsw.distTo(ancestor);

    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (isInValid(v) || isInValid(w)) {
            throw new IllegalArgumentException();
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);
        if (v == w) {
            return v;
        }

        int distance = 2 * digraph.V();
        int ancestor = -1;
        for (int vertice = 0; vertice < digraph.V(); vertice++) {
            if (bfsv.hasPathTo(vertice) && bfsw.hasPathTo(vertice)) {
                int checkedDistance = bfsv.distTo(vertice) + bfsw.distTo(vertice);
                if (checkedDistance < distance) {
                    distance = checkedDistance;
                    ancestor = vertice;
                }
            }
        }

        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

        for (int id: v) {
            if (isInValid(id)) {
                throw new IllegalArgumentException();
            }
        }

        for (int id: w) {
            if (isInValid(id)) {
                throw new IllegalArgumentException();
            }
        }

        int ancestor = ancestor(v, w);
        if (ancestor == -1) {
            return -1;
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);

        return bfsv.distTo(ancestor) + bfsw.distTo(ancestor);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

        for (int id: v) {
            if (isInValid(id)) {
                throw new IllegalArgumentException();
            }
        }

        for (int id: w) {
            if (isInValid(id)) {
                throw new IllegalArgumentException();
            }
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);
        int distance = 2 * digraph.V();
        int ancestor = -1;
        for (int vertice = 0; vertice < digraph.V(); vertice++) {
            if (bfsv.hasPathTo(vertice) && bfsw.hasPathTo(vertice)) {
                int checkedDistance = bfsv.distTo(vertice) + bfsw.distTo(vertice);
                if (checkedDistance < distance) {
                    distance = checkedDistance;
                    ancestor = vertice;
                }
            }
        }

        return ancestor;
    }

    private boolean isInValid(int v) {
        return v < 0 || v > (digraph.V() - 1);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}