package edu.uw.advalgm.networkflow;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uw.advalgm.graph.Edge;
import edu.uw.advalgm.graph.GraphInput;
import edu.uw.advalgm.graph.SimpleGraph;
import edu.uw.advalgm.graph.Vertex;


public class LargeLabelPushRelabel {
    // static String filename = "InputGraphs/VarEdgeCapacity/edgCap1.txt";
    static String filename = "InputGraphs/Bipartite/bp3.txt";
    // static String filename = "InputGraphs/Random/rnd3.txt";
    // static String filename = "InputGraphs/Mesh/m3";
    
    /**
     * 
     * This class is a wrapper class of the edges along with the additional definition of the edges such as capacity, flow and index
     *
     */
    class NEdge {
        Edge edge;
        int cap;
        int flow;
        int index;
        
        NEdge(Edge edge, int cap, int flow, int index) {
            this.edge = edge;
            this.cap = cap;
            this.flow = flow;
            this.index = index;
        }
    }
    
    int n;
    HashMap<Vertex, LinkedList<NEdge>> adjvertex; // AdjVertex stores the list of Adjacent vertices of each Vertex
    // excess stores the excess flow of each vertex
    // distance stores the distance of each vertex to the sink
    // count stores the number of outgoing edges of each vertex
    HashMap<Vertex, Integer> excess, distance, count;
    HashMap<Vertex, Boolean> active; // active is a boolean value for the vertex which are active
    HashMap<Integer, LinkedList<Vertex>> activequeue; // activequeue is a Queue that contains the active vertex
    int queueindex; // index to traverse through the queue
    SimpleGraph simpleGraph; // contains input graph
    Vertex s, t; // Source and Sink vertex
    
    /**
     * This constructor initializes the fields of the class
     * 
     * @param n - no of nodes
     */
    LargeLabelPushRelabel(int n) {
        this.n = n;
        adjvertex = new HashMap<>();
        excess = new HashMap<>();
        distance = new HashMap<>();
        count = new HashMap<>();
        active = new HashMap<>();
        activequeue = new HashMap<>();
        queueindex = 0;
    }
    
    /**
     * This method initializes the values to zero
     * 
     * @param init
     */
    void initializeToZero(HashMap<Vertex, Integer> init) {
        Iterator itr = simpleGraph.vertices();
        while (itr.hasNext()) {
            init.put((Vertex) itr.next(), 0);
        }
    }
    
    /**
     * This method initializes the boolean variable and declares the source and sink vertex
     * 
     * @param init
     */
    void initializeToFalse(HashMap<Vertex, Boolean> init) {
        Iterator itr = simpleGraph.vertices();
        while (itr.hasNext()) {
            Vertex v = (Vertex) itr.next();
            init.put(v, false);
            if (v.getName().equals("s")) {
                s = v;
            }
            if (v.getName().equals("t")) {
                t = v;
            }
        }
    }
    
    /**
     * This method assigns the value of s and t
     */
    void initializeSandT() {
        Iterator itr = simpleGraph.vertices();
        while (itr.hasNext()) {
            Vertex v = (Vertex) itr.next();
            if (v.getName().equals("s")) {
                s = v;
            }
            if (v.getName().equals("t")) {
                t = v;
            }
        }
    }
    
    /**
     * This method is used to initialize the Adjacency list of vertex
     */
    private void initializeAdj() {
        Iterator itr = simpleGraph.edges();
        while (itr.hasNext()) {
            Edge e = (Edge) itr.next();
            if (e.getFirstEndpoint().getName().equals("s")) {
            }
            addEdge(e.getFirstEndpoint(), e.getSecondEndpoint(), (int) (Double.parseDouble(e.getData() + "")));
        }
    }
    
    /**
     * This method populates the adjvertex that contains the vertex and the list of their adjacent edges.
     * 
     * @param from - from vertex of the edge
     * @param to - the vertex to which the edge is pointed to
     * @param cap - the capacity of the edge
     */
    void addEdge(Vertex from, Vertex to, int cap) {
        NEdge e1 = new NEdge(new Edge(from, to, 0, 0), cap, 0, adjvertex.containsKey(to) ? adjvertex.get(to).size() : 0);
        if (adjvertex.containsKey(from)) {
            LinkedList<NEdge> nlis = adjvertex.get(from);
            nlis.add(e1);
            adjvertex.put(from, nlis);
        } else {
            LinkedList<NEdge> nlist = new LinkedList<NEdge>();
            nlist.add(e1);
            adjvertex.put(from, nlist);
        }
        if (from == to) {
            adjvertex.get(from).getLast().index++;
        }
        NEdge e2 = new NEdge(new Edge(to, from, 0, 0), 0, 0, adjvertex.containsKey(from) ? adjvertex.get(from).size() - 1 : 0);
        if (adjvertex.containsKey(to)) {
            LinkedList<NEdge> nlis2 = adjvertex.get(to);
            nlis2.add(e2);
            adjvertex.put(to, nlis2);
        } else {
            LinkedList<NEdge> nlist = new LinkedList<NEdge>();
            nlist.add(e2);
            adjvertex.put(to, nlist);
        }
        
    }
    
    /**
     * This method enqueues the Active nodes into a Queue called activequeue
     * 
     * @param v - vertex to be enqueued
     */
    void Enqueue(Vertex v) {
        if (!active.get(v) && excess.get(v) > 0 && distance.get(v) < n) {
            active.put(v, true);
            if (activequeue.containsKey(distance.get(v))) {
                activequeue.get(distance.get(v)).add(v);
            } else {
                LinkedList<Vertex> nlist = new LinkedList<Vertex>();
                nlist.add(v);
                activequeue.put(distance.get(v), nlist);
            }
            queueindex = Math.max(queueindex, distance.get(v));
        }
    }
    
    /**
     * This method discharges the push to the adjacent vertex with the largest excess value If there are no adjacent nodes, then it relabels the
     * vertex
     * 
     * @param v - Input vertex that is in the active queue
     */
    void Discharge(Vertex v) {
        Iterator itr = adjvertex.get(v).iterator();
        while (itr.hasNext()) {
            NEdge e = (NEdge) itr.next();
            if (excess.get(v) > 0) {
                Push(e);
            } else {
                break;
            }
        }
        
        if (excess.get(v) > 0) {
            if (count.get(v) == 1) {
                Gap(distance.get(v));
            } else {
                Relabel(v);
            }
        }
    }
    
    /**
     * This method is used to push the excess flow in the edge e
     * 
     * @param e - unsaturated edge
     */
    void Push(NEdge e) {
        int amt = Math.min(excess.get(e.edge.getFirstEndpoint()), e.cap - e.flow);
        if (distance.get(e.edge.getFirstEndpoint()) >= (distance.get(e.edge.getSecondEndpoint()) + 1) && amt > 0) {
            e.flow += amt;
            adjvertex.get(e.edge.getSecondEndpoint()).get(e.index).flow -= amt;
            int i1 = excess.get(e.edge.getSecondEndpoint()) + amt;
            excess.put(e.edge.getSecondEndpoint(), i1);
            int i2 = excess.get(e.edge.getFirstEndpoint()) - amt;
            excess.put(e.edge.getFirstEndpoint(), i2);
            Enqueue(e.edge.getSecondEndpoint());
        }
    }
    
    /**
     * This method is used to find the maximum distance between the vertex and the sink, and to enqueue that vertex
     * 
     * @param k - current distance of the vertex
     */
    void Gap(int k) {
        Iterator itr = simpleGraph.vertices();
        while (itr.hasNext()) {
            Vertex v = (Vertex) itr.next();
            if (distance.get(v) >= k) {
                count.put(v, distance.get(v) - 1);
                distance.put(v, Math.max(distance.get(v), n));
                count.put(v, distance.get(v) + 1);
                Enqueue(v);
            }
        }
    }
    
    /**
     * This method is used for relabelling the vertex, and then enqueue them
     * 
     * @param v - The vertex to be relabelled
     */
    void Relabel(Vertex v) {
        count.put(v, distance.get(v) - 1);
        distance.put(v, n);
        Iterator itr = adjvertex.get(v).iterator();
        while (itr.hasNext()) {
            NEdge e = (NEdge) itr.next();
            if (e.cap - e.flow > 0) {
                distance.put(v, Math.min(distance.get(v), distance.get(e.edge.getSecondEndpoint()) + 1));
            }
        }
        count.put(v, distance.get(v) + 1);
        Enqueue(v);
    }
    
    /**
     * This is the main method which is used to find the maximum flow given the source and sink
     * 
     * @param s - source vertex
     * @param t - sink vertex
     * @return maximum flow value
     */
    int getMaxFlow(Vertex s, Vertex t) {
        
        initializeToZero(distance);
        initializeToZero(excess);
        initializeToZero(count);
        initializeToFalse(active);
        initializeAdj();
        
        distance.put(s, 0);
        excess.put(s, 0);
        count.put(s, 0);
        active.put(t, true);
        
        Iterator itr = adjvertex.get(s).iterator();
        while (itr.hasNext()) {
            NEdge e = (NEdge) itr.next();
            excess.put(s, excess.get(s) + e.cap);
        }
        count.put(s, n);
        Enqueue(s);
        active.put(t, true);
        
        while (queueindex >= 0) {
            if (activequeue.containsKey(queueindex) && activequeue.get(queueindex).size() > 0) {
                Vertex v = activequeue.get(queueindex).poll();
                active.put(v, false);
                Discharge(v);
            } else {
                queueindex--;
            }
        }
        return excess.get(t);
    }
    
    /**
     * main - creates a simple graph from the file location and starts the function.
     * 
     * @param args
     */
    
    public static double getMaxFlow(SimpleGraph simplegraph) {
        LargeLabelPushRelabel pr2 = new LargeLabelPushRelabel(simplegraph.numVertices());
        pr2.simpleGraph = simplegraph;
        pr2.initializeSandT();
        long starttime = System.currentTimeMillis();
        int result = pr2.getMaxFlow(pr2.s, pr2.t);
        long endtime = System.currentTimeMillis();
        System.out.println("Time taken :" + (endtime - starttime));
        return result;
    }
}
