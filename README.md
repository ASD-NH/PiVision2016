# PiVision2016

This is the repository for all java code running on our Raspberry Pi coprocessor during the 2016 build season (Stronghold). This code deals primarily with machine vision, as well as creating a UDP server to send machine vision data to the Rio.

## Dependencies

* [Webcam-capture by Sarxos](https://github.com/sarxos/webcam-capture) (Capture images from the webcam)
* [BoofCV](http://boofcv.org) (Perform operations on & analyze captured images)

## Installing Dependencies

For Webcam-capture, download the latest release (v0.3.10), then unzip somewhere safe on your computer. Next go to Eclipse, right click the project, and click `Build Path --> Add External Archives`, then select `webcam-capture-0.3.10.jar` in the folder you extracted. Repeat the process for `slf4j-api-1.7.2.jar` and `bridj-0.6.2.jar` (found in the `lib` subdirectory).
On the Raspberry Pi, replace the included BridJ version with the jar found [here](https://oss.sonatype.org/content/repositories/snapshots/com/nativelibs4java/bridj/0.6.3-SNAPSHOT/bridj-0.6.3-20130316.190111-13.jar) (needs testing).

The installation is similar for BoofCV. Download the compiled and source JARs from [here](http://boofcv.org/index.php?title=Download), and then extract them somewhere safe. Next, go to Eclipse and right click on the PiVision folder, going to `Build path --> Add Libraries`. Select `User Library`, next, and then on the next screen hit the `User Libraries` button. From here, hit `Add External JARs` and navigate to and select all of the files in the BoofCV directory, and then hit OK. Name it something useful like `boofcv`. Click OK on the `User Library` screen, and then check the box next to the library you just made. Then hit finish.
