package com.wolfford.bryan;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

public class AxesDrawer {
	private final static int ANCHOR_TOP = 1;
	private final static int ANCHOR_LEFT = 2;
	private final static int ANCHOR_BOTTOM = 3;
	private final static int ANCHOR_RIGHT = 4;

	private final static double HASH_MARK_FONT_SIZE = 12.0;

	private final static int HORIZONTAL_TEXT_MARGIN = 6;
	private final static int VERTICAL_TEXT_MARGIN = -2;

	private static void drawString(Canvas canvas, Paint paint, String text,
			Point location, int anchor) {
		if (text != null && text.length() > 0) {
			paint.setTextSize((float) HASH_MARK_FONT_SIZE);
			paint.setTypeface(Typeface.MONOSPACE);
			Rect textRect = new Rect();
			int left = (int) (location.x - paint.getTextSize() / 2);
			int top = (int) (location.y - paint.getTextSize() / 2);
			textRect.set(left, top, location.x, location.y);

			switch (anchor) {
			case ANCHOR_TOP:
				textRect.top += textRect.height() / 2 + VERTICAL_TEXT_MARGIN;
				break;
			case ANCHOR_LEFT:
				textRect.left += textRect.width() / 2 + HORIZONTAL_TEXT_MARGIN;
				break;
			case ANCHOR_BOTTOM:
				textRect.top -= textRect.height() / 2 + VERTICAL_TEXT_MARGIN;
				break;
			case ANCHOR_RIGHT:
				textRect.left -= textRect.width() / 2 + HORIZONTAL_TEXT_MARGIN;
				break;
			}

			canvas.drawText(text, textRect.left, textRect.top, paint);
		}
	}

	private final static int HASH_MARK_SIZE = 3;
	private final static int MIN_PIXELS_PER_HASHMARK = 25;

	private static void drawHashMarksInRect(Canvas canvas, Paint paint,
			Rect bounds, Point axisOrigin, double pointsPerUnit) {
		if (pointsPerUnit == 0.0)
			return;

		if (((axisOrigin.x < bounds.left) || (axisOrigin.x > bounds.left
				+ bounds.width()))
				&& ((axisOrigin.y < bounds.top) || (axisOrigin.y > bounds.top
						+ bounds.height()))) {
			return;
		}

		int unitsPerHashmark = (int) (MIN_PIXELS_PER_HASHMARK * 2 / pointsPerUnit);
		if (unitsPerHashmark == 0.0)
			unitsPerHashmark = 1;
		double pixelsPerHashmark = pointsPerUnit * unitsPerHashmark;

		boolean boundsContainsOrigin = bounds.contains(axisOrigin.x,
				axisOrigin.y);
		if (boundsContainsOrigin) {
			if ((axisOrigin.x - pixelsPerHashmark < bounds.left)
					&& (axisOrigin.x + pixelsPerHashmark > bounds.left
							+ bounds.width())
					&& (axisOrigin.y - pixelsPerHashmark < bounds.top)
					&& (axisOrigin.y + pixelsPerHashmark > bounds.top
							+ bounds.height())) {
				return;
			}
		} else {
			if ((axisOrigin.y >= bounds.top)
					&& (axisOrigin.y <= bounds.top + bounds.height())
					&& (bounds.width() <= pixelsPerHashmark)) {
				return;
			}
			if ((axisOrigin.x >= bounds.left)
					&& (axisOrigin.x <= bounds.left + bounds.width())
					&& (bounds.height() <= pixelsPerHashmark)) {
				return;
			}
		}

		Path path = new Path();

		boolean started = false;
		boolean stillGoing = true;

		for (int offset = unitsPerHashmark; !started || stillGoing; offset += unitsPerHashmark) {
			String positiveLabel = null;
			String negativeLabel = null;
			boolean drew = false;
			double scaledOffset = Math.floor(offset * pointsPerUnit);
			Point hashMarkPoint = new Point(
					(int) (axisOrigin.x + scaledOffset), axisOrigin.y);
			if (bounds.contains(hashMarkPoint.x, hashMarkPoint.y)) {
				path.moveTo(hashMarkPoint.x, hashMarkPoint.y - HASH_MARK_SIZE);
				path.lineTo(hashMarkPoint.x, hashMarkPoint.y + HASH_MARK_SIZE);
				if (positiveLabel == null)
					positiveLabel = Integer.toString(offset);
				AxesDrawer.drawString(canvas, paint, positiveLabel,
						hashMarkPoint, ANCHOR_TOP);
				drew = true;
			}
			hashMarkPoint.x = (int) (axisOrigin.x - scaledOffset);
			if (bounds.contains(hashMarkPoint.x, hashMarkPoint.y)) {
				path.moveTo(hashMarkPoint.x, hashMarkPoint.y - HASH_MARK_SIZE);
				path.lineTo(hashMarkPoint.x, hashMarkPoint.y + HASH_MARK_SIZE);
				if (boundsContainsOrigin)
					negativeLabel = positiveLabel;
				if (negativeLabel == null)
					negativeLabel = Integer
							.toString(boundsContainsOrigin ? offset : -offset);
				AxesDrawer.drawString(canvas, paint, negativeLabel,
						hashMarkPoint, ANCHOR_TOP);
				drew = true;
			}
			hashMarkPoint.x = axisOrigin.x;
			hashMarkPoint.y = (int) (axisOrigin.y - scaledOffset);
			if (bounds.contains(hashMarkPoint.x, hashMarkPoint.y)) {
				path.moveTo(hashMarkPoint.x - HASH_MARK_SIZE, hashMarkPoint.y);
				path.lineTo(hashMarkPoint.x + HASH_MARK_SIZE, hashMarkPoint.y);
				if (positiveLabel == null) {
					if (boundsContainsOrigin)
						positiveLabel = negativeLabel;
					if (positiveLabel == null)
						positiveLabel = Integer.toString(offset);
				}
				AxesDrawer.drawString(canvas, paint, positiveLabel,
						hashMarkPoint, ANCHOR_LEFT);
				drew = true;
			}
			hashMarkPoint.y = (int) (axisOrigin.y + scaledOffset);
			if (bounds.contains(hashMarkPoint.x, hashMarkPoint.y)) {
				path.moveTo(hashMarkPoint.x - HASH_MARK_SIZE, hashMarkPoint.y);
				path.lineTo(hashMarkPoint.x + HASH_MARK_SIZE, hashMarkPoint.y);
				if (negativeLabel == null) {
					if (boundsContainsOrigin)
						negativeLabel = positiveLabel;
					if (negativeLabel == null)
						negativeLabel = Integer
								.toString(boundsContainsOrigin ? offset
										: -offset);
				}
				AxesDrawer.drawString(canvas, paint, negativeLabel,
						hashMarkPoint, ANCHOR_LEFT);
				drew = true;
			}
			positiveLabel = null;
			negativeLabel = null;
			if (drew)
				started = true;
			stillGoing = drew;
		}

		canvas.drawPath(path, paint);
	}

	public static void drawAxesInRect(Canvas canvas, Paint paint, Rect bounds,
			Point axisOrigin, double pointsPerUnit) {

		Path path = new Path();
		path.moveTo(bounds.left, axisOrigin.y);
		path.lineTo(bounds.left + bounds.width(), axisOrigin.y);
		path.moveTo(axisOrigin.x, bounds.top);
		path.lineTo(axisOrigin.x, bounds.top + bounds.height());
		canvas.drawPath(path, paint);

		AxesDrawer.drawHashMarksInRect(canvas, paint, bounds, axisOrigin,
				pointsPerUnit);
	}


}
