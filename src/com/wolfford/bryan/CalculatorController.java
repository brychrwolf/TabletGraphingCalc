package com.wolfford.bryan;

import java.util.HashMap;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorController extends Fragment{
	static final String PROGRAMOBJECT = "programObject";
	TextView display; // view
	TextView record;  // view
	TextView varRecord;  // view
	private View calculatorView; // Added on 4/6 for Tablet style
	CalculatorBrain brain; // model
	private boolean userIsInMiddleOfTypingNumber;
	HashMap<String, Number> variableValues = new HashMap<String, Number>();
	
	public void graph(View v){
		//Intent intent = new Intent(Intent.ACTION_VIEW);
		//intent.setClassName(this, GraphController.class.getName());
		//intent.putExtra(PROGRAMOBJECT, (Serializable) getBrain().getProgram());
		//this.startActivity(intent);
	}

	private TextView getDisplay(){
		if(this.display == null)
			this.display = (TextView) calculatorView.findViewById(R.id.display);
		return this.display;
	}
	
	private TextView getRecord(){
		if(this.record == null)
			this.record = (TextView) calculatorView.findViewById(R.id.record);
		return this.record;
	}

	private CalculatorBrain getBrain(){
		if(this.brain == null)
			this.brain = new CalculatorBrain();
		return this.brain;
	}
	
	// Android onClick (like iOS Targets)
	public void digitPressed(View v){
		String digit = ((Button)v).getText().toString();
		Log.d("CALCULATOR", "digitPressed: " + digit);
		if(userIsInMiddleOfTypingNumber){
			// if appending a period, first make sure that this is the first
			// period before appending, otherwise that is an invalid number
			if(!digit.equals(".")){
				getDisplay().append(digit);
			}
			else{
				int position = getDisplay().getText().toString().indexOf(".");
				if (position == -1) {
					getDisplay().append(digit);
				}
				// else no_op
			}
		}
		else{
			getDisplay().setText(digit);
			userIsInMiddleOfTypingNumber = true;
		}
	}
	
	public void operatorPressed(View v){
		if(userIsInMiddleOfTypingNumber){
			enterPressed(v);
		}
		String operation = ((Button) v).getText().toString();
		Log.d("CALCULATOR", "operatorPressed: " + operation);
		getBrain().pushOperation(operation);
		//HashMap<String, Number> testNull = new HashMap<String, Number>();
		updateFullUI();
	}
	
	public void enterPressed(View v){
		String enter = ((Button)v).getText().toString();
		Log.d("CALCULATOR", "enterPressed: " + enter);
		userIsInMiddleOfTypingNumber = false;
		getBrain().pushOperand(Double.parseDouble(getDisplay().getText().toString()));
		getRecord().setText(CalculatorBrain.descriptionOfProgram(getBrain().getProgram()));
	}
	
	public void variablePressed(View v){
		if(userIsInMiddleOfTypingNumber){
			enterPressed(v);
		}
		String variable = ((Button) v).getText().toString();
		Log.d("CALCULATOR", "variablePressed: " + variable);
		getBrain().pushOperand(variable);
		getRecord().setText(CalculatorBrain.descriptionOfProgram(getBrain().getProgram()));
	}
	
	public void cPressed(View v){
		String clear = ((Button)v).getText().toString();
		Log.d("CALCULATOR", "cPressed: " + clear);
		getBrain().washBrain();
		getDisplay().setText("0");
		getRecord().setText(CalculatorBrain.descriptionOfProgram(getBrain().getProgram()));
		userIsInMiddleOfTypingNumber = false;
	}
	
	public void uPressed(View v){
		String undo = ((Button)v).getText().toString();
		String currentText = getDisplay().getText().toString();
		int lenTxt = currentText.length();
		Log.d("CALCULATOR", "uPressed: " + undo);
		if(userIsInMiddleOfTypingNumber){
			if(lenTxt > 0 && !currentText.equals("0")){
				String newText = currentText.subSequence(0, lenTxt-1).toString();
				if(!newText.equals("")){
					getDisplay().setText(newText);
				}else{
					getDisplay().setText("0");
					userIsInMiddleOfTypingNumber = Boolean.FALSE;
				}
			}
		}else if(getBrain().getProgram() != null){
			getBrain().pop();
			double result = CalculatorBrain.runProgram(getBrain().getProgram(), variableValues);
			getDisplay().setText(Double.toString(result));
		}
		if(getBrain().getProgram() != null){
			getRecord().setText(CalculatorBrain.descriptionOfProgram(getBrain().getProgram()));
		}
		//userIsInMiddleOfTypingNumber = false;
	}
	
	private void updateFullUI(){
		double result = CalculatorBrain.runProgram(getBrain().getProgram(), variableValues);
		if(Double.isInfinite(result)){
			//getBrain().washBrain();
			getDisplay().setText("ERR:Div/0. Plz hit [C]");
		}else if(Double.isNaN(result)){
			//getBrain().washBrain();
			getDisplay().setText("ERR:Sqrt(-X). Plz hit [C]");
		}else getDisplay().setText(Double.toString(result));
		getRecord().setText(CalculatorBrain.descriptionOfProgram(getBrain().getProgram()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		calculatorView = inflater.inflate(R.layout.calc, container);
		return calculatorView;
	}
}