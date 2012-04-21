package com.wolfford.bryan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphView extends View{
	public interface GraphViewDataSource {
		Object getProgramObject();
		Point getOriginOffset();
		Float getScale();
	}
	GraphViewDataSource dataSource; /* = new GraphViewDataSource(){
		// All added as a default view
		public Object getProgramObject() {
			Stack<Object> programStack = new Stack<Object>();
			programStack.push('x');
			return programStack;
		}

		public Point getOriginOffset() {
			return DEFAULT_ORIGIN;
		}

		public Float getScale() {
			return DEFAULT_SCALE;
		}};*/
	
	public GraphView(Context context) {
		super(context);
	}
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private static final float DEFAULT_SCALE = 100.0F;
	private static final int DEFAULT_STEP = 1;
	private static final Point DEFAULT_ORIGIN = new Point(10, 470);
	private static final Point DEFAULT_ORIGIN = new Point(10, 470);
	
	@Override
	public void draw(Canvas canvas){
		super.draw(canvas);
		Log.d("LIFECYCLE", "GraphView: draw");
		
		Paint paint = new Paint();
		paint.setColor(Color.CYAN);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1);

		float scale = DEFAULT_SCALE;
		if(this.dataSource.getScale() != null){
			scale = this.dataSource.getScale();
			Log.d("scale", ""+scale);
		}
		
		Point origin = DEFAULT_ORIGIN;
		if(!this.dataSource.getOriginOffset().equals(new Point())){
			origin.x = origin.x + this.dataSource.getOriginOffset().x;
			origin.y = origin.y + this.dataSource.getOriginOffset().y;
			Log.d("origin", ""+origin.x+", "+origin.y);
		}
		
		Rect bounds = new Rect();
		bounds.set(0, 0, canvas.getClipBounds().width(), canvas.getClipBounds().height());
		AxesDrawer.drawAxesInRect(canvas, paint, bounds, origin, scale);
		
		paint.setColor(Color.MAGENTA);
		Path path = new Path();
		HashMap<String, Number> variableValues = new HashMap<String, Number>();
		
		// Function does not scale with the axes!
		variableValues.put("x", 0 - origin.x);
		path.moveTo(0, (float) (origin.y - runProgramUsingVariableValues(this.dataSource.getProgramObject(), variableValues)));
		for(int x = 1; x <= canvas.getClipBounds().width(); x += DEFAULT_STEP){
			variableValues.put("x", x - origin.x);
			path.lineTo(x, (float) (origin.y - runProgramUsingVariableValues(this.dataSource.getProgramObject(), variableValues)));
		}
		canvas.drawPath(path, paint);
	}
	
	private float runProgramUsingVariableValues(Object programObject, HashMap<String, Number> values){
		return (float) CalculatorBrain.runProgram(programObject, values);
	}
	
}
