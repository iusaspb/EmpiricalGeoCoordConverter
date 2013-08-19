package ru.spb.iusa.transport.egcc;
/**
 * 
 * Basic interface 
 * 
 * @author YSA
 *
 */
public interface Converter {
	/**
	 * Convert coordinates from the source CS to  the destination CS 
	 * @param x - coordinates in the source CS 
	 * @return coordinates in the destination CS 
	 */
	double[] convert(double[] x);
	
}