package com.fizzikgames.simplemap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fizzikgames.fizlib.math.Vector2f;

public class MapPanel extends JPanel implements MouseListener, ChangeListener, MouseMotionListener {
	private static final long serialVersionUID = 924475455072253753L;
	private Model model;
	private JPanel dialogCenter; //stupid hack for laziness
	
	public MapPanel(Model m) {
		model = m;
		setLayout(null);
		dialogCenter = new JPanel();
		dialogCenter.setLayout(null);
		dialogCenter.setBounds(this.getWidth() / 2, this.getHeight() / 2, 1, 1);
		add(dialogCenter);
	}
	
	@Override
	public int getWidth() {
		return model.getBackgroundImage().getWidth();
	}
	
	@Override
	public int getHeight() {
		return model.getBackgroundImage().getHeight();
	}
	
	public Model getModel() {
		return model;
	}	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		Stroke defaultStroke = g2.getStroke();
		
		//Turn on anti aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Draw BG
		g2.drawImage(model.getBackgroundImage(), 0, 0, null);
		
		//Draw Polys
			//Draw Hover Fill if applicable
		if (model.getHoverPolygon() != null) {
			Color c = model.getHoverPolygon().getColor();
			g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 125));
			g2.fillPolygon(model.getHoverPolygon().getPolygon());
		}
		for (ServiceAreaPolygon e : model.getPolygons()) {
			//Construct Stroke
			BasicStroke stroke = new BasicStroke(e.getLineWidth());
			g2.setStroke(stroke);
			
			//Draw Fill
			if (model.getDisplayPolyFills()) {
				Color c = e.getColor();
				g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 125));
				g2.fillPolygon(e.getPolygon());
			}
			
			//Draw Outline
			if (model.getDisplayPolyLines()) {
				g2.setColor(e.getColor());
				g2.draw(e.getPolygon());
			}
			
			g2.setStroke(defaultStroke);
		}
		
		//Draw Plots
		for (CityPoint e : model.getCities()) {
			final int w = e.getPlotWidth();
			final int h = e.getPlotHeight();
			final int hw = w / 2;
			final int hh = h / 2;
			final int txoff = 8;
			final int tyoff = -1;
			final int x = (int) e.getLocation().x();
			final int y = (int) e.getLocation().y();
			
			if (model.getDisplayPlotPoints()) {
				g2.setColor(e.getPlotShapeColor());
				g2.fillOval(x - hw, y - hh, w, h);
				g2.drawOval(x - hw - 2, y - hh - 2, w + 4, h + 4);
				g2.drawOval(x - hw - 4, y - hh - 4, w + 8, h + 8);
				g2.setColor(e.getPlotOutlineColor());
				g2.drawOval(x - hw, y - hh, w, h);
			}
			
			if (model.getDisplayPlotLabels()) {
				g2.setFont(model.getCityFont(e.getFontSize()));
				g2.setColor(e.getPlotOutlineColor());
				g2.drawString(e.getName(), x + hw + txoff + 1, y - hh + tyoff + 1);
				g2.drawString(e.getName(), x + hw + txoff - 1, y - hh + tyoff - 1);
				g2.drawString(e.getName(), x + hw + txoff + 1, y - hh + tyoff - 1);
				g2.drawString(e.getName(), x + hw + txoff - 1, y - hh + tyoff + 1);
				g2.setColor(e.getPlotTextColor());
				g2.drawString(e.getName(), x + hw + txoff, y - hh + tyoff);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		final int button = e.getButton();
		final int relx = e.getX();
		final int rely = e.getY();
		
		if (model.getEditState() == Model.EditState.Plot) {
			if (button == MouseEvent.BUTTON1) {
				CityPoint cp = new CityPoint(new Vector2f((float) relx, (float) rely));
				cp.setPlotStyle(model.getPlotWidth(), model.getPlotHeight(), model.getCityFontSize(), model.getPlotShapeColor(), model.getPlotTextColor(), model.getPlotOutlineColor());
				model.addCity(cp);
				
				System.out.println("Plot City Point " + model.getCities().size());
				
				//move dialog center
				dialogCenter.setBounds(relx, rely, 0, 0);
				
				//dialog
				String input = (String) JOptionPane.showInputDialog(dialogCenter, "Enter City Name:", "City RSC Plotting", JOptionPane.PLAIN_MESSAGE);
				
				if ((input != null) && (input.length() > 0)) {
					cp.setName(input);
				}
				else {
					model.removeCity(cp);
				}
				
				model.notifyListeners();
			}
			else if (button == MouseEvent.BUTTON3) {
				Vector2f clickpos = new Vector2f((float) relx, (float) rely);
				
				CityPoint closest = model.findCityClosestToPoint(clickpos);
				
				final int removeDistanceThreshold = (int) Math.sqrt(closest.getPlotWidth() * closest.getPlotHeight()) + 1;
				
				if (closest != null && closest.getLocation().distance(clickpos) < removeDistanceThreshold) {
					model.removeCity(closest);
				}
				
				model.notifyListeners();
			}
		}
		else if (model.getEditState() == Model.EditState.Poly) {
			if (button == MouseEvent.BUTTON1) {
				Vector2f p = new Vector2f((float) relx, (float) rely);
				
				model.addPolygonPoint(p);
				
				model.notifyListeners();
			}
			else if (button == MouseEvent.BUTTON3) {				
				if (model.getHoverPolygon() != null) {
					model.removePolygon(model.getHoverPolygon());
					model.setHoverPolygon(null);
				}
				
				model.notifyListeners();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		repaint();
	}
	
	public void displayPlotOptionsDialog() {
		JTextField txt = null;
		JLabel lbl = null;
		JButton btn = null;
		Color color = null;
		
		final JDialog dialog = new JDialog();
		int absx = (int) this.getLocationOnScreen().getX();
		int absy = (int) this.getLocationOnScreen().getY();
		int dw = 400;
		int dh = 300;
		int hdw = dw / 2;
		int hdh = dh / 2;
		
		dialog.setLayout(null);
		dialog.setResizable(false);
		dialog.setBounds(absx + (getWidth() / 2) - hdw, absy + (getHeight() / 2) - hdh, dw, dh);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Plot Format Options");
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, dw, dh);
		
		//WIDTH HEIGHT FONTSIZE
		
		lbl = new JLabel("Plot Width:");
		lbl.setBounds(10, 5, 200, 25);
		panel.add(lbl);
		 
		txt = new JTextField(model.getPlotWidth() + "");
		txt.setBounds(75, 7, 35, 20);
		panel.add(txt);
		final JTextField plotWidth = txt;
		
		lbl = new JLabel("Plot Height:");
		lbl.setBounds(118, 5, 200, 25);
		panel.add(lbl);
		
		txt = new JTextField(model.getPlotHeight() + "");
		txt.setBounds(185, 7, 35, 20);
		panel.add(txt);
		final JTextField plotHeight = txt;
		
		lbl = new JLabel("Font Size:");
		lbl.setBounds(228, 5, 200, 25);
		panel.add(lbl);
		
		txt = new JTextField(model.getCityFontSize() + "");
		txt.setBounds(288, 7, 50, 20);
		panel.add(txt);
		final JTextField fontSize = txt;
		
		//PLOT SHAPE COLOR
		
		color = model.getPlotShapeColor();
		
		lbl = new JLabel("Point Color:");
		lbl.setBounds(10, 35, 200, 25);
		panel.add(lbl);
		
		lbl = new JLabel("R:");
		lbl.setBounds(110, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getRed() + "");
		txt.setBounds(125, 37, 35, 20);
		panel.add(txt);
		final JTextField r1 = txt;
		
		lbl = new JLabel("G:");
		lbl.setBounds(168, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getGreen() + "");
		txt.setBounds(183, 37, 35, 20);
		panel.add(txt);
		final JTextField g1 = txt;
		
		lbl = new JLabel("B:");
		lbl.setBounds(226, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getBlue() + "");
		txt.setBounds(241, 37, 35, 20);
		panel.add(txt);
		final JTextField b1 = txt;
		
		lbl = new JLabel("A:");
		lbl.setBounds(284, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getAlpha() + "");
		txt.setBounds(299, 37, 35, 20);
		panel.add(txt);
		final JTextField a1 = txt;
		
		btn = new JButton();
		btn.setBackground(color);
		btn.setBounds(345, 37, 35, 19);
		final Color tempcolor1 = color;
		final JButton cbtn1 = btn;
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JColorChooser chooser = new JColorChooser(tempcolor1);
				JDialog cdialog = JColorChooser.createDialog(dialog, "Color Picker", true, chooser, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Accept
						Color colorpicked = chooser.getColor();
						r1.setText(colorpicked.getRed() + "");
						g1.setText(colorpicked.getGreen() + "");
						b1.setText(colorpicked.getBlue() + "");
						a1.setText(colorpicked.getAlpha() + "");
						cbtn1.setBackground(colorpicked);
					}					
				}, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Cancel
					}					
				});
				
				cdialog.setVisible(true);
			}			
		});
		panel.add(btn);
		
		//PLOT TEXT COLOR
		
		color = model.getPlotTextColor();
		
		lbl = new JLabel("Text Color:");
		lbl.setBounds(10, 65, 200, 25);
		panel.add(lbl);
		
		lbl = new JLabel("R:");
		lbl.setBounds(110, 65, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getRed() + "");
		txt.setBounds(125, 67, 35, 20);
		panel.add(txt);
		final JTextField r2 = txt;
		
		lbl = new JLabel("G:");
		lbl.setBounds(168, 65, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getGreen() + "");
		txt.setBounds(183, 67, 35, 20);
		panel.add(txt);
		final JTextField g2 = txt;
		
		lbl = new JLabel("B:");
		lbl.setBounds(226, 65, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getBlue() + "");
		txt.setBounds(241, 67, 35, 20);
		panel.add(txt);
		final JTextField b2 = txt;
		
		lbl = new JLabel("A:");
		lbl.setBounds(284, 65, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getAlpha() + "");
		txt.setBounds(299, 67, 35, 20);
		panel.add(txt);
		final JTextField a2 = txt;
		
		btn = new JButton();
		btn.setBackground(color);
		btn.setBounds(345, 67, 35, 19);
		final Color tempcolor2 = color;
		final JButton cbtn2 = btn;
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JColorChooser chooser = new JColorChooser(tempcolor2);
				JDialog cdialog = JColorChooser.createDialog(dialog, "Color Picker", true, chooser, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Accept
						Color colorpicked = chooser.getColor();
						r2.setText(colorpicked.getRed() + "");
						g2.setText(colorpicked.getGreen() + "");
						b2.setText(colorpicked.getBlue() + "");
						a2.setText(colorpicked.getAlpha() + "");
						cbtn2.setBackground(colorpicked);
					}					
				}, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Cancel
					}					
				});
				
				cdialog.setVisible(true);
			}			
		});
		panel.add(btn);
		
		//PLOT OUTLINE COLOR
		
		color = model.getPlotOutlineColor();
		
		lbl = new JLabel("Outline Color:");
		lbl.setBounds(10, 95, 200, 25);
		panel.add(lbl);
		
		lbl = new JLabel("R:");
		lbl.setBounds(110, 95, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getRed() + "");
		txt.setBounds(125, 97, 35, 20);
		panel.add(txt);
		final JTextField r3 = txt;
		
		lbl = new JLabel("G:");
		lbl.setBounds(168, 95, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getGreen() + "");
		txt.setBounds(183, 97, 35, 20);
		panel.add(txt);
		final JTextField g3 = txt;
		
		lbl = new JLabel("B:");
		lbl.setBounds(226, 95, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getBlue() + "");
		txt.setBounds(241, 97, 35, 20);
		panel.add(txt);
		final JTextField b3 = txt;
		
		lbl = new JLabel("A:");
		lbl.setBounds(284, 95, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getAlpha() + "");
		txt.setBounds(299, 97, 35, 20);
		panel.add(txt);
		final JTextField a3 = txt;
		
		btn = new JButton();
		btn.setBackground(color);
		btn.setBounds(345, 97, 35, 19);
		final Color tempcolor3 = color;
		final JButton cbtn3 = btn;
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JColorChooser chooser = new JColorChooser(tempcolor3);
				JDialog cdialog = JColorChooser.createDialog(dialog, "Color Picker", true, chooser, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Accept
						Color colorpicked = chooser.getColor();
						r3.setText(colorpicked.getRed() + "");
						g3.setText(colorpicked.getGreen() + "");
						b3.setText(colorpicked.getBlue() + "");
						a3.setText(colorpicked.getAlpha() + "");
						cbtn3.setBackground(colorpicked);
					}					
				}, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Cancel
					}					
				});
				
				cdialog.setVisible(true);
			}			
		});
		panel.add(btn);
		
		//APPLY BUTTON
		btn = new JButton("Apply");
		btn.setBounds(8, 242, 100, 25);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setPlotWidth(Integer.valueOf(plotWidth.getText()));
				model.setPlotHeight(Integer.valueOf(plotHeight.getText()));
				model.setCityFontSize(Float.valueOf(fontSize.getText()));
				model.setPlotShapeColor(new Color(
						Integer.valueOf(r1.getText()),
						Integer.valueOf(g1.getText()),
						Integer.valueOf(b1.getText()),
						Integer.valueOf(a1.getText())));
				model.setPlotTextColor(new Color(
						Integer.valueOf(r2.getText()),
						Integer.valueOf(g2.getText()),
						Integer.valueOf(b2.getText()),
						Integer.valueOf(a2.getText())));
				model.setPlotOutlineColor(new Color(
						Integer.valueOf(r3.getText()),
						Integer.valueOf(g3.getText()),
						Integer.valueOf(b3.getText()),
						Integer.valueOf(a3.getText())));
				model.notifyListeners();
				dialog.dispose();
			}			
		});
		panel.add(btn);
		
		//CANCEL BUTTON
		btn = new JButton("Cancel");
		btn.setBounds(286, 242, 100, 25);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}			
		});
		panel.add(btn);
		
		dialog.add(panel);
		dialog.revalidate();
		dialog.setVisible(true);
	}
	
	public void displayPolyOptionsDialog() {
		JTextField txt = null;
		JLabel lbl = null;
		JButton btn = null;
		Color color = null;
		
		final JDialog dialog = new JDialog();
		int absx = (int) this.getLocationOnScreen().getX();
		int absy = (int) this.getLocationOnScreen().getY();
		int dw = 400;
		int dh = 300;
		int hdw = dw / 2;
		int hdh = dh / 2;
		
		dialog.setLayout(null);
		dialog.setResizable(false);
		dialog.setBounds(absx + (getWidth() / 2) - hdw, absy + (getHeight() / 2) - hdh, dw, dh);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Polygon Format Options");
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, dw, dh);
		
		//WIDTH HEIGHT FONTSIZE
		
		lbl = new JLabel("Line Width:");
		lbl.setBounds(10, 5, 200, 25);
		panel.add(lbl);
		 
		txt = new JTextField(model.getPolyLineWidth() + "");
		txt.setBounds(75, 7, 35, 20);
		panel.add(txt);
		final JTextField lineWidth = txt;
		
		//PLOT LINE COLOR
		
		color = model.getPolyLineColor();
		
		lbl = new JLabel("Line Color:");
		lbl.setBounds(10, 35, 200, 25);
		panel.add(lbl);
		
		lbl = new JLabel("R:");
		lbl.setBounds(110, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getRed() + "");
		txt.setBounds(125, 37, 35, 20);
		panel.add(txt);
		final JTextField r1 = txt;
		
		lbl = new JLabel("G:");
		lbl.setBounds(168, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getGreen() + "");
		txt.setBounds(183, 37, 35, 20);
		panel.add(txt);
		final JTextField g1 = txt;
		
		lbl = new JLabel("B:");
		lbl.setBounds(226, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getBlue() + "");
		txt.setBounds(241, 37, 35, 20);
		panel.add(txt);
		final JTextField b1 = txt;
		
		lbl = new JLabel("A:");
		lbl.setBounds(284, 35, 200, 25);
		panel.add(lbl);		
		
		txt = new JTextField(color.getAlpha() + "");
		txt.setBounds(299, 37, 35, 20);
		panel.add(txt);
		final JTextField a1 = txt;
		
		btn = new JButton();
		btn.setBackground(color);
		btn.setBounds(345, 37, 35, 19);
		final Color tempcolor1 = color;
		final JButton cbtn1 = btn;
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JColorChooser chooser = new JColorChooser(tempcolor1);
				JDialog cdialog = JColorChooser.createDialog(dialog, "Color Picker", true, chooser, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Accept
						Color colorpicked = chooser.getColor();
						r1.setText(colorpicked.getRed() + "");
						g1.setText(colorpicked.getGreen() + "");
						b1.setText(colorpicked.getBlue() + "");
						a1.setText(colorpicked.getAlpha() + "");
						cbtn1.setBackground(colorpicked);
					}					
				}, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Cancel
					}					
				});
				
				cdialog.setVisible(true);
			}			
		});
		panel.add(btn);
		
		//APPLY BUTTON
		btn = new JButton("Apply");
		btn.setBounds(8, 242, 100, 25);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setPolyLineWidth(Float.valueOf(lineWidth.getText()));
				model.setPolyLineColor(new Color(
						Integer.valueOf(r1.getText()),
						Integer.valueOf(g1.getText()),
						Integer.valueOf(b1.getText()),
						Integer.valueOf(a1.getText())));
				model.notifyListeners();
				dialog.dispose();
			}			
		});
		panel.add(btn);
		
		//CANCEL BUTTON
		btn = new JButton("Cancel");
		btn.setBounds(286, 242, 100, 25);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}			
		});
		panel.add(btn);
		
		dialog.add(panel);
		dialog.revalidate();
		dialog.setVisible(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		final int mousex = e.getX();
		final int mousey = e.getY();
		
		if (model.getEditState() == Model.EditState.Poly) {
			Vector2f p = new Vector2f((float) mousex, (float) mousey);
			ServiceAreaPolygon poly = model.getPolygonContainingPoint(p);
			
			model.setHoverPolygon(poly);
			
			model.notifyListeners();
		}
		else {
			model.setHoverPolygon(null);
		}
	}
}
