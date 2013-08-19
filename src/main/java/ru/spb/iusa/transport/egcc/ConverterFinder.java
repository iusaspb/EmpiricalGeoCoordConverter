package ru.spb.iusa.transport.egcc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ru.spb.iusa.transport.egcc.osm.OSMData;
import ru.spb.iusa.transport.egcc.osm.OSMNode;
import ru.spb.iusa.transport.egcc.util.ArrayUtils2;
import Jama.Matrix;

public class ConverterFinder {
	
	private static final String TAG_ACCURACY = "acc";
	
	private int m; // the dimension of coordinate space
	
	private int numOfCollectedPoints; // number of points in the sample set
	
	private Converter converter; // the converter 
	
	private double sx [];
	private double sy [];

	private double sxx [][];
	private double sxy [][];
	
	public ConverterFinder() {
		
		this(2);
		
	}
	
	private ConverterFinder(int _m) {
		
		m = _m;
		
	}
	
	public int getNumOfCollectedPoints() {
		
		return numOfCollectedPoints;
	}
	
	public Converter getConverter() {
		
		return converter;
		
	}
	
	/**
	 *  Initialize all structures
	 */
	public void init(){
		
		numOfCollectedPoints = 0;
		
		sx = ArrayUtils2.buildZeroArray(m);
		
		sy = ArrayUtils2.buildZeroArray(m);
		
		sxx = ArrayUtils2.buildZeroMatrix(m);
		
		sxy = ArrayUtils2.buildZeroMatrix(m);
		
		
	};
	
	/**
	 * 
	 * Process next sample pair of coordinates
	 * 
	 * @param x - coordinates in the source CS
	 * @param y - coordinates in the destination CS
	 */
	
	public void collectPoint(double x [], double y[]) {
		
		numOfCollectedPoints++;
		
		for (int i=0; i < m; i++) {
			for (int j=0; j < m; j++) {
				sxx[i][j] += x[i]*x[j];
				sxy[i][j] += x[i]*y[j];
			}
			sx[i]+=x[i];
			sy[i]+=y[i];
		}
	}
	/**
	 * 
	 * Find a converter based on previously collected points.
	 * 
	 * @return the converter
	 */
	public Converter findConverter(){
		
		normalize();
		
		int me = m+1;
		
		Matrix A = new Matrix(me,me);
		
		Matrix b = new Matrix(me,1);
		
		
		//
		
		A.set(0, 0, 1);
		
		//
		
		for (int indexI = 0 ; indexI < m; indexI++) {
			
			A.set(0, indexI+1, sx[indexI]);
			
			A.set(indexI+1, 0, sx[indexI]);
			
			//
			
			for (int indexJ = 0 ; indexJ < m; indexJ++)
				A.set(indexI+1, indexJ+1, sxx[indexI][indexJ]);
		}
		
		//
		
		double xB[] = ArrayUtils2.buildZeroArray(m);
		
		double xA[][]  = ArrayUtils2.buildZeroMatrix(m);
		
		for (int indexI=0; indexI < m; indexI++) {
			
			b.set(0, 0, sy[indexI]);
			
			for (int indexJ=0; indexJ < m; indexJ++) b.set(indexJ+1, 0, sxy[indexJ][indexI]);
			
			Matrix sol = A.solve(b);
			
			xB[indexI]=sol.get(0, 0);
			
			for (int indexJ=0; indexJ < m; indexJ++) xA[indexI][indexJ]=sol.get(indexJ+1, 0);
			
		}
		
		converter = new ConverterImpl (xA, xB);
		
		return getConverter();
		
	}
	
	/**
	 * Find the best converter that convert xs to ys.  
	 * 
	 * @param xs - coordinates in the source CS
	 * @param ys - coordinates in the destination CS
	 * @return the best converter 
	 */
	
	public Converter findConverter(Collection<double[]> xs,Collection<double[]> ys){
		
		if (xs.size() != ys.size()) throw new IllegalArgumentException("xs.size() != ys.size()"); 
		
		init();
		
		Iterator<double[]> itX = xs.iterator();
		
		Iterator<double[]> itY = ys.iterator();
		
		
		while(itX.hasNext()) {
			
			collectPoint(itX.next(),itY.next());
		}
		
		return findConverter();
		
	}
	
	 
	
	/**
	 * Normalize internal structures after collection
	 */
	private void normalize() {
		
		for (int i=0; i < m; i++) {
			for (int j=0; j < m; j++) {
				sxx[i][j]/=numOfCollectedPoints;
				sxy[i][j]/=numOfCollectedPoints;
			}
			sx[i]/=numOfCollectedPoints;
			sy[i]/=numOfCollectedPoints;
		}
		
	}
	
	
	/**
	 * Simple way to run the finder
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		/*
		 *  default file names
		 */
		String unknownCSFileName = "INIT.osm";
		String knownCSFileName = "STANDART.osm";
		String converterFileName = "converter.obj";
		String estimationFileName = "ESTIMATION.osm";
		
	    OSMData unknownCSData = new OSMData();
	    
	    OSMData knownCSData = new OSMData();
	    
	    if (args.length >= 2) {
	    	
	    	unknownCSFileName = args[0];
	    	
	    	knownCSFileName = args[1];
	    }
	    
	    if (args.length >= 3) converterFileName  = args[2];
	    
	    if (args.length >= 4) estimationFileName  = args[3];
	    
	    System.out.println("Find converter ["+unknownCSFileName+"] into ["+knownCSFileName+"].\nStore it in ["+converterFileName+"].");
	    
	    if (estimationFileName != null) System.out.println("Save converted values into ["+estimationFileName+"].");
	    
	    if (!unknownCSData.read(unknownCSFileName) || ! knownCSData.read(knownCSFileName)) {
	    	
	    	System.exit(-1);
	    	
	    }
	    
	    unknownCSData.normalize();
	    
	    knownCSData.normalize();
	    
	    
	    ConverterFinder converterFinder = new ConverterFinder();
	    
	    Collection<OSMNode> yNodes = knownCSData.getNodes();
	    
	    Collection<OSMNode> xNodes = new LinkedList<OSMNode>();
	    
	    converterFinder.init();
	    
	    for (OSMNode yNode : yNodes ) {
	    	
	    	OSMNode xNode = unknownCSData.getNodeById(yNode.getId());
	    	
	    	if (xNode == null) {
	    	
	    		System.err.println("Could not find correspondent node for "+yNode+". Skip it.");
	    		
	    		continue;
	    		
	    	}
	    	
	    	converterFinder.collectPoint(xNode.getLatLon(), yNode.getLatLon());
	    	
	    	xNodes.add(xNode);
	    	
	    }
	    
	    System.out.println("Number of points "+ converterFinder.getNumOfCollectedPoints()+" in  sample.");
	    
	    ConverterImpl converter = (ConverterImpl) converterFinder.findConverter();
	    
	    System.out.println(converter);
	    
	    converter.store(converterFileName);
	    
	    Collection <OSMNode> estimNodes = new ArrayList<OSMNode> (knownCSData.getNodes());
	    
	    for (OSMNode node :  estimNodes) {
	    	
	    	OSMNode xNode = unknownCSData.getNodeById(node.getId());
	    	
	    	if (xNode != null) {
	    		
	    		double [] estimLaton = converter.convert(xNode.getLatLon());
	    		
	    		double [] initLatLon = node.getLatLon();
	    		
	    		node.setLatLon(estimLaton);
	    		
	    		node.setTag(TAG_ACCURACY,ArrayUtils2.distance(estimLaton, initLatLon));
	    		
	    	} else {
	    		
	    		System.err.println("Could not find correspondent node for "+node+". Skip it.");
	    		
	    		continue;
	    		
	    	}
	    	
	    	
	    }
	    
	    //
	    // normalization of accuracy
	    //
	    
	    double totalAcc = 0;
	    
	    for (OSMNode node :  estimNodes) {
	    	
	    	Double acc = (Double) node.getTag(TAG_ACCURACY);
	    	
	    	if (acc != null) totalAcc += acc;
	    	
	    }
	    
	    
	    if (totalAcc > 0 ) {
	    	
		    for (OSMNode node :  estimNodes) {
		    	
		    	Double acc = (Double) node.getTag(TAG_ACCURACY);
		    	
		    	if (acc != null) {
		    		
		    		node.setTag(TAG_ACCURACY, acc/totalAcc);
		    		
		    	}
		    	
		    }
	    	
	    }
	    
	    if (estimationFileName != null) (new OSMData()).write(estimationFileName, estimNodes);
	    
	    System.out.println("END.");
	    
	    
	}

	
	

}
