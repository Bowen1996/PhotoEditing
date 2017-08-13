//Bowen Gui
//Version 1.0.0

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;

/*class similiar to drawingPanel(the photoComponent class) but with
less functions (no flippings) and sets a constant size regardless of
the original picture size */
public class ThumbnailComponent extends JComponent {

	DrawingPanel drawingPanel;
	boolean onThumbNail;
	boolean selected;
	LightTable lightTable;
	ThumbnailComponent itself;

	//sizes of the corresponding drawingPanel's picture's dimensions
	int picWidth;
	int picHeight;

	boolean singleClick = false;
	boolean doubleclicked = false;

	//for the special thumbnail
	boolean scaled;

	//x,y CURRENT locations for thumbnail
	private int x = 0;
	private int y = 0;

	//END POINT for the thumbnail
	private int endX = 0;
	private int endY = 0;

	//size is exactly same as dp originally
	public ThumbnailComponent(DrawingPanel dp, LightTable tl) {
		scaled = false;
		drawingPanel = dp;
		lightTable = tl;
		itself = this;
		dp.addThumbnail(itself);
		onThumbNail = true;
		selected = false;
		picWidth = dp.getPicWidth();
		picHeight = dp.getPicHeight();
		addMouseListener(new m1());
	}

	//special thumbnail
	public ThumbnailComponent(DrawingPanel dp, LightTable tl, boolean special) {
		scaled = true;
		drawingPanel = dp;
		lightTable = tl;
		itself = this;
		dp.addThumbnail(itself);
		onThumbNail = true;
		selected = false;
		picWidth = dp.getPicWidth();
		picHeight = dp.getPicHeight();
		addMouseListener(new m2());
	}

	//getters/setters for the thumbnail's current location
	public void setTheLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setTheEndPoint(double x, double y) {
		endX = (int) x;
		endY = (int) y;
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public class m1 implements MouseListener {
	    public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) { //to photocomponent
            	lightTable.flagged(itself);
            } else if (e.getClickCount() == 1) { //selection (highlight)
            	lightTable.selected(itself);
            }
        }

        public void mouseDragged(MouseEvent e) {}
        public void mouseMoved(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
	}

	public class m2 implements MouseListener {
	    public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
            	lightTable.flagged(itself, 1);
            }
        }

        public void mouseDragged(MouseEvent e) {}
        public void mouseMoved(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
	}

	public void unselect() {
		singleClick = false;
		repaint();
		// setBorder(BorderFactory.createEmptyBorder());
	}

	public boolean selected() {
		return singleClick;
	}

	public void setSelect() {
		singleClick = true;
		// setBorder(BorderFactory.createLineBorder(Color.RED));
        repaint();
	}

	//paints ThumbnailComponent
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(scaled) { //special thumbnail
			double sx = 350.0/picWidth;
			double sy = 350.0/picHeight;
			drawingPanel.paint(g, sx, sy);
		} else { //normal thumbnail
			if(singleClick) {
				g.setColor(Color.RED);
				g.fillRect(0,0,170,170);
			} else {
				g.setColor(Color.WHITE);
				g.fillRect(0,0,170,170);
			}
			double x = 150.0/picWidth;
			double y = 150.0/picHeight;
			drawingPanel.paint(g, x, y);
		}
	}

	public boolean onThumbNail() {
		return onThumbNail;
	}

	public void rePaint() {
		repaint();
	}

	public Dimension getPreferredSize() {
		if(scaled) {
			return new Dimension(300,300);
		} else {
			return new Dimension(150,150);
		}
	}

	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}

	public boolean isFamily() {
		return drawingPanel.isFamily();
	}

	public boolean isVacation() {
		return drawingPanel.isVacation();
	}

	public boolean isWork() {
		return drawingPanel.isWork();
	}

	public boolean isSchool() {
		return drawingPanel.isSchool();
	}
}