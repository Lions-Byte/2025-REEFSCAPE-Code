// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.auto.moveToTarget;
import frc.robot.auto.rightAlign;
import frc.robot.command.centerTarget;
import frc.robot.command.leftCoralAlign;
import frc.robot.command.rightCoralAlign;
import frc.robot.command.centerCoralAlign;
import frc.robot.subsystems.AlgaeSubsystem;
import frc.robot.subsystems.CoralSubsystem;
import frc.robot.subsystems.CoralSubsystem.Setpoint;
import frc.robot.subsystems.DriveSubsystem;
import java.util.List;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;



/*
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {

    CommandXboxController m_driverController =
    new CommandXboxController(OIConstants.kDriverControllerPort);
    //CommandXboxController m_operatorController = 
    //new CommandXboxController(OIConstants.kOperatorControllerPort); 
    GenericHID m_buttonPanel = new GenericHID(OIConstants.kOperatorControllerPort);
    JoystickButton m_homeButton = new JoystickButton(m_buttonPanel, 1);
    JoystickButton m_loadButton = new JoystickButton(m_buttonPanel, 2);
    JoystickButton m_shootButton = new JoystickButton(m_buttonPanel, 3);
    JoystickButton m_leftL2Button = new JoystickButton(m_buttonPanel, 4);
    JoystickButton m_rightL2Button = new JoystickButton(m_buttonPanel, 5);
    JoystickButton m_leftL3Button = new JoystickButton(m_buttonPanel, 6);
    JoystickButton m_rightL3Button = new JoystickButton(m_buttonPanel, 7);
    JoystickButton m_leftL4Button = new JoystickButton(m_buttonPanel, 8);
    JoystickButton m_rightL4Button = new JoystickButton(m_buttonPanel, 9);


    //configuring 


    // The robot's subsystems
  private final DriveSubsystem m_robotDrive = new DriveSubsystem();
  private final CoralSubsystem m_coralSubSystem = new CoralSubsystem();
  private final AlgaeSubsystem m_algaeSubsystem = new AlgaeSubsystem();

  //private final SendableChooser<Command> autoChooser;
  //private CenterApriltagSubsystem m_centerapriltagSubsystem = new CenterApriltagSubsystem(m_robotDrive, MathUtil.applyDeadband( m_driverController.getLeftY(), OIConstants.kDriveDeadband));

  // The driver's controller


  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();

    //autoChooser = AutoBuilder.buildAutoChooser();
    //SmartDashboard.putData("Auto Chooser", autoChooser);
    // Configure default commands
    m_robotDrive.setDefaultCommand(
        // The left stick controls translation of the robot.
        // Turning is controlled by the X axis of the right stick.
        new RunCommand(
            () ->
                m_robotDrive.drive(
                    -MathUtil.applyDeadband(
                        m_driverController.getLeftY(), OIConstants.kDriveDeadband),
                    -MathUtil.applyDeadband(
                        m_driverController.getLeftX(), OIConstants.kDriveDeadband),
                    -MathUtil.applyDeadband(
                        m_driverController.getRightX(), OIConstants.kDriveDeadband),
                    true),
            m_robotDrive));

    // Set the ball intake to in/out when not running based on internal state
    m_algaeSubsystem.setDefaultCommand(m_algaeSubsystem.idleCommand());
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling passing it to a
   * {@link JoystickButton}.
   */
  private void configureButtonBindings() {
    /* */
    // Left Stick Button -> Set swerve to X
    m_driverController.leftStick().whileTrue(m_robotDrive.setXCommand());

    m_driverController.rightStick().onTrue(m_coralSubSystem.removeAlgae());

    // Right Bumper -> Run tube intake
    m_driverController.rightBumper().whileTrue(m_coralSubSystem.runIntakeCommand());
    //m_operatorController.rightBumper().whileTrue(m_coralSubSystem.runIntakeCommand());
    //m_driverController.rightBumper().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kFeederStation));
    //.alongWith(m_coralSubSystem.setSetpointCommand(Setpoint.kFeederStation))
    // Left Bumper -> Run tube intake in reverse
    m_driverController.leftBumper().whileTrue(m_coralSubSystem.reverseIntakeCommand());
    

    // B Button -> Elevator/Arm to human player position, set ball intake to stow
    // when idle
    //m_operatorController.b().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel3));
        //.onTrue(
            //m_coralSubSystem
                //.setSetpointCommand(Setpoint.kFeederStation)
                //.alongWith(m_algaeSubsystem.stowCommand()));

    // A Button -> Elevator/Arm to level 2 position
    //m_operatorController.a().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel2));
    
    // X Button -> Elevator/Arm to level 3 position
    
   // m_operatorController
    //.leftTrigger(OIConstants.kTriggerButtonThreshold)
    //.onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kFeederStation));


    
    //m_operatorController
    //.rightTrigger(OIConstants.kTriggerButtonThreshold)
    //.onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel1));
    // Y Button -> Elevator/Arm to level 4 position
    //m_operatorController.y().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel4));

    // Right Trigger -> Run ball intake, set to leave out when idle
    m_driverController
        .rightTrigger(OIConstants.kTriggerButtonThreshold)
        .whileTrue(m_algaeSubsystem.runIntakeCommand());

    // Left Trigger -> Run ball intake in reverse, set to stow when idle
    m_driverController
        .leftTrigger(OIConstants.kTriggerButtonThreshold)
        .whileTrue(m_algaeSubsystem.reverseIntakeCommand());

    // Start Button -> Zero swerve heading
    m_driverController.start().onTrue(m_robotDrive.zeroHeadingCommand());


    //m_driverController.b().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kFeederStation));
    //m_driverController.a().whileTrue(new centerTarget(m_robotDrive, m_driverController));
    //m_driverController.a().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel2));
    //m_driverController.x().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel3));


    m_driverController.b().whileTrue(new rightCoralAlign(m_robotDrive, m_driverController));
    m_driverController.a().whileTrue(new centerCoralAlign(m_robotDrive, m_driverController));
    m_driverController.x().whileTrue(new leftCoralAlign(m_robotDrive, m_driverController));

    m_driverController.y().onTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel4));
    /*m_driverController.b().OnTrue(m_coralSubsystem.test)
     * 
     * 
     * 
     * 
     * 
     */
    m_homeButton.whileTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kHome));
    m_loadButton.whileTrue(m_coralSubSystem.feederStation().alongWith(new centerTarget(m_robotDrive, m_driverController)));
    m_shootButton.whileTrue(m_coralSubSystem.reverseIntakeCommand());
    m_leftL2Button.whileTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel2).alongWith(new leftCoralAlign(m_robotDrive, m_driverController)));
    m_rightL2Button.whileTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel2).alongWith(new rightCoralAlign(m_robotDrive, m_driverController)));
    m_leftL3Button.whileTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel3).alongWith(new leftCoralAlign(m_robotDrive, m_driverController)));
    m_rightL3Button.whileTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel3).alongWith(new rightCoralAlign(m_robotDrive, m_driverController)));
    m_leftL4Button.whileTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel4).alongWith(new leftCoralAlign(m_robotDrive, m_driverController)));
    m_rightL4Button.whileTrue(m_coralSubSystem.setSetpointCommand(Setpoint.kLevel4).alongWith(new rightCoralAlign(m_robotDrive, m_driverController)));


  }

  public double getSimulationTotalCurrentDraw() {
    // for each subsystem with simulation
    return m_coralSubSystem.getSimulationCurrentDraw()
        + m_algaeSubsystem.getSimulationCurrentDraw();
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return new moveToTarget(m_robotDrive).andThen(new rightAlign(m_robotDrive)).andThen(m_coralSubSystem.reverseIntakeCommand());
   /*  // Create config for trajectory
    TrajectoryConfig config =
        new TrajectoryConfig(
                AutoConstants.kMaxSpeedMetersPerSecond,
                AutoConstants.kMaxAccelerationMetersPerSecondSquared)
            // Add kinematics to ensure max speed is actually obeyed
            .setKinematics(DriveConstants.kDriveKinematics);

    // An example trajectory to follow. All units in meters.
    Trajectory exampleTrajectory =
        TrajectoryGenerator.generateTrajectory(
            // Start at the origin facing the +X direction
            new Pose2d(0, 0, new Rotation2d(0)),
            // Pass through these two interior waypoints, making an 's' curve path
            List.of(new Translation2d(.5, 0), new Translation2d(1, 0)),
            // End 3 meters straight ahead of where we started, facing forward
            new Pose2d(2, 0, new Rotation2d(0)),
            config);

    var thetaController =
        new ProfiledPIDController(
            AutoConstants.kPThetaController, 0, 0, AutoConstants.kThetaControllerConstraints);
    thetaController.enableContinuousInput(-Math.PI, Math.PI);

    SwerveControllerCommand swerveControllerCommand =
        new SwerveControllerCommand(
            exampleTrajectory,
            m_robotDrive::getPose, // Functional interface to feed supplier
            DriveConstants.kDriveKinematics,

            // Position controllers
            new PIDController(AutoConstants.kPXController, 0, 0),
            new PIDController(AutoConstants.kPYController, 0, 0),
            thetaController,
            m_robotDrive::setModuleStates,
            m_robotDrive);

    // Reset odometry to the starting pose of the trajectory.
    m_robotDrive.resetOdometry(exampleTrajectory.getInitialPose());

    //run(new leftCoralAlign(m_robotDrive, m_driverController));

    // Run path following command, then stop at the end.
    return swerveControllerCommand.andThen(() -> m_robotDrive.drive(0, 0, 0, false));

    */
  }
}
