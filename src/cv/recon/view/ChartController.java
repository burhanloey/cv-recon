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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author Burhanuddin
 */
public class ChartController implements Initializable {

    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private LineChart<Number, Number> lineChart;
    
    OutputDisplayController outputController;
    ObservableList<XYChart.Data<Number, Number>> list;
    XYChart.Series<Number, Number> series;
    long startTime;
    long elapsedTime;
    Timer timer;
    
    /**
     * Start timer to run a new thread that updates chart.
     */
    public void startTimer() {
        if (timer == null) {
            list.clear();
            startTime = System.nanoTime();

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    elapsedTime = System.nanoTime() - startTime;
                    elapsedTime = elapsedTime / 1000000;

                    Platform.runLater(() -> {
                        add(outputController.getNonZeroPixelCount());
                    });
                }
            }, 0, 100);
        }
    }
    
    /**
     * Stop timer
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    /**
     * Add non-zero pixel count to chart.
     * @param pixelCount Non-zero pixel count
     */
    private void add(int pixelCount) {
        list.add(new XYChart.Data<>(elapsedTime, pixelCount));
        
        clearOldData();
    }
    
    /**
     * Remove earliest data to limit the graph to only display a maximum
     * amount of data
     */
    private void clearOldData() {
        if (list.size() > 50) {
            list.remove(0);
        }
    }
    
    /**
     * Called from root layout to make reference to output display controller.
     * @param outputController 
     */
    public void setOutputController(OutputDisplayController outputController) {
        this.outputController = outputController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = FXCollections.observableArrayList();
        
        series = new XYChart.Series<>("Start point", list);
        lineChart.getData().add(series);
        
        xAxis.setForceZeroInRange(false);
    }    
    
}