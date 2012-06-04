/**
 * 
 */
package vrobots;

import robocode.*;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import robocode.util.Utils;
/**
 * @author Vovka
 *
 */
public class vrobot extends AdvancedRobot {

	/** ������ ������ � ���������.
	 * ������ ��������
	 * ����������� ��������
	 * �������� ���������� ����� 
	 * ����� �������� � �.�.
	 */
	List<WaveBullet> waves = new ArrayList<WaveBullet>();
	/**������ ���������� GaussFactor's.
	 * 31 - ����� ���������� GaussFactor's
	 * ����� ��������� - ��������
	 * GuessFactor = 0 � ��������
	 */
	static int[] stats = new int[31]; 
	  // ����� ��������� - ��������
	  // GuessFactor 0 � ��������
	/** ����������� �������� �����.
	 * (-1) - ������ ������� �������.
	 * (1) - �� ������� �������.
	 */
	int direction = 1;
	
	/** ������ ������� �����.
	 * true - ��
	 * false - ���
	 */
	private boolean isStart = true;
	/** ����� �����.
	 * 
	 */
	public void run() {

		setColors(Color.gray,Color.black,Color.white); // ������, �����, �����
		// ������ ��� ����� ������������ ���� �� ����� (��������� ����������)
		setAdjustRadarForGunTurn(true); 
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		// ������������� �������� �������� ������
		setMaxVelocity(8);

		while(true) {			
			turnRadarRightRadians(Double.POSITIVE_INFINITY);
		}
	}
	/** ����������, �� ������� ����� ��������.
	 * 
	 */
	double dist = 100;
	/** ����, �� ������� ����� �����������.
	 * 
	 */
	double degr = 360;
	/** ���������� ������� ���������� ����� 100 (����� ���������� ��� ��������)
	 * 
	 */
	double enemyEnergy = 100;
	/** �������� ����������� �������� ��� ������������.
	 * 2 - �������� �����������
	 * 1 - ������������
	 * 0 - ���������� ������� � ������ �����������
	 */
	int changedirection = 0;
	/**
	 * �������: ������� ���������� ������
	 */
	public void onScannedRobot(ScannedRobotEvent e) {		

		// ���������� ���� �� ����� (�� ������ �����)
		double absBearing=e.getBearingRadians()+getHeadingRadians();
	
		// ���������� �� �����
		double distance = e.getDistance();

		// �������� ������
		double firePower =  Math.min(500/distance, 3);		
		
		// ������ ����� �� ������
		setTurnRadarRightRadians(Utils.normalRelativeAngle(
				absBearing - getRadarHeadingRadians())*2);

		// ���������� �����
		double ex = getX() + Math.sin(absBearing) * e.getDistance();
		double ey = getY() + Math.cos(absBearing) * e.getDistance();

 //==============================================================================

		// ��������� ��� ���������� � ���������.
		for (int i=0; i < waves.size(); i++)
		{
			WaveBullet currentWave = (WaveBullet)waves.get(i);
			if (currentWave.checkHit(ex, ey, getTime()))
			{
				waves.remove(currentWave);
				i--;
			}
		}
 
		// ���������� �������� ��������
		double power = Math.min(3, 500/distance);

		// ���� ��������� ����� ��������
		if (e.getVelocity() != 0)
		{
			// ���� ��������� ����� �������� ������ ������� �������
			if (Math.sin(e.getHeadingRadians()-absBearing)*e.getVelocity() < 0)
				direction = -1;
			else
				direction = 1;
		}
		// ������ GaussFactorIndexes
		int[] currentStats = stats;
		
		// ������� ����� ������, ������� ����� ����������� 
		WaveBullet newWave = new WaveBullet(getX(), getY(), absBearing, power,
                        direction, getTime(), currentStats);

		int bestindex = 15;	// �������� � �������� (����� gaussFactor = 0)
		// ������� ������ ������
		for (int i=0; i<31; i++)
			if (currentStats[bestindex] < currentStats[i])
				bestindex = i;
 
		// �������� ��������������:
		double guessfactor = (double)(bestindex - (stats.length - 1) / 2)
                        / ((stats.length - 1) / 2);
		double angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
        double gunAdjust = Utils.normalRelativeAngle(
                		absBearing - getGunHeadingRadians() + angleOffset);
        // ������������ �����
        setTurnGunRightRadians(gunAdjust);
        
        // ���� ������ ��� ����������, ��������� � ������ ���������� � ��������
        if (setFireBullet(power) != null)
                    waves.add(newWave);
                
//========================= �������� =====================================================

        //dist = Math.max(60, e.getDistance()/4);
        // ���� ��� ������ �������
		if (isStart == true)
		{
			// ������������ � ���������� �� 90 ��������
			turnRight(90 + e.getBearing());
			isStart = false;
		}
		else // ����������� �� 15 ��������
			setTurnRight(90+e.getBearing()-15);
		//setAhead(dist);

		// ���� ���� �������� �������
		if (enemyEnergy != e.getEnergy())
		{
			if (changedirection == 2)
			{
				// �������� ������ �� ���������� dist
				setAhead(dist);
				//dist = tempDist;
				System.out.println("Change");
				//setAhead(-dist);			
				//setTurnRight(-degr);
				dist = -dist;
				//degr = -degr;
				// ������������� ����� ��������� ���������� ������
				enemyEnergy = e.getEnergy();
				this.changedirection = 0;
			}
			else
			{
				if (changedirection == 0)
					setAhead(dist);
				//tempDist = dist;
				//dist = 0;
				enemyEnergy = e.getEnergy();
				System.out.println("No change");
				//setAhead(dist);
				this.changedirection++;
			}
		}
		// ���� ����� ��� �� �����������, �� ��������� ��������
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining())<10)
			setFire(firePower);
	}
	/**
	 * �������: � ��� ������.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		//dist = Math.random()*50+80;
		//dist = 200;
		//dist = -dist;
		//setAhead(dist);
		//dist = 100;		
	}
	/**
	 * �������: ��������� � �����
	 */
	public void onHitWall(HitWallEvent e) {
		// �������� ����������� �� ���������������
		dist = -dist;
		//this.changedirection = 0;
		// �������� �� ���������� dist
		setAhead(dist);
	}	
}