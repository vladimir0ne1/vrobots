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
	 * @param x	Первоначальные координаты (откуда произведен выстрел) (X)
	 * @param y Первоначальные координаты (откуда произведен выстрел) (Y)
	 * @param bearing Направление на врага (абсолютное)
	 * @param power Мощность выстрела
	 * @param direction Направление движения врага (по часовой (1), против (-1))
	 * @param time Время выстрела
	 * @param segment Массив индексов (наибольший индекс соответствует наилучшему GaussFactor)
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
	/** Вычисление скорости пули
	 * 
	 * @return 20 - power * 3;
	 */
	public double getBulletSpeed()
	{
		/** power - мощность снаряда (1..3)
		 * 20 - максимальный порог скорости
		 */
		return 20 - power * 3;
	}
 
	/** Вычисление максимальный угла отклонения цели.
	 * 
	 * @return Math.asin(8 / getBulletSpeed());
	 */
	public double maxEscapeAngle()
	{
		/** Рассчитывает максимальный угол отклонения цели.
		 * Треугольник. Стреляем из точки А в точку С.
		 *	b = D (расстояние между нашим роботом и роботом врага)
		 * c = Vb * t (Vb - скорость пули)
		 * a = Vr * t (Vr скорость вражеского робота)
		 * a/sin(A) = c/sin(C) A = asin(Vr/Vb * sin(C))
		 * sin(c) в худшем случае равен 1
		 * Максимальная скорость робота равна 8
		 * Получаем: A = asin(8/vb)
		 * 
		 */
		return Math.asin(8 / getBulletSpeed());
	}
	
	/** Проверяем, попал ли какой-либо из наших снарядов в цель
	 * 
	 * @param enemyX Координаты врага (X)
	 * @param enemyY Координаты врага (Y)
	 * @param currentTime Текущее время
	 * @return false, если мимо, true - иначе
	 */
	public boolean checkHit(double enemyX, double enemyY, long currentTime)
	{
		// Если расстояние, пройденное пулей, больше, чем расстояние до врага - промах
		if (Point2D.distance(startX, startY, enemyX, enemyY) <= 
				(currentTime - fireTime) * getBulletSpeed())
		{
			// Угол, на который сместился враг относительно начального положения
			double desiredDirection = Math.atan2(enemyX - startX, enemyY - startY);
			// Смещение относительно первоначального угла и угла-смещения
			double angleOffset = Utils.normalRelativeAngle(desiredDirection - startBearing);
			// Считаем GaussFactor
			double guessFactor =
				Math.max(-1, Math.min(1, angleOffset / maxEscapeAngle())) * direction;
			// Считаем индекс
			int index = (int) Math.round((returnSegment.length - 1) /2 * (guessFactor + 1));
			// Увеличиваем значение по полученному индексу. Максимальное значение соответствует лучшему GaussFactor
			returnSegment[index]++;
			return true;
		}
		return false;
	}
}
