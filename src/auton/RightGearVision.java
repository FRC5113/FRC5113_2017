package auton;

import subsystems.DriveTrain;
import subsystems.NTHandler;

public class RightGearVision extends GearFrame
{
	private final double MODE_1_SPEED = 0.2;
	private final double MODE_2_SPEED = 0.2;
	private final double MODE_3_SPEED = 0.1;
	
	private int caseSelector = 1;
	
	public void init()
	{
		caseSelector = 1;
	}
	
	public void update(DriveTrain dt, NTHandler nettab)
	{
		nettab.update();
		int distance = nettab.getDistance();
		int mode = nettab.getMode();
		int zone = nettab.getZone();
		double angle = dt.navx.getAngle();
		nettab.print();
		
		switch(caseSelector)
		{
		case 1:
			System.out.println("Driving forward");
			dt.mecanumDrive2(MODE_1_SPEED, 90, 0, angle);
			
			if(zone == 1 || zone == 2 || zone == 3)
				caseSelector++;
			break;
		case 2:
			System.out.println("Driving to peg - coarse");
			if(zone == 3)
				dt.mecanumDrive2(MODE_2_SPEED, 40, .3, angle);
			else if(zone == 2)
				dt.mecanumDrive2(MODE_2_SPEED, 0, 0, angle);
			else if(zone == 1)
				dt.mecanumDrive2(MODE_2_SPEED, 320, -.3, angle);
			
			if(mode == 2)
				caseSelector++;
			
			break;
		case 3:
			System.out.println("Driving to peg - fine");
			if(zone == 3)
				dt.mecanumDrive2(MODE_3_SPEED, 90, 0, angle);
			else if(zone == 2)
				dt.mecanumDrive2(MODE_3_SPEED, 0, 0, angle);
			else if(zone == 1)
				dt.mecanumDrive2(MODE_3_SPEED, 270, 0, angle);
			
			if(distance < 16)
				caseSelector++;
			
			break;
		case 4:
			dt.mecanumDrive2(0, 0, 0, angle);
			System.out.println("Done!");
			break;
		}
	}
}
