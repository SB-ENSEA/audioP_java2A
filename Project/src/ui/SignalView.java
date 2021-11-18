package ui;

import audio.AudioSignal;
import javafx.scene.chart.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;

public class SignalView extends LineChart<Number,Number> {

    private XYChart.Series<Number, Number> series;

    public SignalView(Axis<Number> xAxis, Axis<Number> yAxis,String Title ,String labelX, String labelY) {
        super(xAxis, yAxis); //using the constructor of Linechart
        this.series = new XYChart.Series<>();
        super.setTitle(Title);
        xAxis.setLabel(labelX);
        yAxis.setLabel(labelY);
        super.getData().add(series);
    }

    public void updateData(AudioSignal sig) {
        for(int index = 0 ; index<sig.getFrameSize(); )
        this.getData().add(series); //updates data by changing the series field of the classe.
    }
}

