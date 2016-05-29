import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

import robocode.*;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;


public class JavaRobot extends AdvancedRobot {

    private Commons c = new Commons(this);
    boolean isBlocked;
    private State state;
    public int oppMap[][][];
    private ArrayList<Integer[]> oppList;
    private Map<String, Integer> neigh;
    private ArrayList<Point> openList;
    private ArrayList<Point> closeList;
    private int rows;
    private int columns;
    private int height;
    private int width;
    private Vector<Point> cordList;
    private Optional<Map.Entry<String, Integer>> maxValue;
    private Map<String, Point> directionMap;
    private int destinationX = 900;
    private int destinationY = 900;

    public JavaRobot() {}

    /**
     * Main loop of battle.
     */
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
                case END:
                    turnRadarRight(360);
            }
        }
    }

    /**
     * Initialization of most important variables.
     */
    private void setUpValues(){
        state = State.MAP_RECOGNITION;
        cordList = new Vector<>();
        rows = (int) (getBattleFieldHeight() / 40);
        columns = (int) (getBattleFieldWidth() / 40);
        height = (int)getHeight();
        width = (int)getWidth();
        oppMap = new int[columns][rows][3];
        oppList = new ArrayList<Integer[]>();
        openList = new ArrayList<>();
        closeList = new ArrayList<>();
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
                for(Point x: cordList){
                    int d = c.getDistanceBetween2P((int)x.getX(),(int)x.getY(),oppMap[i][j][0]+20,oppMap[i][j][1]+20);
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

        int i = c.getMyCurrentCell().x;
        int j = c.getMyCurrentCell().y;

        System.out.println("I: " + i + " J: " + j + " X: " + getX() + " Y: " + getY());

        neigh = new HashMap<>();
        directionMap = new HashMap<>();

        if(i<24 && j<24) {
            directionMap.put("UR", new Point(oppMap[i + 1][j + 1][0], oppMap[i + 1][j + 1][1]));
            directionMap.put("DL", new Point(oppMap[i - 1][j - 1][0], oppMap[i - 1][j - 1][1]));
            directionMap.put("R", new Point(oppMap[i + 1][j][0], oppMap[i + 1][j][1]));
            directionMap.put("L", new Point(oppMap[i - 1][j][0], oppMap[i - 1][j][1]));
            directionMap.put("U", new Point(oppMap[i][j + 1][0], oppMap[i][j + 1][1]));
            directionMap.put("D", new Point(oppMap[i][j - 1][0], oppMap[i][j - 1][1]));
            directionMap.put("UL", new Point(oppMap[i - 1][j + 1][0], oppMap[i - 1][j + 1][1]));
            directionMap.put("DR", new Point(oppMap[i + 1][j - 1][0], oppMap[i + 1][j - 1][1]));
        }

        if (i<24 && j<24) {
            if(oppMap[i + 1][j + 1][2]==0 && oppMap[i + 1][j][2]==0 && oppMap[i][j+1][2]==0
                    && !closeList.contains(new Point(oppMap[i + 1][j + 1][0], oppMap[i + 1][j + 1][1]))){
                System.out.println("UR");
                neigh.put("UR",
                        c.getDistanceBetween2P(oppMap[i + 1][j + 1][0], oppMap[i + 1][j + 1][1], destinationX, destinationY));
            }
        }
        if (i>0 && j>0) {
            if(oppMap[i - 1][j - 1][2]==0 && oppMap[i - 1][j][2]==0 && oppMap[i][j - 1][2]==0
                    && !closeList.contains(new Point(oppMap[i - 1][j - 1][0], oppMap[i - 1][j - 1][1]))){
                System.out.println("DL");

                neigh.put("DL",
                        c.getDistanceBetween2P(oppMap[i - 1][j - 1][0], oppMap[i - 1][j - 1][1], destinationX, destinationY));
            }
        }
        if (i<24) {
            if(oppMap[i + 1][j][2]==0 && !closeList.contains(new Point(oppMap[i + 1][j][0], oppMap[i + 1][j][1]))){
                System.out.println("R");

                neigh.put("R",
                        c.getDistanceBetween2P(oppMap[i + 1][j][0], oppMap[i + 1][j][1], destinationX, destinationY));
            }}
        if (i>0) {
            if(oppMap[i-1][j][2]==0 && !closeList.contains(new Point(oppMap[i - 1][j][0], oppMap[i - 1][j][1]))){
                System.out.println("L");

                neigh.put("L",
                        c.getDistanceBetween2P(oppMap[i - 1][j][0], oppMap[i - 1][j][1], destinationX, destinationY));
            }}
        if (j<24) {
            if(oppMap[i][j + 1][2]==0 && !closeList.contains(new Point(oppMap[i][j + 1][0], oppMap[i][j + 1][1]))){
                System.out.println("U");
                neigh.put("U",
                        c.getDistanceBetween2P(oppMap[i][j + 1][0], oppMap[i][j + 1][1], destinationX, destinationY));
            }}
        if (j>0) {

            if(oppMap[i][j - 1][2]==0 && !closeList.contains(new Point(oppMap[i][j - 1][0], oppMap[i][j - 1][1]))){
                System.out.println("D");
                neigh.put("D",
                        c.getDistanceBetween2P(oppMap[i][j - 1][0], oppMap[i][j - 1][1], destinationX, destinationY));
            }
        }
        if (i<24 && j>0) {

            if(oppMap[i + 1][j - 1][2]==0 && oppMap[i + 1][j][2]==0 && oppMap[i][j - 1][2]==0
                    && !closeList.contains(new Point(oppMap[i + 1][j - 1][0],
                                oppMap[i + 1][j - 1][1]))) {
                System.out.println("DR");
                neigh.put("DR",
                        c.getDistanceBetween2P(oppMap[i + 1][j - 1][0], oppMap[i + 1][j - 1][1], destinationX, destinationY));
            }}
        if (i>0 && j<24) {
            if(oppMap[i - 1][j + 1][2]==0 && oppMap[i - 1][j][2]==0 && oppMap[i][j + 1][2]==0 &&
                    !closeList.contains(new Point(oppMap[i - 1][j + 1][0], oppMap[i - 1][j + 1][1]))) {
                System.out.println("UL");

                neigh.put("UL",
                        c.getDistanceBetween2P(oppMap[i - 1][j + 1][0], oppMap[i - 1][j + 1][1], destinationX, destinationY));
            }
        }

        Comparator<? super Map.Entry<String, Integer>> maxValueComparator =
                (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());

        maxValue = neigh.entrySet()
                    .stream().min(maxValueComparator);
        System.out.println("---->>>>> " + maxValue.get().getKey());
        c.goTo(directionMap.get(maxValue.get().getKey()).x+20, directionMap.get(maxValue.get().getKey()).y+20);

        closeList.add(directionMap.get(maxValue.get().getKey()));

        if(c.getDistanceBetween2P((int)getX(),(int)getY(),destinationX,destinationY)<57)
            state = State.END;
    }

    public void debugPath(){
        c.goTo(c.getCellCenter(4,4).x, c.getCellCenter(4,4).y);
        execute();
        waitFor(new MoveCompleteCondition(this));

        c.goTo(c.getCellCenter(5,5).x, c.getCellCenter(5,5).y);
        execute();
        waitFor(new MoveCompleteCondition(this));

        c.goTo(c.getCellCenter(10,16).x, c.getCellCenter(10,16).y);
        execute();
        waitFor(new MoveCompleteCondition(this));
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
        if (maxValue.isPresent()){
            g.drawString("STATE: "
                    + String.valueOf(maxValue.toString()), 50, (int)getBattleFieldHeight()-110);
        }
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
     * - blue free (0)
     * - red occupied (1)
     * - green visited (2)
     * @param g graphics handler
     */
    private void showCellsStatus(Graphics2D g){
        for(int i=0; i<25; i++)
            for(int j=0; j<25; j++)
            {
                if (oppMap[i][j][2] == 0) {
                g.setColor(new Color(66, 50, 255, 0x80));
                g.fillRect(oppMap[i][j][0], oppMap[i][j][1], 40, 40);
                }
                if (oppMap[i][j][2] == 1) {
                    g.setColor(new Color(255, 1, 0x00, 0x80));
                    g.fillRect(oppMap[i][j][0], oppMap[i][j][1], 40, 40);
                }
                if (oppMap[i][j][2] == 2) {
                    g.setColor(new Color(70, 255, 54, 0x80));
                    g.fillRect(oppMap[i][j][0], oppMap[i][j][1], 40, 40);
                }
            }
    }

    private void showNeighCells(Graphics2D g){
        g.setColor(new Color(70, 255, 54, 0x80));
        int i = c.getMyCurrentCell().x;
        int j = c.getMyCurrentCell().y;
        g.fillRect(oppMap[i][j][0], oppMap[i][j][1], 40, 40);
        if (i<24 && j<24)
            g.fillRect(oppMap[i+1][j+1][0], oppMap[i+1][j+1][1], 40, 40);
        if (i>1 && j>1)
            g.fillRect(oppMap[i-1][j-1][0], oppMap[i-1][j-1][1], 40, 40);
        if (i<24)
            g.fillRect(oppMap[i+1][j][0], oppMap[i+1][j][1], 40, 40);
        if (i>1)
            g.fillRect(oppMap[i-1][j][0], oppMap[i-1][j][1], 40, 40);
        if (j<24)
            g.fillRect(oppMap[i][j+1][0], oppMap[i][j+1][1], 40, 40);
        if (j>1)
            g.fillRect(oppMap[i][j-1][0], oppMap[i][j-1][1], 40, 40);
        if (i<24 && j>1)
            g.fillRect(oppMap[i+1][j-1][0], oppMap[i+1][j-1][1], 40, 40);
        if (i>1 && j<24)
            g.fillRect(oppMap[i-1][j+1][0], oppMap[i-1][j+1][1], 40, 40);
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
        if (!cordList.contains(point))
            cordList.add(point);
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