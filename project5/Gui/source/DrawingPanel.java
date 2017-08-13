//Bowen Gui
//Version 1.0.1

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import java.awt.Dimension;
import javax.swing.SwingUtilities;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

//PhotoComponent class, takes care of flipping, adding text and drawing to back of picture
//Also includes the codes for gestures
public class DrawingPanel extends JPanel{

    Point start;
    Point end;
    static Color c = Color.black; //color for normal drawing
    JPanel panel;
    BufferedImage image; //drawing
    Graphics2D  imageGraphics;
    Shape r; //for textbox
    ArrayList<Shape> shapes = new ArrayList<Shape>();
    String path;
    BufferedImage importedImage; //for picture display
    String text = "test";

    //set of booleans
    boolean emptyPath = false;
    boolean onPicture = true;
    boolean isDraw = true;
    boolean enableType = true;

    //info on the current rect
    int currentRectX;
    int currentRectY;
    int currentRectWidth;
    int currentRectHeight;
    int stringWidth; //width of string

    //deminsions of picture
    int importedPicWidth;
    int importedPicHeight;

    //the single thumbnail associated with this drawingPanel. 1-to-1 is good enough
    ThumbnailComponent thumbnail;

    //access to lightTable
    //LightTable lightTable;

    //gestures
    private Path2D.Double rightPath;
    private boolean rightReleased;
    private final ArrayList<Point2D> pointArray;
    private final SigerRecognizer siger;

    //tabs
    private boolean isFamily;
    private boolean isWork;
    private boolean isSchool;
    private boolean isVacation;


    private boolean isSelection = false;


    private LightTable lightTable;

    DrawingPanel(boolean isDraw, boolean onPicture, LightTable lt) {
        this.isDraw = isDraw;
        this.onPicture = onPicture;
        lightTable = lt;
        path = "";
        rightPath = new Path2D.Double();
        rightReleased = true;
        pointArray = new ArrayList<Point2D>();
        siger = new SigerRecognizer();

        //default no tabs selected for photoComponent
        isFamily = false;
        isWork = false;
        isSchool = false;
        isVacation = false;

        try {
            importedImage = ImageIO.read(new File(path)); //picture
            ImageIcon temp = new ImageIcon(path); //for getting width/height of picture
            importedPicWidth = temp.getIconWidth();
            importedPicHeight = temp.getIconHeight();
            image = new BufferedImage(importedPicWidth,importedPicHeight, BufferedImage.TYPE_INT_ARGB); //draw
            imageGraphics = image.createGraphics();        
        } catch(IOException e) {
            System.out.println("cannot read image");
        }
    }

    public boolean onPicture() {
        return onPicture;
    }

    public void setDraw(boolean isDraw) {
        this.isDraw = isDraw;
    }

    public void enableType(boolean allowType) {
        enableType = allowType;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public int getPicWidth() {
        return importedPicWidth;
    }

    public int getPicHeight() {
        return importedPicHeight;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Graphics2D getGraphics2D() {
        return imageGraphics;
    }

    public void addThumbnail(ThumbnailComponent tn) {
        thumbnail = tn;
    }

    public ThumbnailComponent getThumbnail() {
        return thumbnail;
    }

    public void setPath(String p) {
        path = p;
        try {
            importedImage = ImageIO.read(new File(path)); //picture
            ImageIcon temp = new ImageIcon(path); //for getting width/height of picture
            importedPicWidth = temp.getIconWidth();
            importedPicHeight = temp.getIconHeight();

            image = new BufferedImage(importedPicWidth,importedPicHeight, BufferedImage.TYPE_INT_ARGB); //draw
            imageGraphics = image.createGraphics();
            addKeyListener(new k1());
            addMouseMotionListener(new ml());
            addMouseListener(new ml());
            repaint();

        } catch(IOException e) {
            System.out.println("cannot read image");
        }
    }

    public class k1 implements KeyListener{
        public void keyPressed(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {}
        public void keyTyped(KeyEvent e) {
            if(enableType) {
                text += Character.toString(e.getKeyChar());
                String[] retStrings = splitIntoLine(text, currentRectWidth/11); //about 7 pixels per char
                int y = currentRectY + 10; //current y position of text
                // int tempTextHeight;


                ArrayList<String> previousText = new ArrayList<String>();

                for(int i = 0; i < retStrings.length; i++) {
                    int tempHeight = y - currentRectY + 5;
                    String s = retStrings[i]; //gets current string
                    if (currentRectHeight < y - currentRectY) { //redraw rectangle
                        //first draws rectangle with incrased height
                        imageGraphics.setPaint(Color.YELLOW);
                        imageGraphics.fillRect(currentRectX, currentRectY, currentRectWidth, tempHeight);
                        imageGraphics.setPaint(Color.BLACK);
                        imageGraphics.drawRect(currentRectX, currentRectY, currentRectWidth, tempHeight);

                        //draws the previous text
                        int textY = currentRectY + 10;
                        for (String sp : previousText) {
                            imageGraphics.setPaint(Color.BLACK);
                            imageGraphics.drawString(sp, currentRectX, textY);
                            textY += 18;
                        }

                        //finally adds the new text
                        imageGraphics.setPaint(Color.BLACK);
                        imageGraphics.drawString(s, currentRectX, textY);
                        previousText.add(s);

                    } else { //fits in original
                        previousText.add(s);
                        imageGraphics.setPaint(Color.BLACK);
                        imageGraphics.drawString(s, currentRectX, y);
                    }
                    tempHeight += 20;
                    repaint();
                    y += 18;
                }
            } else {
                System.out.println("no type enable");
            }
        }
    }

    //only called if not empty path
    //contains code for drawing, writing out textbox, and flipping image
    public class ml implements MouseMotionListener, MouseListener {
        boolean singleClick = false;
        public void mouseClicked(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) { //left click
                //System.out.println("left click");
                if (e.getClickCount() == 2) {
                    singleClick = false;
                    if(onPicture) {
                        onPicture = false;
                        repaint();
                    } else {
                        onPicture = true;
                        repaint();
                    }
                } else if (e.getClickCount() == 1) {
                    singleClick = true;
                }
            } //shouldn't do anything if right click
        }
        public void mouseMoved(MouseEvent ev) {}
        public void mousePressed(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                if (!(onPicture)) {

                    if (rightPath.contains(e.getX(), e.getY())) { //left click within selection borders
                        System.out.println("wihtin border");
                    } else { //deselects the border
                        System.out.println("outside border");
                        isSelection = false;
                        rightPath = new Path2D.Double();
                        pointArray.clear(); //? don't know what point array is
                        repaint();
                    }

                    if (isDraw) {
                        end = e.getPoint();
                    } else {
                        start = new Point(e.getX(), e.getY());
                        end = start;
                        text = "";
                        repaint();
                    }
                }
            } else if(SwingUtilities.isRightMouseButton(e)) { //right click
                rightPath.moveTo(e.getX(), e.getY());
                rightReleased = false;
                pointArray.add(rightPath.getCurrentPoint());
            }
        }
        public void mouseDragged(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                if (onPicture == false && isDraw) {

                    if(rightPath.contains(e.getX(), e.getY())) { //drags things within border

                    } else { //draws a normal line
                        start = end;
                        end = e.getPoint();
                        imageGraphics.setColor(c);
                        imageGraphics.setStroke(new BasicStroke(5));
                        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        imageGraphics.drawLine(start.x, start.y, end.x, end.y);
                        repaint();
                    }
                }
            } else if(SwingUtilities.isRightMouseButton(e)) { //right click
                rightPath.lineTo(e.getX(), e.getY());
                rightReleased = false;
                pointArray.add(rightPath.getCurrentPoint());
            }
        }
        public void mouseReleased(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                if (!(onPicture)) {

                    if (isDraw) { //drawing
                        start = null;
                        end = null;
                    } else { //creating the post-it note
                        currentRectX = Math.min(start.x, e.getX());
                        currentRectY = Math.min(start.y, e.getY());
                        currentRectWidth = Math.abs(start.x - e.getX());
                        currentRectHeight = Math.abs(start.y - e.getY());

                        if(currentRectWidth < 50) {
                            currentRectWidth = 50;
                        }

                        if(currentRectHeight < 50) {
                            currentRectHeight = 50;
                        }

                        imageGraphics.setPaint(Color.BLACK);
                        imageGraphics.drawRect(currentRectX, currentRectY, currentRectWidth, currentRectHeight);
                        imageGraphics.setPaint(Color.YELLOW);
                        imageGraphics.fillRect(currentRectX, currentRectY, currentRectWidth, currentRectHeight);

                        start = null;
                        end = null;
                        repaint();
                    }
                } 
            } else if(SwingUtilities.isRightMouseButton(e)) { //right click
                SigerRecognizer.PatternIndex index = siger.matchToTemplates(siger.buildDirectionVector(pointArray));
                switch (index) {
                    case FORWARD:
                        lightTable.forward();
                        isSelection = false;
                        break;
                    case PREVIOUS:
                        lightTable.previous();
                        isSelection = false;
                        break;
                    case DELETE:
                        lightTable.delete();
                        isSelection = false;
                        break;
                    case VACATION:
                        if(isVacation) {
                            isVacation = false;
                        } else {
                            isVacation = true;
                        }
                        lightTable.vacation();
                        isSelection = false;
                        break;
                    case WORK:
                        if(isWork) {
                            isWork = false;
                        } else {
                            isWork = true;
                        }
                        lightTable.work();
                        isSelection = false;
                        break;
                    case SCHOOL:
                        if(isSchool) {
                            isSchool = false;
                        } else {
                            isSchool = true;
                        }
                        isSelection = false;
                        lightTable.school();
                        break;
                    case FAMILY:
                        if(isFamily) {
                            isFamily = false;
                        } else {
                            isFamily = true;
                        }
                        lightTable.family();
                        isSelection = false;
                        break;
                    case SELECTION:
                        if(onPicture) {
                            lightTable.selection(false); //on picture means no action
                        } else {
                            lightTable.selection(true); //on draw means action
                            isSelection = true;
                            repaint();
                        }
                        break;
                    case NONE:
                        lightTable.none();
                        isSelection = false;
                        break;
                    default:
                        break;
                }
                if(isSelection) {
                    rightReleased = true;
                    repaint();
                } else {
                    rightPath = new Path2D.Double();
                    rightReleased = true;
                    pointArray.clear(); //? don't know what point array is
            }
            }
        }
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    //methods for text
    private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
        return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    private void paintBackground(Graphics2D g2) {
        g2.setPaint(Color.LIGHT_GRAY);
        for (int i = 0; i < getSize().width; i += 10) {
            Shape line = new Line2D.Float(i, 0, i, getSize().height);
            g2.draw(line);
        }

        for (int i = 0; i < getSize().height; i += 10) {
            Shape line = new Line2D.Float(0, i, getSize().width, i);
            g2.draw(line);
        } 
    }

    public void paint(Graphics g, double scalex, double scaley) {
        Graphics2D graphics2D = (Graphics2D)g.create();
        graphics2D.scale(scalex,scaley);
        Graphics g1 = (Graphics) graphics2D;
        g1.drawImage(importedImage,25,25, null);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        stringWidth = g.getFontMetrics().stringWidth(text);
        Graphics2D g2 = (Graphics2D) g;
        paintBackground(g2);
        if(this.path == "") {
            System.out.println("empty path; did not do anything");
        } else {        
            if(onPicture) { //displaypicture
                g2.drawImage(importedImage, 0,0,null);
            } else {
                if (!(isDraw)) { //text
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawImage(image, null, 0,0); //basically drawing white canvas + whatever was drawn
                    //processText(text);
                } else { //drawing
                    g2.drawImage(image, null, 0,0);
                }
            }

            //gesture: only works if picture is there; doesn't matter if front or back of picture
            if (!rightReleased) {
                g2.setColor(new Color(255, 0, 0));
                g2.draw(rightPath);
            }

            //selection borders
            if(isSelection) {
                g2.setColor(new Color(255,0,0));
                g2.draw(rightPath);
            }
        }
        repaint();
        revalidate();
    }

    public ArrayList<Shape> getShape() {
        return shapes;
    }

    //string splitter
    private String[] splitIntoLine(String input, int maxWidth){ //maxwidth is the number of pixels in the rectangle.width

        StringTokenizer tok = new StringTokenizer(input, " ");
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            while(word.length() > maxWidth){
                output.append(word.substring(0, maxWidth-lineLen) + "\n");
                word = word.substring(maxWidth-lineLen);
                lineLen = 0;
            }

            if (lineLen + word.length() > maxWidth) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");

            lineLen += word.length() + 1;
        }
        return output.toString().split("\n");
    }


    //for tabs section
    public boolean isFamily() {
        return isFamily;
    }

    public boolean isVacation() {
        return isVacation;
    }

    public boolean isWork() {
        return isWork;
    }

    public boolean isSchool() {
        return isSchool;
    }

    public void setFamily(boolean bool) {
        isFamily = bool;
    }

    public void setVacation(boolean bool) {
        isVacation = bool;
    }
    public void setWork(boolean bool) {
        isWork = bool;
    }
    public void setSchool(boolean bool) {
        isSchool = bool;
    }
}