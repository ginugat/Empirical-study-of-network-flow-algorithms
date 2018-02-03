package edu.uw.advalgm.networkflow;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uw.advalgm.graph.Edge;
import edu.uw.advalgm.graph.SimpleGraph;
import edu.uw.advalgm.graph.Vertex;

public final class ScalingFordFulkerson {
    
    private final Vertex source;
    private final Vertex sink;
    private final SimpleGraph simpleGraph;
    private double maxFlow;
    
    public ScalingFordFulkerson(final SimpleGraph simpleGraph, final Hashtable nodeEdges) {
        this.source = (Vertex) nodeEdges.get("s");
        this.sink = (Vertex) nodeEdges.get("t");
        
        this.simpleGraph = simpleGraph;
        
        GraphUtils.transformEdgeToHaveCapacityAndFlow(this.simpleGraph);
    }
    
    public static double calculateMaxFlow(SimpleGraph simpleGraph, Hashtable nodeEdges) {
        ScalingFordFulkerson scalingFordFulkerson;
        scalingFordFulkerson = new ScalingFordFulkerson(simpleGraph, nodeEdges);
        
        long start = System.currentTimeMillis();
        double ourMaxFlow = scalingFordFulkerson.getMaxFlow();
        long end = System.currentTimeMillis();
        double run_time = end - start;
        System.out.println("Running time is " + run_time + " milliseconds");
        
        return ourMaxFlow;
    }
    
    /**
     * Computes the max flow by finding s-t path which can accomodate delta (2 * n < maxCapacity )
     *
     * @return - maxFlow of the graph
     */
    public double getMaxFlow() {
        
        /* compute max capacity the graph can allow */
        double maxCapacity = Double.MIN_VALUE;
        
        for (final Iterator iterator = this.simpleGraph.edges(); iterator.hasNext();) {
            final EdgeData edgeData = (EdgeData) ((Edge) iterator.next()).getData();
            maxCapacity = Math.max(maxCapacity, edgeData.getEdgeCapacity());
        }
        
        /* get the highest power of 2 less than maxCapacity */
        int delta = getDelta(maxCapacity);
        
        /*
         * Find a s-t path where we can push the flow by delta. If we cannot find such path, then try with delta/2 recursively
         */
        while (delta > 0) {
            LinkedList<Vertex> stPath = GraphUtils.doDFS(this.source, this.sink, this.simpleGraph, delta);
            while (stPath.size() > 1) {
                
                /* increase the flow through the found s-t path and compute the max flow */
                increaseFlowThroughPath(stPath, delta);
                
                /* reset the graph */
                GraphUtils.markAllVerticesAsUnvisited(this.simpleGraph);
                
                /* find the next path which can accommodate the flow */
                stPath = GraphUtils.doDFS(this.source, this.sink, this.simpleGraph, delta);
            }
            
            GraphUtils.markAllVerticesAsUnvisited(this.simpleGraph);
            
            /* try finding a path which can push half as much as the original flow */
            delta /= 2;
        }
        
        /* return the max flow */
        return this.maxFlow;
    }
    
    /**
     * Update the flow of each edge incident on the found s-t path.
     *
     * @param sTPath - s-t path found by DFS which can accommodate the delta flow
     * @param increaseFlowBy - increase the flow by
     */
    private void increaseFlowThroughPath(final LinkedList<Vertex> sTPath, final int increaseFlowBy) {
        int i = 0;
        while (i < sTPath.size() - 1) {
            final Vertex currentVertex = sTPath.get(i);
            final Vertex nextVertex = sTPath.get(i + 1);
            
            final Iterator incidentEdges = this.simpleGraph.incidentEdges(currentVertex);
            
            /* iterate through all the edges and increment/decrement the flow by increaseFlowBy value */
            while (incidentEdges.hasNext()) {
                final Edge adjacent = (Edge) incidentEdges.next();
                final EdgeData currentEdge = (EdgeData) adjacent.getData();
                
                /* for forward edge - increment flow */
                if (adjacent.getSecondEndpoint() == nextVertex && isEquals(nextVertex, GraphUtils.FORWARD_EDGE)) {
                    currentEdge.setEdgeFlow(currentEdge.getEdgeFlow() + increaseFlowBy);
                    break;
                }
                /* for backward edge - decrement flow */
                else if (adjacent.getFirstEndpoint() == nextVertex && isEquals(nextVertex, GraphUtils.BACKWARD_EDGE)) {
                    currentEdge.setEdgeFlow(currentEdge.getEdgeFlow() - increaseFlowBy);
                }
            }
            i++;
        }
        
        /* increase the total maxFlow */
        this.maxFlow += increaseFlowBy;
    }
    
    /**
     * Compares the edgeDirection to the given vertex direction
     */
    private boolean isEquals(final Vertex nextVertex, final String edgeDirection) {
        return nextVertex.getData().equals(edgeDirection);
    }
    
    /**
     * Computes the max 2^x value which is <= maxCapacity
     *
     * @param maxCapacity - upperBound
     * @return 2^x <= maxCapacity
     */
    private int getDelta(final double maxCapacity) {
        return (int) Math.pow(2, (Math.log(maxCapacity) / Math.log(2)));
    }
}
