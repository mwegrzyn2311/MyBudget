package controller.dialog;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
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

    protected void textFieldIntoAccountNumberField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d{4} )+(\\d{0,4})")) {
                newValue = newValue.replaceAll("[^\\d ]", "");
                textField.setText(newValue);
            }
            // Make space after each 4 digits
            if ((newValue.length()-4)%5 == 0) {
                newValue = newValue.concat(" ");
                textField.setText(newValue);
            }
        });
    }

    protected void textFieldIntoMoneyField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Just so that the field can be empty
            if(newValue.matches("[\\d]?")) {
                return;
            }
            // Format: 1456.12 || 145.1 || 1561. || 156 -> Or semicolon instead of dot
            if (!newValue.matches("(\\d)+([,.]\\d{0,2})?")) {
                // We simply abort user changes
                textField.setText(oldValue);
            }
        });
    }

    protected TreeItem<String> getTreeViewItem(TreeItem<String> root, String value) {
        for (TreeItem<String> item : root.getChildren()) {
            if(item !=null) {
                if (item.getValue().equals(value))
                    return item;

                for (TreeItem<String> child : item.getChildren()) {
                    if(child.getValue().equals(value)) {
                        return child;
                    }
                }
            }
        }
        return null;
    }
}
