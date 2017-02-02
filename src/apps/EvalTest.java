package apps;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class EvalTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFirstEval() {
		String expression = "1+3";
		//String fileName = "";
		
		Expression expr = new Expression(expression);
		expr.buildSymbols();
		
		float result = expr.evaluate();
		assertEquals(4, result, 0);
		//fail("Not yet implemented");
	}
	
	@Test
	public void testBuildSymbolsForArraysWithLongNames(){
		String expression = "arrayA[8]";
		String fileName = "etest2.txt";
		Expression expr = new Expression(expression);
		expr.buildSymbols();
		
		try {
			Scanner scanner = new Scanner(new File(fileName));
			expr.loadSymbolValues(scanner);
		} catch(Exception ex) {
			//log error, etc
		}
		
		assertEquals(1, expr.arrays.size());
		
		int[] firstValueArray = expr.arrays.get(0).values;
		assertEquals(10, firstValueArray.length);
		assertEquals(5, firstValueArray[3]);
		assertEquals(12, firstValueArray[8]);
		assertEquals(1, firstValueArray[9]);
	}

	@Test
	public void testBuildSymbolsForArrays(){
		String expression = "A[2]";
		String fileName = "etest1.txt";
		Expression expr = new Expression(expression);
		expr.buildSymbols();
		
		try {
			Scanner scanner = new Scanner(new File(fileName));
			expr.loadSymbolValues(scanner);
		} catch(Exception ex) {
			//log error, etc
		}
		
		assertEquals(1, expr.arrays.size());
		
		int[] firstValueArray = expr.arrays.get(0).values;
		assertEquals(5, firstValueArray.length);
		assertEquals(3, firstValueArray[2]);
		assertEquals(5, firstValueArray[4]);
	}
	
	@Test
	public void testEvaluateSingleArrayIndex(){
		String expression = "A[2]";
		String fileName = "etest1.txt";		
		Expression expr = buildNewExpressionWithFile(expression, fileName);
		float result = expr.evaluate();
		assertEquals(3, result, 0);
	}
	
	@Test
	public void testEvaluateNestedParentheses(){
		String expression = "2+(3+4*(1+0))";
		//String fileName = "etest1.txt";		
		Expression expr = buildNewExpression(expression);
		float result = expr.evaluate();
		assertEquals(9, result, 0);
	}
	
	@Test
	public void testEvaluateNestedParenthesesWithVars(){
		String expression = "varx+(3+vary*(arrayA[3]+2))";
		String fileName = "etest2.txt";		
		Expression expr = buildNewExpressionWithFile(expression, fileName);
		float result = expr.evaluate();
		assertEquals(44, result, 0);
	}
	
	@Test
	public void testEvaluateNestedArrays(){
		String expression = "arrayA[arrayA[9]*(arrayA[3]+2)+1]-varx";
		String fileName = "etest2.txt";		
		Expression expr = buildNewExpressionWithFile(expression, fileName);
		float result = expr.evaluate();
		assertEquals(6, result, 0);		
	}
	
	@Test
	public void testEvaluateNestedArraysWithParentheses(){
		String expression = "arrayA[arrayA[(12-4)]-9]/2";
		String fileName = "etest2.txt";		
		Expression expr = buildNewExpressionWithFile(expression, fileName);
		float result = expr.evaluate();
		assertEquals(2.5, result, 0);	
	}
	
	@Test
	public void testEvaluateSuperNest(){
		String expression = "2+(3+(1+(0+(9+1+(5+3)))))";		
		Expression expr = buildNewExpression(expression);
		float result = expr.evaluate();
		assertEquals(24, result, 0);
	}
	
	Expression buildNewExpressionWithFile(String expression, String fileName) {
		Expression expr = buildNewExpression(expression);
		
		try {
			Scanner scanner = new Scanner(new File(fileName));
			expr.loadSymbolValues(scanner);
		} catch(Exception ex) {
			//log error, etc
		}
		
		return expr;
	}
	
	Expression buildNewExpression(String expression) {
		Expression expr = new Expression(expression);
		expr.buildSymbols();
		return expr;
	}
}