package org.ivis.layout.sbgn;

import org.ivis.layout.cose.CoSEEdge;

/**
 * This class implements SBGN Process Diagram specific data and functionality
 * for edges.
 * 
 * @author Begum Genc
 * 
 *         Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public class SbgnPDEdge extends CoSEEdge
{
	public int correspondingAngle;
	public boolean isProperlyOriented;

	/**
	 * Constructor
	 */
	public SbgnPDEdge(SbgnPDNode source, SbgnPDNode target, Object vEdge)
	{
		super(source, target, vEdge);
		correspondingAngle = 0;
		isProperlyOriented = false;
	}

	public SbgnPDEdge(SbgnPDNode source, SbgnPDNode target, Object vEdge, String type)
	{
		super(source, target, vEdge);
		this.type = type;
		correspondingAngle = 0;
		isProperlyOriented = false;
	}

	public void copy(SbgnPDEdge edge)
	{
		setSource(edge.getSource());
		setTarget(edge.getTarget());
		label = edge.label;
		type = edge.type;
		correspondingAngle = edge.correspondingAngle;
		isProperlyOriented = edge.isProperlyOriented;
		idealLength = edge.idealLength;
		isInterGraph = edge.isInterGraph;
		bendpoints = edge.bendpoints;
		isOverlapingSourceAndTarget = edge.isOverlapingSourceAndTarget;
		lca = edge.lca;
		length = edge.length;
		lengthX = edge.lengthX;
		lengthY = edge.lengthY;
		sourceInLca = edge.sourceInLca;
	}
	
	public boolean isEffector() {
		return SbgnPDConstants.MODULATION.equalsIgnoreCase(type) ||
				SbgnPDConstants.STIMULATION.equalsIgnoreCase(type) ||
				SbgnPDConstants.CATALYSIS.equalsIgnoreCase(type) ||
				SbgnPDConstants.INHIBITION.equalsIgnoreCase(type) ||
				SbgnPDConstants.NECESSARY_STIMULATION.equalsIgnoreCase(type);
	}
	
	public boolean isRigidEdge() {
		return SbgnPDConstants.RIGID_EDGE.equalsIgnoreCase(type);
	}
}
