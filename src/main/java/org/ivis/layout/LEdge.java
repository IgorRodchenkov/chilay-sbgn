package org.ivis.layout;

import java.util.*;

import org.ivis.util.*;

/**
 * This class represents an edge (l-level) for layout purposes.
 *
 * @author Erhan Giral
 * @author Ugur Dogrusoz
 * @author Cihan Kucukkececi
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public class LEdge extends LGraphObject
{
// -----------------------------------------------------------------------------
// Section: Instance variables
// -----------------------------------------------------------------------------
	/*
	 * Source and target nodes of this edge
	 */
	protected LNode source;
	protected LNode target;

	/*
	 * Whether this edge is an intergraph one
	 */
	protected boolean isInterGraph;

	/*
	 * The length of this edge ( l = sqrt(x^2 + y^2) )
	 */
	protected double length;
	protected double lengthX;
	protected double lengthY;

	/*
	 * Whether source and target node rectangles intersect, requiring special
	 * treatment
	 */
	protected boolean isOverlapingSourceAndTarget = false;

	/*
	 * Bend points for this edge
	 */
	protected List<PointD> bendpoints;

	/*
	 * Lowest common ancestor graph (lca), and source and target nodes in lca
	 */
	protected LGraph lca;
	protected LNode sourceInLca;
	protected LNode targetInLca;

// -----------------------------------------------------------------------------
// Section: Constructors and initializations
// -----------------------------------------------------------------------------
	/**
	 * Non-public Constructor.
	 *
	 * @param source source node
	 * @param target target node
	 * @param vEdge edge
	 */
	protected LEdge(LNode source, LNode target, Object vEdge)
	{
		super(vEdge);
		this.bendpoints = new ArrayList();
		this.source = source;
		this.target = target;
	}

// -----------------------------------------------------------------------------
// Section: Accessors
// -----------------------------------------------------------------------------
	/**
	 * This method returns the source node of this edge.
	 * @return source node
	 */
	public LNode getSource()
	{
		return source;
	}

	/**
	 * This method sets the source node of this edge.
	 * @param source source node
	 */
	public void setSource(LNode source)
	{
		this.source = source;
	}

	/**
	 * This method returns the target node of this edge.
	 * @return target node
	 */
	public LNode getTarget()
	{
		return target;
	}

	/**
	 * This method sets the target node of this edge.
	 * @param target target node
	 */
	public void setTarget(LNode target)
	{
		this.target = target;
	}

	/**
	 * This method returns whether or not this edge is an inter-graph edge.
	 * @return true/false - whether this is an inter-graph edge or not
	 */
	public boolean isInterGraph()
	{
		return isInterGraph;
	}

	/**
	 * This method returns the length of this edge. Note that this value might
	 * be out-dated at times during a layout operation.
	 * @return edge's length
	 */
	public double getLength()
	{
		return length;
	}

	/**
	 * This method returns the x component of the length of this edge. Note that
	 * this value might be out-dated at times during a layout operation.
	 * @return length along x axis
	 */
	public double getLengthX()
	{
		return lengthX;
	}

	/**
	 * This method returns the y component of the length of this edge. Note that
	 * this value might be out-dated at times during a layout operation.
	 * @return length along y axis
	 */
	public double getLengthY()
	{
		return lengthY;
	}

	/**
	 * This method returns whether or not this edge has overlapping source and
	 * target.
	 *
	 * @return true when this edge has overlapping source and target; false otherwise
	 */
	public boolean isOverlapingSourceAndTarget()
	{
		return isOverlapingSourceAndTarget;
	}

	/**
	 * This method resets the overlapping source and target status of this edge.
	 */
	public void resetOverlapingSourceAndTarget()
	{
		isOverlapingSourceAndTarget = false;
	}

	/**
	 * This method returns the list of bend points of this edge.
	 * @return list of points
	 */
	public List<PointD> getBendpoints()
	{
		return bendpoints;
	}

	/**
	 * This method clears all existing bendpoints and sets given bendpoints as 
	 * the new ones.
	 * @param bendPoints list of bend points
	 */
	public void reRoute(List<PointD> bendPoints)
	{
		this.bendpoints.clear();
		
		this.bendpoints.addAll(bendPoints);
	}

	public LGraph getLca()
	{
		return lca;
	}

	public LNode getSourceInLca()
	{
		return sourceInLca;
	}

	public LNode getTargetInLca()
	{
		return targetInLca;
	}

// -----------------------------------------------------------------------------
// Section: Remaining methods
// -----------------------------------------------------------------------------
	/**
	 * This method returns the end of this edge different from the input one.
	 * @param node node
	 * @return end node
	 */
	public LNode getOtherEnd(LNode node)
	{
		if (source.equals(node))
		{
			return target;
		}
		else if (target.equals(node))
		{
			return source;
		}
		else
		{
			throw new IllegalArgumentException(
				"Node is not incident " + "with this edge");
		}
	}

	/**
	 * This method finds the other end for this node, and returns its ancestor
	 * node, possibly the other end node itself, that is in the input graph. It
	 * returns null if none of its ancestors is in the input graph.
	 *
	 * @param node node
	 * @param graph graph
	 *
	 * @return the other end node
	 */
	public LNode getOtherEndInGraph(LNode node, LGraph graph)
	{
		LNode otherEnd = getOtherEnd(node);
		LGraph root = graph.getGraphManager().getRoot();

		while (true)
		{
			if (otherEnd.getOwner() == graph)
			{
				return otherEnd;
			}

			if (otherEnd.getOwner() == root)
			{
				break;
			}

			otherEnd = otherEnd.getOwner().getParent();
		}

		return null;
	}

	/**
	 * This method updates the length of this edge as well as whether or not the
	 * rectangles representing the geometry of its end nodes overlap.
	 */
	public void updateLength()
	{
		double[] clipPointCoordinates = new double[4];

		isOverlapingSourceAndTarget =
			IGeometry.getIntersection(target.getRect(),
				source.getRect(),
				clipPointCoordinates);

		if (!isOverlapingSourceAndTarget)
		{
			// target clip point minus source clip point gives us length

			lengthX = clipPointCoordinates[0] - clipPointCoordinates[2];
			lengthY = clipPointCoordinates[1] - clipPointCoordinates[3];

			if (Math.abs(lengthX) < 1.0)
			{
				lengthX = IMath.sign(lengthX);
			}

			if (Math.abs(lengthY) < 1.0)
			{
				lengthY = IMath.sign(lengthY);
			}

			length = Math.sqrt(
				lengthX * lengthX + lengthY * lengthY);
		}
	}

	/**
	 * This method updates the length of this edge using the end nodes centers
	 * as opposed to clipping points to simplify calculations involved.
	 */
	public void updateLengthSimple()
	{
		// target center minus source center gives us length

		lengthX = target.getCenterX() - source.getCenterX();
		lengthY = target.getCenterY() - source.getCenterY();

		if (Math.abs(lengthX) < 1.0)
		{
			lengthX = IMath.sign(lengthX);
		}

		if (Math.abs(lengthY) < 1.0)
		{
			lengthY = IMath.sign(lengthY);
		}

		length = Math.sqrt(
			lengthX * lengthX + lengthY * lengthY);
	}

// -----------------------------------------------------------------------------
// Section: Testing methods
// -----------------------------------------------------------------------------
	/**
	 * This method prints the topology of this edge.
	 */
	void printTopology()
	{
		System.out.print( (label == null ? "?" : label) + "[" +
			(source.label == null ? "?" : source.label) + "-" +
			(target.label == null ? "?" : target.label) + "] ");
	}
}