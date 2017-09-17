package com.fizzikgames.simplemap;

import java.awt.Color;
import java.awt.Polygon;

import com.fizzikgames.fizlib.math.Vector2f;

public class ServiceAreaPolygon {
	private Color color;
	private Polygon poly;
	private float lineWidth;
	
	public ServiceAreaPolygon() {
		color = null;
		lineWidth = 1;
		poly = new Polygon();
	}
	
	public Polygon getPolygon() {
		return poly;
	}
	
	public void addPoint(Vector2f v) {
		poly.addPoint((int) v.x(), (int) v.y());
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public float getLineWidth() {
		return lineWidth;
	}
	
	public void setLineWidth(float w) {
		lineWidth = w;
	}
	
	public static String getColorHash(Color c) {
		return "" + c.getRed() + c.getBlue() + c.getGreen() + c.getAlpha();
	}
}
