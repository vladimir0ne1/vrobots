package vrobots;

import java.awt.geom.Point2D;

import robocode.util.Utils;

class WaveBullet
{
	private double startX, startY, startBearing, power;
	private long   fireTime;
	private int    direction;
	private int[]  returnSegment;
 
	/**
	 * 
	 * @param x	�������������� ���������� (������ ���������� �������) (X)
	 * @param y �������������� ���������� (������ ���������� �������) (Y)
	 * @param bearing ����������� �� ����� (����������)
	 * @param power �������� ��������
	 * @param direction ����������� �������� ����� (�� ������� (1), ������ (-1))
	 * @param time ����� ��������
	 * @param segment ������ �������� (���������� ������ ������������� ���������� GaussFactor)
	 */
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
	/** ���������� �������� ����
	 * 
	 * @return 20 - power * 3;
	 */
	public double getBulletSpeed()
	{
		/** power - �������� ������� (1..3)
		 * 20 - ������������ ����� ��������
		 */
		return 20 - power * 3;
	}
 
	/** ���������� ������������ ���� ���������� ����.
	 * 
	 * @return Math.asin(8 / getBulletSpeed());
	 */
	public double maxEscapeAngle()
	{
		/** ������������ ������������ ���� ���������� ����.
		 * �����������. �������� �� ����� � � ����� �.
		 *	b = D (���������� ����� ����� ������� � ������� �����)
		 * c = Vb * t (Vb - �������� ����)
		 * a = Vr * t (Vr �������� ���������� ������)
		 * a/sin(A) = c/sin(C) A = asin(Vr/Vb * sin(C))
		 * sin(c) � ������ ������ ����� 1
		 * ������������ �������� ������ ����� 8
		 * ��������: A = asin(8/vb)
		 * 
		 */
		return Math.asin(8 / getBulletSpeed());
	}
	
	/** ���������, ����� �� �����-���� �� ����� �������� � ����
	 * 
	 * @param enemyX ���������� ����� (X)
	 * @param enemyY ���������� ����� (Y)
	 * @param currentTime ������� �����
	 * @return false, ���� ����, true - �����
	 */
	public boolean checkHit(double enemyX, double enemyY, long currentTime)
	{
		// ���� ����������, ���������� �����, ������, ��� ���������� �� ����� - ������
		if (Point2D.distance(startX, startY, enemyX, enemyY) <= 
				(currentTime - fireTime) * getBulletSpeed())
		{
			// ����, �� ������� ��������� ���� ������������ ���������� ���������
			double desiredDirection = Math.atan2(enemyX - startX, enemyY - startY);
			// �������� ������������ ��������������� ���� � ����-��������
			double angleOffset = Utils.normalRelativeAngle(desiredDirection - startBearing);
			// ������� GaussFactor
			double guessFactor =
				Math.max(-1, Math.min(1, angleOffset / maxEscapeAngle())) * direction;
			// ������� ������
			int index = (int) Math.round((returnSegment.length - 1) /2 * (guessFactor + 1));
			// ����������� �������� �� ����������� �������. ������������ �������� ������������� ������� GaussFactor
			returnSegment[index]++;
			return true;
		}
		return false;
	}
}
