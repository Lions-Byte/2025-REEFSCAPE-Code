package frc.robot.auto;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.LimelightAutoConstants;
import frc.robot.LimelightHelpers;
import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class moveToTarget extends Command {
    private final DriveSubsystem m_drive;

    public moveToTarget(DriveSubsystem drive) {
        m_drive = drive;
        addRequirements(m_drive);
    }

    @Override
    public void initialize() {
        m_drive.drive(0, 0, 0, false);
        LimelightHelpers.setPipelineIndex("limelight-main", 1);
    }

    @Override
    public void execute() {
        double forwardSpeed = 0.1;
        double angularVelocity = limelight_aim_proportional();

        m_drive.drive(forwardSpeed, angularVelocity, 0, false);
        System.out.println(LimelightHelpers.getTX("limelight-main"));
        System.out.println(LimelightHelpers.getCurrentPipelineIndex("limelight-main"));
        SmartDashboard.putBoolean("isFlush", (LimelightHelpers.getTA("limelight-main") > 0.3));
        SmartDashboard.putNumber("baseflush", LimelightHelpers.getTA("limelight-main"));
    }

    @Override
    public boolean isFinished() {
        double ta = LimelightHelpers.getTA("limelight-main");
        double tx = LimelightHelpers.getTX("limelight-main");
        return ta > 10.0 && Math.abs(tx) <= 2.0;
    }

    @Override
    public void end(boolean interrupted) {
        m_drive.drive(0, 0, 0, false);
    }

    double limelight_aim_proportional() {
        double kP = 0.035;
        double targetingAngularVelocity = LimelightHelpers.getTX("limelight-main") * kP;
        targetingAngularVelocity *= LimelightAutoConstants.kMaxAngularSpeed;
        targetingAngularVelocity *= -1.0;
        return targetingAngularVelocity;
    }
}