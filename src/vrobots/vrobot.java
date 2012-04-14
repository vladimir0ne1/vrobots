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

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
/**
 * @author Vovka
 *
 */


import robocode.util.Utils;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;     // for Point2D's
import java.util.ArrayList; // for collection of waves



 
class WaveBullet
{
	//123
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
	
	
	
	//========================
	public static int BINS = 47;
    public static double _surfStats[] = new double[BINS];
    public Point2D.Double _myLocation;     // our bot's location
    public Point2D.Double _enemyLocation;  // enemy bot's location
 
    public ArrayList _enemyWaves;
    public ArrayList _surfDirections;
    public ArrayList _surfAbsBearings;
 
    public static double _oppEnergy = 100.0;
 
   /** This is a rectangle that represents an 800x600 battle field,
    * used for a simple, iterative WallSmoothing method (by PEZ).
    * If you're not familiar with WallSmoothing, the wall stick indicates
    * the amount of space we try to always have on either end of the tank
    * (extending straight out the front or back) before touching a wall.
    */
    public static Rectangle2D.Double _fieldRect
        = new java.awt.geom.Rectangle2D.Double(18, 18, 764, 564);
    public static double WALL_STICK = 160;
 
    
	//========================
	
	
	
	public void run() {
		
		 _enemyWaves = new ArrayList();
	        _surfDirections = new ArrayList();
	        _surfAbsBearings = new ArrayList();
	        
		//Random();
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
	

	class EnemyWave {
	    Point2D.Double fireLocation;
	    long fireTime;
	    double bulletVelocity, directAngle, distanceTraveled;
	    int direction;

	    public EnemyWave() { }
	}

	public double wallSmoothing(Point2D.Double botLocation, double angle, int orientation) {
	    while (!_fieldRect.contains(project(botLocation, angle, WALL_STICK))) {
	        angle += orientation*0.05;
	    }
	    return angle;
	}

	public static Point2D.Double project(Point2D.Double sourceLocation,
	    double angle, double length) {
	    return new Point2D.Double(sourceLocation.x + Math.sin(angle) * length,
	        sourceLocation.y + Math.cos(angle) * length);
	}

	public static double absoluteBearing(Point2D.Double source, Point2D.Double target) {
	    return Math.atan2(target.x - source.x, target.y - source.y);
	}

	public static double limit(double min, double value, double max) {
	    return Math.max(min, Math.min(value, max));
	}

	public static double bulletVelocity(double power) {
	    return (20.0 - (3.0*power));
	}

	public static double maxEscapeAngle(double velocity) {
	    return Math.asin(8.0/velocity);
	}

	public static void setBackAsFront(AdvancedRobot robot, double goAngle) {
	    double angle =
	        Utils.normalRelativeAngle(goAngle - robot.getHeadingRadians());
	    if (Math.abs(angle) > (Math.PI/2)) {
	        if (angle < 0) {
	            robot.setTurnRightRadians(Math.PI + angle);
	        } else {
	            robot.setTurnLeftRadians(Math.PI - angle);
	        }
	        robot.setBack(100);
	    } else {
	        if (angle < 0) {
	            robot.setTurnLeftRadians(-1*angle);
	       } else {
	            robot.setTurnRightRadians(angle);
	       }
	        robot.setAhead(100);
	    }
	}
	
	double dist = 100;
	double tempDist = 100;
	double degr = 360;
	int scannedX, scannedY;
	double newEnemyX, newEnemyY;
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	double enemyEnergy = 100;
	
	int changedirection = 0;

	public void onScannedRobot(ScannedRobotEvent e) {		
		
		
		//========== wave surf //
		
		_myLocation = new Point2D.Double(getX(), getY());
		 
        double lateralVelocity = getVelocity()*Math.sin(e.getBearingRadians());
        double absBearing = e.getBearingRadians() + getHeadingRadians();
 
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing
            - getRadarHeadingRadians()) * 2);
 
        _surfDirections.add(0,
            new Integer((lateralVelocity >= 0) ? 1 : -1));
        _surfAbsBearings.add(0, new Double(absBearing + Math.PI));
 
 
        double bulletPower = _oppEnergy - e.getEnergy();
        if (bulletPower < 3.01 && bulletPower > 0.09
            && _surfDirections.size() > 2) {
            EnemyWave ew = new EnemyWave();
            ew.fireTime = getTime() - 1;
            ew.bulletVelocity = bulletVelocity(bulletPower);
            ew.distanceTraveled = bulletVelocity(bulletPower);
            ew.direction = ((Integer)_surfDirections.get(2)).intValue();
            ew.directAngle = ((Double)_surfAbsBearings.get(2)).doubleValue();
            ew.fireLocation = (Point2D.Double)_enemyLocation.clone(); // last tick
 
            _enemyWaves.add(ew);
        }
 
        _oppEnergy = e.getEnergy();
 
        // update after EnemyWave detection, because that needs the previous
        // enemy location as the source of the wave
        _enemyLocation = project(_myLocation, absBearing, e.getDistance());
 
        updateWaves();
        doSurfing();
		
		//============= gauss //
		
		
		double absBearing_gauss=e.getBearingRadians()+getHeadingRadians();
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
		
		/*System.out.println("BattleFieldDiagonal: " + BattleFieldDiagonal);
		System.out.println("distance: " + distance);
		System.out.println("firePower: " + firePower);
		
		System.out.println("bulletTime: " + bulletTime);
		System.out.println("enemyPath: " + enemyPath);
		System.out.println("gunAdjustment: " + gunAdjustment);		
		*/
		double enemyHeading = Utils.normalAbsoluteAngleDegrees(getHeading() + e.getBearing());
		
		double radarTurn = enemyHeading - getRadarHeading();
		double gunTurn = enemyHeading - getGunHeading();
		
		
		//System.out.println("enemyHeading: " + enemyHeading);
		//System.out.println("\nradarTurn: " + radarTurn);
		//setTurnRadarRight(radarTurn*2);
		setTurnRadarRightRadians(Utils.normalRelativeAngle(
				absBearing_gauss - getRadarHeadingRadians())*2);
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
		//setAhead(dist);
		
		
		if (enemyEnergy != e.getEnergy())
		//{
		//	ahead(100);
			
		//}
		{
			if (changedirection == 2)
			{
				setAhead(dist);
				//setAhead(e.getDistance()/2);
				//dist = tempDist;
				System.out.println("Change");
				//setAhead(-dist);			
				//setTurnRight(-degr);
				//dist = -dist;
				//degr = -degr;
				enemyEnergy = e.getEnergy();
				this.changedirection = 0;
			}
			else
			{
				if (changedirection == 0)
					//setAhead(e.getDistance()/2);
					setAhead(dist);
				//tempDist = dist;
				//dist = 0;
				enemyEnergy = e.getEnergy();
				System.out.println("No change");
				//setAhead(dist);
				this.changedirection++;
			}
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
        // If the _enemyWaves collection is empty, we must have missed the
        // detection of this wave somehow.
        if (!_enemyWaves.isEmpty()) {
            Point2D.Double hitBulletLocation = new Point2D.Double(
                e.getBullet().getX(), e.getBullet().getY());
            EnemyWave hitWave = null;
 
            // look through the EnemyWaves, and find one that could've hit us.
            for (int x = 0; x < _enemyWaves.size(); x++) {
                EnemyWave ew = (EnemyWave)_enemyWaves.get(x);
 
                if (Math.abs(ew.distanceTraveled -
                    _myLocation.distance(ew.fireLocation)) < 50
                    && Math.abs(bulletVelocity(e.getBullet().getPower()) 
                        - ew.bulletVelocity) < 0.001) {
                    hitWave = ew;
                    break;
                }
            }
 
            if (hitWave != null) {
                logHit(hitWave, hitBulletLocation);
 
                // We can remove this wave now, of course.
                _enemyWaves.remove(_enemyWaves.lastIndexOf(hitWave));
            }
        }
    }
	
	//====================
	
	public void updateWaves() {
        for (int x = 0; x < _enemyWaves.size(); x++) {
            EnemyWave ew = (EnemyWave)_enemyWaves.get(x);
 
            ew.distanceTraveled = (getTime() - ew.fireTime) * ew.bulletVelocity;
            if (ew.distanceTraveled >
                _myLocation.distance(ew.fireLocation) + 50) {
                _enemyWaves.remove(x);
                x--;
            }
        }
    }
	
	public EnemyWave getClosestSurfableWave() {
        double closestDistance = 50000; // I juse use some very big number here
        EnemyWave surfWave = null;
 
        for (int x = 0; x < _enemyWaves.size(); x++) {
            EnemyWave ew = (EnemyWave)_enemyWaves.get(x);
            double distance = _myLocation.distance(ew.fireLocation)
                - ew.distanceTraveled;
 
            if (distance > ew.bulletVelocity && distance < closestDistance) {
                surfWave = ew;
                closestDistance = distance;
            }
        }
 
        return surfWave;
    }
	
	 // Given the EnemyWave that the bullet was on, and the point where we
    // were hit, calculate the index into our stat array for that factor.
    public static int getFactorIndex(EnemyWave ew, Point2D.Double targetLocation) {
        double offsetAngle = (absoluteBearing(ew.fireLocation, targetLocation)
            - ew.directAngle);
        double factor = Utils.normalRelativeAngle(offsetAngle)
            / maxEscapeAngle(ew.bulletVelocity) * ew.direction;
 
        return (int)limit(0,
            (factor * ((BINS - 1) / 2)) + ((BINS - 1) / 2),
            BINS - 1);
    }
    
 // Given the EnemyWave that the bullet was on, and the point where we
    // were hit, update our stat array to reflect the danger in that area.
    public void logHit(EnemyWave ew, Point2D.Double targetLocation) {
        int index = getFactorIndex(ew, targetLocation);
 
        for (int x = 0; x < BINS; x++) {
            // for the spot bin that we were hit on, add 1;
            // for the bins next to it, add 1 / 2;
            // the next one, add 1 / 5; and so on...
            _surfStats[x] += 1.0 / (Math.pow(index - x, 2) + 1);
        }
    }
    
    public Point2D.Double predictPosition(EnemyWave surfWave, int direction) {
        Point2D.Double predictedPosition = (Point2D.Double)_myLocation.clone();
        double predictedVelocity = getVelocity();
        double predictedHeading = getHeadingRadians();
        double maxTurning, moveAngle, moveDir;
 
        int counter = 0; // number of ticks in the future
        boolean intercepted = false;
 
        do {    // the rest of these code comments are rozu's
            moveAngle =
                wallSmoothing(predictedPosition, absoluteBearing(surfWave.fireLocation,
                predictedPosition) + (direction * (Math.PI/2)), direction)
                - predictedHeading;
            moveDir = 1;
 
            if(Math.cos(moveAngle) < 0) {
                moveAngle += Math.PI;
                moveDir = -1;
            }
 
            moveAngle = Utils.normalRelativeAngle(moveAngle);
 
            // maxTurning is built in like this, you can't turn more then this in one tick
            maxTurning = Math.PI/720d*(40d - 3d*Math.abs(predictedVelocity));
            predictedHeading = Utils.normalRelativeAngle(predictedHeading
                + limit(-maxTurning, moveAngle, maxTurning));
 
            // this one is nice ;). if predictedVelocity and moveDir have
            // different signs you want to breack down
            // otherwise you want to accelerate (look at the factor "2")
            predictedVelocity +=
                (predictedVelocity * moveDir < 0 ? 2*moveDir : moveDir);
            predictedVelocity = limit(-8, predictedVelocity, 8);
 
            // calculate the new predicted position
            predictedPosition = project(predictedPosition, predictedHeading,
                predictedVelocity);
 
            counter++;
 
            if (predictedPosition.distance(surfWave.fireLocation) <
                surfWave.distanceTraveled + (counter * surfWave.bulletVelocity)
                + surfWave.bulletVelocity) {
                intercepted = true;
            }
        } while(!intercepted && counter < 500);
 
        return predictedPosition;
    }
    
    public double checkDanger(EnemyWave surfWave, int direction) {
        int index = getFactorIndex(surfWave,
            predictPosition(surfWave, direction));
 
        return _surfStats[index];
    }
 
    public void doSurfing() {
        EnemyWave surfWave = getClosestSurfableWave();
 
        if (surfWave == null) { return; }
 
        double dangerLeft = checkDanger(surfWave, -1);
        double dangerRight = checkDanger(surfWave, 1);
 
        double goAngle = absoluteBearing(surfWave.fireLocation, _myLocation);
        if (dangerLeft < dangerRight) {
            goAngle = wallSmoothing(_myLocation, goAngle - (Math.PI/2), -1);
        } else {
            goAngle = wallSmoothing(_myLocation, goAngle + (Math.PI/2), 1);
        }
 
        setBackAsFront(this, goAngle);
    }
	
	//=======================
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		dist = -dist;
		setAhead(dist);
		
	}	
}