package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.util.ElapsedTime;

// This is the intake class and functionaity combined with the driving function
// THIS HAS SHOOTING

@TeleOp(name="Full Bot", group="Linear OpMode")
public class DrivePlusIntakeShoot extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors and intake.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor Launcher = null;
    private DcMotor Intake;

    // Actual intake power that changes gradually
    private double currentIntakePower = 0;

    // How quickly the intake changes power
    private final double intakeRampRate = 0.05;

    // Double 0-1 determining how fast the intake will spin
    private double IntakePowerState = 1;
    // Is Intake active, starts false
    private boolean IntakeActive = false;
    private boolean PrecisionMode = false;
    private boolean ReverseIntake = false;

    private boolean LauncherActive = false;

    private boolean TrianglePressed = false;
    private boolean SquarePressed = false;

    @Override
    public void runOpMode() {

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive"); // 1 on expansion
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive"); // 2 on expansion
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive"); // 0 on control hub
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive"); // 1 on control hub
        Intake = hardwareMap.get(DcMotor.class, "intake_main"); // 0 on expansion

        Launcher = hardwareMap.get(DcMotor.class, "launcher"); // 3 on expansion

        Launcher.setDirection(DcMotor.Direction.FORWARD);

        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
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
            double y  = -gamepad1.left_stick_x;
            double x  = gamepad1.right_stick_x;
            double rx = gamepad1.right_stick_y;

            // Mecanum wheel powers
            double frontLeftPower  = y + x + rx;
            double frontRightPower = y - x - rx;
            double backLeftPower   = y - x + rx;
            double backRightPower  = y + x - rx;

            // Normalize wheel powers
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
            if(gamepad1.squareWasPressed() && (gamepad1.squareWasReleased() || !SquarePressed))
            {
                PrecisionMode = !PrecisionMode;
                SquarePressed = true;
            }

            /* */

            // If precision mode, slow everything to 25% output
            if (PrecisionMode) {
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
            if(gamepad1.triangleWasPressed() && (gamepad1.triangleWasReleased() || !TrianglePressed))
            {
                IntakeActive = !IntakeActive;
                TrianglePressed = true;
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

            // REVERSE INTAKE IF RIGHT BUTTON IS PRESSED
            ReverseIntake = !gamepad1.dpad_right;

            // LAUNCHER SECTION
                LauncherActive = gamepad1.circle;

            if(LauncherActive)
            {
                Launcher.setPower(1);
            } else {
                Launcher.setPower(0);
            }


            // Intake target power
            double targetIntakePower = 0;

            if (IntakeActive) {
                targetIntakePower = IntakePowerState;

                if (gamepad1.dpad_down) {
                    targetIntakePower = -IntakePowerState;
                }
            }

            // Gradually move current power toward target power.
            if (currentIntakePower < targetIntakePower) {
                currentIntakePower += intakeRampRate;

                if (currentIntakePower > targetIntakePower) {
                    currentIntakePower = targetIntakePower;
                }
            }
            else if (currentIntakePower > targetIntakePower) {
                currentIntakePower -= intakeRampRate;

                if (currentIntakePower < targetIntakePower) {
                    currentIntakePower = targetIntakePower;
                }
            }

            // Apply the gradually changing power
            Intake.setPower(currentIntakePower);

            telemetry.addData("PrecisionMode", PrecisionMode);
            telemetry.addData("IntakeActive", IntakeActive);
            telemetry.addData("IntakeTargetPower", currentIntakePower);
            telemetry.addData("IntakePower", Intake.getPower());

            telemetry.addData("LauncherActive", LauncherActive);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.update();
        }
    }}
