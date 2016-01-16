# PiVision2016

This is the repository for all java code running on our Raspberry Pi coprocessor during the 2016 build season (Stronghold). This code deals primarily with machine vision, as well as creating a UDP server to send machine vision data to the Rio.

## Dependencies

* [Webcam-capture by Sarxos](https://github.com/sarxos/webcam-capture) (Capture images from the webcam)
* [JMagick](http://www.jmagick.org/6.4.0/) (Perform common operations on captured images)

## Installing Dependencies

For Webcam-capture, download the latest release (v0.3.10), then unzip somewhere safe on your computer. Next go to Eclipse, right click the project, and click `Build Path --> Add External Archives`, then select `webcam-capture-0.3.10.jar` in the folder you extracted. Repeat the process for `slf4j-api-1.7.2.jar` and `bridj-0.6.2.jar` (found in the `lib` subdirectory).
On the Raspberry Pi, replace the included BridJ version with the jar found [here](https://oss.sonatype.org/content/repositories/snapshots/com/nativelibs4java/bridj/0.6.3-SNAPSHOT/bridj-0.6.3-20130316.190111-13.jar) (needs testing).

The JMagick install only works on Linux for now, and involves some complicated steps. Install with caution. Maybe get help or something. The instructions are as follows:
* Install JMagick to your system: `sudo apt-get install libjmagick6-java libjmagick6-jni`
* Copy `jmagick-6.6.9.jar` from `/usr/share/java/` to `jre/lib/ext/` in your java directory. Here is the command to do it on my system: `sudo cp /usr/share/java/jmagick-6.6.9.jar /usr/lib/jvm/java-7-oracle/jre/lib/ext/jmagick-6.6.9.jar`. If you need help finding your java home directory, try running `locate jre/lib/ext`.
* Right click the PiVision project in eclipse, and go to `Build Path --> Add External Archives`. Add `jmagick-6.6.9.jar` from `/usr/share/java/`.
* Under `Build Path --> Configure Build Path`, click the arrow on `JRE System Library` and `jmagick-6.6.9.jar`, and set `/usr/lib/jni` as the Native Library Location for both of them.
* Run `sudo ln -s /usr/lib/jni/libJMagick.so /usr/lib/libJMagick.so` to help JMagick find its shared library.
