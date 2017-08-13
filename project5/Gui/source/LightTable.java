//Bowen Gui
//Version 1.0.0

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Component;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.Box;

//This class is an advanced DrawingPanel that takes care of all the viewing modes
public class LightTable extends JPanel {

	ArrayList<ThumbnailComponent> thumbnailArray;
	ArrayList<DrawingPanel> dpArray; //NOTE: dpArray and currentThumb are 1 to 1, meaning index 0 of dp should be
	ThumbnailComponent currentThumb; //the dp for index 0 of currentThumb
	DrawingPanel currentDP;
	JPanel retPanel = new JPanel();
	Component[] components = retPanel.getComponents();
	LightTable itself;
	Homework5 homework5;
	ThumbnailComponent splitViewMainThumb;
	boolean hasPicture = false;
	Magnet magnetLayout;

	public LightTable(Homework5 hw5) {
		thumbnailArray = new ArrayList<ThumbnailComponent>();
		dpArray = new ArrayList<DrawingPanel>();
		itself = this;
		homework5 = hw5;
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
            	magnetLayout = new Magnet();
            }
        });
	}

	public Magnet getMagnetLayout() {
		return magnetLayout;
	}

	public void addThumbnailsToMagnet() {
		for(ThumbnailComponent thumbnail: thumbnailArray) {
			System.out.println("added thumbnail to magnet class");
			magnetLayout.addThumbnail(thumbnail);
		}
		//adds magnets after everyhing so its on top
		//magnetLayout.setMagnets();
	}

	public void flagged(ThumbnailComponent tc, int something) {
		currentDP = tc.getDrawingPanel();
		homework5.doubleClick();
	}

	public void flagged(ThumbnailComponent tc) {
		currentThumb = tc;
		currentDP = currentThumb.getDrawingPanel();
		homework5.doubleClick();
	}

	public void selected(ThumbnailComponent tc) {
		for(int i = 0; i < thumbnailArray.size(); i++) {
			thumbnailArray.get(i).unselect();
		}
		tc.setSelect();
		currentThumb = tc;
		currentDP = dpArray.get(thumbnailArray.indexOf(currentThumb));
		splitViewMainThumb = tc;
		homework5.updateThumb();
		homework5.changeSplitViewMain();
	}

	//handling the special thumbnail
	public void special(ThumbnailComponent tc) {
		currentDP = tc.getDrawingPanel();
	}

	public boolean hasPicture() {
		return hasPicture;
	}

	public void addDrawingPanel(DrawingPanel dp) {
		hasPicture = true;
		if(currentThumb != null) {
			currentThumb.unselect();
		}
		currentDP = dp;
		ThumbnailComponent tn = new ThumbnailComponent(dp, itself);
		currentThumb = tn;
		currentThumb.setSelect();
		thumbnailArray.add(tn);
		dpArray.add(dp);
	}

	public DrawingPanel getCurrentDP() {
		return currentDP;
	}

	public int getCurrentDPindex() {
		return dpArray.indexOf(currentDP);
	}

	public void setCurrentDP(DrawingPanel dp) {
		currentDP = dp;
	}

	public void setCurrentDP(int i) {
		currentDP = dpArray.get(i);
	}


	public boolean allowNext(int mode) {
		int dpIndex = dpArray.indexOf(currentDP);
		int tnIndex = thumbnailArray.indexOf(currentThumb);
		if(mode == 0) {
			return (dpIndex + 1 < dpArray.size());
		} else {
			return (tnIndex + 1 < thumbnailArray.size());
		}
	}

	public JPanel next(int mode) {
		int dpIndex = dpArray.indexOf(currentDP);
		int tnIndex = thumbnailArray.indexOf(currentThumb);
		JPanel retPanel = new JPanel();
		
		if(dpIndex + 1 < dpArray.size() && dpArray.size() == thumbnailArray.size()) {
			currentThumb.unselect();
			currentThumb = thumbnailArray.get(tnIndex + 1);
			currentDP = dpArray.get(dpIndex + 1);
			retPanel = change(mode);
		}

		return retPanel;
	}

	public boolean allowPrevious(int mode) {
		int dpIndex = dpArray.indexOf(currentDP);
		int tnIndex = thumbnailArray.indexOf(currentThumb);
		if(mode == 0) {
			return (dpIndex - 1 >= 0);
		} else {
			return (tnIndex - 1 >= 0);
		}
	}

	public JPanel previous(int mode) {
		int dpIndex = dpArray.indexOf(currentDP);
		int tnIndex = thumbnailArray.indexOf(currentThumb);

		JPanel retPanel = new JPanel();

		if(dpIndex - 1 >= 0 && dpArray.size() == thumbnailArray.size()) {
			currentThumb.unselect();
			currentThumb = thumbnailArray.get(tnIndex - 1);
			currentDP = dpArray.get(dpIndex - 1);
			retPanel = change(mode);
		}

		return retPanel;
	}

	public boolean isEmpty() {
		if(thumbnailArray.size() == 0 || dpArray.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	//deletes the currently selected drawingPanel. Returns the next currentDP if possible.
	public void delete(int mode) {
		JPanel retPanel = new JPanel();
		
		int index = getCurrentDPindex();

		dpArray.remove(currentDP);
		thumbnailArray.remove(currentThumb);

		if(dpArray.size() == 0) {
			hasPicture = false;
		} else if(dpArray.size() == 1) {
			currentDP = dpArray.get(0);
			currentThumb = thumbnailArray.get(0);
			change(mode);
		} else if(index == dpArray.size()) { //came to end wrap around.
			currentDP = dpArray.get(0);
			currentThumb = thumbnailArray.get(0);
			change(mode);
		} else {
			currentDP = dpArray.get(index);
			currentThumb = thumbnailArray.get(index);
			change(mode);
		}
	}


	public Component[] getComponents() {
		return retPanel.getComponents();
	}

	public JPanel change(int mode) {
		if(thumbnailArray!= null && dpArray!=null) {
			if(mode == 0) { //Photo View Mode
				retPanel.removeAll();
				currentDP.setPreferredSize(new Dimension(currentDP.getPicWidth() + 195,currentDP.getPicHeight() + 117));
				return currentDP;
			} else if(mode == 1) { //Browser Mode
				retPanel.removeAll();
				retPanel.setLayout(new GridLayout(2,2));
				for(ThumbnailComponent thumbnail : thumbnailArray) {
					thumbnail.rePaint();
					retPanel.add(thumbnail);
				}
				currentThumb.setSelect();
				components = retPanel.getComponents();
				int width = ((thumbnailArray.size() + 1) / 2) * 180;
				retPanel.setPreferredSize(new Dimension(width, 380));
				return retPanel;
			} else if(mode == 2) { //Split View Mode
				retPanel.removeAll();
				
				//creates copy of currentThumb, temp (but does not share same memory address)
				splitViewMainThumb = new ThumbnailComponent(currentThumb.getDrawingPanel(), itself, true);

	
				JPanel scrollBottom = new JPanel();
				scrollBottom.setLayout(new GridLayout(1,1));

    			
				for(ThumbnailComponent thumbnail : thumbnailArray) {
					thumbnail.unselect();
					thumbnail.rePaint();
					scrollBottom.add(thumbnail);
				}

				int width = thumbnailArray.size() * 180;

				scrollBottom.setPreferredSize(new Dimension(width,250));
				currentThumb.setSelect();
				retPanel.setPreferredSize(new Dimension(width,250));
				retPanel.add(splitViewMainThumb, BorderLayout.EAST);
				retPanel.add(scrollBottom, BorderLayout.SOUTH);
				return retPanel;
			}
		}
		return null;
	}


	public boolean currentDPisFamily() {
		return currentDP.isFamily();
	}

	public ThumbnailComponent getSpecial() {
		return splitViewMainThumb;
	}

	public ThumbnailComponent getSplitViewMain() {
		return splitViewMainThumb;
	}

	public void forward() {
		homework5.forward();
	}

	public void previous() {
		homework5.previous();
	}

	public void delete() {
		homework5.delete();
	}

	public void vacation() {
		homework5.vacation();
	}

	public void work() {
		homework5.work();
	}

	public void school() {
		homework5.school();
	}

	public void family() {
		homework5.family();
	}

	public void selection(boolean bool) { //true == on draw, false == on picture which means no action
		homework5.selection(bool);
	}

	public void none() {
		homework5.none();
	}

	public ArrayList<ThumbnailComponent> getThumbnailArray() {
		return thumbnailArray;
	}
}