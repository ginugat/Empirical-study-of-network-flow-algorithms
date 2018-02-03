package edu.uw.advalgm.networkflow;

/**
 * Edge data contains the edge capacity, edge flow and also has information about whether the edge is back edge or forward edge. This will be used to
 * set {@code Edge.setData(Object)}
 * 
 * @author Chaitra SG
 */
public class EdgeData {
    
    private double edgeCapacity;
    private double edgeFlow;
    private boolean isBackedge;
    
    public EdgeData() {
        
    }
    
    public EdgeData(double edgeCapacity) {
        this.edgeCapacity = edgeCapacity;
    }
    
    /**
     * @return the edgeCapacity
     */
    public double getEdgeCapacity() {
        return edgeCapacity;
    }
    
    /**
     * @param edgeCapacity the edgeCapacity to set
     */
    public void setEdgeCapacity(double edgeCapacity) {
        this.edgeCapacity = edgeCapacity;
    }
    
    /**
     * @return the edgeFlow
     */
    public double getEdgeFlow() {
        return edgeFlow;
    }
    
    /**
     * @param edgeFlow the edgeFlow to set
     */
    public void setEdgeFlow(double edgeFlow) {
        this.edgeFlow = edgeFlow;
    }
    
    /**
     * @return the isBackedge
     */
    public boolean isBackedge() {
        return isBackedge;
    }
    
    /**
     * @param isBackedge the isBackedge to set
     */
    public void setBackedge(boolean isBackedge) {
        this.isBackedge = isBackedge;
    }
    
}
