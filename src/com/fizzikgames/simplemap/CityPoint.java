package com.fizzikgames.simplemap;

import java.awt.Color;

import com.fizzikgames.fizlib.math.Vector2f;

public class CityPoint {
	private Vector2f location;
	private String name;
	private int plotWidth;
	private int plotHeight;
	private Color plotShapeColor;
	private Color plotTextColor;
	private Color plotOutlineColor;
	private float fontsize;
	
	public CityPoint() {
		location = new Vector2f();
		name = "";
	}
	
	public CityPoint(Vector2f location) {
		this.location = location.copy();
		name = "";
	}
	
	public CityPoint(Vector2f location, String name) {
		this.location = location.copy();
		this.name = name;
	}
	
	public Vector2f getLocation() {
		return location.copy();
	}
	
	public void setLocation(Vector2f location) {
		this.location = location.copy();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public float getFontSize() {
		return fontsize;
	}
	
	public Color getPlotShapeColor() {
		return plotShapeColor;
	}
	
	public Color getPlotTextColor() {
		return plotTextColor;
	}
	
	public Color getPlotOutlineColor() {
		return plotOutlineColor;
	}
	
	public int getPlotWidth() {
		return plotWidth;
	}
	
	public int getPlotHeight() {
		return plotHeight;
	}
	
	public void setPlotStyle(int w, int h, float fs, Color shape, Color text, Color outline) {
		plotWidth = w;
		plotHeight = h;
		fontsize = fs;
		plotShapeColor = shape;
		plotTextColor = text;
		plotOutlineColor = outline;
	}
}
