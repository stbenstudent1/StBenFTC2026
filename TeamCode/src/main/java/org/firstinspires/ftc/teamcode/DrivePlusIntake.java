package org.firstinspires.ftc.teamcode;

import android.icu.number.Precision;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

// This is the intake class and functionaity combined with the driving function

@TeleOp(name="Drive Plus Intake", group="Linear OpMode")
public class DrivePlusIntake extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors and intake.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor Intake;

    // Double 0-1 determining how fast the intake will spin
    private double IntakePowerState = 1;
    // Is Intake active, starts false
    private boolean IntakeActive = false;
    private boolean PrecisionMode = false;

    @Override
    public void runOpMode() {

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive"); // 1 on expansion
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive"); // 2 on expansion
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive"); // 0 on control hub
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive"); // 1 on control hub
        Intake = hardwareMap.get(DcMotor.class, "intake_main"); // 0 on expansion

        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        Intake.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)f
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            /* THIS PART WAS FROM OWEN'S DRIVE CODE, PLEASE TEST */
            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double y   = -gamepad1.left_stick_y;
            double x =  gamepad1.left_stick_x;
            double rx     =  gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power
            double frontLeftPower  = (y + x + rx);
            double frontRightPower   = (y - x + rx);
            double backLeftPower = (y - x - rx);
            double backRightPower  = (y + x - rx);
            /*           */

            // Normalize the values so no wheel power exceeds 100%
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            /*
            frontLeftPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            backLeftPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            frontRightPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            backRightPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            */

            /* Precision mode section */

            // Square toggles precision mode
            if(gamepad1.squareWasPressed() && gamepad1.squareWasReleased())
            {
                PrecisionMode = !PrecisionMode;
            }

            /* */

            // If precision mode, slow everything to 25% output
            if (PrecisionMode)
            {
                frontLeftPower *= 0.25;
                frontRightPower *= 0.25;
                backLeftPower *= 0.25;
                backRightPower *= 0.25;
            }

            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

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

            // Intake power 0 if not active, else PowerState
            if (!IntakeActive) {
                Intake.setPower(0);
            } else {
                Intake.setPower(IntakePowerState);
            }

            telemetry.addData("PrecisionMode", PrecisionMode);
            telemetry.addData("IntakeActive", IntakeActive);
            telemetry.addData("IntakeTargetPower", IntakePowerState);
            telemetry.addData("IntakePower", Intake.getPower());

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.update();
        }
    }}
