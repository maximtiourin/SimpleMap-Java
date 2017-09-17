package com.fizzikgames.simplemap;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBoxMenuItem;

/**
 * Quick and Dirty implementation of a simple business map software
 * @author Maxim Tiourin
 * @version 1.00
 */
public class Viewer extends JFrame implements ChangeListener, KeyListener {
	private static final long serialVersionUID = 5800762002767776331L;
	private static String TITLE = "\"Simple Map\"";
	private static String AUTHOR = "Maxim Tiourin";
	private static String VERSION = "v0.4.2"; //major.minor.miniscule
	private Model model;
	private JFrame frame;
	private JPanel contentPane;
	private MapPanel mapPanel;
	private JMenuItem mntmPlotFormat;
	private JMenuItem mntmPolygonFormat;
	private JMenuItem mntmSave;
	private JMenuItem mntmExportImage;
	private JRadioButtonMenuItem rdbtnmntmCityPlotting;
	private JRadioButtonMenuItem rdbtnmntmServiceAreaDrawing;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Viewer frame = new Viewer(new Model());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Viewer(Model m) {
		rdbtnmntmServiceAreaDrawing = null;
		rdbtnmntmCityPlotting = null;
		mntmExportImage = null;
		mntmSave = null;
		mntmPolygonFormat = null;
		mntmPlotFormat = null;
		mapPanel = null;
		model = m;
		frame = this;
		
		final int width = 800;
		final int height = 600;
		
		model.addListener(this);
		this.addKeyListener(this);
		
		setTitle(TITLE + " by " + AUTHOR + " " + VERSION + "     (Tool: City Plotting)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds((((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2) - (width / 2), 
				(((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()) / 2) - (height / 2), width, height);
		setResizable(false);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose Map Background Image");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File (png, gif, jpg)", "png", "jpg", "jpeg", "gif");
				fc.setFileFilter(filter);
				int result = fc.showOpenDialog(frame);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					mntmPlotFormat.setEnabled(true);
					mntmPolygonFormat.setEnabled(true);
					mntmSave.setEnabled(true);
					mntmExportImage.setEnabled(true);
					if (mapPanel == null) {
						File file = fc.getSelectedFile();
						
						model.loadBackgroundImage(file);
						
						final BufferedImage bimg = model.getBackgroundImage();
						final int w = bimg.getWidth() + 7;
						final int h = bimg.getHeight() + 49;
						frame.setBounds((((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2) - (w / 2), 
								(((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()) / 2) - (h / 2), w, h);
						
						mapPanel = new MapPanel(model);
						contentPane.addMouseListener(mapPanel);
						contentPane.addMouseMotionListener(mapPanel);
						contentPane.add(mapPanel);
					}
					else {
						File file = fc.getSelectedFile();
						
						model.clear();
						
						model.loadBackgroundImage(file);
						
						final BufferedImage bimg = model.getBackgroundImage();
						final int w = bimg.getWidth() + 7;
						final int h = bimg.getHeight() + 49;
						frame.setBounds((((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2) - (w / 2), 
								(((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()) / 2) - (h / 2), w, h);
					}					
				}
			}
		});
		mnFile.add(mntmNew);
		
		JMenuItem mntmLoad = new JMenuItem("Load");
		mntmLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose File To Open");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Simple Map File (smap)", "smap");
				fc.setFileFilter(filter);
				int result = fc.showOpenDialog(frame);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					mntmPlotFormat.setEnabled(true);
					mntmPolygonFormat.setEnabled(true);
					mntmSave.setEnabled(true);
					mntmExportImage.setEnabled(true);
					if (mapPanel == null) {
						File file = fc.getSelectedFile();
						
						model.load(file);
						
						final BufferedImage bimg = model.getBackgroundImage();
						final int w = bimg.getWidth() + 7;
						final int h = bimg.getHeight() + 49;
						frame.setBounds((((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2) - (w / 2), 
								(((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()) / 2) - (h / 2), w, h);
						
						mapPanel = new MapPanel(model);
						contentPane.addMouseListener(mapPanel);
						contentPane.addMouseMotionListener(mapPanel);
						contentPane.add(mapPanel);
					}
					else {
						File file = fc.getSelectedFile();
						
						model.clear();
						
						model.load(file);
						
						final BufferedImage bimg = model.getBackgroundImage();
						final int w = bimg.getWidth() + 7;
						final int h = bimg.getHeight() + 49;
						frame.setBounds((((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2) - (w / 2), 
								(((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()) / 2) - (h / 2), w, h);
					}			
				}
			}			
		});
		mnFile.add(mntmLoad);
		
		mntmSave = new JMenuItem("Save");
		mntmSave.setEnabled(false);
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose File Save Location");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Simple Map File (smap)", "smap");
				fc.setFileFilter(filter);
				int result = fc.showSaveDialog(frame);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					model.save(fc.getSelectedFile());				
				}
			}			
		});
		mnFile.add(mntmSave);
		
		mntmExportImage = new JMenuItem("Export Image");
		mntmExportImage.setEnabled(false);
		mntmExportImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Choose Image Export Location");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Image (png)", "png");
				fc.setFileFilter(filter);
				int result = fc.showSaveDialog(frame);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					BufferedImage img = new BufferedImage(model.getBackgroundImage().getWidth(), model.getBackgroundImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
					mapPanel.paintComponent(img.getGraphics());
					try {
						if (fc.getSelectedFile().getName().contains(".png")) {
							ImageIO.write(img, "png", fc.getSelectedFile());
						}
						else {
							ImageIO.write(img, "png", new File(fc.getSelectedFile().getName() + ".png"));
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}				
				}
			}			
		});
		mnFile.add(mntmExportImage);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		mntmPlotFormat = new JMenuItem("Plot Format (F1)");
		mntmPlotFormat.setEnabled(false);
		mntmPlotFormat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPanel.displayPlotOptionsDialog();
			}
		});
		mnEdit.add(mntmPlotFormat);
		
		mntmPolygonFormat = new JMenuItem("Polygon Format (F2)");
		mntmPolygonFormat.setEnabled(false);
		mntmPolygonFormat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPanel.displayPolyOptionsDialog();
			}
		});
		mnEdit.add(mntmPolygonFormat);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		rdbtnmntmCityPlotting = new JRadioButtonMenuItem("City Plotting (1)");
		rdbtnmntmCityPlotting.setSelected(true);
		rdbtnmntmCityPlotting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setEditState(Model.EditState.Plot);
				frame.setTitle(TITLE + " by " + AUTHOR + " " + VERSION + "     (Tool: City Plotting)");
				rdbtnmntmCityPlotting.setSelected(true);
				rdbtnmntmServiceAreaDrawing.setSelected(false);
			}			
		});
		mnTools.add(rdbtnmntmCityPlotting);
		
		rdbtnmntmServiceAreaDrawing = new JRadioButtonMenuItem("Service Area Drawing (2)");
		rdbtnmntmServiceAreaDrawing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setEditState(Model.EditState.Poly);
				frame.setTitle(TITLE + " by " + AUTHOR + " " + VERSION + "     (Tool: Service Area Drawing)");
				rdbtnmntmCityPlotting.setSelected(false);
				rdbtnmntmServiceAreaDrawing.setSelected(true);
			}			
		});
		mnTools.add(rdbtnmntmServiceAreaDrawing);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		final JCheckBoxMenuItem chckbxmntmPlotPoints = new JCheckBoxMenuItem("Plot Points");
		chckbxmntmPlotPoints.setSelected(model.getDisplayPlotPoints());
		chckbxmntmPlotPoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setDisplayPlotPoints(chckbxmntmPlotPoints.isSelected());
			}			
		});
		mnView.add(chckbxmntmPlotPoints);
		
		final JCheckBoxMenuItem chckbxmntmPlotLabels = new JCheckBoxMenuItem("Plot Labels");
		chckbxmntmPlotLabels.setSelected(model.getDisplayPlotLabels());
		chckbxmntmPlotLabels.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setDisplayPlotLabels(chckbxmntmPlotLabels.isSelected());
			}			
		});
		mnView.add(chckbxmntmPlotLabels);
		
		final JCheckBoxMenuItem chckbxmntmServiceAreaLines = new JCheckBoxMenuItem("Service Area Lines");
		chckbxmntmServiceAreaLines.setSelected(model.getDisplayPolyLines());
		chckbxmntmServiceAreaLines.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setDisplayPolyLines(chckbxmntmServiceAreaLines.isSelected());
			}			
		});
		mnView.add(chckbxmntmServiceAreaLines);
		
		final JCheckBoxMenuItem chckbxmntmServiceAreaFills = new JCheckBoxMenuItem("Service Area Fills");
		chckbxmntmServiceAreaFills.setSelected(model.getDisplayPolyFills());
		chckbxmntmServiceAreaFills.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setDisplayPolyFills(chckbxmntmServiceAreaFills.isSelected());
			}			
		});
		mnView.add(chckbxmntmServiceAreaFills);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("content pane mouse event");
			}
		});
		setContentPane(contentPane);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (mntmPlotFormat.isEnabled() && e.getKeyCode() == KeyEvent.VK_F1) {
			mapPanel.displayPlotOptionsDialog();
		}
		else if (mntmPolygonFormat.isEnabled() && e.getKeyCode() == KeyEvent.VK_F2) {
			mapPanel.displayPolyOptionsDialog();
		}
		else if (e.getKeyCode() == KeyEvent.VK_1) {
			rdbtnmntmCityPlotting.doClick();
		}
		else if (e.getKeyCode() == KeyEvent.VK_2) {
			rdbtnmntmServiceAreaDrawing.doClick();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
