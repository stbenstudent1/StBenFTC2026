package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

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
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses START)f
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive())
        {
            if (!auto_finished) {
                telemetry.addLine("Running Auto...");
                telemetry.update();

                // Move forward
                setAllMotorPower(0.1);
                sleep(1000);

                // Move backward
                setAllMotorPower(-0.1);
                sleep(1000);

                auto_finished = true;
            } else {
                telemetry.addLine("Auto Finished");
                telemetry.update();
            }
        }
    }
}
