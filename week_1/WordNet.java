import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;

/**
 * @author Dima Pasieka
 */
public class WordNet {
    private final HashMap<Integer, String> idToNoun;
    private final HashMap<String, Bag<Integer>> nounToId;
    private final Digraph digraph;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        idToNoun = new HashMap<Integer, String>();
        nounToId = new HashMap<String, Bag<Integer>>();
        In inSynsets = new In(synsets);

        while (inSynsets.hasNextLine()) {
            String[] lineField = inSynsets.readLine().split(",");
            int id = Integer.parseInt(lineField[0]);
            String synset = lineField[1];
            String[] nouns = synset.split(" ");
            idToNoun.put(id, synset);

            for (String noun : nouns) {
                if (nounToId.containsKey(noun)) {
                    Bag<Integer> bag = nounToId.get(noun);
                    bag.add(id);
                    nounToId.put(noun, bag);
                } else {
                    Bag<Integer> bag = new Bag<Integer>();
                    bag.add(Integer.valueOf(id));
                    nounToId.put(noun, bag);
                }
            }
        }

        digraph = new Digraph(idToNoun.size());
        In inHypernyms = new In(hypernyms);
        while (inHypernyms.hasNextLine()) {
            String[] lineField = inHypernyms.readLine().split(",");
            int id = Integer.parseInt(lineField[0]);
            Bag<Integer> bag = new Bag<Integer>();

            for (int i = 1; i < lineField.length; i++) {
                int hypernymId = Integer.parseInt(lineField[i]);
                bag.add(Integer.valueOf(hypernymId));
                digraph.addEdge(id, hypernymId);
            }
        }
        DirectedCycle cyc = new DirectedCycle(digraph);
        if (cyc.hasCycle()) {
            throw new IllegalArgumentException("Not acyclic");
        }

        int root = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (!digraph.adj(i).iterator().hasNext()) {
                root++;
            }
        }

        if (root != 1) {
            throw new IllegalArgumentException("Not a rooted DAG");
        }

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToId.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }

        return nounToId.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        Bag<Integer> idsA = nounToId.get(nounA);
        Bag<Integer> idsB = nounToId.get(nounB);
        SAP sap = new SAP(digraph);

        return sap.length(idsA, idsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        Bag<Integer> idsA = nounToId.get(nounA);
        Bag<Integer> idsB = nounToId.get(nounB);
        SAP sap = new SAP(digraph);
        int ancestor = sap.ancestor(idsA, idsB);

        return idToNoun.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        String synsets = "/synsets.txt";
        String hypernyms = "/hypernyms.txt";
        WordNet w = new WordNet(synsets, hypernyms);

        System.out.println(w.sap("Test noun A", "Test noun B"));
        System.out.println(w.distance("Test noun A", "Test noun B"));
    }
}