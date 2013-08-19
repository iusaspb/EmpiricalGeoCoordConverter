package ru.spb.iusa.transport.egcc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import ru.spb.iusa.transport.egcc.util.ArrayUtils2;

/**
 * Implementation of simple linear (affine) conversation. 
 * 
 * y = A*x+b
 *  
 * @author YSA
 *
 */
public class ConverterImpl implements Serializable, Converter {
	
	private static final long serialVersionUID = 1L;
	
	private double b[];
	
	private double A[][];
	
	public ConverterImpl(){};
	
	public ConverterImpl(double[][] _a, double[] _b) {
		
		this();
		
		setB(_b);
		
		setA(_a); 
		
	}

	public double[] getB() {
		return b;
	}
	public void setB(double[] _b) {
		b = _b;
	}
	public double[][] getA() {
		return A;
	}
	public void setA(double[][] _a) {
		A = _a;
	}
	
	public double[] getShift(){
		return getB(); 
	}
	
	@Override
	public double[] convert(double[] x){
		
		return ArrayUtils2.linearTransform(A, b, x);
		
	}
	
	public double[] getAccuracy(double[] x, double[] y){
		
		double [] accuracy = new double [x.length];
		
		double [] est = ArrayUtils2.linearTransform(A, b, x);
		
		for (int index = 0; index < x.length; index++ ) {
			
			accuracy[index] = y[index] - est[index]; 
		}
		
		return accuracy;
		
	}
	
	public double getAverageAccuracy(Collection<double[]> x,Collection<double[]> y) {
		
		double avDist = 0;
		
		if (x.isEmpty()) return avDist;   
		
		Iterator<double[]> itX = x.iterator();
		
		Iterator<double[]> itY = y.iterator();
		
		while (itX.hasNext()) {
			
			avDist += ArrayUtils2.length(getAccuracy(itX.next(),itY.next()));
			
		}
		
		return avDist/x.size();
		
	}
	
	
	/**
	 * 
	 * Serialize the converter
	 * 
	 * @param fileName
	 * @return true if everything is OK and false otherwise
	 * 
	 */
	public boolean store(String fileName){
		
		boolean res = false;
		
		try {
			
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
			
			oos.writeObject(this);
			
			oos.close();
			
			res = true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
/**
 * 
 * Deserialize a converter
 * 
 * @param fileName
 * 
 * @return converter or NULL if there are any errors. 
 */
	public static ConverterImpl restore(String fileName) {
		
		try {
			
			ObjectInputStream  ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
			
			ConverterImpl  converter = (ConverterImpl) ois.readObject();
			
			ois.close();
			
			return converter;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	@Override
	public String toString() {
		return "Converter [b="+Arrays.toString(b)+", A="+ArrayUtils2.toString(A)+")]";
	}
	
}
