package org.ivis.layout.cose;

import org.ivis.layout.LGraphObject;

/**
 * This class implements Coarsening Graph specific data and functionality for edges.
 *
 * @author Alper Karacelik
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public class CoarseningEdge extends CoSEEdge
{
// -----------------------------------------------------------------------------
// Section: Constructors and initializations
// -----------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public CoarseningEdge(CoSENode source, CoSENode target, LGraphObject vEdge)
	{
		super(source, target, vEdge);
	}
	
	public CoarseningEdge()
	{
		this(null, null, null);
	}
}
