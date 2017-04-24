package org.ivis.layout;

import java.util.*;

/**
 * This class represents a graph manager (l-level) for layout purposes. A graph
 * manager maintains a collection of graphs, forming a compound graph structure
 * through inclusion, and maintains the inter-graph edges. You may refer to the
 * following article for technical details:
 * 		U. Dogrusoz and B. Genc, "A Multi-Graph Approach to Complexity
 * 		Management in Interactive Graph Visualization",
 * 		Computers &amp; Graphics, vol. 30/1, pp. 86-97, 2006.
 *
 * @author Erhan Giral
 * @author Ugur Dogrusoz
 * @author Cihan Kucukkececi
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public class LGraphManager
{
// -----------------------------------------------------------------------------
// Section: Instance variables
// -----------------------------------------------------------------------------
	/*
	 * Graphs maintained by this graph manager, including the root of the
	 * nesting hierarchy
	 */
	private List graphs;

	/*
	 * Inter-graph edges in this graph manager. Notice that all inter-graph
	 * edges go here, not in any of the edge lists of individual graphs (either
	 * source or target node's owner graph).
	 */
	private List edges;

	/*
	 * All nodes (excluding the root node) and edges (including inter-graph
	 * edges) in this graph manager. For efficiency purposes we hold references
	 * of all layout objects that we operate on in arrays. These lists are
	 * generated once we know that the topology of the graph manager is fixed,
	 * immediately before layout starts.
	 */
	private Object[] allNodes;
	private Object[] allEdges;

	/*
	 * Similarly we have a list of nodes for which gravitation should be
	 * applied. This is determined once, prior to layout, and used afterwards.
	 */
	private Object[] allNodesToApplyGravitation;

	/*
	 * The root of the inclusion/nesting hierarchy of this compound structure
	 */
	private LGraph rootGraph;

	/*
	 * Layout object using this graph manager
	 */
	private Layout layout;
	
	/*
	 * Cluster Manager of all graphs managed by this graph manager
	 */
	private ClusterManager clusterManager;
	
// -----------------------------------------------------------------------------
// Section: Constructors and initialization
// -----------------------------------------------------------------------------

	protected LGraphManager() {
		graphs = new ArrayList();
		edges = new ArrayList();
		allNodes = null;
		allEdges = null;
		allNodesToApplyGravitation = null;
		rootGraph = null;
		clusterManager = new ClusterManager();
		layout = null;
	}

	/**
	 * Constructor
	 * @param layout layout alg.
	 */
	public LGraphManager(Layout layout)
	{
		this();
		this.layout = layout;
	}

// -----------------------------------------------------------------------------
// Section: Topology related
// -----------------------------------------------------------------------------
	/**
	 * This method adds a new graph to this graph manager and sets as the root.
	 * It also creates the root graph as the parent of the root graph.
	 */
	public LGraph addRoot()
	{
		setRootGraph(add(layout.newGraph(null),
			layout.newNode(null)));
		
		return rootGraph;
	}

	/**
	 * This method adds the input graph into this graph manager. The new graph
	 * is associated as the child graph of the input parent node. If the parent
	 * node is null, then the graph is set to be the root.
	 */
	public LGraph add(LGraph newGraph, LNode parentNode)
	{
		assert (newGraph != null) : "Graph is null!";
		assert (parentNode != null) : "Parent node is null!";
		assert (!graphs.contains(newGraph)) :
			"Graph already in this graph mgr!";

		graphs.add(newGraph);

		assert (newGraph.parent == null) : "Already has a parent!";
		assert (parentNode.child == null) : "Already has a child!";
		newGraph.parent = parentNode;
		parentNode.child = newGraph;

		return newGraph;
	}

	/**
	 * This method adds the input edge between specified nodes into this graph
	 * manager. We assume both source and target nodes to be already in this
	 * graph manager.
	 */
	public LEdge add(LEdge newEdge, LNode sourceNode, LNode targetNode)
	{
		LGraph sourceGraph = sourceNode.getOwner();
		LGraph targetGraph = targetNode.getOwner();

		assert (sourceGraph != null && sourceGraph.getGraphManager() == this ) :
			"Source not in this graph mgr!";
		assert (targetGraph != null && targetGraph.getGraphManager() == this ) :
			"Target not in this graph mgr!";

		if (sourceGraph == targetGraph)
		{
			newEdge.isInterGraph = false;
			return sourceGraph.add(newEdge, sourceNode, targetNode);
		}
		else
		{
			newEdge.isInterGraph = true;

			// set source and target
			newEdge.source = sourceNode;
			newEdge.target = targetNode;

			// add edge to inter-graph edge list
			assert (!edges.contains(newEdge)) :
				"Edge already in inter-graph edge list!";
			edges.add(newEdge);

			// add edge to source and target incidency lists
			assert (newEdge.source != null && newEdge.target != null) :
				"Edge source and/or target is null!";
			assert (!newEdge.source.edges.contains(newEdge) &&
				!newEdge.target.edges.contains(newEdge)) :
					"Edge already in source and/or target incidency list!";

			newEdge.source.edges.add(newEdge);
			newEdge.target.edges.add(newEdge);

			return newEdge;
		}
	}

	/**
	 * This method removes the input graph from this graph manager. 
	 */
	public void remove(LGraph graph)
	{
		assert (graph.getGraphManager() == this) :
			"Graph not in this graph mgr";
		assert (graph == this.rootGraph ||
			(graph.parent != null && graph.parent.graphManager == this)) :
				"Invalid parent node!";

		// first the edges (make a copy to do it safely)
		List edgesToBeRemoved = new ArrayList();

		edgesToBeRemoved.addAll(graph.getEdges());

		LEdge edge;
		for (Object obj : edgesToBeRemoved)
		{
			edge = (LEdge) obj;
			graph.remove(edge);
		}

		// then the nodes (make a copy to do it safely)
		List nodesToBeRemoved = new ArrayList();

		nodesToBeRemoved.addAll(graph.getNodes());

		LNode node;
		for (Object obj : nodesToBeRemoved)
		{
			node = (LNode) obj;
			graph.remove(node);
		}

		// check if graph is the root
		if (graph == rootGraph)
		{
			setRootGraph(null);
		}

		// now remove the graph itself
		graphs.remove(graph);

		// also reset the parent of the graph
		graph.parent = null;
	}

	/**
	 * This method removes the input inter-graph edge from this graph manager.
	 */
	public void remove(LEdge edge)
	{
		assert (edge != null) : "Edge is null!";
		assert (edge.isInterGraph) : "Not an inter-graph edge!";
		assert (edge.source != null && edge.target != null) :
			"Source and/or target is null!";

		// remove edge from source and target nodes' incidency lists

		assert (edge.source.edges.contains(edge) &&
			edge.target.edges.contains(edge)) :
				"Source and/or target doesn't know this edge!";

		edge.source.edges.remove(edge);
		edge.target.edges.remove(edge);

		// remove edge from owner graph manager's inter-graph edge list

		assert (edge.source.owner != null &&
			edge.source.owner.getGraphManager() != null) :
			"Edge owner graph or owner graph manager is null!";
		assert (edge.source.owner.getGraphManager().edges.contains(edge)) :
			"Not in owner graph manager's edge list!";

		edge.source.owner.getGraphManager().edges.remove(edge);
	}

	/**
	 * This method calculates and updates the bounds of the root graph.
	 */
	public void updateBounds()
	{
		rootGraph.updateBounds(true);
	}
// -----------------------------------------------------------------------------
// Section: Accessors
// -----------------------------------------------------------------------------
	/**
	 * This method returns the cluster manager of all graphs managed by this
	 * graph manager.
	 */
	public ClusterManager getClusterManager()
	{
		return clusterManager;
	}
	
	/**
	 * This method retuns the list of all graphs managed by this graph manager.
	 */
	public List getGraphs()
	{
		return graphs;
	}

	/**
	 * This method returns the list of all inter-graph edges in this graph
	 * manager.
	 */
	public List getInterGraphEdges()
	{
		return edges;
	}

	/**
	 * This method returns the list of all nodes in this graph manager. This
	 * list is populated on demand and should only be called once the topology
	 * of this graph manager has been formed and known to be fixed.
	 */
	public Object[] getAllNodes()
	{
		if (allNodes == null)
		{
			LinkedList nodeList = new LinkedList();

			for (Iterator iterator = getGraphs().iterator();
				 iterator.hasNext();)
			{
				nodeList.addAll(((LGraph) iterator.next()).getNodes());
			}

			allNodes = nodeList.toArray();
		}

		return allNodes;
	}

	/**
	 * This method nulls the all nodes array so that it gets re-calculated with
	 * the next invocation of the accessor. Needed when topology changes.
	 */
	public void resetAllNodes()
	{
		allNodes = null;
	}

	/**
	 * This method nulls the all edges array so that it gets re-calculated with
	 * the next invocation of the accessor. Needed when topology changes. 
	 */
	public void resetAllEdges()
	{
		allEdges = null;
	}
	
	/**
	 * This method nulls the all nodes to apply gravition array so that it gets 
	 * re-calculated with the next invocation of the accessor. Needed when
	 * topology changes. 
	 */
	public void resetAllNodesToApplyGravitation()
	{
		allNodesToApplyGravitation = null;
	}
	
	/**
	 * This method returns the list of all edges (including inter-graph edges)
	 * in this graph manager. This list is populated on demand and should only
	 * be called once the topology of this graph manager has been formed and
	 * known to be fixed.
	 */
	public Object[] getAllEdges()
	{
		if (allEdges == null)
		{
			LinkedList edgeList = new LinkedList();

			for (Iterator iterator = getGraphs().iterator();
				 iterator.hasNext();)
			{
				edgeList.addAll(((LGraph) iterator.next()).getEdges());
			}

			edgeList.addAll(edges);
			
			allEdges = edgeList.toArray();
		}

		return allEdges;
	}

	/**
	 * This method returns the array of all nodes to which gravitation should be
	 * applied.
	 */
	public Object[] getAllNodesToApplyGravitation()
	{
		return allNodesToApplyGravitation;
	}

	/**
	 * This method sets the array of all nodes to which gravitation should be
	 * applied from the input list.
	 */
	public void setAllNodesToApplyGravitation(List nodeList)
	{
		assert allNodesToApplyGravitation == null;

		allNodesToApplyGravitation = nodeList.toArray();
	}

	/**
	 * This method sets the array of all nodes to which gravitation should be
	 * applied from the input array.
	 */
	public void setAllNodesToApplyGravitation(Object [] nodes)
	{
		assert allNodesToApplyGravitation == null;

		allNodesToApplyGravitation = nodes;
	}

	/**
	 * This method returns the root graph (root of the nesting hierarchy) of
	 * this graph manager. Nesting relations must form a tree.
	 */
	public LGraph getRoot()
	{
		return rootGraph;
	}

	/**
	 * This method sets the root graph (root of the nesting hierarchy) of this
	 * graph manager. Nesting relations must form a tree.
	 * @param graph
	 */
	public void setRootGraph(LGraph graph)
	{
		assert (graph.getGraphManager() == this) : "Root not in this graph mgr!";

		rootGraph = graph;

		// root graph must have a root node associated with it for convenience
		if (graph.parent == null)
		{
			graph.parent = layout.newNode("Root node");
		}
	}

	/**
	 * This method returns the associated layout object, which operates on this
	 * graph manager.
	 */
	public Layout getLayout()
	{
		return layout;
	}

	/**
	 * This method sets the associated layout object, which operates on this
	 * graph manager.
	 */
	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

// -----------------------------------------------------------------------------
// Section: Remaining methods
// -----------------------------------------------------------------------------
	/**
	 * This method checks whether one of the input nodes is an ancestor of the
	 * other one (and vice versa) in the nesting tree. Such pairs of nodes
	 * should not be allowed to be joined by edges.
	 */
	public static boolean isOneAncestorOfOther(LNode firstNode,
		LNode secondNode)
	{
		assert firstNode != null && secondNode != null;

		if (firstNode == secondNode)
		{
			return true;
		}

		// Is second node an ancestor of the first one?

		LGraph ownerGraph = firstNode.getOwner();
		LNode parentNode;

		do
		{
			parentNode = ownerGraph.getParent();

			if (parentNode == null)
			{
				break;
			}

			if (parentNode == secondNode)
			{
				return true;
			}

			ownerGraph = parentNode.getOwner();
			if(ownerGraph == null)
			{
				break;
			}
		} while (true);

		// Is first node an ancestor of the second one?

		ownerGraph = secondNode.getOwner();

		do
		{
			parentNode = ownerGraph.getParent();

			if (parentNode == null)
			{
				break;
			}

			if (parentNode == firstNode)
			{
				return true;
			}

			ownerGraph = parentNode.getOwner();
			if(ownerGraph == null)
			{
				break;
			}
		} while (true);

		return false;
	}
	
	/**
	 * This method calculates the lowest common ancestor of each edge.
	 */
	public void calcLowestCommonAncestors()
	{
		LEdge edge;
		LNode sourceNode;
		LNode targetNode;
		LGraph sourceAncestorGraph;
		LGraph targetAncestorGraph;

		for (Object obj : getAllEdges())
		{
			edge = (LEdge)obj;

			sourceNode = edge.source;
			targetNode = edge.target;
			edge.lca =  null;
			edge.sourceInLca = sourceNode;
			edge.targetInLca = targetNode;

			if (sourceNode == targetNode)
			{
				edge.lca = sourceNode.getOwner();
				continue;
			}

			sourceAncestorGraph = sourceNode.getOwner();

			while (edge.lca == null)
			{
				targetAncestorGraph = targetNode.getOwner();

				while (edge.lca == null)
				{
					if (targetAncestorGraph == sourceAncestorGraph)
					{
						edge.lca = targetAncestorGraph;
						break;
					}

					if (targetAncestorGraph == rootGraph)
					{
						break;
					}

					assert edge.lca == null;
					edge.targetInLca = targetAncestorGraph.getParent();
					targetAncestorGraph = edge.targetInLca.getOwner();
				}

				if (sourceAncestorGraph == rootGraph)
				{
					break;
				}

				if (edge.lca == null)
				{
					edge.sourceInLca = sourceAncestorGraph.getParent();
					sourceAncestorGraph = edge.sourceInLca.getOwner();
				}
			}

			assert edge.lca != null;
		}
	}

	/**
	 * This method finds the lowest common ancestor of given two nodes.
	 * 
	 * @param firstNode
	 * @param secondNode
	 * @return lowest common ancestor
	 */
	public LGraph calcLowestCommonAncestor(LNode firstNode, LNode secondNode)
	{
		if (firstNode == secondNode)
		{
			return firstNode.getOwner();
		}

		LGraph firstOwnerGraph = firstNode.getOwner();

		do
		{
			if (firstOwnerGraph == null)
			{
				break;
			}

			LGraph secondOwnerGraph = secondNode.getOwner();
		
			do
			{			
				if (secondOwnerGraph == null)
				{
					break;
				}

				if (secondOwnerGraph == firstOwnerGraph)
				{
					return secondOwnerGraph;
				}
				
				secondOwnerGraph = secondOwnerGraph.getParent().getOwner();
			} while (true);

			firstOwnerGraph = firstOwnerGraph.getParent().getOwner();
		} while (true);

		return firstOwnerGraph;
	}

	/**
	 * This method calculates depth of each node in the inclusion tree (nesting
	 * hierarchy).
	 */
	public void calcInclusionTreeDepths()
	{
		calcInclusionTreeDepths(rootGraph, 1);
	}
	
	/*
	 * Auxiliary method for calculating depths of nodes in the inclusion tree.
	 */
	private void calcInclusionTreeDepths(LGraph graph, int depth)
	{
		LNode node;

		for (Object obj : graph.getNodes())
		{
			node = (LNode) obj;

			node.inclusionTreeDepth = depth;

			if (node.child != null)
			{
				calcInclusionTreeDepths(node.child, depth + 1);
			}
		}
	}
	
	
	public boolean includesInvalidEdge()
	{
		LEdge edge;
		
		for (Object obj : edges)
		{
			edge = (LEdge) obj;
			
			if (isOneAncestorOfOther(edge.source, edge.target))
			{
				return true;
			}
		}
		return false;
	}
	
// -----------------------------------------------------------------------------
// Section: Testing methods
// -----------------------------------------------------------------------------
	/**
	 * This method prints the topology of this graph manager.
	 */
	void printTopology()
	{
		rootGraph.printTopology();

		LGraph graph;

		for (Object obj : graphs)
		{
			graph = (LGraph) obj;

			if (graph != rootGraph)
			{
				graph.printTopology();
			}
		}

		System.out.print("Inter-graph edges:");
		LEdge edge;

		for (Object obj : edges)
		{
			edge = (LEdge) obj;

			edge.printTopology();
		}

		System.out.println();
		System.out.println();
	}
}