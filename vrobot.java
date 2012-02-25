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
		System.out.println("sin 30: " + Math.sin(Math.PI/6));
		System.out.println("sin 120: " + Math.sin(Math.PI/6 + Math.PI/2));
		System.out.println("sin 210: " + Math.sin(Math.PI/6 + Math.PI));
		System.out.println("sin 300: " + Math.sin(Math.PI/6 + 3*Math.PI));
		System.out.println("arcsin 300: " + Math.asin(0.5));
		
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
	
	public double getFutureEnemyX()
	{
		return 0;
	}
	
	public double getFutureEnemyY()
	{
		return 0;
	}
	
	public double getGunAdjustmentAngle(ScannedRobotEvent e, double newEnemyX, double newEnemyY)
	{
		double heading = e.getHeading();
		
		
		return 0;
	}
	

	/**
	 * onScannedRobot: What to do when you see another robot
	 */

	public void onScannedRobot(ScannedRobotEvent e) {
		
		double BattleFieldHeight = getBattleFieldHeight();
		double BattleFieldWidth = getBattleFieldWidth();
		double BattleFieldDiagonal = Math.sqrt(Math.pow(BattleFieldHeight, 2) + Math.pow(BattleFieldWidth, 2));		
		
		double myHeadingRadians = getHeadingRadians();
		double distance = e.getDistance();
		
		double firePower = Math.min(400/distance, 3);		
		
		double bulletTime = distance/(20 - 3*firePower);
		double enemyPath = bulletTime*e.getVelocity();
		
		double enemyX = distance*Math.sin(myHeadingRadians);
		double enemyY = distance*Math.cos(myHeadingRadians);
		
		double newEnemyX = enemyPath*Math.sin(e.getHeadingRadians());
		double newEnemyY = enemyPath*Math.cos(e.getHeadingRadians());
		
		double gunAdjustment = Math.asin(enemyPath/(2*distance))*2*180/Math.PI;
		
		System.out.println("BattleFieldDiagonal: " + BattleFieldDiagonal);
		System.out.println("distance: " + distance);
		System.out.println("firePower: " + firePower);
		
		System.out.println("bulletTime: " + bulletTime);
		System.out.println("enemyPath: " + enemyPath);
		System.out.println("gunAdjustment: " + gunAdjustment);		
		
		double enemyHeading = Utils.normalAbsoluteAngleDegrees(getHeading() + e.getBearing());
		
		double radarTurn = enemyHeading - getRadarHeading();
		double gunTurn = enemyHeading - getGunHeading();
		
		System.out.println("enemyHeading: " + enemyHeading);
		System.out.println("\nradarTurn: " + radarTurn);
		setTurnRadarRight(radarTurn*2);
		setTurnGunRight(gunTurn + gunAdjustment);
		//setAhead(6);
		//setTurnRight(100);
		
		fire(firePower);
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		//back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		//setAhead(100);
	}	
}