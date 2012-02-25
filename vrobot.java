/**
 * 
 */
package vrobots;

import robocode.*;
import java.awt.*;
import robocode.util.*;
import java.awt.geom.*;

/**
 * @author Vovka
 *
 */
public class vrobot extends AdvancedRobot {
	/**
	 * run: Kva's default behavior
	 */
	String Turn = "left";
	double rt = 10000;
	boolean flag = false;
	
	public double getNormalHeading(double h, double head)
	{
		if (h > 180)
			return 360 - h;
		else if (h <= 180)
			return head - h;
		
		return 180;
	}
	public double getNormalBearing(double b)
	{
		if (b >= 0)
			return b;
		else
			return  360 + b;
	}
	
	public void run() {
		

		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		
		double normalHeading = getNormalHeading(getHeading(), 0);
		System.out.println("Heading: " + getHeading());
		System.out.println("h: " + normalHeading);
		
		System.out.println("New h: " +  normalHeading);

		System.out.println("Heading: " + getHeading());	

		System.out.println("sin30: " + Math.sin(30*(Math.PI/180)));
		System.out.println("Relative (210) : " + Utils.normalAbsoluteAngleDegrees(-150));
		System.out.println("Relative (210) : " + Utils.normalRelativeAngleDegrees(210));
		System.out.println("Relative (330) : " + Utils.normalRelativeAngleDegrees(330));
		while(true) {			
			turnRadarRightRadians(rt);

		}
	}
	
	public double getBearingWithSign(double heading, double bearing)
	{
		if(heading > bearing)
			return -bearing;
		else
			return bearing;
	}
	

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	//@SuppressWarnings("deprecation")
	public void onScannedRobot(ScannedRobotEvent e) {
		double enemyHeading = Utils.normalAbsoluteAngleDegrees(getHeading() + e.getBearing());
		
		double radarTurn = enemyHeading - getRadarHeading();
		double gunTurn = enemyHeading - getGunHeading();
		
		System.out.println("enemyHeading: " + enemyHeading);
		System.out.println("\nradarTurn: " + radarTurn);
		setTurnRadarRight(radarTurn);
		
		setTurnGunRight(gunTurn);
		setAhead(5);
		setTurnRight(100);
		
		fire(3);
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(100);
	}	
}