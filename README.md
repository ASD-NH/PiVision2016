# PiVision2016

This is the repository for all java code running on our Raspberry Pi coprocessor during the 2016 build season (Stronghold). This code deals primarily with machine vision, as well as creating a UDP server to send machine vision data to the Rio.

## Dependencies

* [Webcam-capture by Sarxos](https://github.com/sarxos/webcam-capture) (Capture images from the webcam)
* [BoofCV](http://boofcv.org) (Perform operations on & analyze captured images)

## Installing Dependencies

In Eclipse, right click on your copy of the PiVision2016 project, then go to `Build Path --> Add External Archives`. From here, navigate to your Eclipse workspace, and then to `PiVision2016/libs` and select the `.jars` immediately inside of the folder (ignore the `boofcv` folder for now). Hit OK.

Next, right click on the project again, and go to `Build Path --> Add Libaries`. Select `User Library`. On the next screen, hit the `User Libraries Button`. From here, hit `Add External JARs` and select all the files in that `boofcv` folder we saw earlier (inside libs). Hit okay, name the library `boofcv`, and then click OK on the `User Library` screen. Now, just check the box next to the library you just made and hit finish.

##Deploying to Pi

* Uncomment out the line in VisionServer.java relating to the webcam driver
* In Eclipse, go to `File --> Export` and select "Runnable JAR file". Hit Next, and then ensure that the Launch configuration is set to `VisionServer - PiVision2016`. Set the export destination to somewhere convenient on your computer
* Hit Finish, and then OK on any subsequent dialogs.
* Either copy the created JAR manually to the Pi, or use the `scp` command to transfer the file over the LAN. To transfer using `scp`, open up a terminal and run `scp PiVision2016.jar pi@IP-HERE:~/Desktop/PiVision2016.jar` while in the directory with the JAR you created. Make sure to replace the IP with the Pi's IP address. The password is `team2342`
* Run the JAR either manually or through SSH with `java -jar Desktop/PiVision.jar`. Again, the password is `team2342`.
