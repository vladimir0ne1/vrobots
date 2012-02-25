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
	private boolean isStart = true;
	public void run() {
		

		setColors(Color.gray,Color.black,Color.white); // body,gun,radar
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		setMaxVelocity(5);
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
			turnRadarRightRadians(Double.POSITIVE_INFINITY);

		}
	}
	

	double dist = 100;
	double degr = 360;
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	double enemyEnergy = 100;

	public void onScannedRobot(ScannedRobotEvent e) {		
		
		double absBearing=e.getBearingRadians()+getHeadingRadians();
		
		
		
		double BattleFieldHeight = getBattleFieldHeight();
		double BattleFieldWidth = getBattleFieldWidth();
		double BattleFieldDiagonal = Math.sqrt(Math.pow(BattleFieldHeight, 2) + Math.pow(BattleFieldWidth, 2));		
		
		double myHeadingRadians = getHeadingRadians();
		double distance = e.getDistance();
		
		double firePower =  Math.min(500/distance, 3);		
		
		double bulletTime = distance/(20 - 3*firePower);
		double enemyPath = bulletTime*e.getVelocity();
		
		//double enemyX = distance*Math.sin(myHeadingRadians);
		//double enemyY = distance*Math.cos(myHeadingRadians);
		
		//double newEnemyX = enemyPath*Math.sin(e.getHeadingRadians());
		//double newEnemyY = enemyPath*Math.cos(e.getHeadingRadians());
		
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
		//setTurnRadarRight(radarTurn*2);
		setTurnRadarRightRadians(Utils.normalRelativeAngle(
				absBearing - getRadarHeadingRadians())*2);
		//setTurnGunRight(gunTurn + gunAdjustment);
		setTurnGunRight(gunTurn);
		//turnRight(90+enemyHeading - getHeading());
		
		if (isStart == true)
		{
			turnRight(90 + e.getBearing());
			isStart = false;
		}
		else
			setTurnRight(90+e.getBearing()-15);
		setAhead(dist);
		//setTurnRight(10000);
		
		//if (enemyEnergy!= e.getEnergy())
		//{
		//setAhead(-dist);
		//setTurnRight(-degr);
		//dist = -dist;
		//degr = -degr;
		//enemyEnergy = e.getEnergy();
		//}
		
		
		//setTurnRight(degr);
		//setAhead(dist);
		
		//dist--;
		//degr--;
		//if ()
		//setTurnRight(100);
		
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining())<10)
			setFire(firePower);
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		dist *= -1;
		//setAhead(dist);
	//	ahead(100);
		// Replace the next line with any behavior you would like
		//back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		dist = -dist;
		// Replace the next line with any behavior you would like
		//setAhead(100);
	}	
}