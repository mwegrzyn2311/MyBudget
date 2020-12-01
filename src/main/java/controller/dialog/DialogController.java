package controller.dialog;

import javafx.stage.Stage;

public abstract class DialogController {
    protected boolean approved = false;
    protected Stage stage;

    public boolean isApproved() {
        return approved;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


}
