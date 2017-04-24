package org.ivis.layout;

/**
 * This interface is used for clustering purposes. Any class that
 * implements this interface may be used as a cluster object. The main purpose
 * of this interface is to easily map LNode and NodeModel among each other,
 * for layout purposes.
 *
 * @author Shatlyk Ashyralyyev
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public interface Clustered {
	/**
	 * This method add this node model into a cluster with given cluster ID. If
	 * such cluster doesn't exist in ClusterManager, it creates a new cluster.
	 * @param clusterID id
	 */
	void addCluster(int clusterID);
	
	/**
	 * This method adds a new cluster into clustered object's clusters
	 * @param cluster cluster
	 */
	void addCluster(Cluster cluster);
	
	/**
	 * This method removes the cluster from clustered object's clusters
	 * @param cluster cluster
	 */
	void removeCluster(Cluster cluster);
	
	/**
	 * This method resets all clusters of the clustered object
	 */
	void resetClusters();

	/**
	 * This method returns the left of this node.
	 * @return left
	 */
	double getLeft();
	
	/**
	 * This method returns the right of this node.
	 * @return right
	 */
	double getRight();
	
	/**
	 * This method returns the top of this node.
	 * @return top
	 */
	double getTop();

	/**
	 * This method returns the bottom of this node.
	 * @return bottom
	 */
	double getBottom();
	
	/**
	 * This method returns the parent of clustered object.
	 * If it is a root object, then null should be returned.
	 *
	 * @return parent
	 */
	Clustered getParent();
}
