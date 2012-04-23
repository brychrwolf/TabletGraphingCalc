package com.wolfford.bryan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainController extends Activity {
    private CalculatorController calculatorController;
    private GraphController graphController;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        calculatorController = (CalculatorController) getFragmentManager().findFragmentById(R.id.fragment1);
    }
    
    public void graph(View v){
    	calculatorController.graph(v);
	}
    public void digitPressed(View v){
    	calculatorController.digitPressed(v);
	}
    public void operatorPressed(View v){
    	calculatorController.operatorPressed(v);
	}
    public void enterPressed(View v){
    	calculatorController.enterPressed(v);
	}
    public void variablePressed(View v){
    	calculatorController.variablePressed(v);
	}
    public void cPressed(View v){
    	calculatorController.cPressed(v);
	}
    public void uPressed(View v){
    	calculatorController.uPressed(v);
	}
    
    public void setScale(View v) {
    	graphController.setScale(v);
	}
}