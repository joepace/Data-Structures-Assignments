package apps;

import java.io.*;
import java.util.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols()
    {
    		/** COMPLETE THIS METHOD **/
    	scalars = new ArrayList<ScalarSymbol>();
    	arrays = new ArrayList<ArraySymbol>();
    	Stack<String> symbols = new Stack<String>();
    	StringTokenizer myTok = new StringTokenizer(expr, delims, true);
    	String token = "";
    	
    	while(myTok.hasMoreTokens())
    	{
    		token = myTok.nextToken();
    		if(Character.isLetter(token.charAt(0)) || token.equals("["))
    		{
    			symbols.push(token);
    		}
    	}
    	
    	while(symbols.isEmpty() == false)
    	{
    		token = symbols.pop();
    		
    		if(token.equals("["))
    		{
    			token = symbols.pop();
    			ArraySymbol arraysym = new ArraySymbol(token);
    			if(arrays.indexOf(arraysym) == -1)
    			{
    				arrays.add(arraysym);
    			}
    		}
    		
    		else	
    		{
    			ScalarSymbol scalarsym = new ScalarSymbol(token);
    			if(scalars.indexOf(scalarsym) == -1)
    			{
    				scalars.add(scalarsym);
    			}
    		}
    	} 
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() 
    {
    	/** COMPLETE THIS METHOD **/
    	expr = expr.replaceAll(" ", "");
    	return evaluate(expr);
    }
    
    private float evaluate(String expr)
    {
    	Stack<Float> numbers = new Stack<Float>();
    	Stack<String> operators = new Stack<String>();
    	StringTokenizer st = new StringTokenizer(expr, delims, true);
    	String tk = new String("");
    	String arrayName = "";
    	
    	while(st != null)
    	{
    		if(!st.hasMoreTokens())
    		{
    			break;
    		}
    		
    		tk = st.nextToken();
    		
    		if(tk.equals("(") || tk.equals("["))
    		{
    			int openIndex = 0;
    			int closeIndex = 0;
    			
    			if(tk.equals("("))
    			{
    				openIndex = expr.indexOf('(');
    				closeIndex = getClosePIndex(expr, openIndex);
    			}
    			
    			else
    			{
    				openIndex = expr.indexOf('[');
    				closeIndex = getCloseBracketIndex(expr, openIndex);
    			}
    			
    			float parenEval = evaluate(expr.substring(openIndex + 1, closeIndex));
    			
    			if(tk.equals("["))
    			{
    				float bracketEval = getArrValueWithName(arrayName, (int)parenEval);
	    			numbers.push(bracketEval);
		    	}
    			
    			else
		    	{
		    		numbers.push(parenEval);
		    	}
    			evalWhenDivMult(operators, numbers);
    			st = new StringTokenizer(expr.substring(closeIndex + 1), delims, true);    			
	    	}
    		
    		else if(getIndOfArrName(tk) != -1)
    		{
	    		arrayName = tk;	    		
	    	}
	    	
	    	else if(Character.isLetter(tk.charAt(0)))
	    	{
	    		ScalarSymbol ss = new ScalarSymbol(tk);
	    		int scalarSymbolIndex = scalars.indexOf(ss);
	    		float scalarVal = scalars.get(scalarSymbolIndex).value;
	    		numbers.push(scalarVal);
	    		evalWhenDivMult(operators, numbers);
	    	}
    		
	    	else if(tk.equals("/") || tk.equals("*") || tk.equals("+") || tk.equals("-"))
	    	{
	    		operators.push(tk);
	    	}
	    	
	    	else if(isConstNum(tk))
	    	{
	    		int constant = Integer.parseInt(tk);
	    		numbers.push((float)constant); 
	    		evalWhenDivMult(operators, numbers);
	    	}   
    	}
    	 
    	if(operators.isEmpty())
    	{
    		return numbers.pop();
    	}    	
    	
    	Stack<Float> numsCorrect = new Stack<Float>();
        Stack<String> opsCorrect = new Stack<String>();
        
        while(operators.isEmpty() == false)
        {
        	opsCorrect.push(operators.pop());
        }
        
        while(numbers.isEmpty() == false)
        {
        	numsCorrect.push(numbers.pop());
        }
        
        while(opsCorrect.isEmpty() == false)
        {
        	evalStack(opsCorrect, numsCorrect, true);
        }
        
        return numsCorrect.pop();
    }
    
    private void evalStack(Stack<String> ops, Stack<Float> nums, boolean corrected)
    {
    	String opTop = ops.pop();
    	float result = 0;
    	float first = 0;
    	float second = 0;
    	
    	if (corrected == true)
    	{
    		second = nums.pop();
    		first = nums.pop();
    	}
    	else
    	{
    		first = nums.pop();
    		second = nums.pop();
    	}
    	
    	if (opTop.equals("/"))
    	{
    		result = second / first;
    	}
    	
    	else if (opTop.equals("*"))
    	{
    		result = second * first;
    	}
    	
    	else if (opTop.equals("+"))
    	{
    		result = second + first;
    	}
    	
    	else if (opTop.equals("-"))
    	{
    		result = second - first;
    	}
    	
    	nums.push(result);
    }
    
    private int getArrValueWithName(String name, int valueIndex)
    {
    	int arrayIndex = 0;
		for(int i = 0; i < arrays.size(); i++)
		{
			if(arrays.get(i).name.equals(name)){
				arrayIndex = i;
			}
		}
		int[] arrayVals = arrays.get(arrayIndex).values;
		return arrayVals[valueIndex];
    }
    
    private int getIndOfArrName(String name)
    {
        for(int i = 0; i < arrays.size(); i++)
        {
          if(name.equals(arrays.get(i).name))
          {
            return i;
          }
        }
        return -1;
    }
    
    private int getClosePIndex(String expr, int openIndex)
    {
    	int closeInd = openIndex;
    	int counter = 1;
    	while(counter > 0){
    		char c = expr.charAt(++closeInd);
    		if(c == '('){
    			counter++;
    		}else if(c == ')'){
    			counter--;
    		}
    	}
    	return closeInd;
    }
    
   private void evalWhenDivMult(Stack<String> ops, Stack<Float> nums)
   {
    	if(!ops.isEmpty())
    	{
    		String opTop = ops.peek();
    		if(opTop.equals("/") || opTop.equals("*"))
    		{
    			evalStack(ops, nums, false);
    		}
    	}
    } 
   
   private int getCloseBracketIndex(String expr, int openIndex)
   {
	   int closeInd = openIndex;
	   int counter = 1;
	   while(counter > 0)
	   {
		   char c = expr.charAt(++closeInd);
		   if(c == '[')
		   {
			   counter++;
		   }
		   
		   else if(c == ']')
		   {
			   counter--;
		   }
	   }
	   return closeInd;
   }
   
   private boolean isConstNum(String token)
   {
	   	try{
	   		Integer.parseInt(token);
	   		return true;
	   	}catch(NumberFormatException e){
	   		return false;
   	}
}
    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
