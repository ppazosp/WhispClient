package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorViewController {

    @FXML
    Label errorLabel;

    @FXML
    Button closeButton;

    public void setErrorMessage(String error){
        errorLabel.setText(error);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
