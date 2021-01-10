package controller.graphs;

import com.google.inject.Inject;
import controller.TabController;
import dao.StatisticDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.controlsfx.control.SegmentedButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

public class OutcomePerTopCategoryController extends TabController {
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
    ComboBox<String> monthSelect;

    @FXML
    PieChart pieChart;

    @Inject
    public OutcomePerTopCategoryController(final StatisticDao statisticDao) {
        this.statisticDao = statisticDao;
    }

    @FXML
    public void initialize() {
        modeSelect.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            monthSelect.setVisible(monthlyToggle.isSelected());
            if(monthSelect.getSelectionModel().isEmpty()) {
                monthSelect.getSelectionModel().selectFirst();
            }
            update();
        });

        yearSelect.valueProperty().addListener((value) -> {
            monthSelect.getSelectionModel().clearSelection();
            monthSelect.setItems(FXCollections.observableList(
                    statisticDao.getAvailableOutcomeHistoryMonthsInYear(
                            yearSelect.getSelectionModel().getSelectedItem()
                    ).stream()
                            .map(StatisticDao::mapMonthToName)
                            .collect(Collectors.toList())
            ));
            if(monthSelect.getSelectionModel().isEmpty()) {
                monthSelect.getSelectionModel().selectFirst();
            }
            if(!monthlyToggle.isSelected()) {
                update();
            }
        });
        monthSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                update();
            }
        });

        pieChart.setAnimated(false);
        pieChart.setTitle("Outcome per top category");
        pieChart.setLabelsVisible(false);

    }

    void setupChart(Map<String, BigDecimal> stats) {
        BigDecimal sum = stats.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        for(String category: stats.keySet()) {
            pieChart.getData().add(new PieChart.Data(
                    category,
                    stats.get(category).setScale(
                            2, RoundingMode.HALF_UP
                    ).divide(sum, RoundingMode.HALF_UP).doubleValue() * 100
            ));
        }

        pieChart.getData().forEach(data -> {
            Tooltip tooltip = new Tooltip();
            tooltip.setShowDelay(new Duration(0));
            tooltip.setText(data.getName() +" " + stats.get(data.getName()).abs()+" - " + data.getPieValue() + "%");
            Tooltip.install(data.getNode(), tooltip);
        });

    }

    private void update() {
        pieChart.getData().clear();
        if(yearlyToggle.isSelected()) {
            setupChart(statisticDao.getTopCategoryOutcomeInYear(
                    yearSelect.getSelectionModel().getSelectedItem()
            ));
        } else {
            setupChart(statisticDao.getTopCategoryOutcomeInMonth(
                    yearSelect.getSelectionModel().getSelectedItem(),
                    StatisticDao.mapMonthNameToMonth(monthSelect.getSelectionModel().getSelectedItem())
            ));
        }
    }

    @Override
    public void onSelected() {
            yearSelect.setItems(FXCollections.observableList(statisticDao.getAvailableOutcomeHistoryYears()));
            if(yearSelect.getSelectionModel().isEmpty()) {
                yearSelect.getSelectionModel().selectFirst();
            }
            monthSelect.setItems(FXCollections.observableList(
                    statisticDao.getAvailableOutcomeHistoryMonthsInYear(
                            yearSelect.getSelectionModel().getSelectedItem()
                    ).stream()
                            .map(StatisticDao::mapMonthToName)
                            .collect(Collectors.toList())
            ));
            if(monthSelect.getSelectionModel().isEmpty()) {
                monthSelect.getSelectionModel().selectFirst();
            }
            update();
    }
}
