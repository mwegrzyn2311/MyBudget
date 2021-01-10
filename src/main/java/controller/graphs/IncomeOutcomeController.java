package controller.graphs;

import com.google.inject.Inject;
import controller.TabController;
import dao.StatisticDao;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.SegmentedButton;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.function.ToIntFunction;

public class IncomeOutcomeController extends TabController {
    final StatisticDao statisticDao;

    @FXML
    SegmentedButton modeSelect;

    @FXML
    ToggleButton yearlyToggle;

    @FXML
    ToggleButton monthlyToggle;

    @FXML
    ComboBox<String> yearSelect;

    @FXML
    BarChart<String, BigDecimal> chart;

    @FXML
    CategoryAxis categoryAxis;

    @FXML
    NumberAxis valueAxis;

    enum Mode {
        YEARLY, MONTHLY
    }

    private Mode mode = Mode.YEARLY;

    @Inject
    public IncomeOutcomeController(final StatisticDao statisticDao) {
        this.statisticDao = statisticDao;
    }

    @FXML
    public void initialize() {
        modeSelect.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            yearSelect.setVisible(monthlyToggle.isSelected());
            if(monthlyToggle.isSelected()) {
                mode = Mode.MONTHLY;
            } else {
                mode = Mode.YEARLY;
            }
            update();
        });

        yearSelect.setOnAction((value) -> update());
        chart.setAnimated(false);
    }

    void setupChartYearly() {
        Map<String, StatisticDao.IncomeOutcome> stats = statisticDao.getYearlyStatistic();
        XYChart.Series<String, BigDecimal> income = new XYChart.Series<>();
        XYChart.Series<String, BigDecimal> outcome = new XYChart.Series<>();

        income.setName("Income");

        outcome.setName("Outcome");

        for(String year: stats.keySet()) {
            income.getData().add(new XYChart.Data<>(year, stats.get(year).income));
            outcome.getData().add(new XYChart.Data<>(year, stats.get(year).outcome.abs()));
        }

        income.getData().sort(Comparator.comparingInt(entry -> Integer.parseInt(entry.getXValue())));
        outcome.getData().sort(Comparator.comparingInt(entry -> Integer.parseInt(entry.getXValue())));

        chart.getData().add(outcome);
        chart.getData().add(income);
    }

    void setupChartMonthly(String year) {
        Map<String, StatisticDao.IncomeOutcome> monthlyStats = statisticDao.getMonthlyStatistic(year);
        XYChart.Series<String, BigDecimal> income = new XYChart.Series<>();
        XYChart.Series<String, BigDecimal> outcome = new XYChart.Series<>();

        income.setName("Income");

        outcome.setName("Outcome");

        for(String month: monthlyStats.keySet()) {
            income.getData().add(new XYChart.Data<>(month, monthlyStats.get(month).income));
            outcome.getData().add(new XYChart.Data<>(month, monthlyStats.get(month).outcome.abs()));
        }
        Comparator<XYChart.Data<String, BigDecimal>> monthComparator = new Comparator<>() {
            @Override
            public int compare(XYChart.Data<String, BigDecimal> lhs, XYChart.Data<String, BigDecimal> rhs) {
                try {
                    SimpleDateFormat fmt = new SimpleDateFormat("MMM", Locale.US );
                    return fmt.parse(lhs.getXValue()).compareTo(fmt.parse(rhs.getXValue()));
                } catch (ParseException ex) {
                    return lhs.getXValue().compareTo(rhs.getXValue());
                }
            }
        };

        income.getData().sort(monthComparator);
        outcome.getData().sort(monthComparator);

        chart.getData().add(outcome);
        chart.getData().add(income);
    }

    private void update() {
        if(mode == Mode.YEARLY) {
            chart.getData().clear();
            setupChartYearly();
        } else {
            chart.getData().clear();
            setupChartMonthly(yearSelect.getSelectionModel().getSelectedItem());
        }
    }

        @Override
    public void onSelected() {
            yearSelect.setItems(FXCollections.observableList(statisticDao.availableHistoryYears()));
            if(yearSelect.getSelectionModel().isEmpty()) {
                yearSelect.getSelectionModel().selectFirst();
            }
            update();
    }
}
