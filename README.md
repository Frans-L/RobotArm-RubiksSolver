# Robotic Arms - Rubik's Cube Solver

Two 6 axis robotic arms that solves Rubik's cube. A picture of the cube is taken by an Android app and information of the cube is send the robotic arms over Bluetooth. The robotic arms are made of Legos. The motors are controlled by Mindstorms NXT 2.0. The Android app is programmed in Java. (WIP) 

You can read more detailed version in Finnish: [Palikkatakomo Foorumi](http://www.palikkatakomo.org/forum/read.php?4,38848)

<p align="center">
  <img src="https://github.com/Frans-L/RobotArm-RubiksSolver/blob/master/Pictures/arms.jpg?raw=true" alt="A picture of the robots arms"/>
</p>

## Idea

The idea is to build two robotic arms that solves Rubik's cube. The robotic arms have 6 degrees of freedom. There should be an Android app that captures images of a Rubik's cube. The app also calculates a solution. The solution is sent to the robotic arms over Bluetooth.

The arms are built by using 12 servo motors, 4 NXTs, a can of compressed air and by bunch of Legos. The Android app is programmed in Java.

## Status

* **Done**
	* Robotic Arms
	* Kinematics
	* Servo Controls
	* Communication between NXT bricks
* **WIP**
	* Image Recognition
		* App that takes pictures - *DONE*
		* Recognize the Cube - *WIP*
	* Communication between NXT and phone
		* Send data to NXT over Bluetooth - *DONE*
		* Add data sending feature to the App - *WIP*
	* Combine everything

Read more detailed version in Finnish: [Palikkatakomo Foorumi](http://www.palikkatakomo.org/forum/read.php?4,38848)

Note: At the moment, only the code of the Android app is on Github.

## Authors

* **Henry P** - Management, Desinging, Building, NXC Progamming [Zwenkka](https://github.com/Zwenkka)
* **Frans L** - Java && NXC Progamming, App Designing [Frans-L](https://github.com/Frans-L)


## License

This project is licensed under the Apache Software License 2.0.
