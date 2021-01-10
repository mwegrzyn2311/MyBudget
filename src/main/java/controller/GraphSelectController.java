package controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import service.FxmlLoaderService;

public class GraphSelectController extends TabController{
    private final FxmlLoaderService fxmlLoaderService;

    @FXML
    private Button incomeOutcomeStatisticButton;

    @FXML
    private Button outcomeStatisticButton;

    @Inject
    public GraphSelectController(final FxmlLoaderService fxmlLoaderService) {
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @FXML
    public void initialize() {
        incomeOutcomeStatisticButton.setOnAction(event -> tabAreaController.openTab(
                fxmlLoaderService.getLoader(
                    getClass().getResource("/view/graphs/IncomeOutcome.fxml")
                ), "Income/Outcome"
        ));

        outcomeStatisticButton.setOnAction(event -> tabAreaController.openTab(
                fxmlLoaderService.getLoader(
                        getClass().getResource("/view/graphs/OutcomePerTopCategory.fxml")
                ), "Outcome per top category"
        ));
    }

    @Override
    public void onSelected() {

    }
}
