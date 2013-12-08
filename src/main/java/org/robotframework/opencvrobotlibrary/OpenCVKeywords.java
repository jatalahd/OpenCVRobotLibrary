package org.robotframework.opencvrobotlibrary;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeywordOverload;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;


@RobotKeywords
public class OpenCVKeywords {

    //static { System.loadLibrary("opencv_java247"); }

    static { 
        try {
            File libfile = new File("opencv_dll.so");
            if ( !libfile.isFile() ) {
                InputStream in = ((URL) ( OpenCVKeywords.class.getResource("/opencv_java247_dll.so") )).openStream();
                OutputStream out = new BufferedOutputStream( new FileOutputStream(libfile) );
                int len = 0;
                byte[] buf = new byte[8192];
                while ((len = in.read(buf)) > -1) out.write(buf,0,len);
                out.close(); in.close();
            }
          System.load(libfile.getAbsolutePath());
        } catch (IOException x) { }
    }


    /* Class internal variables */
    private double defaultSimilarity;
    private double unity;
    private DecimalFormat df = new DecimalFormat("#.####");
  
    /* Constructor with initializers */
    public OpenCVKeywords() {
        this.defaultSimilarity = 0.95D;
        this.unity = 1.00D;
    }
    
    /* Customised Exception handling class */
    private class NotFoundError extends Exception {  
        public NotFoundError(String msg) {
            super(msg);
        }
    }
    
    @RobotKeyword("The similarity of an image and a template is evaluated using the squared difference method. "
                   + "The underlying algorithm uses the OpenCV function MatchTemplate with SQDIFF_NORMED parameter. "
                   + "This method is suitable for calculating the similarity between two colour images, "
                   + "where one needs to verify also the similarity of the colours."
                   + "The resulting similarity is returned as a floating point value in the range 0.00 - 1.00, "
                   + "where 1.00 states the images to be identical. "
                   + "The default limit of similarity is internally set as 0.95, but the optional similarity= parameter "
                   + "can be used to assert against arbitrary similarity requirements.\n\n"
                   + "Example:\n"
                   + "| MatchTemplateSqDiff | C:\\path_to_image\\image.png | C:\\path_to_template\\template.png | 0.97 |\n")
    @ArgumentNames({"image","template","similarity="})
    public void matchTemplateSqDiff(String image, String template, String similarity) throws NotFoundError {
        double res = templateMatcher_SQDIFF_NORMED(image, template);
        if ( res < Float.valueOf(similarity) ) {
            throw new NotFoundError("Error: Could not locate template image - " + template + " - from image - " + image +
                                    ", limit set for similarity is " + similarity + " and returned similarity was " + df.format(res) );
        }
    }
    
    @RobotKeywordOverload
    public void matchTemplateSqDiff(String image, String template) throws NotFoundError {
        double res = templateMatcher_SQDIFF_NORMED(image, template);
        if ( res < defaultSimilarity ) {
            throw new NotFoundError("Error: Could not locate template image - " + template + " - from image - " + image +
                                    ", limit set for similarity is " + defaultSimilarity + " and returned similarity was " + df.format(res) );
        }
    }
    
    @RobotKeyword("The similarity of an image and a template is evaluated using the cross correlation method. "
                   + "The underlying algorithm uses the OpenCV function MatchTemplate with CCORR_NORMED parameter. "
                   + "This method is suitable for calculating the similarity between two images, "
                   + "where colour difference is not meaningful but the shape of the objects need to match. "
                   + "Prior to matching, the differential of the images is evaluated to leave only the outlines of the colour borders. "
                   + "The resulting similarity is returned as a floating point value in the range 0.00 - 1.00, "
                   + "where 1.00 states the images to be identical. "
                   + "The default limit of similarity is internally set as 0.95, but the optional similarity= parameter "
                   + "can be used to assert against arbitrary similarity requirements.\n\n"
                   + "Example:\n"
                   + "| MatchTemplateCrossCorr | C:\\path_to_image\\image.png | C:\\path_to_template\\template.png | 0.88 |\n")
    @ArgumentNames({"image","template","similarity="})
    public void matchTemplateCrossCorr(String image, String template, String similarity) throws NotFoundError {
        double res = templateMatcher_CCORR_NORMED(image, template);
        if ( res < Float.valueOf(similarity) ) {
            throw new NotFoundError("Error: Could not locate template image - " + template + " - from image - " + image +
                                    ", limit set for similarity is " + similarity + " and returned similarity was " + df.format(res) );
        }
    }
    
    @RobotKeywordOverload
    public void matchTemplateCrossCorr(String image, String template) throws NotFoundError {
        double res = templateMatcher_CCORR_NORMED(image, template);
        if ( res < defaultSimilarity ) {
            throw new NotFoundError("Error: Could not locate template image - " + template + " - from image - " + image +
                                    ", limit set for similarity is " + defaultSimilarity + " and returned similarity was " + df.format(res) );
        }
    }
    
    
    private double templateMatcher_SQDIFF_NORMED(String image, String template) {
        Mat img = new Mat(); Mat templ = new Mat(); Mat result = new Mat();
        int match_method = Imgproc.TM_SQDIFF_NORMED;
    
        img = Highgui.imread( image, 1 );
        templ = Highgui.imread( template, 1 );
    
        int result_cols =  img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create( result_cols, result_rows, CvType.CV_32FC1 );

        Imgproc.matchTemplate( img, templ, result, match_method );
        Core.MinMaxLocResult x = Core.minMaxLoc( result );

        // For SQDIFF and SQDIFF_NORMED, the best matches are lower values.
        // For all the other methods, the higher the better
        Point matchLoc = x.minLoc;
        System.out.println( "FOUND LOCATION OF THE TEMPLATE IMAGE:\n" +
                           "top left x-coordinate in pixels: " + matchLoc.x +
                           "\n top letf y-coordinate in pixels: " + matchLoc.y +
                           "\n minimum similarity value: " + df.format(x.minVal) +
                           "\n maximum similarity value: " + df.format(x.maxVal) + 
                           "\n\n RETURNED SIMILARITY VALUE: " + df.format(unity - x.minVal) );  
        
        return (unity - x.minVal);
    }

    
    private double templateMatcher_CCORR_NORMED(String image, String template) {
        Mat img = new Mat(); Mat templ = new Mat(); Mat result = new Mat();
        Mat img_gray= new Mat();      Mat templ_gray= new Mat();
        Mat img_grad = new Mat();     Mat templ_grad = new Mat();
        Mat grad_x = new Mat();       Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();   Mat abs_grad_y = new Mat();
        int match_method = Imgproc.TM_CCORR_NORMED; int scale = 1; int delta = 0;
  
        img = Highgui.imread( image, 1 );
        templ = Highgui.imread( template, 1 );
    
        Imgproc.cvtColor(img, img_gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(templ, templ_gray, Imgproc.COLOR_RGB2GRAY);
    
        Imgproc.Sobel( img_gray, grad_x, CvType.CV_16S, 1, 0, 3, scale, delta, Imgproc.BORDER_DEFAULT );
        Core.convertScaleAbs( grad_x, abs_grad_x );    
        Imgproc.Sobel( img_gray, grad_y, CvType.CV_16S, 0, 1, 3, scale, delta, Imgproc.BORDER_DEFAULT );
        Core.convertScaleAbs( grad_y, abs_grad_y );
        Core.addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, img_grad );

        Imgproc.Sobel( templ_gray, grad_x, CvType.CV_16S, 1, 0, 3, scale, delta, Imgproc.BORDER_DEFAULT );
        Core.convertScaleAbs( grad_x, abs_grad_x );
        Imgproc.Sobel( templ_gray, grad_y, CvType.CV_16S, 0, 1, 3, scale, delta, Imgproc.BORDER_DEFAULT );
        Core.convertScaleAbs( grad_y, abs_grad_y );
        Core.addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, templ_grad );
    
        int result_cols =  img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;

        result.create( result_cols, result_rows, CvType.CV_16S );
    
       Imgproc.matchTemplate( img_grad, templ_grad, result, match_method );
       Core.MinMaxLocResult x = Core.minMaxLoc( result );

       // For SQDIFF and SQDIFF_NORMED, the best matches are lower values.
       // For all the other methods, the higher the better
       Point matchLoc = x.maxLoc;
       System.out.println( "FOUND LOCATION OF THE TEMPLATE IMAGE:\n" +
                           "top left x-coordinate in pixels: " + matchLoc.x +
                           "\n top letf y-coordinate in pixels: " + matchLoc.y +
                           "\n minimum similarity value: " + df.format(x.minVal) +
                           "\n maximum similarity value: " + df.format(x.maxVal) +
                           "\n\n RETURNED SIMILARITY VALUE: " + df.format(x.maxVal) );   
       return x.maxVal;
    }    

} // End Of OpenCVKeywords
