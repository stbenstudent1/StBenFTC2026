package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

// THIS IS JUST SHOOTING & INTAKE

@TeleOp(name="DriveIntake Plus Shoot", group="Linear OpMode")
public class Shoot extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors and intake.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor Launcher = null;
    private DcMotor Intake;

    // Double 0-1 determining how fast the intake will spin
    private double IntakePowerState = 1;
    // Is Intake active, starts false
    private boolean IntakeActive = false;

    private boolean LauncherActive = false;

    @Override
    public void runOpMode() {

        Intake = hardwareMap.get(DcMotor.class, "intake_main"); // 0 on expansion

        Launcher = hardwareMap.get(DcMotor.class, "launcher");

        Launcher.setDirection(DcMotor.Direction.FORWARD);

        Intake.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)f
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            /* INTAKE SECTION */

            // Triangle toggles the intake
            if(gamepad1.triangleWasPressed() && gamepad1.triangleWasReleased())
            {
                IntakeActive = !IntakeActive;
            }

            // Right Trigger press = increase motor power by 0.2, max 1
            if(gamepad1.rightTriggerWasPressed() && gamepad1.rightTriggerWasReleased())
            {
                if (IntakePowerState < 0.9)
                {
                    IntakePowerState += 0.2;
                }
            }
            // Right Bumper press = decrease motor power by 0.2, min 0
            if(gamepad1.rightBumperWasPressed() && gamepad1.rightBumperWasReleased())
            {
                if (IntakePowerState > 0.1)
                {
                    IntakePowerState -= 0.2;
                }
            }

            // LAUNCHER SECTION
                LauncherActive = gamepad1.circle;

            if(LauncherActive)
            {
                Launcher.setPower(1);
            } else {
                Launcher.setPower(0);
            }


            // Intake power 0 if not active, else PowerState
            if (!IntakeActive) {
                Intake.setPower(0);
            } else {
                Intake.setPower(IntakePowerState);
            }

            telemetry.addData("IntakeActive", IntakeActive);
            telemetry.addData("IntakeTargetPower", IntakePowerState);
            telemetry.addData("IntakePower", Intake.getPower());

            telemetry.addData("LauncherActive", LauncherActive);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }}
