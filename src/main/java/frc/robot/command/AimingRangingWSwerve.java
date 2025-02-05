package frc.robot.command;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.Constants.LimelightAutoConstants;
import frc.robot.LimelightHelpers;
import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.math.MathUtil;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;





public class AimingRangingWSwerve extends Command{
    private final DriveSubsystem m_drive;
    private final CommandXboxController m_driverController;


    double limelight_range_proportional()
    {    
    double kP = .1;
    double targetingForwardSpeed = LimelightHelpers.getTY("limelight") * kP;
    targetingForwardSpeed *= LimelightAutoConstants.kMaxSpeedMetersPerSecond;
    targetingForwardSpeed *= -1.0;
    return targetingForwardSpeed;
    }

  double limelight_aim_proportional()
  {    
    // kP (constant of proportionality)
    // this is a hand-tuned number that determines the aggressiveness of our proportional control loop
    // if it is too high, the robot will oscillate around.
    // if it is too low, the robot will never reach its target
    // if the robot never turns in the correct direction, kP should be inverted.
    double kP = .035;

    // tx ranges from (-hfov/2) to (hfov/2) in degrees. If your target is on the rightmost edge of 
    // your limelight 3 feed, tx should return roughly 31 degrees.
    double targetingAngularVelocity = LimelightHelpers.getTX("limelight") * kP;

    // convert to radians per second for our drive method
    targetingAngularVelocity *= LimelightAutoConstants.kMaxAngularSpeed;

    //invert since tx is positive when the target is to the right of the crosshair
    targetingAngularVelocity *= -1.0;

    return targetingAngularVelocity;
  }


public AimingRangingWSwerve(DriveSubsystem drive, CommandXboxController controller)
{
    m_drive = drive;
    m_driverController = controller;
    addRequirements(m_drive);
}
@Override
public void initialize()
{
    m_drive.drive(0, 0, 0, false);
}
@Override
public void execute() 
{
    m_drive.drive(limelight_range_proportional(), 0, limelight_aim_proportional(), false);
}
//MathUtil.applyDeadband(m_driverController.getLeftY()
@Override
public void end(boolean interrupted) 
{
    m_drive.drive(0, 0, 0, false);
}




}
