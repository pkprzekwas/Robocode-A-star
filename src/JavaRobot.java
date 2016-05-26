import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Vector;

import robocode.*;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;


public class JavaRobot extends AdvancedRobot {

    boolean isBlocked;
    private State state;
    private int oppMap[][][];
    private ArrayList<Integer[]> oppList;
    private ArrayList<int[]> openList;
    private ArrayList<int[]> closeList;
    private int rows;
    private int columns;
    private int height;
    private int width;
    private Vector<Point> coordList;

    private int destinationX = 900;
    private int destinationY = 900;

    public JavaRobot() {}

    public void run() {
        setUpValues();

        // Putting value of the center of cell to map
        for(int i=0; i<25; i++)
            for(int j=0; j<25; j++)
            {
                oppMap[i][j][0] = 40*i;
                oppMap[i][j][1] = 40*j;
            }

        while (true) {
            turnRadarRight(360);
            switch (state) {
                case MAP_RECOGNITION:
                    createMapOfEnemies();
                    break;
                case ANALYZE:
                    findPath();
            }
        }
    }

    /**
     * Initialization of most important variables.
     */
    private void setUpValues(){
        state = State.MAP_RECOGNITION;
        coordList = new Vector<>();
        rows = (int) (getBattleFieldHeight() / 40);
        columns = (int) (getBattleFieldWidth() / 40);
        height = (int)getHeight();
        width = (int)getWidth();
        oppMap = new int[columns][rows][3];
        oppList = new ArrayList<Integer[]>();
        openList = new ArrayList<int[]>();
        closeList = new ArrayList<int[]>();
    }

    /**
     * Iterates through all map cells. If cell is
     * clear puts 0 (no parts of enemy's tank)
     * else puts 1.
     */
    private void createMapOfEnemies(){
        for(int i=0; i<25; i++)
            for(int j=0; j<25; j++)
            {
                for(Point x: coordList){
                    int d = getDistanceBetween2P((int)x.getX(),(int)x.getY(),oppMap[i][j][0]+20,oppMap[i][j][1]+20);
                    if (d<40) {
                        oppMap[i][j][2] = 1;
                    }
                    else
                    if (oppMap[i][j][2] != 1)
                        oppMap[i][j][2] = 0;
                }
            }
        state = State.ANALYZE;
    }

    /**
     * Path find implemented using A star algorithm.
     */
    public void findPath(){

    }

    /**
     * Moves our robot to the given position.
     * @param x cord of destination point
     * @param y cord of destination point
     */
    private void goTo(double x, double y) {
	/* Transform our coordinates into a vector */
        x -= getX();
        y -= getY();
	/* Calculate the angle to the target position */
        double angleToTarget = Math.atan2(x, y);
	/* Calculate the turn required get there */
        double targetAngle = Utils.normalRelativeAngle(angleToTarget - getHeadingRadians());
	/*
	 * The Java Hypot method is a quick way of getting the length
	 * of a vector. Which in this case is also the distance between
	 * our robot and the target location.
	 */
        double distance = Math.hypot(x, y);
	/* This is a simple method of performing set front as back */
        double turnAngle = Math.atan(Math.tan(targetAngle));
        setTurnRightRadians(turnAngle);
        if(targetAngle == turnAngle) {
            setAhead(distance);
        } else {
            setBack(distance);
        }
    }

    /**
     * Returns distance between two points of two dimensional platform.
     * @param x1 of first point
     * @param y1 of first point
     * @param x2 of second point
     * @param x2 of second point
     * @return Distance between two point as integer build in type
     */
    public int getDistanceBetween2P(int x1, int y1, int x2, int y2){
        return (int)Math.hypot(Math.abs(x2-x1), (Math.abs(y2-y1)));
    }

    /**
     * Shows values for debugging process
     * @param g graphics handler
     */
    private void printDebugParameters(Graphics2D g){
        g.setColor(new Color(254, 255, 249, 254));
        g.setFont(new Font("Courier New", Font.ITALIC, 12));
        g.drawString("X: "
                + String.valueOf(getX()),50, (int)getBattleFieldHeight()-50);
        g.drawString("Y: "
                + String.valueOf(getY()), 50, (int)getBattleFieldHeight()-70);
        g.drawString("STATE: "
                + String.valueOf(state.toString()), 50, (int)getBattleFieldHeight()-90);
    }

    /**
     * Draws boarders of sells
     * @param g graphics handler
     */
    private void drawCells(Graphics2D g){
        g.setColor(new Color(252, 255, 0x00, 0x80));
        for(int i = 0; i < getBattleFieldHeight(); i+=40){
            g.drawLine(0, i, (int)getBattleFieldHeight(), i);
        }
        for(int i = 0; i < getBattleFieldWidth(); i+=40){
            g.drawLine(i, 0, i, (int)getBattleFieldWidth());
        }
    }

    /**
     * Fills cells with proper color:
     * - red occupied
     * - blue free
     * - green visited
     * @param g graphics handler
     */
    private void showCellsStatus(Graphics2D g){
        for(int i=0; i<25; i++)
            for(int j=0; j<25; j++)
            {
                if (oppMap[i][j][2] == 1) {
                    g.setColor(new Color(255, 1, 0x00, 0x80));
                    g.fillRect(oppMap[i][j][0], oppMap[i][j][1], 40, 40);
                }
                if (oppMap[i][j][2] == 0) {
                    g.setColor(new Color(66, 50, 255, 0x80));
                    g.fillRect(oppMap[i][j][0], oppMap[i][j][1], 40, 40);
                }
                if (oppMap[i][j][2] == 2) {
                    g.setColor(new Color(70, 255, 54, 0x80));
                    g.fillRect(oppMap[i][j][0], oppMap[i][j][1], 40, 40);
                }
            }
    }

    private void showNeighCells(Graphics2D g){

    }

    @Override
    public void onHitWall(HitWallEvent e) {
        isBlocked = true;
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        isBlocked = true;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        Point point = new Point(0, 0);
        Double angle = e.getBearingRadians();
        Double d = e.getDistance();
        Double coordX = getX() + d * Math.sin(angle);
        Double coordY = getY() + d * Math.cos(angle);
        point.setLocation(coordX.intValue(), coordY.intValue());
        if (!coordList.contains(point))
            coordList.add(point);
    }

    /**
     * Main painting method.
     * @param g graphics handler
     */
    @Override
    public void onPaint(Graphics2D g) {
        drawCells(g);
        showCellsStatus(g);
        showNeighCells(g);
        printDebugParameters(g);
    }
}