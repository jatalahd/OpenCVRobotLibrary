OpenCVRobotLibrary
==================

Exploratory WebDriver Java library for Robot Framework, built upon the AnnotationLibrary provided by the Robot's javalib-core distribution.

This is a basic Maven project. To create a standalone jar package with dependencies, just type: "mvn clean package" in the directory where the pom.xml is located

NOTE! The opencv-java version 2.4.7 maven dependency in the pom.xml is satisfied by making a local maven installation of the opencv-247.jar file, with added native library updated inside the .jar file. The opencv-247.jar file is obtained by following these instructions:

http://docs.opencv.org/2.4.7/doc/tutorials/introduction/desktop_java/java_dev_intro.html

After obtaining the opencv-247.jar and the corresponding native library (e.g. opencv-java-247.so) rename the native library to opencv_java247_dll.so and put this library file inside the .jar using the command:

jar uf opencv-247.jar opencv_java247_dll.so

(The code looks for the library file by that name and unpacks it from the .jar at runtime)

Once the opencv-247.jar has been updated with the library file, the local maven installation is done with the command:

mvn install:install-file -Dfile=opencv-247.jar -DgroupId=org.opencv -DartifactId=opencv-java -Dversion=2.4.7 -Dpackaging=jar

After this the build can be run successfully. To make things roll with Robot Framework, one can use jybot or the .jar distribution of Robot Framework. When using the .jar distribution, one should set the CLASSPATH as: set CLASSPATH=robotframework-2.x.x.jar;OpenCVRobotLibrary-1.0-SNAPSHOT-jar-with-dependencies.jar;

and then run the test case file with the command: java org.robotframework.RobotFramework run test.txt

Please check the OpenCVRobotLibrary.html for details on the keywords provided.
