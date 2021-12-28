package agh.ics.oop.gui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class LinearPlot {
    // number of living animals, number of plants, avg energy lvl, avg lifetime, avg number of children
    private final List<XYChart.Series<Number, Number>> series = new ArrayList<>() {{
        for (int i = 0; i < 5; i++)
            add(new XYChart.Series<>());
    }};

    private final LineChart<Number, Number> plot;

    public LinearPlot(String title)
    {
        NumberAxis daysAxis = new NumberAxis();
        daysAxis.setLabel("days");
        plot = new LineChart<>(daysAxis, new NumberAxis());
        plot.setTitle(title);

        String[] names = {
                "animals",
                " plants",
                "avg energy",
                "avg lifetime",
                "avg children"
        };

        for(int i = 0; i<series.size(); i++)
        {
            series.get(i).setName(names[i]);
            plot.getData().add(i, series.get(i));
        }
    }

    public void addPlotToHBox(HBox hbox)
    {
        hbox.getChildren().add(this.plot);
    }

    public void updatePlot(int dayCount, Number[] dailyData)
    {
        for(int i = 0; i < dailyData.length; i++)
            series.get(i).getData().add((new XYChart.Data<>(dayCount, dailyData[i])));
    }

    public Number[] getAverageData()
    {
        // series.size() == 5
        int n = series.size();
        Number[] res = new Number[n];
        double[] sums = new double[n];
        for(int i = 0; i < n; i++)
        {
            var data = series.get(i).getData();

            for (XYChart.Data<Number, Number> datum : data)
                sums[i] += datum.getYValue().doubleValue();
        }

        int count = series.get(0).getData().size();
        for(int i = 0; i < n; i++)
            res[i] = sums[i]/count;

        return res;
    }
}
