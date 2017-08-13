//Bowen Gui
//version 1.0.3

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.awt.event.ItemEvent;

//this is the updated Homework4 class for Homework5
public class Homework5 extends JFrame {

	private JLabel statusBar;
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu file;
	private JMenuItem importMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem exitMenuItem;
	private JMenu view;
	private JRadioButtonMenuItem photoView;
	private JRadioButtonMenuItem gridView;
	private JRadioButtonMenuItem splitView;
	private JRadioButtonMenuItem magnetView;
	private ButtonGroup viewsExclusive;
	private JPanel centerPanel;
	private JPanel bottomPanel;
	private JPanel leftPanel;
	private JToggleButton toggleButton1;
	private JToggleButton toggleButton2;
	private JToggleButton toggleButton3;
	private JToggleButton toggleButton4;
	private JRadioButton drawingButton;
	private JRadioButton textButton;
	private ButtonGroup drawingTextExclusive;
	private JPanel pDrawingText;
	private JButton nextButton;
	private JButton previousButton;
	private JPanel pNextPrevious;
	private JScrollPane jScrollPane;
	private ImageIcon loadedImage;
	private JLabel centerLabel;
	private String path = "";
	private ImageIcon backgroundImage;
	private JPanel panels = null; //cardlayout
	private JPanel panel1; //imagePanel
	private LightTable lightTable;
	private CardLayout cardLayout;
	private BufferedImage importedImage;
	private String text = "";
	private boolean onText;
	private int mode = 0; //what mode pictures are seen

	//details about the picture
	private ArrayList<Integer> picWidths = new ArrayList<Integer>(); //should be the same index as the corresponding drawingPanel
	private ArrayList<Integer> picHeights = new ArrayList<Integer>();

	private JPanel grey; //when no pictures
	private boolean previousNextAction = false;


	//all the control classes that takes command from users
	class importAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("import");
			String direction = System.getProperty("user.dir");
			File userDir = new File(direction);
			JFileChooser file = new JFileChooser(userDir);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("image files", new String[] {"jpg", "png", "gif"});
			file.addChoosableFileFilter(filter);
			file.setFileFilter(filter);
			int result = file.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = file.getSelectedFile();
				path = selectedFile.getAbsolutePath(); //gets the path as string
				DrawingPanel drawtemp = new DrawingPanel(true,true, lightTable);
				drawtemp.setPath(path);
				lightTable.addDrawingPanel(drawtemp); //adds the drawingPanel
				jScrollPane.setViewportView(lightTable.change(mode));
				picWidths.add(drawtemp.getPicWidth());
				picHeights.add(drawtemp.getPicHeight());
				if(picHeights.size() > 6) {
					frame.setPreferredSize(changeWindowSize());
				} else {
					frame.setSize(changeWindowSize());
				}

			}
		}
	}



	private Dimension changeWindowSize() {
		Dimension retDimension = new Dimension();
		if (mode == 0) {
			retDimension.setSize(getPicWidth() + 195, getPicHeight() + 117);
		} else if (mode == 1) {
			if (picHeights.size() > 6) {
				retDimension.setSize(new Dimension(540 + 195, 450));
			} else {
				int width = ((picWidths.size() + 1) / 2) * 180;
				retDimension.setSize(width + 195, 450);
			}
		} else if (mode == 2) {
			retDimension.setSize(600,600);
		} else if (mode == 3) {
			retDimension.setSize(700,500);
		}
		return retDimension;
	}

	public void noPicture() {
		mode = 0;
		toggleButton1.setSelected(false);
		toggleButton2.setSelected(false);
		toggleButton3.setSelected(false);
		toggleButton4.setSelected(false);
		photoView.setSelected(true);
		jScrollPane.setViewportView(grey);
		frame.setSize(500,270);
	}

	public void updateThumb() { //updates from clicking on thumb
		if(lightTable.getCurrentDP().isFamily()) {
			toggleButton1.setSelected(true);
		} else {
			toggleButton1.setSelected(false);
		}

		if(lightTable.getCurrentDP().isVacation()) {
			toggleButton2.setSelected(true);
		} else {
			toggleButton2.setSelected(false);
		}

		if(lightTable.getCurrentDP().isWork()) {
			toggleButton3.setSelected(true);
		} else {
			toggleButton3.setSelected(false);
		}

		if(lightTable.getCurrentDP().isSchool()) {
			toggleButton4.setSelected(true);
		} else {
			toggleButton4.setSelected(false);
		}	
	}

	public int getPicWidth() {
		return picWidths.get(lightTable.getCurrentDPindex());
	}

	public int getPicHeight() {
		return picHeights.get(lightTable.getCurrentDPindex());
	}

	class deleteAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("delete");
			int index = lightTable.getCurrentDPindex();
			picWidths.remove(index);
			picHeights.remove(index);
			lightTable.delete(mode);
			if(lightTable.hasPicture()) {
				jScrollPane.setViewportView(lightTable.change(mode));
				frame.setSize(changeWindowSize());
				updateThumb();
			} else {
				noPicture();
			}
		}
	}

	class exitAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	class photoViewAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("photoView");
			if(!(lightTable.isEmpty())) {
				mode = 0;
				jScrollPane.setViewportView(lightTable.change(mode));
				frame.setSize(changeWindowSize());
			} else {
				//mode = 0;
			}
		}
	}

	class gridViewAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("gridView");
			mode = 1; //can change mode EVEN IF there is no imported pictures!!!!
			if(!(lightTable.isEmpty())) {
				jScrollPane.add(lightTable.change(mode));
				jScrollPane.setViewportView(lightTable.change(mode));
				frame.setSize(changeWindowSize());
			}	
		}
	}

	class splitViewAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("splitView");
			mode = 2;
			if(!(lightTable.isEmpty())) {
				JPanel split = new JPanel();
				jScrollPane.setViewportView(lightTable.change(mode));
				frame.setSize(changeWindowSize());
			}
		}
	}

	class magnetViewAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("magnet view");
			mode = 3;
			if(!(lightTable.isEmpty())) {
				lightTable.addThumbnailsToMagnet();
				jScrollPane.setViewportView(lightTable.getMagnetLayout());
				frame.setSize(changeWindowSize());
			}
		}
	}

	class tB1Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("family");
		}
	}

	class tB2Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("vacation");
		}
	}

	class tB3Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("work");
		}
	}

	class tB4Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("school");
		}
	}

	class drawingAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("drawing");
		}
	}

	class textAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("text");
		}
	}

	class previousAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("previous");
			if (lightTable.allowPrevious(mode)) {
				jScrollPane.setViewportView(lightTable.previous(mode));
				lightTable.getCurrentDP().setDraw(true);
				drawingButton.setSelected(true);

				previousNextAction = true;
				toggleButton1.setSelected(false);

				frame.setSize(changeWindowSize());

				updateThumb();
			} else {
				System.out.println("no previous pictures");
			}
		}
	}

	class nextAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusBar.setText("next");
			if (lightTable.allowNext(mode)) {
				jScrollPane.setViewportView(lightTable.next(mode));
				lightTable.getCurrentDP().setDraw(true);
				drawingButton.setSelected(true);

				previousNextAction = true;
				toggleButton1.setSelected(false);

				frame.setSize(changeWindowSize());
				updateThumb();
			} else {
				System.out.println("no next pictures");
			}
		}
	}

	public void forward() {
		statusBar.setText("next");
		if(lightTable.allowNext(mode)) {
			jScrollPane.setViewportView(lightTable.next(mode));
			lightTable.getCurrentDP().setDraw(true);
			drawingButton.setSelected(true);
			previousNextAction = true;
			frame.setSize(changeWindowSize());
			updateThumb();
		}
	}


	public void previous() {
		statusBar.setText("previous");
		if (lightTable.allowPrevious(mode)) {
			jScrollPane.setViewportView(lightTable.previous(mode));
			lightTable.getCurrentDP().setDraw(true);
			drawingButton.setSelected(true);
			previousNextAction = true;
			frame.setSize(changeWindowSize());
			updateThumb();
		}
	}

	public void delete() {
		statusBar.setText("delete");
		int index = lightTable.getCurrentDPindex();
		picWidths.remove(index);
		picHeights.remove(index);
		lightTable.delete(mode);
		if(lightTable.hasPicture()) {
			jScrollPane.setViewportView(lightTable.change(mode));
			frame.setSize(changeWindowSize());
		} else {
			noPicture();
		}		
	}


	public void family() {
		statusBar.setText("family");
		if(toggleButton1.isSelected()) {
			toggleButton1.setSelected(false);	
		} else {
			toggleButton1.setSelected(true);
		}
	}

	public void vacation() {
		statusBar.setText("vacation");
		if(toggleButton2.isSelected()) {
			toggleButton2.setSelected(false);	
		} else {
			toggleButton2.setSelected(true);
		}
	}

	public void work() {
		statusBar.setText("work");
		if(toggleButton3.isSelected()) {
			toggleButton3.setSelected(false);	
		} else {
			toggleButton3.setSelected(true);
		}
	}

	public void school() {
		statusBar.setText("school");
		if(toggleButton4.isSelected()) {
			toggleButton4.setSelected(false);	
		} else {
			toggleButton4.setSelected(true);
		}
	}

	public void none() {
		statusBar.setText("no pattern found");
	}

	public void selection(boolean bool) {
		if(bool) { //on draw, action
			statusBar.setText("selection-not on picture");
		} else { //on picture no action
			statusBar.setText("selection-on picture");
		}
	}

	/*initializes the fields, organizes them into subcomponents of
	each other, adds in some business logic (ButtonGroups), and finally
	assigns most of the fields to the controller classes.
	*/
	public Homework5(Homework1Resources hw1R) {
		frame = new JFrame();

		//****top of the window (menus) ****
		menuBar = new JMenuBar();

		//file menu with import,delete,exit
		file = new JMenu(hw1R.getFile());
		importMenuItem = new JMenuItem(hw1R.getImport());
		deleteMenuItem = new JMenuItem(hw1R.getDelete());
		exitMenuItem = new JMenuItem(hw1R.getExit());
		file.add(importMenuItem);
		file.add(deleteMenuItem);
		file.add(exitMenuItem);
		menuBar.add(file);

		importMenuItem.addActionListener(new importAction());
		deleteMenuItem.addActionListener(new deleteAction());
		exitMenuItem.addActionListener(new exitAction());

		//view menu with photoview (set to default), grid view, split view
		view = new JMenu(hw1R.getView());
		photoView = new JRadioButtonMenuItem(hw1R.getPhotoView());
		gridView = new JRadioButtonMenuItem(hw1R.getGridView());
		splitView = new JRadioButtonMenuItem(hw1R.getSplitView());
		magnetView = new JRadioButtonMenuItem("magnet");

		photoView.setSelected(true);

		viewsExclusive = new ButtonGroup();
		viewsExclusive.add(photoView);
		viewsExclusive.add(gridView);
		viewsExclusive.add(splitView);
		viewsExclusive.add(magnetView); //magnet for homework5

		view.add(photoView);
		view.add(gridView);
		view.add(splitView);
		view.add(magnetView);
		menuBar.add(view);

		photoView.addActionListener(new photoViewAction());
		gridView.addActionListener(new gridViewAction());
		splitView.addActionListener(new splitViewAction());
		magnetView.addActionListener(new magnetViewAction());

		//****bottom (status bar) ****
		/*the text that shows what action is currently being performed; defaults to nothing
		this is seen in the bottom of the screen. */
		statusBar = new JLabel(hw1R.getStatusBarDefault());
		bottomPanel = new JPanel();
		bottomPanel.add(statusBar);


		//****left (the user controls for picture) ****
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		/*reason for toggleButtonX for variable name is because I think the name will eventually be customized by user,
		thus it is not possible to know what text will be inserted. the four texts below are just possible examples. */
		toggleButton1 = new JToggleButton(hw1R.getTB1());
		toggleButton1.setAlignmentX(Component.CENTER_ALIGNMENT);
		toggleButton2 = new JToggleButton(hw1R.getTB2());
		toggleButton2.setAlignmentX(Component.CENTER_ALIGNMENT);
		toggleButton3 = new JToggleButton(hw1R.getTB3());
		toggleButton3.setAlignmentX(Component.CENTER_ALIGNMENT);
		toggleButton4 = new JToggleButton(hw1R.getTB4());
		toggleButton4.setAlignmentX(Component.CENTER_ALIGNMENT);

		leftPanel.add(toggleButton1); //family
		leftPanel.add(toggleButton2); //vacation
		leftPanel.add(toggleButton3); //work
		leftPanel.add(toggleButton4); //school

		//radiobuttons drawing or text; user can only select one at a time.
		drawingButton = new JRadioButton(hw1R.getDraw());
		drawingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		textButton = new JRadioButton(hw1R.getText());
		textButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		drawingTextExclusive = new ButtonGroup();
		drawingTextExclusive.add(drawingButton);
		drawingTextExclusive.add(textButton);

		pDrawingText = new JPanel();
		pDrawingText.add(drawingButton);
		pDrawingText.add(textButton);

		leftPanel.add(pDrawingText); //draw

		//next previous buttons
		previousButton = new JButton(hw1R.getPrevious());
		previousButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		nextButton = new JButton(hw1R.getNext());
		nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		pNextPrevious = new JPanel();
		pNextPrevious.add(previousButton);
		pNextPrevious.add(nextButton);

		leftPanel.add(pNextPrevious); //next

		toggleButton1.addActionListener(new tB1Action());
		toggleButton1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					lightTable.getCurrentDP().setFamily(true);
				} else if(e.getStateChange() == ItemEvent.DESELECTED) {
					lightTable.getCurrentDP().setFamily(false);
				}
			}
		});
		toggleButton2.addActionListener(new tB2Action());
		toggleButton2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					lightTable.getCurrentDP().setVacation(true);
				} else if(e.getStateChange() == ItemEvent.DESELECTED) {
					lightTable.getCurrentDP().setVacation(false);
				}
			}
		});
		toggleButton3.addActionListener(new tB3Action());
		toggleButton3.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					lightTable.getCurrentDP().setWork(true);
				} else if(e.getStateChange() == ItemEvent.DESELECTED) {
					lightTable.getCurrentDP().setWork(false);
				}
			}
		});
		toggleButton4.addActionListener(new tB4Action());
		toggleButton4.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					lightTable.getCurrentDP().setSchool(true);
				} else if(e.getStateChange() == ItemEvent.DESELECTED) {
					lightTable.getCurrentDP().setSchool(false);
				}
			}
		});
		drawingButton.addActionListener(new drawingAction());
		textButton.addActionListener(new textAction());
		nextButton.addActionListener(new nextAction());
		previousButton.addActionListener(new previousAction());

		//****center of the window (where the pictures go) ****
		drawingButton.setSelected(true); //drawing true on default
		lightTable = new LightTable(this); //default photoview mode
		grey = new JPanel();
		grey.setBackground(Color.GRAY);
		jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(grey);

		//radio button for drawing/text
		drawingButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (!(lightTable.getCurrentDP().onPicture())) { //can only alter radiobuttons if on draw
					lightTable.getCurrentDP().setDraw(true);
				}
			}
		});

		textButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (!(lightTable.getCurrentDP().onPicture())) { //can only alter radiobuttons if on draw
					lightTable.getCurrentDP().setDraw(false); //means text = true
				}
			}
		});


		//organizing the panels into desired window locations
		frame.add(jScrollPane, BorderLayout.CENTER);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		frame.add(leftPanel, BorderLayout.WEST);

		//sets up the window
		frame.setJMenuBar(menuBar);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setMinimumSize(new Dimension(175,270));
    	frame.setSize(500,270);
		frame.setVisible(true);
	}

	public void doubleClick() {
		mode = 0;
		frame.setSize(changeWindowSize());
		jScrollPane.setViewportView(lightTable.change(mode));
		photoView.setSelected(true);
		statusBar.setText("photoView");
	}

	public void changeSplitViewMain() {
		if(mode == 2) {
			jScrollPane.setViewportView(lightTable.change(2));
			splitView.setSelected(true);
		}
	}

	public static void main(String[] args) {
		Homework1Resources hw1R = new Homework1Resources();
		new Homework5(hw1R);
	}
}