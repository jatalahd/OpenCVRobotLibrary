import org.robotframework.javalib.library.AnnotationLibrary;

public class OpenCVRobotLibrary extends AnnotationLibrary {
    public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
    public static final String ROBOT_LIBRARY_VERSION = "1.0";
    
    public OpenCVRobotLibrary() {
        super("org/robotframework/opencvrobotlibrary/OpenCVKeywords.class");
    }
}
