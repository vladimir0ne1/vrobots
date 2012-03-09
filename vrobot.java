/**
 * 
 */
package vrobots;

import robocode.*;
import java.awt.*;
import robocode.util.*;
import java.awt.geom.*;

import java.awt.Color;
import java.awt.Graphics2D;

import java.util.List;
import java.util.ArrayList;
/**
 * @author Vovka
 *
 */


import robocode.util.Utils;


 
class WaveBullet
{
	private double startX, startY, startBearing, power;
	private long   fireTime;
	private int    direction;
	private int[]  returnSegment;
 
	public WaveBullet(double x, double y, double bearing, double power,
			int direction, long time, int[] segment)
	{
		startX         = x;
		startY         = y;
		startBearing   = bearing;
		this.power     = power;
		this.direction = direction;
		fireTime       = time;
		returnSegment  = segment;
	}
	
	public double getBulletSpeed()
	{
		return 20 - power * 3;
	}
 
	public double maxEscapeAngle()
	{
		return Math.asin(8 / getBulletSpeed());
	}
	
	public boolean checkHit(double enemyX, double enemyY, long currentTime)
	{
		// if the distance from the wave origin to our enemy has passed
		// the distance the bullet would have traveled...
		if (Point2D.distance(startX, startY, enemyX, enemyY) <= 
				(currentTime - fireTime) * getBulletSpeed())
		{
			double desiredDirection = Math.atan2(enemyX - startX, enemyY - startY);
			double angleOffset = Utils.normalRelativeAngle(desiredDirection - startBearing);
			double guessFactor =
				Math.max(-1, Math.min(1, angleOffset / maxEscapeAngle())) * direction;
			int index = (int) Math.round((returnSegment.length - 1) /2 * (guessFactor + 1));
			returnSegment[index]++;
			return true;
		}
		return false;
	}
} // end WaveBullet class



public class vrobot extends AdvancedRobot {
	
	List<WaveBullet> waves = new ArrayList<WaveBullet>();
	static int[] stats = new int[31]; // 31 is the number of unique GuessFactors we're using
	  // Note: this must be odd number so we can get
	  // GuessFactor 0 at middle.
	int direction = 1;
	
	
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
		setMaxVelocity(8);
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
	int scannedX, scannedY;
	double newEnemyX, newEnemyY;
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	double enemyEnergy = 100;

	public void onScannedRobot(ScannedRobotEvent e) {		
		
		
		
		double absBearing=e.getBearingRadians()+getHeadingRadians();
		double BattleFieldHeight = getBattleFieldHeight();
		double BattleFieldWidth = getBattleFieldWidth();
		double BattleFieldDiagonal = Math.sqrt(Math.pow(BattleFieldHeight, 2) + Math.pow(BattleFieldWidth, 2));		
		
		//double myHeadingRadians = getHeadingRadians();
		double distance = e.getDistance();
		
		double firePower =  Math.min(500/distance, 3);		
		
		double bulletTime = distance/(20 - 3*firePower);
		double enemyPath = bulletTime*e.getVelocity();
		
		//double enemyX = distance*Math.sin(myHeadingRadians);
		//double enemyY = distance*Math.cos(myHeadingRadians);
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
		
		 scannedX = (int)(getX() + Math.sin(angle) * e.getDistance());
	     scannedY = (int)(getY() + Math.cos(angle) * e.getDistance());
		 
		newEnemyX = enemyPath*Math.sin(e.getHeadingRadians());
		newEnemyY = enemyPath*Math.cos(e.getHeadingRadians());
		
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
		//setTurnGunRight(gunTurn);
		//turnRight(90+enemyHeading - getHeading());
		

		double absBearing2 = getHeadingRadians() + e.getBearingRadians();
		 
		// find our enemy's location:
		double ex = getX() + Math.sin(absBearing2) * e.getDistance();
		double ey = getY() + Math.cos(absBearing2) * e.getDistance();
		
 //==============================================================================
		
		for (int i=0; i < waves.size(); i++)
		{
			WaveBullet currentWave = (WaveBullet)waves.get(i);
			if (currentWave.checkHit(ex, ey, getTime()))
			{
				waves.remove(currentWave);
				i--;
			}
		}
 
		double power = Math.min(3, 500/distance);

		if (e.getVelocity() != 0)
		{
			if (Math.sin(e.getHeadingRadians()-absBearing2)*e.getVelocity() < 0)
				direction = -1;
			else
				direction = 1;
		}
		int[] currentStats = stats; // This seems silly, but I'm using it to
					    // show something else later
		WaveBullet newWave = new WaveBullet(getX(), getY(), absBearing2, power,
                        direction, getTime(), currentStats);
		
		int bestindex = 15;	// initialize it to be in the middle, guessfactor 0.
		for (int i=0; i<31; i++)
			if (currentStats[bestindex] < currentStats[i])
				bestindex = i;
 
		// this should do the opposite of the math in the WaveBullet:
		double guessfactor = (double)(bestindex - (stats.length - 1) / 2)
                        / ((stats.length - 1) / 2);
		double angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
                double gunAdjust = Utils.normalRelativeAngle(
                        absBearing2 - getGunHeadingRadians() + angleOffset);
                setTurnGunRightRadians(gunAdjust);
                
             
                if (setFireBullet(power) != null)
                    waves.add(newWave);
//==============================================================================
		
		if (isStart == true)
		{
			turnRight(90 + e.getBearing());
			isStart = false;
		}
		else
			setTurnRight(90+e.getBearing()-15);
		setAhead(dist);
		
		
		if (enemyEnergy!= e.getEnergy())
		//{
		//	ahead(100);
			
		//}
		{
		setAhead(-dist);
		setTurnRight(-degr);
		dist = -dist;
		degr = -degr;
		enemyEnergy = e.getEnergy();
		}
		
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining())<10)
			setFire(firePower);
		
	}
	
	public void onPaint(Graphics2D g) {
		 // Set the paint color to a red half transparent color
	     g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
	 
	     // Draw a line from our robot to the scanned robot
	     g.drawLine(scannedX, scannedY, (int)getX(), (int)getY());
	 
	     // Draw a filled square on top of the scanned robot that covers it
	     g.fillRect(scannedX - 20, scannedY - 20, 40, 40);
	 }

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		dist *= -1;
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		dist = -dist;
	}	
}