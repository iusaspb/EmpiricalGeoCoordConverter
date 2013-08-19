package ru.spb.iusa.transport.egcc.util;

public class ArrayUtils2Test {
	
	private double[][] identity = ArrayUtils2.buildIdentityMatrix(3);
	
	public void testInverse1(){
		
		double[][] A = ArrayUtils2.buildZeroMatrix(3);
		
		A[0][0] =1;
		A[1][1] =1;
		A[2][2] =1;
		
		double[][] A_1 = ArrayUtils2.inverse(A);
		
		compare(identity,ArrayUtils2.mult(A,A_1));
		
	}
	
	public void testInverse2(){
		
		double[][] A = ArrayUtils2.buildZeroMatrix(3);
		
		A[0][2] =1;
		A[1][1] =1;
		A[2][0] =1;
		
		double[][] A_1 = ArrayUtils2.inverse(A);
		
		compare(identity,ArrayUtils2.mult(A,A_1));
		
	}
	
	public void testInverse3(){
		
		double[][] A = ArrayUtils2.buildZeroMatrix(3);
		
		A[0][0] =1;
		A[0][1] = 2;
		A[1][0] = A[0][1];
		A[1][1] = 3;
		A[0][2] = 4;
		A[2][0] = A[0][2];
		A[1][2] = 5;
		A[2][1]= A[1][2];
		A[2][2] = 6;
		
		double[][] A_1 = ArrayUtils2.inverse(A);
		
		compare(identity,ArrayUtils2.mult(A,A_1));
		
	}
	
	
	private void compare(double[][] A, double[][] B) {
		int size = A.length;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				assert(A[i][j] == B[i][j]);
			}
		}
		
		 
		
	}

}
