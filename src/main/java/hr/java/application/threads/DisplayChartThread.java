package hr.java.application.threads;

import javafx.scene.chart.AreaChart;

public class DisplayChartThread extends DatabaseThread implements Runnable{

    private final AreaChart<String, Integer> chart;

    public DisplayChartThread(AreaChart<String, Integer> chart){ this.chart = chart; }

    @Override
    public void run() {
        super.displayChart(chart);
    }
}
