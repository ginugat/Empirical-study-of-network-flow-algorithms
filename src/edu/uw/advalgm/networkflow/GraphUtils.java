package edu.uw.advalgm.networkflow;

import java.util.Iterator;
import java.util.LinkedList;

import edu.uw.advalgm.graph.Edge;
import edu.uw.advalgm.graph.SimpleGraph;
import edu.uw.advalgm.graph.Vertex;

/**
 * Helper class which helps to perform several graph operations like DFS.
 */
public class GraphUtils {
    
    public static final String FORWARD_EDGE = "-->";
    public static final String BACKWARD_EDGE = "<--";
    
    /**
     * Transform edge{capacity} to edge{capacity, flow}
     *
     * @param simpleGraph - given graph
     */
    public static void transformEdgeToHaveCapacityAndFlow(final SimpleGraph simpleGraph) {
        final Iterator iterator = simpleGraph.edges();
        while (iterator.hasNext()) {
            final Edge edge = (Edge) iterator.next();
            if (edge.getData() instanceof Double) {
                edge.setData(new EdgeData((double) edge.getData()));
            }
        }
    }
    
    /**
     * Marks all the vertices in the given graph as unvisited
     *
     * @param simpleGraph - given graph
     */
    public static void markAllVerticesAsUnvisited(final SimpleGraph simpleGraph) {
        final Iterator iterator = simpleGraph.vertices();
        while (iterator.hasNext()) {
            final Vertex v = (Vertex) iterator.next();
            if (v.getData() != null) {
                v.setData(null);
            }
        }
    }
    
    /**
     * Given the source, sink and the overall graph, this method finds the s-t path through which the flow can be increased by the given value
     *
     * @param source - starting vertex
     * @param sink - end vertex
     * @param simpleGraph - given graph
     * @param increaseFlowBy - by how much value the flow has to be increased
     * @return - linkedList of nodes in the s-t path
     */
    public static LinkedList<Vertex> doDFS(final Vertex source, final Vertex sink, final SimpleGraph simpleGraph,
            final int increaseFlowBy) {
        /* holds the s_t_path */
        final LinkedList<Vertex> sTPath = new LinkedList<>();
        
        /* mark the source as visited and add it to the s_t_path */
        source.setData("+");
        sTPath.add(source);
        
        do {
            final Vertex currentVertex = sTPath.peekLast();
            
            final Iterator incidentEdges = simpleGraph.incidentEdges(currentVertex);
            
            /* iterate through all incident edges and see if we can increase the flow */
            while (incidentEdges.hasNext()) {
                final Edge adjacentEdge = (Edge) incidentEdges.next();
                final EdgeData edgeData = (EdgeData) adjacentEdge.getData();
                final Vertex secondEndpoint = adjacentEdge.getSecondEndpoint();
                
                final boolean canEdgeAccommodateMoreFlow = edgeData.getEdgeFlow() + increaseFlowBy <= edgeData.getEdgeCapacity();
                
                if (canEdgeAccommodateMoreFlow && secondEndpoint.getData() == null) {
                    secondEndpoint.setData(FORWARD_EDGE);
                    sTPath.add(secondEndpoint);
                    if (secondEndpoint == sink) {
                        return sTPath;
                    }
                    break;
                }
                /* backtrack */
                else if (increaseFlowBy <= edgeData.getEdgeFlow() && secondEndpoint == currentVertex) {
                    final Vertex firstEndpoint = adjacentEdge.getFirstEndpoint();
                    if (firstEndpoint.getData() == null) {
                        firstEndpoint.setData(BACKWARD_EDGE);
                        sTPath.add(firstEndpoint);
                        break;
                    }
                }
                
                /* if no further edge incident */
                if (!incidentEdges.hasNext()) {
                    sTPath.removeLast();
                }
            }
        } while (!sTPath.isEmpty());
        
        /* return the s-t path */
        return sTPath;
    }
}
