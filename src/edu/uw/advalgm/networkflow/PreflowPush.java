package edu.uw.advalgm.networkflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.uw.advalgm.graph.Edge;
import edu.uw.advalgm.graph.SimpleGraph;
import edu.uw.advalgm.graph.Vertex;

/** This program will find max flow using pre flow push algorithm **/

public class PreflowPush {
    static long starttime;
    static long endtime;
    
    // Definition of a node in the graph
    static class Node {
        String name;
        int height = 0;
        double eflow = 0;
        boolean isSS; // true when node is sink or source
        List<Pedge> edges = new ArrayList<>();
        
        public Node(String name) {
            this.name = name;
        }
        
    }
    
    /* Definition of an edge in the graph. Named Pedge because of naming conflict in graphCode */
    static class Pedge {
        Node src;
        Node dest;
        double capacity;
        double flow;
        Pedge backEdge;
        
        public Pedge(Node src, Node dest, double capacity) {
            this.src = src;
            this.dest = dest;
            this.capacity = capacity;
            src.edges.add(this);
        }
        
        public double getRemainingCapacity() {
            return capacity - flow;
        }
    }
    
    public static double findMaxFlow(SimpleGraph sg) {
        HashMap<String, Node> mapNodes = new HashMap<>();
        Iterator i;
        // to map vertices to nodes
        for (i = sg.vertices(); i.hasNext();) {
            Vertex v = (Vertex) i.next();
            mapNodes.put((String) v.getName(), new Node((String) v.getName()));
        }
        
        // to map edges in Graphcode to Pedge
        for (i = sg.edgeList.iterator(); i.hasNext();) {
            Edge e = (Edge) i.next();
            Node src = mapNodes.get(e.getFirstEndpoint().getName());
            Node dest = mapNodes.get(e.getSecondEndpoint().getName());
            double capacity = Double.parseDouble(e.getData().toString());
            Pedge forwardEdge = new Pedge(src, dest, capacity);
            Pedge backEdge = new Pedge(dest, src, 0);
            forwardEdge.backEdge = backEdge;
            backEdge.backEdge = forwardEdge;
        }
        // Initializing values for source node.
        Node source = mapNodes.get("s");
        source.height = mapNodes.size();
        source.eflow = Integer.MAX_VALUE; // excess flow of source node set to max
        source.isSS = true;
        
        // Initializing values for sink node.
        Node sink = mapNodes.get("t");
        sink.isSS = true;
        
        // This is to calculate the run time of the program
        starttime = System.currentTimeMillis();
        computeMaxFlow(source);
        endtime = System.currentTimeMillis();
        System.out.println("Running Time (millisec):" + (endtime - starttime));
        
        return sink.eflow;
    }
    
    // Method to calculate the max flow
    private static void computeMaxFlow(Node src) {
        // All the nodes with excess flow are stored in a queue.
        Queue<Node> queue = new LinkedList<>();
        queue.add(src);
        
        // This while loop is to ensure the flow while there is a node with excess flow.
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            int minHeight = Integer.MAX_VALUE;
            // calculating minimum height for relabelling
            for (Pedge e : currNode.edges) {
                if ((minHeight > e.dest.height) && (e.getRemainingCapacity() > 0)) {
                    minHeight = e.dest.height;
                }
            }
            // relabel operation to enable flow from currNode.
            if (minHeight != Integer.MAX_VALUE && minHeight >= currNode.height) {
                currNode.height = minHeight + 1;
            }
            // pushing flow to neighboring nodes
            for (Pedge e : currNode.edges) {
                if (currNode.height > e.dest.height) {
                    
                    /*
                     * flowPossible is the value of flow that can be pushed. It is the minimum of the excess flow of the node and the remaining
                     * capacity of the edge.
                     */
                    double flowPossible = Math.min(e.getRemainingCapacity(), currNode.eflow);
                    
                    // Assigning flow values to corresponding edges based on flowPossible.
                    e.dest.eflow = e.dest.eflow + flowPossible;
                    e.flow = e.flow + flowPossible;
                    e.backEdge.flow = e.backEdge.flow - flowPossible;
                    currNode.eflow = currNode.eflow - flowPossible;
                    
                    // If the destination node has excess flow and if it is not the source/sink, it is added to the queue.
                    if (e.dest.eflow > 0 && !e.dest.isSS) {
                        queue.add(e.dest);
                    }
                    if (currNode.eflow <= 0) {
                        break;
                    }
                }
            }
            if (currNode.eflow > 0 && !currNode.isSS) {
                queue.add(currNode);
            }
        }
    }
}
