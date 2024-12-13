package whisp.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import whisp.client.ClientApplication;
import whisp.utils.Logger;

public class AuthViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    // *******************************************************************************************

    int mode;

    ClientApplication clientApp;
    String username;
    String oldPassword;
    String newPassword;

    @FXML
    Label errorLabel;
    @FXML
    TextField digit1;
    @FXML
    TextField digit2;
    @FXML
    TextField digit3;
    @FXML
    TextField digit4;
    @FXML
    TextField digit5;
    @FXML
    TextField digit6;



    //*******************************************************************************************
    //* INITIALIZERS
    // *******************************************************************************************

    /**
     * Inicializa el controlador de la escena de validación en el registro.
     *
     * <p>
     * Hace que los fields para dígitos de validación permitan solo un int
     * por field y salten al siguiente si están llenos.
     *</p>
     *
     * @param username nombre del usuario que se quiera validar
     * @param oldPassword contraseña vieja del usuario si se llama para la validación de registro, sino null
     * @param newPassword contraseña vieja del usuario si se llama para la validación de registro, sino null
     * @param clientApp referencia a la clase princial de flujo de ejecución
     */
    public void initialize(String username, String oldPassword, String newPassword, ClientApplication clientApp, int mode){

        this.clientApp = clientApp;
        this.username = username;
        this.mode = mode;

        if(mode == 1) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        TextField[] code = {digit1, digit2, digit3, digit4, digit5, digit6};

        for (int i = 0; i < code.length; i++) {
            TextField currentField = code[i];
            TextField nextField = (i < code.length - 1) ? code[i + 1] : null;

            currentField.textProperty().addListener((_, oldValue, newValue) -> {
                if (!newValue.matches("\\d?")) {
                    currentField.setText(oldValue);
                } else if (!newValue.isEmpty() && nextField != null) {
                    nextField.requestFocus();
                }
            });
        }

        Logger.info("AuthView initialized correctly");
    }



    //*******************************************************************************************
    //* FXML METHODS
    // *******************************************************************************************

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de retroceder.
     * <p>
     *     Carga la escena de login
     * </p>
     */
    @FXML
    public void back(){
        Logger.info("Back button pressed, going back to Login scene...");
        clientApp.showLoginScene();
    }

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de validar.
     *
     * <p>
     *      Intenta validar al usuario conectándose a la clase principal para llegar al servidor.
     *      Si lo consigue llama a {@code back}, si no muestra una label de error.
     * </p>
     *
     * <p>
     *      Antes de comunicarse comprueba que los campos
     *      de la escena han sido correctamente rellenados
     * </p>
     */
    @FXML
    public void validate(){
        Logger.info("Validate button pressed, trying to validate user...");
        if(digit1.getText().isEmpty() ||
                digit2.getText().isEmpty() ||
                digit3.getText().isEmpty() ||
                digit4.getText().isEmpty() ||
                digit5.getText().isEmpty() ||
                digit6.getText().isEmpty()){
            Logger.info("At least one digit is empty, showing error label...");
            errorLabel.setVisible(true);
            errorLabel.setText("Blanks are not allowed");
            return;
        }

        String scode = digit1.getText() +
                digit2.getText() +
                digit3.getText() +
                digit4.getText() +
                digit5.getText() +
                digit6.getText();
        int code = Integer.parseInt(scode);
        Logger.info("Code " + code + " introduced, checking with server...");

        clientApp.showLoadingScene();
        new Thread(() -> {
            boolean isValid = clientApp.validate(username, code);

            Platform.runLater(() -> {
                if (isValid) {
                    Logger.info("Validation completed");

                    if (mode == 1) {
                        Logger.info("Changing password...");
                            clientApp.changePassword(username, oldPassword, newPassword);
                            Logger.info("Password changed successfully");
                            clientApp.showLoginScene();
                    }else {
                        clientApp.showMenuStage(username);
                    }
                } else {
                    clientApp.quitLoadingScene();
                    Logger.info("Validation failed, showing error label...");
                    errorLabel.setVisible(true);
                    errorLabel.setText("Invalid Code");
                }
            });
        }).start();
    }
}
