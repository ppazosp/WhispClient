package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    // *******************************************************************************************

    @FXML
    Label errorLabel;
    @FXML
    Button closeButton;



    //*******************************************************************************************
    //* INITIALIZERS
    // *******************************************************************************************

    /**
     * Establece un mensaje de error en {@link ErrorViewController#errorLabel}.
     *
     * @param error el mensaje de error que se mostrará en la etiqueta de error.
     */
    public void setErrorMessage(String error){
        errorLabel.setText(error);
    }



    //*******************************************************************************************
    //* FXML METHODS
    // *******************************************************************************************

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
