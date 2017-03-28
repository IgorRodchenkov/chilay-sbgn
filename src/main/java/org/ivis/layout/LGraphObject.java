package org.ivis.layout;

/**
 * This class implements a base class for l-level graph objects (nodes, edges,
 * and graphs).
 *
 * @author: Ugur Dogrusoz
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public class LGraphObject
{
// -----------------------------------------------------------------------------
// Section: Instance variables
// -----------------------------------------------------------------------------
	/**
	 * Associated view object
	 */
	public LGraphObject vGraphObject;

	/**
	 * Label
	 */
	public String label;
	
	/**
	 * Associated type
	 */
	public String type;
	

	public LGraphObject(LGraphObject vGraphObject)
	{
		this.vGraphObject = vGraphObject;
	}
	public LGraphObject(String label)
	{
		this.label = label;
	} //for tests...

	public String toString() {
		return label;
	}
}