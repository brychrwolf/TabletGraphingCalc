package com.wolfford.bryan;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.wolfford.bryan.GraphView.GraphViewDataSource;

public class GraphController extends Fragment implements GraphViewDataSource{
	Object programObject; // model
	private float scale = 100.0F;
	private Point originOffset = new Point();
	TextView program; // view

	public void setProgramObject(Object obj) {
		this.programObject = obj;
		this.graphView.invalidate();
	}
	
    private void setOriginOffset(int x, int y) {
    	this.originOffset.x = (int) x / 2;
    	this.originOffset.y = (int) y / 2;
		this.graphView.invalidate();
	}
    
    public void setScale(View v) {
		int btnId = ((Button) v).getId();
		if(btnId == R.id.zoomIn) this.scale = this.scale + this.scale/5;
		else if(btnId == R.id.zoomOut) this.scale = this.scale - this.scale/5;
		this.graphView.invalidate();
	}

	// view
	private GraphView graphView = null;
	protected GestureDetector gestureDetector;
	private View newGraphView; // added at 46:41 of week 7
	
    /** Called when the activity is first created. */
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		newGraphView = inflater.inflate(R.layout.graph, container);
		this.graphView = (GraphView) newGraphView.findViewById(R.id.graphView1);
		
		this.graphView.dataSource = this;
		this.gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,	float distanceX, float distanceY) {
				setOriginOffset((int) (originOffset.x - distanceX), (int) (originOffset.y - distanceY));
				return super.onScroll(e1, e2, distanceX, distanceY);
			}
			
		});
		graphView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});
		
		//this.setProgramObject(getIntent().getExtras().get(CalculatorController.PROGRAMOBJECT));
		//this.getProgram().setText(CalculatorBrain.descriptionOfProgram(this.getProgramObject()));
		return newGraphView;
    }

	/*public TextView getProgram(){
		if(this.program == null)
			this.program = (TextView) findViewById(R.id.program);
		return this.program;
	}*/
	
    public Object getProgramObject(){
		Stack<Object> programStack = new Stack<Object>();
		Iterator<Object> iterator = ((List<Object>) this.programObject).iterator();
		while(iterator.hasNext())
			programStack.push(iterator.next());
		return programStack;
	}

    public Point getOriginOffset(){
		return this.originOffset;
	}
    
    public Float getScale(){
		return this.scale;
	}
}