package ru.spb.iusa.transport.egcc.osm;

import java.util.HashMap;
import java.util.Map;
/**
 * A very basic class to handle OSM node
 * @author YSA
 *
 */
public class OSMNode implements Comparable<OSMNode> {
	
	private long id; // can be <> initId after normalization of OSMData
	
	private long initId;// id from file  
	
	private double lat;
	
	private double lon;
	
	private Map<String,Object> tags = null; 
	
	public OSMNode(long _id, double _lat, double _lon) {
		
		super();
		
		id = _id;
		
		initId = id;
		
		lat = _lat;
		
		lon = _lon;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public long getInitId() {
		return initId;
	}
	
	public double[] getLatLon() {
		
		return new double[] {lat,lon};
		
	}
	
	public void setLatLon (double[] latLon ) {
		
		lat = latLon[0];
		
		lon = latLon[1];
		
	}
	
	public Object setTag(String name, Object value) {
		
		if (tags == null) tags = new HashMap<String,Object>();
		
		return tags.put(name, value);
		
	}
	
	public Object getTag(String name) {
		
		return (tags != null) ? tags.get(name): null;
		
	}
	
	public boolean areTagsExist(){
		
		return (tags != null) ? !tags.isEmpty(): false;
	}
	
	public Map<String, Object> getTags() {
		return tags;
	}
	
	public int compareTo(OSMNode o) {
		return Long.signum(id - o.id);
	}
	@Override
	public String toString() {
		return "Node [initId=" + initId + "]";
	}
}
