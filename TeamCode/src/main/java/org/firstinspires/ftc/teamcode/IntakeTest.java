package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="IntakeTest", group="Linear OpMode")
public class IntakeTest extends LinearOpMode {

    // Declare OpMode members for motor.
    private final ElapsedTime runtime = new ElapsedTime();
    private volatile DcMotor Intake = null;

    // Double 1-5 determining how fast the intake will spin
    private double IntakePowerState = 0;
    private boolean IntakeActive = false;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables
        Intake = hardwareMap.get(DcMotor.class, "intake_main");
        Intake.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Y toggles the intake
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

            // Intake power 0 if not active, else PowerState
            if (!IntakeActive) {
                Intake.setPower(0);
            } else {
                Intake.setPower(IntakePowerState);
            }

            telemetry.addData("IntakeActive", IntakeActive);
            telemetry.addData("TargetPower", IntakePowerState);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Power", Intake.getPower());
            telemetry.update();
        }
    }}
