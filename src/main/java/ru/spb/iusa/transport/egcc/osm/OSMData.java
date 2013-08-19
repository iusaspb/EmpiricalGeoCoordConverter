package ru.spb.iusa.transport.egcc.osm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
/**
 * A very basic class to handle OSM datafile
 * @author YSA
 *
 */
public class OSMData extends DefaultHandler {
	
	private static final String OSMPrefix = "<?xml version='1.0' encoding='UTF-8'?><osm version='0.6' upload='false' date='%s' generator='XXX'>";
	private static final String OSMNodeNoTagTemplate = "<node id='%d' visible='true' lat='%f' lon='%f'/>\n";
	private static final String OSMNodeTagStartTemplate = "<node id='%d' visible='true' lat='%f' lon='%f'>\n";
	private static final String OSMNodeTagTemplate = "<tag k=\"%s\" v=\"%s\"/>\n";
	private static final String OSMNodeTagEndTemplate = "</node>\n";
	private static final String OSMPostfix = "</osm>";
	
	private static final String EL_WAY = "way";
	private static final String EL_WAY_NODE = "nd";
	private static final String EL_NODE = "node";
	
	private Map<Long,OSMNode> nodes;
	
	private List<OSMWay> ways;
	
	private OSMWay currentWay;
	
	public OSMData() {}
	
	@Override
    public void startDocument ()	throws SAXException    {
		
		nodes = new HashMap<Long,OSMNode>();
		
		ways = new LinkedList<OSMWay>();
		
    }
	
	@Override
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
		
		if (EL_WAY.equals(localName) ){
			
			if (currentWay != null) throw new IllegalStateException("currentWay != null");
			
			String wayId = atts.getValue("id");
			
			currentWay = new OSMWay(Long.parseLong(wayId));
			
			ways.add(currentWay);
			
		}else if (EL_WAY_NODE.equals(localName) ){
			
			String wayNodeId = atts.getValue("ref");
			
			OSMNode node = nodes.get(Long.parseLong(wayNodeId));
			
			if (node == null) throw new IllegalStateException("Unknown node ref with id="+wayNodeId);
			
			if (currentWay == null) throw new IllegalStateException("currentWay == null");  
			
			currentWay.addNode(node);
			
		} else if (EL_NODE.equals(localName) ){
			
			String strNodeId = atts.getValue("id");
			
			String strLat = atts.getValue("lat"), strLon = atts.getValue("lon");
			
			OSMNode node = new OSMNode(Long.parseLong(strNodeId),Double.parseDouble(strLat),Double.parseDouble(strLon));
			
			nodes.put(node.getId(), node);
			
			
		}
		
	}
	
	@Override
    public void endElement (String uri, String localName, String qName) {
		
		if (EL_WAY.equals(localName) ) {
			
			currentWay = null;
			
		}		
		
	}
	
	
	@Override
    public void endDocument ()	throws SAXException    {
		
    }

	public List<OSMWay> getWays() {
		return ways;
	}

	public Collection<OSMNode> getNodes() {
		
		return nodes.values();
		
	}
	
	public OSMNode getNodeById(Long id) {
		
		return nodes.get(id);
		
	}
	/**
	 * 
	 * Find max wayId and set new wayId as a difference between maxWayId and the init wayId.
	 * 
	 * Then make the same procedure with nodes.
	 * 
	 */
	public void normalize(){
		
		//
		// ways
		//
		
		long baseWayId = - Long.MAX_VALUE;
		
		for (OSMWay way : ways) baseWayId = Math.max(baseWayId, way.getId());
		
		for (OSMWay way : ways) way.setId(baseWayId- way.getId());
		
		//
		// nodes
		//
		
		long baseNodeId = - Long.MAX_VALUE;
		
		for (OSMNode node : nodes.values()) baseNodeId = Math.max(baseNodeId, node.getId());
		
		Map<Long,OSMNode> _nodes = new HashMap<Long,OSMNode> ();
		
		for (OSMNode node : nodes.values()) {
			
			node.setId(baseNodeId- node.getId());
			
			_nodes.put(node.getId(), node);
			
		}
		
		nodes.clear();
		
		nodes = _nodes;
		
	}
	
	/**
	 * 
	 * Read OSM data from file.
	 * 
	 * @param fileName
	 * @return true if reading is successful 
	 */
	public boolean read(String fileName) {
		
		boolean res = false;
		
		try {
		
			 InputStream  osmFileStream = new FileInputStream(fileName);
			
		     SAXParserFactory factory = SAXParserFactory.newInstance();

		     SAXParser saxParser = factory.newSAXParser();
		     
		     XMLReader reader = saxParser.getXMLReader();
		     
		     reader.setFeature("http://xml.org/sax/features/namespaces", true);
		      
		     reader.setContentHandler(this);
		     
		     reader.parse(new InputSource(osmFileStream));
		      
		     osmFileStream.close();
			
		     res = true;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return res;
		
	}
	
	/**
	 * Write nodes into file
	 * @param fileName
	 * @param nodes
	 * @return  true if writing is successful
	 */
	public boolean write(String fileName, Collection<OSMNode> nodes) {
		
        try {
			PrintWriter out = new PrintWriter(fileName,"UTF-8");
			
			out.println(OSMPrefix);
			
			for (OSMNode  node :  nodes) {
				
				if (node.areTagsExist()) {
					
					out.format((Locale)null,OSMNodeTagStartTemplate,node.getInitId(),node.getLat(),node.getLon());
					
					for (Entry<String, Object> tag : node.getTags().entrySet() ) {
						
						out.format((Locale)null,OSMNodeTagTemplate,tag.getKey(),tag.getValue());
						
					}
					
					out.print(OSMNodeTagEndTemplate);
					
				} else {
					
					out.format((Locale)null,OSMNodeNoTagTemplate,node.getInitId(),node.getLat(),node.getLon());
					
				}
			}
			
			out.println(OSMPostfix);
			
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return true;
	}


}
