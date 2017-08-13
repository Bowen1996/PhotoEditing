import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

//class where magnets can be added; seperate class from the other modes. have to specifically
//select this class to get the magnet functions.
public class Magnet extends JPanel implements MouseListener, MouseMotionListener {

    private JPanel panel = new JPanel(null);
    private JPanel leftPanel = new JPanel();
    private int mouseX = 200; //starting points for the new magnet that's added.
    private int mouseY = 100;
    private int clicX = 0;
    private int clicY = 0;
    private ArrayList<JLabel> magnets = new ArrayList<JLabel>();
    private int endX = 0;
    private int endY = 0;
    private boolean dragged = false; //whether mouse is dragging

    private ArrayList<ThumbnailComponent> thumbnailArray = new ArrayList<ThumbnailComponent> (); //need access to thumbnail class?
    private ArrayList<Integer> magnetPointArray = new ArrayList<Integer>(8);

    //timer
    public final static int delay = 70;
    Timer timer;

    //imports thumbnails from lightTable
    public Magnet() {
        //setting preferred size
        setPreferredSize(new Dimension(500,500));

        //setting layout and color for panels.
        panel.setBackground(Color.WHITE);
        leftPanel.setBackground(Color.GREEN);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        //creating buttons, alligning so buttons are in the center
        JToggleButton familyButton = new JToggleButton("family");
        familyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JToggleButton vacationButton = new JToggleButton("vacation");
        vacationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JToggleButton workButton = new JToggleButton("work");
        workButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JToggleButton schoolButton = new JToggleButton("school");
        schoolButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //adding buttons the left "menu" panel
        leftPanel.add(familyButton);
        leftPanel.add(vacationButton);
        leftPanel.add(workButton);
        leftPanel.add(schoolButton);

        //action listeners: selecting = addMagnet, deselecting = removeMagnet
        //can only have one magnet of a specific tag at a time
        familyButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("selected");
                    addMagnet("family");
                } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                    System.out.println("deselected");
                    removeMagnet("family");
                }
            }
        });

        vacationButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("selected");
                    addMagnet("vacation");
                } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                    System.out.println("deselected");
                    removeMagnet("vacation");
                }
            }
        });

        workButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("selected");
                    addMagnet("work");
                } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                    System.out.println("deselected");
                    removeMagnet("work");
                }
            }
        });

        schoolButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("selected");
                    addMagnet("school");
                } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                    System.out.println("deselected");
                    removeMagnet("school");
                }
            }
        });


        //adding to main Panel.
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(leftPanel, BorderLayout.WEST);

        //magnetPointArray set to 0
        for(int i = 0; i < 8; i++) {
            magnetPointArray.add(0);
        }

        //timer
        tImer();
    }

    private void tImer() {
        ActionListener timerAction = new ActionListener ()
        {

            int currentX = 0;
            int currentY = 0;
            double speed = 1.0;
            public void actionPerformed(ActionEvent e) {
                for(ThumbnailComponent current: thumbnailArray) {
                    //properties of that specific thumbnail
                    endX = current.getEndX();
                    endY = current.getEndY();
                    double fraction = getFraction(endX - currentX, endY - currentY);
                    if(thumbnailArray.get(0).getX() == endX && !dragged) {
                        timer.stop();
                        currentX = 0;
                        currentY = 0;
                    } else {
                        if(currentX < endX && currentY < endY) {
                            current.setTheLocation(currentX += speed * fraction, currentY += speed);
                        } else if (currentX > endX && currentY < endY) {
                            current.setTheLocation(currentX -= speed * fraction, currentY += speed);
                        } else if (currentX < endX && currentY > endY) {
                            current.setTheLocation(currentX += speed * fraction, currentY -= speed);
                        } else if (currentX > endX && currentY > endY) {
                            current.setTheLocation(currentX -= speed * fraction, currentY -= speed);
                        } else if (currentX == endX && currentY < endY) {
                            current.setTheLocation(currentX, currentY += speed);
                        } else if (currentX > endX && currentY == endY) {
                            current.setTheLocation(currentX -= speed * fraction, currentY);
                        } else if (currentX == endX && currentY > endY) {
                            current.setTheLocation(currentX, currentY -= speed);
                        } else if (currentX < endX && currentX == endY) {
                            current.setTheLocation(currentX += speed * fraction, currentY);
                        } else {
                            System.out.println("not suppose to happen goddamn it");
                        }

                        // int x = current.getX();
                        // int y = current.getY();
                        current.setLocation(currentX, currentY);
                    }
                }
            }
        };

        timer = new Timer(delay, timerAction);
        timer.start();
    }

    //returns ratio x:y, multiply by y variable only
    private double getFraction(int x, int y) {
        if(x == 0 && y == 0) {
            return 0.0;
        } else {
            if (y == 0) {
                return 0.0;
            } else {
                return x/y * 1.0;
            }
        }
    }


    private int findMagnet(String magnetName) {
        for(int i = 0; i < magnets.size(); i++) {
            if(magnets.get(i).getText().equals(magnetName)) {
                return i;
            }
        }
        System.out.println("no magnet");
        return -1;
    }

    public void addMagnet(String name) {
        JLabel label = new JLabel(name);
        label.setOpaque(true);
        label.setBackground(Color.GREEN);
        label.setBounds(mouseX, mouseY, 100, 50);
        label.addMouseMotionListener(this);
        label.addMouseListener(this);
        magnets.add(label);
        panel.add(label);
        panel.repaint();
        updateMagnetPointArray(label, false);
        recalculatedEndPoint();
        tImer();
    }

    public void removeMagnet(String name) {
        if(!timer.isRunning()) {
            JLabel removeMagnet = new JLabel();
            for(JLabel magnet: magnets) {
                if(magnet.getText().equals(name)) {
                    removeMagnet = magnet;
                }
            }
            updateMagnetPointArray(removeMagnet, true);
            magnets.remove(removeMagnet);
            panel.remove(removeMagnet);
            recalculatedEndPoint();
            panel.repaint();
        } else {
            System.out.println("animation still occuring, cannot remove magnet");
        }
    }

    public void addThumbnail(ThumbnailComponent thumbnail) {
        // ThumbnailComponent copyThumbnail = thumbnail.copy();
        thumbnailArray.add(thumbnail);
        thumbnail.setBounds(0, 0, 170, 170); //starts at 0,0
        panel.add(thumbnail);
        panel.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragged = true;
        for(JLabel magnet: magnets) {
            if(e.getSource() == magnet) {
                updateMagnetPointArray(magnet, false);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragged = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for(JLabel magnet: magnets) {
            if (e.getSource() == magnet) {
                JComponent jc = (JComponent)e.getSource();
                jc.setLocation(jc.getX()+e.getX()-clicX, jc.getY()+e.getY()-clicY);
                updateMagnetPointArray(magnet, false);
            }
        }
    }

    public void mouseMoved(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    private void updateMagnetPointArray(JLabel magnet, boolean delete) {
        int x = 0;
        int y = 0;
        if(delete) {
            x = averageMagnetsX();
            y = averageMagnetsY();
        } else {
            x = magnet.getX();
            y = magnet.getY();
        }
        if(magnet.getText().equals("family")) {
            magnetPointArray.set(0, x);
            magnetPointArray.set(1, y);
        } else if (magnet.getText().equals("vacation")) {
            magnetPointArray.set(2, x);
            magnetPointArray.set(3, y);
        } else if (magnet.getText().equals("work")) {
            magnetPointArray.set(4, x);
            magnetPointArray.set(5, y);
        } else if (magnet.getText().equals("school")) {
            magnetPointArray.set(6, x);
            magnetPointArray.set(7, y);
        } else {
            System.out.println("error updatemagnet point");
        }
    }

    //caculates end points for each ThumbnailComponent based on their properties and the locations of the magnets present
    private void recalculatedEndPoint() {
        double x = 0;
        double y = 0;
        for(ThumbnailComponent thumbnail: thumbnailArray) {
            if(thumbnail.isFamily() && thumbnail.isVacation() && thumbnail.isWork() && thumbnail.isSchool()) {
                x = (magnetPointArray.get(0) + magnetPointArray.get(2) + magnetPointArray.get(4) + magnetPointArray.get(6)) / 4.0;
                y = (magnetPointArray.get(1) + magnetPointArray.get(3) + magnetPointArray.get(5) + magnetPointArray.get(7)) / 4.0;
                // averageMagnets();
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isFamily() && thumbnail.isVacation() && thumbnail.isWork()) {
                x = (magnetPointArray.get(0) + magnetPointArray.get(2) + magnetPointArray.get(4)) / 3.0;
                y = (magnetPointArray.get(1) + magnetPointArray.get(3) + magnetPointArray.get(5)) / 3.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isFamily() && thumbnail.isVacation() && thumbnail.isSchool()) {
                x = (magnetPointArray.get(0) + magnetPointArray.get(2) + magnetPointArray.get(6)) / 3.0;
                y = (magnetPointArray.get(1) + magnetPointArray.get(3) + magnetPointArray.get(7)) / 3.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isVacation() && thumbnail.isWork() && thumbnail.isSchool()) {
                x = (magnetPointArray.get(2) + magnetPointArray.get(4) + magnetPointArray.get(6)) / 3.0;
                y = (magnetPointArray.get(3) + magnetPointArray.get(5) + magnetPointArray.get(7)) / 3.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isFamily() && thumbnail.isWork() && thumbnail.isSchool()) {
                x = (magnetPointArray.get(0) + magnetPointArray.get(4) + magnetPointArray.get(6)) / 3.0;
                y = (magnetPointArray.get(1) + magnetPointArray.get(5) + magnetPointArray.get(7)) / 3.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isFamily() && thumbnail.isWork()) {
                x = (magnetPointArray.get(0) + magnetPointArray.get(4)) / 2.0;
                y = (magnetPointArray.get(1) + magnetPointArray.get(5)) / 2.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isFamily() && thumbnail.isVacation()) {
                x = (magnetPointArray.get(0) + magnetPointArray.get(2)) / 2.0;
                y = (magnetPointArray.get(1) + magnetPointArray.get(3)) / 2.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isFamily() && thumbnail.isSchool()) {
                x = (magnetPointArray.get(0) + magnetPointArray.get(6)) / 2.0;
                y = (magnetPointArray.get(1) + magnetPointArray.get(7)) / 2.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isVacation() && thumbnail.isSchool()) {
                x = (magnetPointArray.get(2) + magnetPointArray.get(6)) / 2.0;
                y = (magnetPointArray.get(3) + magnetPointArray.get(7)) / 2.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isVacation() && thumbnail.isWork()) {
                x = (magnetPointArray.get(2) + magnetPointArray.get(4)) / 2.0;
                y = (magnetPointArray.get(3) + magnetPointArray.get(5)) / 2.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isWork() && thumbnail.isSchool()) {
                x = (magnetPointArray.get(4) + magnetPointArray.get(6)) / 2.0;
                y = (magnetPointArray.get(5) + magnetPointArray.get(7)) / 2.0;
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isFamily()) {
                x = magnetPointArray.get(0);
                y = magnetPointArray.get(1);
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isVacation()) {
                x = magnetPointArray.get(2);
                y = magnetPointArray.get(3);
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isWork()) {
                x = magnetPointArray.get(4);
                y = magnetPointArray.get(5);
                thumbnail.setTheEndPoint(x,y);
            } else if (thumbnail.isSchool()) {
                x = magnetPointArray.get(6);
                y = magnetPointArray.get(7);
                thumbnail.setTheEndPoint(x,y);
            } else {
                System.out.println("error recalculate end point");
            }
        }
    }

    private int averageMagnetsX() {
        return 0;
    }

    private int averageMagnetsY() {
        return 0;
    }
}