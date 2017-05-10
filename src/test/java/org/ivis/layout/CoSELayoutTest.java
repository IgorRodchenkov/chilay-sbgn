package org.ivis.layout;

import org.ivis.layout.cose.CoSEEdge;
import org.ivis.layout.cose.CoSELayout;
import org.ivis.layout.cose.CoSENode;
import org.junit.Test;

/**
 * CoSELayout tester
 *
 * @author Ugur Dogrusoz
 */
//@Ignore
public class CoSELayoutTest
{

	@Test
	public void testLayout() throws Exception
	{
		Layout layout = new CoSELayout();
		LGraphManager gm = layout.getGraphManager();
		LGraph g1 = gm.addRoot();
		LNode n1 = g1.add(layout.newNode("n1"));
		LNode n2 = g1.add(layout.newNode("n2"));
		LGraph g2 = gm.add(layout.newGraph("G2"), n2);
		LNode n3 = g2.add(layout.newNode("n3"));
		LNode n4 = g2.add(layout.newNode("n4"));
		LEdge e1 = gm.add(layout.newEdge("e1-2"), n1, n2);
		LEdge e2 = gm.add(layout.newEdge("e1-3"), n1, n3);
		LGraph g3 = layout.newGraph("G3");
		gm.add(g3, n3);
		LNode n5 = g3.add(layout.newNode("n5"));
		LNode n6 = g3.add(layout.newNode("n6"));
		LEdge e3 = g3.add(layout.newEdge("e5-6"), n5, n6);
		LEdge e4 = gm.add(layout.newEdge("e4-6"), n4, n6);

		layout.runLayout();
	}
}