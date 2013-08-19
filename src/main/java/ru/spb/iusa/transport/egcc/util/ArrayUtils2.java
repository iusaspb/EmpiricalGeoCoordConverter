package ru.spb.iusa.transport.egcc.util;

import java.util.Arrays;


/**
 * 
 * Set of basic matrix utilities.
 * 
 * @author YSA
 *
 */
public class ArrayUtils2 {
	
	/**
	 * 
	 * @return zero coordinates
	 */
	public static  double [] buildZeroArray(int m){
		
		double [] array =  new double [m];
		
		Arrays.fill(array,0);
		
		return array;

	}
	
	/**
	 * 
	 * @return zero matrix of m*m size
	 */
	public static  double [][] buildZeroMatrix(int m) {
		
		double [][] matrix =  new double [m][m];
		
		for (int index =0; index < m; index++) Arrays.fill(matrix[index],0);
		
		return matrix;
		
	}
	
	/**
	 * 
	 * @return ident matrix of m*m size
	 */
	public static  double [][] buildIdentityMatrix(int m) {
		
		double [][] matrix =  buildZeroMatrix(m);
		
		for (int index =0; index < m; index++) matrix[index][index] =1;
		
		return matrix;
		
	}
	
	
	
	public static double distance(double[]x, double[]y){
		
		double dist = 0;
		
		for (int index = 0; index <x.length; index++ ) dist +=(x[index]-y[index])*(x[index]-y[index]);
		
		return Math.sqrt(dist);
	}

	public static double[] linearTransform(double [][] A, double []b, double [] x){
		
		int size = x.length;
		
		double [] y = new double [size];
		
		for (int i=0; i < size; i++) {
			
			y[i]=0;
			
			for (int j=0; j < size; j++) y[i] += A[i][j]*x[j];
			
			y[i]+=b[i];
		}
		
		return y;
		
	}
	
	
	public static double length(double [] x) {
		
		double len = 0;
		
		for (int index = 0; index <x.length; index++ ) len +=x[index]*x[index];
		
		return Math.sqrt(len);
		
	}
	
	public static double[][] mult(double[][] A,double[][] B) {
		
		if (A.length != B.length) throw new IllegalArgumentException("dim A != dim B"); 
		
		double[][] AB = buildZeroMatrix(A.length);
		
		for (int i = 0; i < A.length; i ++) {
			for (int j = 0; j < A.length; j ++) {
				AB[i][j] = 0;
				for (int k = 0; k < A.length; k ++) {
					AB[i][j]+= A[i][k]*B[k][j];
				}
			}
		}
		return AB;
		
	}
	
	public static double det(double[][] matrix){
		
		if ( matrix.length != 3) throw new UnsupportedOperationException("matrix.length != 3");
		
		return  matrix[0][0]*(matrix[1][1]*matrix[2][2]-matrix[1][2]*matrix[2][1]) 
				- matrix[0][1]*(matrix[1][0]*matrix[2][2]-matrix[1][2]*matrix[2][0])
				+ matrix[0][2]*(matrix[1][0]*matrix[2][1]-matrix[1][1]*matrix[2][0]);
	}
	
	public static double[][] inverse(double[][] A){
		
		if ( A.length != 3) throw new UnsupportedOperationException("matrix.length != 3");
		
		double det = det(A);
		
		if (det == 0) throw new IllegalStateException("Input matrix is ot invertable"); 
		
		double[][] inversion = buildZeroMatrix(A.length) ;
		
		inversion[0][0] = A[1][1]*A[2][2]-A[1][2]*A[2][1];
		inversion[1][0] = -A[1][0]*A[2][2]+A[1][2]*A[2][0];
		inversion[2][0] = A[1][0]*A[2][1]-A[1][1]*A[2][0];
		
		inversion[0][1] = A[0][2]*A[2][1]-A[0][1]*A[2][2];
		inversion[1][1] = A[0][0]*A[2][2]-A[0][2]*A[2][0];
		inversion[2][1] = A[0][1]*A[2][0]-A[0][0]*A[2][1];
		
		inversion[0][2] = A[0][1]*A[1][2]-A[0][2]*A[1][1];
		inversion[1][2] = A[0][2]*A[1][0]-A[0][0]*A[1][2];
		inversion[2][2] = A[0][0]*A[1][1]-A[0][1]*A[1][0];
		
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A.length; j++) {
				inversion[i][j] /= det;
			}
		}
		
		return inversion;
		
	}
	
	public static String toString(double[][] A) {
		
		StringBuilder sb = new StringBuilder("(");
		
		for (double [] rawA : A) sb.append(Arrays.toString(rawA)).append(',');
		
		sb.delete(sb.length()-1, sb.length()).append(')');
		
		return sb.toString();
		
	}

}
