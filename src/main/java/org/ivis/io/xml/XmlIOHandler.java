package org.ivis.io.xml;

import java.io.InputStream;
import java.util.HashMap;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.ivis.io.xml.model.EdgeComplexType;
import org.ivis.io.xml.model.EdgeComplexType.BendPointList.BendPoint;
import org.ivis.io.xml.model.GraphObjectComplexType;
import org.ivis.io.xml.model.NodeComplexType;
import org.ivis.io.xml.model.NodeComplexType.Bounds;
import org.ivis.io.xml.model.ObjectFactory;
import org.ivis.io.xml.model.View;
import org.ivis.layout.LEdge;
import org.ivis.layout.LGraph;
import org.ivis.layout.LGraphManager;
import org.ivis.layout.LGraphObject;
import org.ivis.layout.LNode;
import org.ivis.layout.Layout;
import org.ivis.util.PointD;

/**
 * TODO: remove this (not used) class
 * @deprecated
 */
public class XmlIOHandler
{
	/*
	 * Layout object needed to run layout
	 */
	private Layout layout;

	/*
	 * Graph manager of the layout object
	 */
	private LGraphManager gm;

	/*
	 * Root graph of the graph manager associated with the layout object
	 */
	private LGraph rootGraph;

	/*
	 * Mapping between xml objects and L-level objects.
	 */
	private HashMap<GraphObjectComplexType, LGraphObject> xmlObjectToLayoutObject;
	private HashMap<String, GraphObjectComplexType> xmlIDToXMLObject;

	private JAXBContext jaxbContext;

	private ObjectFactory objectFactory;

	private View loadedModel;

	/*
	 * Constructor
	 */
	public XmlIOHandler(Layout layout) throws JAXBException
	{
		this.xmlObjectToLayoutObject = new HashMap<GraphObjectComplexType, LGraphObject>();
		this.xmlIDToXMLObject = new HashMap<String, GraphObjectComplexType>();

		this.jaxbContext = JAXBContext.newInstance("org.ivis.io.xml.model");
		this.objectFactory = new ObjectFactory();

		this.layout = layout;

		// create initial topology: a graph manager associated with this layout,
		// containing an empty root graph as its only graph

		this.gm = this.layout.getGraphManager();
		this.rootGraph = this.gm.addRoot();
	}

	/**
	 * This method reads the xml file from the given input stream and populates
	 * given l-level model.
	 * 
	 * @param inputStream
	 * @throws JAXBException
	 * @throws OperationNotSupportedException
	 */
	public void fromXML(InputStream inputStream) throws JAXBException,
			OperationNotSupportedException
	{
		Unmarshaller unmarshaller = this.jaxbContext.createUnmarshaller();

		this.loadedModel = (View) unmarshaller.unmarshal(inputStream);

		for (NodeComplexType nodeType : this.loadedModel.getNode())
		{
			parseNode(nodeType, null);
		}

		for (EdgeComplexType edgeType : this.loadedModel.getEdge())
		{
			parseEdge(edgeType);
		}
	}

	/**
	 * This method processes each node from XML file. It basically creates an
	 * associated l-level node and copies its geometry information. If the node
	 * has children, it recursively parses those children.
	 */
	private void parseNode(NodeComplexType xmlNode,
			NodeComplexType parentXmlNode)
			throws OperationNotSupportedException
	{
		LNode lNode = this.layout.newNode(null);
		this.xmlObjectToLayoutObject.put(xmlNode, lNode);
		this.setIdIndex(xmlNode);

		// Locate newly created node.
		if (parentXmlNode != null)
		{
			LNode parentLNode = (LNode) this.xmlObjectToLayoutObject
					.get(parentXmlNode);
			assert parentLNode.getChild() != null : "Parent node doesn't have child graph.";
			parentLNode.getChild().add(lNode);
		}
		else
		{
			this.rootGraph.add(lNode);
		}

		// Copy geometry
		Bounds bounds = xmlNode.getBounds();
		lNode.setLocation(bounds.getX(), bounds.getY());
		lNode.setWidth(bounds.getWidth());
		lNode.setHeight(bounds.getHeight());
		lNode.type = xmlNode.getType().getValue();
		lNode.label = xmlNode.getId();

		// Copy cluster IDs
		if (xmlNode.getClusterIDs() != null)
		{
			for (String id : xmlNode.getClusterIDs().getClusterID())
			{
				lNode.addCluster(Integer.parseInt(id));
			}
		}

		// If it has children, go and recursively parse them.
		if (xmlNode.getChildren() != null)
		{
			LGraph childGraph = this.gm.add(this.layout.newGraph(null), lNode);

			for (NodeComplexType childNode : xmlNode.getChildren().getNode())
			{
				parseNode(childNode, xmlNode);
			}
		}
	}

	/**
	 * This method process each edge from XML file. It mainly creates an
	 * associated l-level edge with the proper source and target.
	 */
	private void parseEdge(EdgeComplexType xmlEdge)
			throws OperationNotSupportedException
	{
		LEdge lEdge = this.layout.newEdge(null);

		lEdge.type = xmlEdge.getType().getValue();
		lEdge.label = xmlEdge.getId();

		// Find source and target nodes
		String sourceXmlNodeId = xmlEdge.getSourceNode().getId();
		String targetXmlNodeId = xmlEdge.getTargetNode().getId();
		LNode sourceLNode = (LNode) this.xmlObjectToLayoutObject
				.get(this.xmlIDToXMLObject.get(sourceXmlNodeId));
		LNode targetLNode = (LNode) this.xmlObjectToLayoutObject
				.get(this.xmlIDToXMLObject.get(targetXmlNodeId));

		// Throw exception if referenced nodes does not exist.
		if (sourceLNode == null)
		{
			throw new OperationNotSupportedException("Source node with the "
					+ "given ID <" + sourceXmlNodeId
					+ "> does not exist for edge " + "with ID <"
					+ xmlEdge.getId() + ">");
		}
		if (targetLNode == null)
		{
			throw new OperationNotSupportedException("Source node with the "
					+ "given ID <" + targetXmlNodeId
					+ "> does not exist for edge " + "with ID <"
					+ xmlEdge.getId() + ">");
		}

		this.gm.add(lEdge, sourceLNode, targetLNode);
		this.xmlObjectToLayoutObject.put(xmlEdge, lEdge);

		// TODO: Edges are not currently forced to have unique id s. However
		// we might force them to have so in the future
		// this.setIdIndex(xmlEdge);

		// Copy bend points, if any.
		if (xmlEdge.getBendPointList() != null)
		{
			for (BendPoint xmlBendPoint : xmlEdge.getBendPointList()
					.getBendPoint())
			{
				PointD bendPoint = new PointD(xmlBendPoint.getX(),
						xmlBendPoint.getY());
				lEdge.getBendpoints().add(bendPoint);
			}
		}
	}


	/**
	 * This method build the index to xml object mapping by ensuring that the
	 * given xml object has unique id.
	 * 
	 * @param xmlGraphObject
	 * @throws OperationNotSupportedException
	 */
	private void setIdIndex(GraphObjectComplexType xmlGraphObject)
			throws OperationNotSupportedException
	{
		String id = xmlGraphObject.getId();

		// Check id, if it is used before throw an exception, otherwise index it
		if (this.xmlIDToXMLObject.get(id) != null)
		{
			throw new OperationNotSupportedException("The ID:" + id
					+ " is used" + " more than one time");
		}
		else
		{
			this.xmlIDToXMLObject.put(id, xmlGraphObject);
		}
	}

}