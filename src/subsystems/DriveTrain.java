package subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
	
	//Not PWM WHYYYYYYYYYYYYYYYYYYYYYYYYY!!!!!!!!
	public WPI_TalonSRX fl;
	public WPI_TalonSRX fr;
	public WPI_TalonSRX bl;
	public WPI_TalonSRX br;
	
	public AHRS navx;
	
	private long startTime;
	private long elapsedTime;
	
	RobotDrive roboDrive;
	
	//AnalogGyro gyro;
	
	JoystickManager jm;
	
	public void init() {
		//Initialize and set to CAN IDs.
		//We can remap the IDs from within the web browser at roboRIO-5113.local

		fl = new WPI_TalonSRX(14);//All of these needs to be changed
		fr = new WPI_TalonSRX(1);
		bl = new WPI_TalonSRX(15);
		br = new WPI_TalonSRX(0);
		
		navx = new AHRS(SerialPort.Port.kUSB);
		navx.reset();
		navx.resetDisplacement();
	 	//navx.setAngleAdjustment(90.0);
		
		fl.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		fr.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		bl.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		br.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		/*
		fl.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        fl.reverseSensor(false);
        fl.setProfile(0);
        fl.setF(0.1097);//Found it on the internet - Andy
        fl.setP(0.22);//Found it on the internet - Andy
        fl.setI(0); 
        fl.setD(0);
        
        fr.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        fr.reverseSensor(false);
        fr.setProfile(0);
        fr.setF(0.1097);//Found it on the internet - Andy
        fr.setP(0.22);//Found it on the internet - Andy
        fr.setI(0); 
        fr.setD(0);
        
        bl.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        bl.reverseSensor(false);
        bl.setProfile(0);
        bl.setF(0.1097);//Found it on the internet - Andy
        bl.setP(0.22);//Found it on the internet - Andy
        bl.setI(0); 
        bl.setD(0);
        
        br.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        br.reverseSensor(false);
        br.setProfile(0);
        br.setF(0.1097);//Found it on the internet - Andy
        br.setP(0.22);//Found it on the internet - Andy
        br.setI(0); 
        br.setD(0);
		*/
		startTime = System.currentTimeMillis();
		
		//roboDrive = new RobotDrive(bl, fl, br, fr);
		
		roboDrive = new RobotDrive(fl, bl, fr, br);
		
		/*gyro = new AnalogGyro(0);
		gyro.initGyro();
		System.out.println("Gyro is now initiated\t" + gyro.getAngle());
		
		gyro.calibrate();*/
	}
	
	public void update(JoystickManager jm) {
		elapsedTime = System.currentTimeMillis() - startTime;
		SmartDashboard.putNumber("Gyro Angle", navx.getAngle());
		//System.out.println("gyro angle" + navx.getAngle());
		/*System.out.println("X: " + navx.getVelocityX() + "\nY: " + navx.getVelocityY() + "\nZ: " + navx.getVelocityZ());
		System.out.println("Angle: " + navx.getAngle());*/
	}

	//Controls the drive train
	public void mecanumDrive(double magnitude, double angle, double rotation)
	{			
		//Makes sure that magnitude fits into the range [0, 0.99] as expected. Hardware errors can otherwise cause small movement changes.
		magnitude = Math.min(Math.abs(magnitude), 0.99);
					
		//As the mecanum drive is X-Shaped, we must adjust to be at 45* angles.
		double newDirection = angle + (double) (Math.PI / 4);
		double cosine = (double) Math.cos(newDirection);
		double sine = (double) Math.sin(newDirection);
		
		double frontRightSpeed = (sine * magnitude - rotation);
		double frontLeftSpeed = -(cosine * magnitude - rotation);
		double backRightSpeed = -(cosine * magnitude + rotation);
		double backLeftSpeed = (sine * magnitude + rotation);		
		
		if(frontLeftSpeed >= 1)
			frontLeftSpeed = 0.99;
		else if(frontLeftSpeed <= -1)
			frontLeftSpeed = -0.99;
		
		if(frontRightSpeed >= 1)
			frontRightSpeed = 0.99;
		else if(frontRightSpeed <= -1)
			frontRightSpeed = -0.99;
		
		if(backLeftSpeed >= 1)
			backLeftSpeed = 0.99;
		else if(backLeftSpeed <= -1)
			backLeftSpeed = -0.99;
		
		if(backRightSpeed >= 1)
			backRightSpeed = 0.99;
		else if(backRightSpeed <= -1)
			backRightSpeed = -0.99;
		
		fl.set(frontLeftSpeed);
		fr.set(frontRightSpeed);
		bl.set(backLeftSpeed);
		br.set(backRightSpeed);
	}
	
	public void mecanumDrive2(double mag, double angle, double rotation, double navX)
	{
		double x = mag * Math.cos(angle * Math.PI / 180);
		double y = mag * Math.sin(angle * Math.PI / 180);
		
		roboDrive.mecanumDrive_Cartesian(x, y, rotation, navX);
	}
	
	public void fod(double x, double y, double rotation, double navX) {
		//roboDrive.mecanumDrive_Cartesian(x, rotation, y, navX);
		roboDrive.mecanumDrive_Cartesian(x, y, rotation, navX);
	}
	
	public void tankDrive(double leftValue, double rightValue)
	{	
		double leftPower = leftValue;
		double rightPower = rightValue;
		
		fl.set(leftPower);
		bl.set(leftPower);
		fr.set(rightPower);
		br.set(rightPower);
	}
	
	 public double[] rotateV(double x, double y, double angle) {
		    double cosA = Math.cos(angle * (Math.PI / 180.0));
		    double sinA = Math.sin(angle * (Math.PI / 180.0));
		    double[] out = new double[2];
		    out[0] = x * cosA - y * sinA;
		    out[1] = x * sinA + y * cosA;
		    return out;
		  }
	
	/*public double getGyroAngle() {
		return gyro.getAngle();
	}*/
}