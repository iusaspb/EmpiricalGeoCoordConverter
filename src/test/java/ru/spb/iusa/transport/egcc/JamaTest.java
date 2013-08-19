package ru.spb.iusa.transport.egcc;

import Jama.Matrix;

public class JamaTest {
	
	
	/**
	 *   (5,11) = ((1,2),(3,4)) *x
	 *   Answer: (1,2) 
	 *   	
	 */
	public void testSolveLinearQuations(){
		
		 Matrix A = new Matrix(2,2);
		 
		 A.set(0, 0, 1);
		 A.set(0, 1, 2);
		 A.set(1, 0, 3);
		 A.set(1, 1, 4);
		 
		 Matrix b = new Matrix(2,1);
		 
		 b.set(0, 0, 5);
		 b.set(1, 0, 11);
	     
		 Matrix x = A.solve(b);
	     
	     assert(x.get(0, 0) ==1);
	     assert(x.get(1, 0) ==2);
		
	}

	public static void main(String[] args) {
		
		(new JamaTest()).testSolveLinearQuations();
	     
	}
	
	

}
