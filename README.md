# Dual Robotic Arm (6 Degrees of Freedom) - Rubik's Cube Solver

Two 6 axis robotic arms that solves Rubik's cube. An android phone works as a camera and sends information about the cube to the robotic arms. The robotic arms are made of Legos. Motors are controlled by Mindstorms NXT 2.0. (WIP) 

You can read more detailed version in Finnish: [Palikkatakomo Foorumi]http://www.palikkatakomo.org/forum/read.php?4,38848

<p align="center">
  <img src="https://github.com/Frans-L/RobotArm-RubiksSolver/blob/master/Pictures/arms.jpg?raw=true" alt="A picture of the robots arms"/>
</p>

## Idea

The ideas are to build two robotic arms that solves Rubik's cube. The robotic arms have 6 degrees of freedom. There should be an Android app that captures images of a Rubik's cube. Then the app calculates a solution. The solution is sent to Robotic arms over Bluetooth.

The arms are built by using 12 servo motors, 4 NXTs, a can of compressed air and by bunch of Legos. The Android app is programmed with Java.

## Status

* **Done**
	* Robot Arms
	* Kinematics
	* Servo Controls
	* Communication between NXT bricks
* **WIP**
	* Image Recognition
		* App that takes pictures - DONE
		* Recognize the Cube - WIP
	* Communication between NXT and phone
		* Send data to NXT over Bluetooth - DONE
		* Add data sending feature to the App - WIP
	* Combine everything


Note: Read more detailed version in Finnish: [Palikkatakomo Foorumi]http://www.palikkatakomo.org/forum/read.php?4,38848

## Authors

* **Henry P** - Management, Desinging, Building, NXC Progamming [Zwenkka](https://github.com/Zwenkka)
* **Frans L** - Java && NXC Progamming, App Designing [Frans-L](https://github.com/Frans-L)


## License

This project is licensed under the Apache Software License 2.0.
