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

	/** Массив данных о выстрелах.
	 * Откуда стреляли
	 * Направление движения
	 * Исходные координаты врага 
	 * Время выстрела и т.п.
	 */
	List<WaveBullet> waves = new ArrayList<WaveBullet>();
	/**Массив уникальных GaussFactor's.
	 * 31 - число уникальных GaussFactor's
	 * Число элементов - нечетное
	 * GuessFactor = 0 в середине
	 */
	static int[] stats = new int[31]; 
	  // Число элементов - нечетное
	  // GuessFactor 0 в середине
	/** Направление движения врага.
	 * (-1) - против часовой стрелки.
	 * (1) - по часовой стрелке.
	 */
	int direction = 1;
	
	/** Только начался раунд.
	 * true - да
	 * false - нет
	 */
	private boolean isStart = true;
	/** Точка входа.
	 * 
	 */
	public void run() {

		setColors(Color.gray,Color.black,Color.white); // Корпус, пушка, радар
		// Делаем все части независимыми друг от друга (вращаются независимо)
		setAdjustRadarForGunTurn(true); 
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		// Устанавливаем скорость движения робота
		setMaxVelocity(8);

		while(true) {			
			turnRadarRightRadians(Double.POSITIVE_INFINITY);
		}
	}
	/** Расстояние, на которое нужно отъехать.
	 * 
	 */
	double dist = 100;
	/** Угол, на который нужно довернуться.
	 * 
	 */
	double degr = 360;
	/** Изначально энергия противника равна 100 (будет изменяться при выстреле)
	 * 
	 */
	double enemyEnergy = 100;
	/** Изменить направление движения или остановиться.
	 * 2 - изменить направление
	 * 1 - остановиться
	 * 0 - продолжить движени в данном направлении
	 */
	int changedirection = 0;
	/**
	 * Событие: засекли вражеского робота
	 */
	public void onScannedRobot(ScannedRobotEvent e) {		

		// Абсолютный угол на врага (до центра врага)
		double absBearing=e.getBearingRadians()+getHeadingRadians();
	
		// Расстояние до врага
		double distance = e.getDistance();

		// Мощность заряда
		double firePower =  Math.min(500/distance, 3);		
		
		// Захват врага на радаре
		setTurnRadarRightRadians(Utils.normalRelativeAngle(
				absBearing - getRadarHeadingRadians())*2);

		// Координаты врага
		double ex = getX() + Math.sin(absBearing) * e.getDistance();
		double ey = getY() + Math.cos(absBearing) * e.getDistance();

 //==============================================================================

		// проверяем всю информацию о выстрелах.
		for (int i=0; i < waves.size(); i++)
		{
			WaveBullet currentWave = (WaveBullet)waves.get(i);
			if (currentWave.checkHit(ex, ey, getTime()))
			{
				waves.remove(currentWave);
				i--;
			}
		}
 
		// Вычисление мощности выстрела
		double power = Math.min(3, 500/distance);

		// Если вражеский робот движется
		if (e.getVelocity() != 0)
		{
			// Если вражеский робот движется против часовой стрелки
			if (Math.sin(e.getHeadingRadians()-absBearing)*e.getVelocity() < 0)
				direction = -1;
			else
				direction = 1;
		}
		// Массив GaussFactorIndexes
		int[] currentStats = stats;
		
		// Создаем новый снаряд, который будем отслеживать 
		WaveBullet newWave = new WaveBullet(getX(), getY(), absBearing, power,
                        direction, getTime(), currentStats);

		int bestindex = 15;	// Начинаем с середины (здесь gaussFactor = 0)
		// Находим лучший индекс
		for (int i=0; i<31; i++)
			if (currentStats[bestindex] < currentStats[i])
				bestindex = i;
 
		// Обратные преобразования:
		double guessfactor = (double)(bestindex - (stats.length - 1) / 2)
                        / ((stats.length - 1) / 2);
		double angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
        double gunAdjust = Utils.normalRelativeAngle(
                		absBearing - getGunHeadingRadians() + angleOffset);
        // Корректируем пушку
        setTurnGunRightRadians(gunAdjust);
        
        // Если только что выстрелили, добавляем в массив информацию о выстреле
        if (setFireBullet(power) != null)
                    waves.add(newWave);
                
//========================= Движение =====================================================

        //dist = Math.max(60, e.getDistance()/4);
        // Если бой только начался
		if (isStart == true)
		{
			// Развернуться к противнику на 90 градусов
			turnRight(90 + e.getBearing());
			isStart = false;
		}
		else // Довернуться на 15 градусов
			setTurnRight(90+e.getBearing()-15);
		//setAhead(dist);

		// Если враг произвел выстрел
		if (enemyEnergy != e.getEnergy())
		{
			if (changedirection == 2)
			{
				// Проехать вперед на расстояние dist
				setAhead(dist);
				//dist = tempDist;
				System.out.println("Change");
				//setAhead(-dist);			
				//setTurnRight(-degr);
				dist = -dist;
				//degr = -degr;
				// Зафиксировать новое состояние вражеского робота
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
		// Если пушка еще не довернулась, то задержать стрельбу
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining())<10)
			setFire(firePower);
	}
	/**
	 * Событие: В нас попали.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		//dist = Math.random()*50+80;
		//dist = 200;
		//dist = -dist;
		//setAhead(dist);
		//dist = 100;		
	}
	/**
	 * Событие: врезались в стену
	 */
	public void onHitWall(HitWallEvent e) {
		// Поменять направление на противоположное
		dist = -dist;
		//this.changedirection = 0;
		// Проехать на расстояние dist
		setAhead(dist);
	}	
}