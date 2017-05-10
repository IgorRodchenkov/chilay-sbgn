package org.ivis.layout;

import org.ivis.layout.cose.CoSEEdge;
import org.ivis.layout.cose.CoSENode;

/**
 * Created by igor on 10/05/17.
 */
public final class TestUtils {

    /**
     * This method checks if there is a node with null vGraphObject
     */
    public static boolean checkVGraphObjects(LGraphManager graphManager)
    {
        if (graphManager.getAllEdges() == null)
        {
            System.out.println("Edge list is null!");
        }
        if (graphManager.getAllNodes() == null)
        {
            System.out.println("Node list is null!");
        }
        if (graphManager.getGraphs() == null)
        {
            System.out.println("Graph list is null!");
        }
        for (Object obj: graphManager.getAllEdges())
        {
            CoSEEdge e = (CoSEEdge) obj;
            //NodeModel nm = (NodeModel) v.vGraphObject;

            if (e.vGraphObject == null)
            {
                System.out.println("Edge(Source): " + e.getSource().getRect() + " has null vGraphObject!");
                return false;
            }
        }

        for (Object obj: graphManager.getAllNodes())
        {
            CoSENode v = (CoSENode) obj;
            //NodeModel nm = (NodeModel) v.vGraphObject;

            if (v.vGraphObject == null)
            {
                System.out.println("Node: " + v.getRect() + " has null vGraphObject!");
                return false;
            }
        }

        for (Object obj: graphManager.getGraphs())
        {
            LGraph l = (LGraph) obj;
            if (l.vGraphObject == null)
            {
                System.out.println("Graph with " + l.getNodes().size() + " nodes has null vGraphObject!");
                return false;
            }
        }
        return true;
    }
}
