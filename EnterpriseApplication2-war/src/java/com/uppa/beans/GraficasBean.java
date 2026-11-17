package com.uppa.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.charts.ChartData;

// Modelos de gráficas
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.donut.DonutChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;

// Ejes
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;

// IMPORTS CORRECTOS DE TITULO Y LEYENDA EN PRIMEFACES:
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.title.Title;

@Named("graficasBean")
@RequestScoped
public class GraficasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private DonutChartModel donutModel;
    private BarChartModel barModel;
    private LineChartModel lineModel;

    @PostConstruct
    public void init() {
        createDonutModel();
        createBarModel();
        createLineModel();
    }

    private void createDonutModel() {
        donutModel = new DonutChartModel();
        ChartData data = new ChartData();

        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(163); values.add(32); values.add(22); values.add(18); values.add(13);
        dataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("#80b67e"); bgColors.add("#d45656"); bgColors.add("#fba518");
        bgColors.add("#368ab8"); bgColors.add("#f9cb43");
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);

        List<String> labels = new ArrayList<>();
        labels.add("Sanos");
        labels.add("Burbuja seca (Verticillium)");
        labels.add("Mancha bacterial");
        labels.add("Mildiú del champiñón");
        labels.add("Trichoderma verde");
        data.setLabels(labels);

        donutModel.setData(data);

        DonutChartOptions options = new DonutChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("248 escaneos totales");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        options.setLegend(legend);

        donutModel.setOptions(options);
    }

    private void createBarModel() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Escaneos totales");

        List<Number> values = new ArrayList<>();
        values.add(74); values.add(52); values.add(41); values.add(30); values.add(24); values.add(16); values.add(11);
        barDataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("#80b67e"); bgColors.add("#368ab8"); bgColors.add("#f9cb43");
        bgColors.add("#fba518"); bgColors.add("#3c3546"); bgColors.add("#d45656"); bgColors.add("#8a8395");
        barDataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        labels.add("A. bisporus"); labels.add("P. ostreatus"); labels.add("L. edodes");
        labels.add("C. cibarius"); labels.add("B. edulis"); labels.add("M. esculenta"); labels.add("F. velutipes");
        data.setLabels(labels);

        barModel.setData(data);

        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        
        // setBeginAtZero se aplica sobre el Axis en lugar del Tick:
        linearAxes.setBeginAtZero(true);
        
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Escaneos por especie — acumulado 2026");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(false);
        options.setLegend(legend);

        barModel.setOptions(options);
    }

    private void createLineModel() {
        lineModel = new LineChartModel();
        ChartData data = new ChartData();

        LineChartDataSet dataSet1 = createLineDataSet("Burbuja seca (Verticillium)", "#d45656", "rgba(212, 86, 86, 0.18)", 4, 6, 11, 8, 5, 3);
        LineChartDataSet dataSet2 = createLineDataSet("Mancha bacterial", "#fba518", "rgba(251, 165, 24, 0.18)", 2, 4, 8, 7, 4, 2);
        LineChartDataSet dataSet3 = createLineDataSet("Mildiú del champiñón", "#368ab8", "rgba(54, 138, 184, 0.18)", 1, 3, 6, 5, 2, 1);
        LineChartDataSet dataSet4 = createLineDataSet("Trichoderma verde", "#f9cb43", "rgba(249, 203, 67, 0.18)", 1, 2, 4, 3, 2, 1);

        data.addChartDataSet(dataSet1);
        data.addChartDataSet(dataSet2);
        data.addChartDataSet(dataSet3);
        data.addChartDataSet(dataSet4);

        List<String> labels = new ArrayList<>();
        labels.add("Enero"); labels.add("Febrero"); labels.add("Marzo");
        labels.add("Abril"); labels.add("Mayo"); labels.add("Junio");
        data.setLabels(labels);

        lineModel.setData(data);

        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Frecuencia mensual de enfermedades — Ene a Jun 2026");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        options.setLegend(legend);

        lineModel.setOptions(options);
    }

    private LineChartDataSet createLineDataSet(String label, String color, String bgColor, int... values) {
        LineChartDataSet ds = new LineChartDataSet();
        ds.setLabel(label);
        List<Object> data = new ArrayList<>();
        for (int v : values) data.add(v);
        ds.setData(data);
        ds.setBorderColor(color);
        ds.setBackgroundColor(bgColor);
        ds.setFill(true);
        ds.setTension(0.38);
        return ds;
    }

    public DonutChartModel getDonutModel() { return donutModel; }
    public BarChartModel getBarModel() { return barModel; }
    public LineChartModel getLineModel() { return lineModel; }
}   