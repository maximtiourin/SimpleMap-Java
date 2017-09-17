package com.fizzikgames.simplemap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.DatatypeConverter;

import com.fizzikgames.fizlib.math.Vector2f;
import com.fizzikgames.fizlib.util.StringUtil;

public class Model {
	public enum EditState {
		Plot, Poly;
	}
	
	private BufferedImage bgImage;
	private ArrayList<CityPoint> cities;
	private ArrayList<ChangeListener> listeners;
	private HashMap<String, ServiceAreaPolygon> polygons;
	private HashMap<Float, Font> cityFonts;
	private float cityFontSize;
	private int plotWidth;
	private int plotHeight;
	private float polyLineWidth;
	private Color plotShapeColor;
	private Color plotTextColor;
	private Color plotOutlineColor;
	private Color polyLineColor;
	private EditState editState;
	private boolean displayPlotPoints;
	private boolean displayPlotLabels;
	private boolean displayPolyLines;
	private boolean displayPolyFills;
	private ServiceAreaPolygon hoverPoly;
	
	public Model() {		
		bgImage = null;
		cities = new ArrayList<CityPoint>();
		listeners = new ArrayList<ChangeListener>();
		polygons = new HashMap<String, ServiceAreaPolygon>();
		cityFonts = new HashMap<Float, Font>();
		cityFontSize = 16f;
		plotWidth = 16;
		plotHeight = 16;
		polyLineWidth = 4f;
		plotShapeColor = Color.BLACK;
		plotTextColor = Color.BLACK;
		plotOutlineColor = new Color(255, 255, 255, 150);
		polyLineColor = Color.RED;
		editState = EditState.Plot;
		hoverPoly = null;
		displayPlotPoints = true;
		displayPlotLabels = true;
		displayPolyLines = true;
		displayPolyFills = true;
	}
	
	public void addCity(CityPoint cp) {
		cities.add(cp);
		notifyListeners();
	}
	
	public void removeCity(CityPoint cp) {
		cities.remove(cp);
		notifyListeners();
	}
	
	/**
	 * Returns the city point closest to the given point vector
	 */
	public CityPoint findCityClosestToPoint(Vector2f v) {
		CityPoint nearcity = null;
		float nearest = Float.MAX_VALUE;
		
		for (CityPoint e : cities) {
			float distance = e.getLocation().distance(v);
			if (distance < nearest) {
				nearest = distance;
				nearcity = e;
			}
		}
		
		return nearcity;
	}
	
	public float getCityFontSize() {
		return cityFontSize;
	}
	
	public void setCityFontSize(float f) {
		cityFontSize = f;
		notifyListeners();
	}
	
	/**
	 * Returns the city font with the desired size, uses a hashmap to cache
	 * previous sizes
	 * @param size
	 * @return
	 */
	public Font getCityFont(float size) {
		if (cityFonts.containsKey(size)) {
			return cityFonts.get(size);
		}
		else {
			try {
				Font font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/expressway.ttf")).deriveFont(size);
				cityFonts.put(size, font);
				return font;
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} //half assed resource management
		}
		
		return null;
	}
	
	/**
	 * Attempts to add the polygon point to an existing polygon with the same color, otherwise
	 * creates a new one.
	 */
	public void addPolygonPoint(Vector2f v) {
		Color c = polyLineColor;
		String hash = ServiceAreaPolygon.getColorHash(c);
		
		if (polygons.containsKey(hash)) {
			polygons.get(hash).addPoint(v);
		}
		else {
			ServiceAreaPolygon sap = new ServiceAreaPolygon();
			sap.setColor(getPolyLineColor());
			sap.setLineWidth(getPolyLineWidth());
			sap.addPoint(v);
			polygons.put(hash, sap);
		}
		
		notifyListeners();
	}
	
	/**
	 * Returns a list of all polygons in the set
	 */
	public List<ServiceAreaPolygon> getPolygons() {
		ArrayList<ServiceAreaPolygon> tlist = new ArrayList<ServiceAreaPolygon>();
		
		for (ServiceAreaPolygon e : polygons.values()) {
			tlist.add(e);
		}
		
		return tlist;
	}
	
	/**
	 * Returns the polygon that contains the given point if there is one
	 */
	public ServiceAreaPolygon getPolygonContainingPoint(Vector2f v) {
		for (ServiceAreaPolygon e : getPolygons()) {
			if (e.getPolygon().contains((int) v.x(), (int) v.y())) {
				return e;
			}
		}
		
		return null;
	}
	
	public void removePolygon(ServiceAreaPolygon poly) {
		String hash = ServiceAreaPolygon.getColorHash(poly.getColor());
		
		polygons.remove(hash);
		
		notifyListeners();
	}
	
	public int getPlotWidth() {
		return plotWidth;
	}
	
	public void setPlotWidth(int w) {
		plotWidth = w;
		notifyListeners();
	}
	
	public int getPlotHeight() {
		return plotHeight;
	}
	
	public void setPlotHeight(int h) {
		plotHeight = h;
		notifyListeners();
	}
	
	public Color getPlotShapeColor() {
		return plotShapeColor;
	}
	
	public void setPlotShapeColor(Color c) {
		plotShapeColor = c;
		notifyListeners();
	}
	
	public Color getPlotTextColor() {
		return plotTextColor;
	}
	
	public void setPlotTextColor(Color c) {
		plotTextColor = c;
		notifyListeners();
	}
	
	public Color getPlotOutlineColor() {
		return plotOutlineColor;
	}
	
	public void setPlotOutlineColor(Color c) {
		plotOutlineColor = c;
		notifyListeners();
	}
	
	public Color getPolyLineColor() {
		return polyLineColor;
	}
	
	public void setPolyLineColor(Color c) {
		polyLineColor = c;
		notifyListeners();
	}
	
	public float getPolyLineWidth() {
		return polyLineWidth;
	}
	
	public void setPolyLineWidth(float w) {
		polyLineWidth = w;
		notifyListeners();
	}
	
	public ServiceAreaPolygon getHoverPolygon() {
		return hoverPoly;
	}
	
	public void setHoverPolygon(ServiceAreaPolygon poly) {
		hoverPoly = poly;
		notifyListeners();
	}
	
	public boolean getDisplayPlotPoints() {
		return displayPlotPoints;
	}
	
	public void setDisplayPlotPoints(boolean b) {
		displayPlotPoints = b;
		notifyListeners();
	}
	
	public boolean getDisplayPlotLabels() {
		return displayPlotLabels;
	}
	
	public void setDisplayPlotLabels(boolean b) {
		displayPlotLabels = b;
		notifyListeners();
	}
	
	public boolean getDisplayPolyLines() {
		return displayPolyLines;
	}
	
	public void setDisplayPolyLines(boolean b) {
		displayPolyLines = b;
		notifyListeners();
	}
	
	public boolean getDisplayPolyFills() {
		return displayPolyFills;
	}
	
	public void setDisplayPolyFills(boolean b) {
		displayPolyFills = b;
		notifyListeners();
	}
	
	public List<CityPoint> getCities() {
		return cities;
	}
	
	public EditState getEditState() {
		return editState;
	}
	
	public void setEditState(EditState s) {
		editState = s;
		notifyListeners();
	}
	
	public void addListener(ChangeListener l) {
		listeners.add(l);
	}
	
	public void notifyListeners() {
		for (ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}
	
	public void removeListener(ChangeListener l) {
		listeners.remove(l);
	}
	
	public void loadBackgroundImage(File file) {
		try {
			bgImage = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		notifyListeners();
	}
	
	public BufferedImage getBackgroundImage() {
		return bgImage;
	}
	
	public void clear() {
		cities.clear();
		polygons.clear();
		bgImage = null;
		hoverPoly = null;
	}
	
	public void save(File file) {
		try {
			String output = "";
			String ml = "_";
			String eol = "=";
			
			//Push BG Image
			output += "[bgimage]";
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();			
			ImageIO.write(getBackgroundImage(), "png", baos);
			
			byte[] bytes = baos.toByteArray();
			String imagestring = DatatypeConverter.printBase64Binary(bytes);			
			baos.close();
			
			output += "" + imagestring;
			
			//Push Plot Points
			output += "[plot]" + this.getCities().size() + eol;
			
			for (CityPoint e : this.getCities()) {
				output += ((int) e.getLocation().x()) + ml
						+ ((int) e.getLocation().y()) + ml
						+ e.getName() + ml
						+ ((int) e.getPlotWidth()) + ml
						+ ((int)e.getPlotHeight()) + ml
						+ e.getFontSize() + ml
						+ e.getPlotShapeColor().getRGB() + ml
						+ e.getPlotTextColor().getRGB() + ml
						+ e.getPlotOutlineColor().getRGB() + eol;
			}
			
			//Push Poly Points
			output += "[poly]" + this.getPolygons().size() + eol;
			
			for (ServiceAreaPolygon e : this.getPolygons()) {
				output += e.getLineWidth() + ml
						+ e.getColor().getRGB() + ml
						+ e.getPolygon().npoints + ml;
						
				for (int i = 0; i < e.getPolygon().npoints; i++) {
					output += ((int) e.getPolygon().xpoints[i]) + ml
							+ ((int) e.getPolygon().ypoints[i]) + ml;
				}
				
				output += eol;
			}
			
			if (file.getName().contains(".smap")) {
				PrintWriter out = new PrintWriter(file);
				out.println(output);
				out.close();
			}
			else {
				PrintWriter out = new PrintWriter(new File(file.getName() + ".smap"));
				out.println(output);
				out.close();
			}
			/*FileOutputStream fos = new FileOutputStream(file);
			GZIPOutputStream gzout = new GZIPOutputStream(fos);
			gzout.write(output.getBytes("UTF-8"));
			gzout.close();*/
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		notifyListeners();
	}
	
	public void load(File file) {
		String input = "";
		String ml = "_";
		String eol = "=";
		String s2, s3;
		
		try {
			Scanner in = new Scanner(new FileReader(file));
			
			String s = in.nextLine();
			input += s;
			while (in.hasNextLine()) {
				s = in.nextLine();
				input += s;
			}
			
			in.close();
			
			String rest = input;
			
			//Pull BG Image
			s = StringUtil.substring(rest, "[bgimage]", "[plot]", true);
			byte[] imagebytes = DatatypeConverter.parseBase64Binary(s);
			ByteArrayInputStream bais = new ByteArrayInputStream(imagebytes);
			bgImage = ImageIO.read(bais);
			//ImageIO.write(bgImage, "png", new File("testload.png"));
			bais.close();
			
			//Pull Plot Information
			s = StringUtil.substring(rest, "[plot]", "[poly]", true);
			s2 = StringUtil.trimSubstring(s, eol, true)[0];
			s3 = StringUtil.substring(s, eol, true);
			final int cityCount = Integer.valueOf(s2);
			
			String stemp = s3;
			for (int i = 0; i < cityCount; i++) { 
				String[] cut = new String[]{"", stemp};
				
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final int x = Integer.valueOf(cut[0]);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final int y = Integer.valueOf(cut[0]);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final String name = cut[0];
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final int w = Integer.valueOf(cut[0]);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final int h = Integer.valueOf(cut[0]);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final float fontsize = Float.valueOf(cut[0]);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final Color shapeColor = new Color(Integer.valueOf(cut[0]), true);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final Color textColor = new Color(Integer.valueOf(cut[0]), true);
				cut = StringUtil.trimSubstring(cut[1], eol, true);
				final Color outlineColor = new Color(Integer.valueOf(cut[0]), true);
				
				CityPoint cp = new CityPoint(new Vector2f((float) x, (float) y), name);
				cp.setPlotStyle(w, h, fontsize, shapeColor, textColor, outlineColor);
				this.addCity(cp);
				
				stemp = cut[1];
			}
			
			//Pull Poly Information
			s = StringUtil.substring(rest, "[poly]", true);
			s2 = StringUtil.trimSubstring(s, eol, true)[0];
			s3 = StringUtil.substring(s, eol, true);
			final int polyCount = Integer.valueOf(s2);
			
			stemp = s3;
			for (int i = 0; i < polyCount; i++) {
				ServiceAreaPolygon sap = new ServiceAreaPolygon();
				
				String[] cut = new String[]{"", stemp};
				
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final float lineWidth = Float.valueOf(cut[0]);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final Color polyColor = new Color(Integer.valueOf(cut[0]), true);
				cut = StringUtil.trimSubstring(cut[1], ml, true);
				final int pointCount = Integer.valueOf(cut[0]);
				
				sap.setLineWidth(lineWidth);
				sap.setColor(polyColor);
				
				int[] polyxps = new int[pointCount];
				int[] polyyps = new int[pointCount];
				String stemp2 = cut[1];
				for (int ii = 0; ii < pointCount; ii++) {
					String[] ccut = new String[]{"", stemp2};
					
					ccut = StringUtil.trimSubstring(ccut[1], ml, true);
					polyxps[ii] = Integer.valueOf(ccut[0]);
					ccut = StringUtil.trimSubstring(ccut[1], ml, true);
					polyyps[ii] = Integer.valueOf(ccut[0]);
					
					sap.addPoint(new Vector2f((float) polyxps[ii], (float) polyyps[ii]));
					
					stemp2 = ccut[1];
				}
				
				cut = StringUtil.trimSubstring(stemp2, eol, true);
				
				String polyhash = ServiceAreaPolygon.getColorHash(sap.getColor());
				polygons.put(polyhash, sap);
				
				stemp = cut[1];
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		notifyListeners();
	}
}
