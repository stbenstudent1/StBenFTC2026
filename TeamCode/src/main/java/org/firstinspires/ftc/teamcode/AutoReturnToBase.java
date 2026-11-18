package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

// Move forward 1 second, wait 10 seconds, move backward 1 second
// THIS AUTO MODE HAS BEEN TESTED AND DOES ITS INTENDED FUNCTION PERFECTLY
@Autonomous(name = "Auto Return To Base", group = "Linear OpMode")
public class AutoReturnToBase extends LinearOpMode {
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private boolean auto_finished = false;

    public void setAllMotorPower(double value)
    {
        frontLeftDrive.setPower(value);
        backLeftDrive.setPower(value);
        frontRightDrive.setPower(value);
        backRightDrive.setPower(value);
    }

    @Override
    public void runOpMode()
    {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive"); // 1 on expansion
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive"); // 2 on expansion
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive"); // 0 on control hub
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive"); // 1 on control hub

        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses START)f
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive())
        {
            // Run auto until bot returns to base and wait.
            if (!auto_finished) {
                telemetry.addLine("Running Auto...");
                telemetry.update();

                // Move forward
                setAllMotorPower(0.5);
                sleep(1000);
                setAllMotorPower(0);

                // Wait 10 seconds before returning
                sleep(10000);

                // Stop motors
                setAllMotorPower(0);
                // Wait 10 seconds before returning
                sleep(10000);

                // Move backward
                setAllMotorPower(-0.5);
                sleep(1000);
                setAllMotorPower(0);

                auto_finished = true;
            } else {
                telemetry.addLine("Auto Finished");
                telemetry.update();
            }
        }
    }
}
