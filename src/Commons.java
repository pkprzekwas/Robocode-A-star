import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.awt.*;

/**
 * Class with common methods.
 */
public class Commons {

    private JavaRobot robot;
    public static final int CellSize = 40;

    public Commons(JavaRobot robot){
        this.robot = robot;
    }
    /**
     * Returns distance between two points of two dimensional platform.
     * @param x1 of first point
     * @param y1 of first point
     * @param x2 of second point
     * @param x2 of second point
     * @return Distance between two point as integer build in type
     */
    public Integer getDistanceBetween2P(int x1, int y1, int x2, int y2){
        return new Integer((int)Math.hypot(Math.abs(x2-x1), (Math.abs(y2-y1))));
    }

    /**
     * Moves our robot to the given position.
     * @param x cord of destination point
     * @param y cord of destination point
     */
    public void goTo(double x, double y) {
	/* Transform our coordinates into a vector */
        x -= robot.getX();
        y -= robot.getY();
	/* Calculate the angle to the target position */
        double angleToTarget = Math.atan2(x, y);
	/* Calculate the turn required get there */
        double targetAngle = Utils.normalRelativeAngle(angleToTarget - robot.getHeadingRadians());
	/*
	 * The Java Hypot method is a quick way of getting the length
	 * of a vector. Which in this case is also the distance between
	 * our robot and the target location.
	 */
        double distance = Math.hypot(x, y);
	/* This is a simple method of performing set front as back */
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.setTurnRightRadians(turnAngle);
        if(targetAngle == turnAngle) {
            robot.setAhead(distance);
        } else {
            robot.setBack(distance);
        }
    }

    /**
     * Returns cell of which most of robot's area covers.
     * @return Point(x cord of cell, y cord of cell)
     */
    public Point getMyCurrentCell(){
        return new Point((int)robot.getX()/CellSize, (int)robot.getY()/CellSize);
    }

    /**
     * Returns center of cell with given A and B
     * @return Point(x cord of cell center, y cord of cell center)
     */
    public Point getCellCenter(int a, int b){
        return new Point(robot.mainMap[a][b][0]+20,robot.mainMap[a][b][1]+20);
    }
}
