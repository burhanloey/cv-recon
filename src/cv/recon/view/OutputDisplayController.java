/*
 * Copyright (C) 2015 Burhanuddin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cv.recon.view;

import cv.recon.util.MatFXUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

/**
 * FXML Controller class
 *
 * @author Burhanuddin
 */
public class OutputDisplayController implements Initializable {

    @FXML
    private ImageView outputView;
    
    BackgroundSubtractorMOG2 bsmog;
    FeatureDetector detector;
    Mat fgMask;
    Mat kernel;
    Mat output;
    MatOfKeyPoint keyPoints;
    WritableImage writableImage;
    
    /**
     * Update output view after image processing.
     * @param src Original Mat
     */
    public void updateView(Mat src) {
        if (!src.empty()) {
            processImage(src);
        }

        if (!output.empty()) {
            if (writableImage == null) {
                writableImage = MatFXUtils.toFXImage(output, null);
            } else {
                MatFXUtils.toFXImage(output, writableImage);
            }
            outputView.setImage(writableImage);
        }
    }
    
    /**
     * Image processing.
     * @param src Source Mat
     */
    private void processImage(Mat src) {
        subtractBackground(src);
        detector.detect(src, keyPoints, fgMask);
        Features2d.drawKeypoints(src, keyPoints, output);
    }
    
    /**
     * Subtract background using BackgroundSubtractorMOG2
     * @param src Source Mat
     */
    private void subtractBackground(Mat src) {
        if (bsmog != null) {
            bsmog.apply(src, fgMask);
            
            Imgproc.erode(fgMask, fgMask, kernel);
            Imgproc.dilate(fgMask, fgMask, kernel);
            
            output.setTo(new Scalar(0));
            src.copyTo(output, fgMask);
        }
    }
    
    /**
     * Start background subtraction using first frame as background frame.
     */
    public void startBackgroundSubtraction() {
        bsmog = new BackgroundSubtractorMOG2(100, 75f, false);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fgMask = new Mat();
        output = new Mat();
        keyPoints = new MatOfKeyPoint();
        
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10, 10));
        detector = FeatureDetector.create(FeatureDetector.SURF);
    }    
    
}
