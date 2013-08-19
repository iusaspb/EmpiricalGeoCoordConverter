package ru.spb.iusa.transport.egcc.osm;

import java.util.LinkedList;
import java.util.List;
/**
 * 
 * A very basic class to handle OSM way
 * 
 * @author YSA
 *
 */
public class OSMWay implements Comparable<OSMWay>{
	
	private long id; // can be <> initId after normalization of OSMData
	
	private long initId;// id from file  
	
	private List<OSMNode> nodes;
	
	public OSMWay(long _id) {
		id = _id;
		initId = id;
		nodes = new LinkedList<OSMNode>();
	}
	public void setId(long id) {
		this.id = id;
	}
	public void addNode(OSMNode node) {
		nodes.add(node);
	}
	public List<OSMNode> getNodes() {
		return nodes;
	}
	public long getId() {
		return id;
	}
	public int compareTo(OSMWay o) {
		int res = nodes.size()- o.nodes.size();
		if (res != 0) return res;
		for (int index  = 0; index < nodes.size(); index++) {
			res= nodes.get(index).compareTo(o.nodes.get(index));
			if (res != 0) return res;
		}
		return 0;
	}
	@Override
	public String toString() {
		return "Way [initId=" + initId + "]";
	}
	
	

}
