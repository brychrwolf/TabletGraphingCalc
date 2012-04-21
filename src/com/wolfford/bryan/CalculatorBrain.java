package com.wolfford.bryan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import android.util.Log;

public class CalculatorBrain {
	private Stack<Object> _programStack;
	
	private Stack<Object> getProgramStack(){
		if(_programStack == null){
			_programStack = new Stack<Object>();
		}
		return _programStack;
	}
	
	Object getProgram(){
		if(_programStack == null){
			_programStack = new Stack<Object>();
		}
		return _programStack.clone();
	}
	
	void pushOperand(double operand){
		getProgramStack().push(operand);
	}
	
	// in the case of a variable
	void pushOperand(String operand){
		getProgramStack().push(operand);
	}

	public void pushOperation(String operation){
		getProgramStack().push(operation);
	}
	
	public void pop(){
		if(!getProgramStack().isEmpty()){
			getProgramStack().pop();
		}
	}
	
	@SuppressWarnings("unchecked")
	// I"m not using this, but someone might one day?
	static double runProgram(Object program){
		Stack<Object> programStack = null;
		if(program instanceof Stack<?>){
			programStack = (Stack<Object>)program;
		} else {
			// TODO throw an exception?
		}
		return popObjectOffStack(programStack);
	}
	
	static private Stack<Object> replaceAllOccurencesOfVariableInStackWithValue(String variable, Stack<Object> programStack, Number variableValue){
		//Stack<Object> stackClone = (Stack<Object>) programStack.clone();
		int location = -1;
		while(programStack.search(variable) != -1){
			location = programStack.size() - programStack.search(variable);;
			programStack.remove(location);
			programStack.insertElementAt(variableValue, location);
		}
		return programStack;
	}
	
	@SuppressWarnings("unchecked")
	static double runProgram(Object program, HashMap<String, Number> variableValues){
		Stack<Object> programStack = null;
		//Log.d("runProgram", program.toString());
		if(program instanceof Stack<?>){
			programStack = (Stack<Object>)program;
			// replace all the variables in program with a value: try HashMap, default 0
			// check if variableUsedInProgram is not null since we are forced to make an empty stack = null in the assignment
			if(variablesUsedInProgram(program) != null){
				for(String variable : variablesUsedInProgram(program)){
					Double variableValue = 0.0;
					if(variableValues.containsKey(variable))
						variableValue = variableValues.get(variable) != null ? variableValues.get(variable).doubleValue() : 0.0;
					else variableValue = 0.0;
					programStack = replaceAllOccurencesOfVariableInStackWithValue(variable, programStack, (Double)variableValue);
				}
			}
		} else {
			// TODO throw an exception?
		}
		return popObjectOffStack(programStack);
	}

	@SuppressWarnings("unchecked")
	static Set<String> variablesUsedInProgram(Object program){
		Set<String> variableSet = new HashSet<String>(5);
		Stack<Object> programStack = null;
		if(program instanceof Stack<?>){
			programStack = (Stack<Object>)program;
			if(programStack.contains("x"))
				variableSet.add("x");
			/*if(programStack.contains("β"))
				variableSet.add("β");
			if(programStack.contains("γ"))
				variableSet.add("γ");
			if(programStack.contains("δ"))
				variableSet.add("δ");*/
		} else {
			// TODO throw an exception?
			variableSet = null;
		}
		return variableSet.isEmpty() ? null : variableSet;
	}
	
	static String listUsedVariables(Set<String> variables, HashMap<String, Number> variableValues){
		String result = "";
		Double variableValue = 0.0;
		if(variables != null){
			for(String variable : variables){
				if(variableValues.containsKey(variable))
					variableValue = (variableValues.get(variable) != null ? variableValues.get(variable).doubleValue() : 0.0);
				else variableValue = 0.0;
				result = (result == "" ? variable + "=" + variableValue : result + ", " + variable + "=" + variableValue);
			}
		}
		return result;
	}
	
	private static double popObjectOffStack(Stack<Object> stack){
		double result = 0.0;
		Object topOfStack = null;
		if(!stack.empty()){
			topOfStack = stack.pop();
		}
		if(topOfStack instanceof Double){
			result = (Double)topOfStack;
		}
		else if(topOfStack instanceof String){
			String operation = (String)topOfStack;
			if(operation.equals("+")){
				result = popObjectOffStack(stack) + popObjectOffStack(stack);
			}
			else if (operation.equals("*")){
				result = popObjectOffStack(stack) * popObjectOffStack(stack);
			}
			else if (operation.equals("-")){
				double subtrahend = popObjectOffStack(stack);
				result = popObjectOffStack(stack) - subtrahend;	
			}
			else if (operation.equals("/")){
				double divisor = popObjectOffStack(stack);
				try{
					result = popObjectOffStack(stack) / divisor;
				}
				catch(ArithmeticException e){
					// Divide by Zero Error Caught!
					// should result in a "Infinity" on the screen
				}
			}
			else if (operation.equals("sin")){
				result = Math.sin(popObjectOffStack(stack));
			}
			else if (operation.equals("cos")){
				result = Math.cos(popObjectOffStack(stack));
			}
			else if (operation.equals("sqrt")){
				try{
					result = Math.sqrt(popObjectOffStack(stack));
				}
				catch(ArithmeticException e){
					// sqrt(-num) Error Caught!
					// should result in a "NaN" on the screen
				}				
			}
			else if (operation.equals("π")){
				result = Math.PI;
			} // variables without values turn into 0
			else result = 0.0;
		}
		return result;
	}
	
	private static boolean isP1Operation(Object operation){
		if(!(operation instanceof String))
			return Boolean.FALSE;
		else{
			if(operation.equals("+") || operation.equals("-")){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
		
	}private static boolean isP2Operation(Object operation){
		if(!(operation instanceof String))
			return Boolean.FALSE;
		else{
			if(operation.equals("*") || operation.equals("/")){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	@SuppressWarnings("unchecked") 
	static String descriptionOfProgram(Object program) {
		String returnString = "";
		if(program instanceof Stack<?>){
			Stack<Object> programStack = null;
			programStack = (Stack<Object>)program;
			//for(Object item : recursivePostfixToInfix(programStack)){
			//	returnString += item.toString();
			//}
			returnString = recursivePostfixToInfix(programStack); //recursivePostfixToInfix(programStack);
		}
		return returnString;
	}
	
	/*static String newRPTI(Stack<Object> programStack){
		String result = "";
		Object topOfStack = null;
		if(!programStack.empty()){
			topOfStack = programStack.pop();
			//if(!programStack.empty()) result = descriptionOfProgram(programStack) + ", ";
			if(topOfStack instanceof Double){
				result += topOfStack.toString();
			}
			else if(topOfStack instanceof String){
				String operation = (String)topOfStack;
				if(isP1Operation(operation)){
					String subProgram = descriptionOfProgram(programStack);
					result += "(" + descriptionOfProgram(programStack) + operation + subProgram + ")";
					//result = descriptionOfProgram(programStack) + ", " + result;
				}
				else if(isP2Operation(operation)){
					String subProgram = descriptionOfProgram(programStack);
					result += descriptionOfProgram(programStack) + operation + subProgram;
					//result = descriptionOfProgram(programStack) + ", " + result;
				}
				else if (operation.equals("sin") || operation.equals("cos") || operation.equals("sqrt")){
					String subProgram = descriptionOfProgram(programStack);
					result += operation + (parenthesisExists(subProgram) ? "" : "(") + subProgram + (parenthesisExists(subProgram) ? "" : ")");
					//result = descriptionOfProgram(programStack) + ", " + result;
				}
				else{
					//if(!programStack.empty()) result = descriptionOfProgram(programStack) + ", ";
					result += operation;
				}
				
			}
		}
	//if(!programStack.empty()) result = descriptionOfProgram(programStack) + ", " + result;
	return result;
	}*/
	
	static String recursivePostfixToInfix(Stack<Object> programStack) {
		String result = "";
		Object topOfStack = null;
		if(!programStack.empty()){
			topOfStack = programStack.pop();
			if(topOfStack instanceof Double){
				result = topOfStack.toString();
			}
			else if(topOfStack instanceof String){
				String operation = (String)topOfStack;
				if(isP1Operation(operation)){
					String subProgram = descriptionOfProgram(programStack);
					result = "(" + descriptionOfProgram(programStack) + operation + subProgram + ")";
				}
				else if(isP2Operation(operation)){
					String subProgram = descriptionOfProgram(programStack);
					result = descriptionOfProgram(programStack) + operation + subProgram;
				}
				else if (operation.equals("sin") || operation.equals("cos") || operation.equals("sqrt")){
					String subProgram = descriptionOfProgram(programStack);
					result = operation + (parenthesisExists(subProgram) ? "" : "(") + subProgram + (parenthesisExists(subProgram) ? "" : ")");
				}
				else result = operation;	
			}
		}
	/*for(Object item : result){
		result.push(item.toString() + ", ");
	}*/
	return result;
	}
	
	static private Boolean parenthesisExists(String inputString){
		if(inputString.charAt(0) == '(' && inputString.charAt(inputString.length()-1) == ')'){
			return Boolean.TRUE;
		}else return Boolean.FALSE;
	}
	
	public void washBrain(){
		getProgramStack().removeAllElements();
	}
}
