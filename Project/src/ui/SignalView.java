package ui;

import javafx.scene.chart.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;

public class SignalView extends LineChart<Number,Number> {

    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();

    public SignalView(Axis<Number> axis, Axis<Number> axis1) {
        super(axis, axis1);
    }
/*
    public updateData(){
        this.series

    }*/
}
