# Robotic Arms - Rubik's Cube Solver

Two 6 axis robotic arms that solve a Rubik's cube. A picture of the cube is taken by an Android app and information of the cube is send to the robotic arms over Bluetooth. The robotic arms are made of Legos. The motors are controlled by Mindstorms NXT 2.0. The Android app is programmed in Java. (WIP) 

You can read more detailed version in Finnish: [Palikkatakomo Foorumi](http://www.palikkatakomo.org/forum/read.php?4,38848)

<p align="center">
  <img src="https://github.com/Frans-L/RobotArm-RubiksSolver/blob/master/Pictures/arms.jpg?raw=true" alt="A picture of the robots arms"/>
</p>

## Idea

The idea is to build two robotic arms that can solve a Rubik's cube. The robotic arms have 6 degrees of freedom. There should be an Android app that captures images of a Rubik's cube. The app also calculates a solution. The solution is sent to the robotic arms over Bluetooth.

The arms are built by using 12 servo motors, 4 NXTs, a can of compressed air and by a bunch of Legos. The Android app is programmed in Java.

---

## Status

* **Robotic Arms**
    * Kinematics **✔**
    * Servo Controls **✔**
    * Grabber **✔**
    * Compressed Air **✔**
    * Movements **WIP**

* **Android App**
    * Image Recognition
        * Capturing Images **✔**
        * Recognizing a Cube **WIP**
    * Sending Information
        * Communication **✔**
        * Sending the solution

* **Communication**
    * NXT -> NXT **✔**
    * Android -> NXT **✔**
    * NXT -> Android **?**

* **Solving the Cube**
    * With Internet **✔**
    * Without Internet 


*Note*: At the moment, only the code of the Android app is on Github.

---

## Authors

* **Henry P** - Management, Designing, Building, NXC Progamming [Zwenkka](https://github.com/Zwenkka)
* **Frans L** - Java && NXC Progamming, App Designing [Frans-L](https://github.com/Frans-L)


## License

This project is licensed under the Apache Software License 2.0.

The classes under 'bluetooth' package are modified versions from the project [nxt-remote-control](https://github.com/jfedor2/nxt-remote-control/tree/master/src/org/jfedor/nxtremotecontrol)
made by [jfedor2](https://github.com/jfedor2).

* bluetooth.FindNXT.java is a modified version from [ChooseDeviceActivity.java](https://github.com/jfedor2/nxt-remote-control/blob/master/src/org/jfedor/nxtremotecontrol/NXTTalker.java)
* bluetooth.NXTTalker.java is a modified version from [NXTTalker.java](https://github.com/jfedor2/nxt-remote-control/blob/master/src/org/jfedor/nxtremotecontrol/NXTTalker.java)

