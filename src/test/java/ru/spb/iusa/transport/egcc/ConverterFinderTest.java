package ru.spb.iusa.transport.egcc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.spb.iusa.transport.egcc.ConverterImpl;
import ru.spb.iusa.transport.egcc.ConverterFinder;
import ru.spb.iusa.transport.egcc.util.ArrayUtils2;


public class ConverterFinderTest {
	
	
	public void test1(){
		
		double [][] expectedA = {{1,2},{3,4}};
		
		double [] expectedB = {5,6};
		
		/*
		 *  build init data
		 *   
		 *  y = expectedA*x+expectedB
		 *  
		 *  so the finder must find a conversion as above formula 
		 *   
		 */
		
		List<double[]> xs= new ArrayList<double[]>();
		
		List<double[]> ys= new ArrayList<double[]>();
		
		for (int i1 = 0; i1< 5; i1++) {
			for (int i2 = 0; i2< 5; i2++) {
				
				double x [] = {i1,i2};
				
				xs.add(x);
				
				double y [] = ArrayUtils2.linearTransform(expectedA, expectedB, x);
				
				ys.add(y);
				
			}
		}
		
		/*
		 *  find converter
		 */
		
		ConverterFinder finder = new ConverterFinder();
		
		finder.findConverter(xs, ys);
		
		ConverterImpl tr = (ConverterImpl) finder.getConverter();
		
		/*
		 *  check
		 */
		
		// b
		
		double b [] = tr.getShift();
		
		for (int index = 0; index < b.length; index++) assert (expectedB[index] == b[index]);
		
		// A
		
		double A[][] = tr.getA();
		
		for (int indexI =  0; indexI <  A.length; indexI++) {
			
			for (int indexJ =  0; indexJ <  A.length; indexJ++) {
				
				assert (expectedA[indexI][indexJ] == A[indexI][indexJ]);
				
			}
			
		}
		
		/*
		 *  output results just to look at
		 */
		
		System.out.println("b "+Arrays.toString(b));
		
		System.out.println("A");
		
		for (double [] row  : A) {
			
			System.out.println(Arrays.toString(row));
		}
		
		System.out.println(tr);
		
		System.out.println("average accuracy " + tr.getAverageAccuracy(xs, ys));
		
	}

	public static void main(String[] args) {
		
		(new ConverterFinderTest()).test1();
		
	}
		

}

