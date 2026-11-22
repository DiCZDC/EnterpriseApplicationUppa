package com.uppa.beans;

import CDIs.ASesion;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import logica.MGraficas;

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

/**
 * Gráficas de vistas/graficas/index.xhtml con los análisis reales del usuario en sesión.
 */
@Named("graficasBean")
@RequestScoped
public class GraficasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Meses que abarca la gráfica de tendencia. */
    private static final int MESES = 6;

    /** Paleta de Úppa (paleta.css): verde, rosa, naranja, azul, dorado, uva, gris. */
    private static final String[] COLORES = {"#80b67e", "#d45656", "#fba518", "#368ab8", "#f9cb43", "#3c3546", "#8a8395"};
    private static final String[] RELLENOS = {"rgba(128, 182, 126, 0.18)", "rgba(212, 86, 86, 0.18)",
        "rgba(251, 165, 24, 0.18)", "rgba(54, 138, 184, 0.18)", "rgba(249, 203, 67, 0.18)",
        "rgba(60, 53, 70, 0.18)", "rgba(138, 131, 149, 0.18)"};

    @EJB
    private MGraficas mGraficas;

    @Inject
    private ASesion sesion;

    private DonutChartModel donutModel;
    private BarChartModel barModel;
    private LineChartModel lineModel;
    private long totalEscaneos;
    private boolean hayEnfermedades;

    @PostConstruct
    public void init() {
        int idUsuario = sesion.getIdUsuario() == null ? -1 : sesion.getIdUsuario();
        createDonutModel(idUsuario);
        createBarModel(idUsuario);
        createLineModel(idUsuario);
    }

    private void createDonutModel(int idUsuario) {
        Map<String, Long> estado = mGraficas.estadoGeneral(idUsuario);
        totalEscaneos = mGraficas.total(estado);

        donutModel = new DonutChartModel();
        ChartData data = new ChartData();

        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>(estado.values());
        dataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        int i = 0;
        for (String etiqueta : estado.keySet()) {
            // "Sanos" siempre en verde; las enfermedades toman el resto de la paleta
            bgColors.add("Sanos".equals(etiqueta) ? COLORES[0] : COLORES[1 + (i++ % (COLORES.length - 1))]);
        }
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        data.setLabels(new ArrayList<>(estado.keySet()));

        donutModel.setData(data);

        DonutChartOptions options = new DonutChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText(totalEscaneos + (totalEscaneos == 1 ? " escaneo total" : " escaneos totales"));
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        options.setLegend(legend);

        donutModel.setOptions(options);
    }

    private void createBarModel(int idUsuario) {
        Map<String, Long> especies = mGraficas.especiesIdentificadas(idUsuario, COLORES.length);

        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Escaneos totales");

        List<Number> values = new ArrayList<>(especies.values());
        barDataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            bgColors.add(COLORES[i % COLORES.length]);
        }
        barDataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(barDataSet);
        data.setLabels(new ArrayList<>(especies.keySet()));

        barModel.setData(data);

        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();

        // setBeginAtZero se aplica sobre el Axis en lugar del Tick:
        linearAxes.setBeginAtZero(true);

        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setStepSize(1);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Escaneos por especie — top " + COLORES.length);
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(false);
        options.setLegend(legend);

        barModel.setOptions(options);
    }

    private void createLineModel(int idUsuario) {
        Map<String, long[]> series = mGraficas.enfermedadesPorMes(idUsuario, MESES);
        List<String> meses = mGraficas.ultimosMeses(MESES);
        hayEnfermedades = !series.isEmpty();

        lineModel = new LineChartModel();
        ChartData data = new ChartData();

        int i = 1; // el índice 0 (verde) se reserva para "sanos"
        for (Map.Entry<String, long[]> e : series.entrySet()) {
            int c = i++ % COLORES.length;
            data.addChartDataSet(createLineDataSet(e.getKey(), COLORES[c], RELLENOS[c], e.getValue()));
        }

        data.setLabels(new ArrayList<>(meses));
        lineModel.setData(data);

        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Frecuencia mensual de enfermedades — " + meses.get(0) + " a " + meses.get(meses.size() - 1));
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        options.setLegend(legend);

        lineModel.setOptions(options);
    }

    private LineChartDataSet createLineDataSet(String label, String color, String bgColor, long... values) {
        LineChartDataSet ds = new LineChartDataSet();
        ds.setLabel(label);
        List<Object> data = new ArrayList<>();
        for (long v : values) data.add(v);
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
    public long getTotalEscaneos() { return totalEscaneos; }
    public boolean isSinDatos() { return totalEscaneos == 0; }
    public boolean isHayEnfermedades() { return hayEnfermedades; }
}
