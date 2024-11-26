package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import whisp.client.ClientApplication;
import whisp.utils.Logger;

public class ChangePasswordViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    // *******************************************************************************************

    ClientApplication clientApp;

    @FXML
    Label errorLabel;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField oldPasswordField;
    @FXML
    PasswordField newPasswordField;
    @FXML
    PasswordField repeatPasswordField;



    //*******************************************************************************************
    //* INITIALIZERS
    // *******************************************************************************************

    /**
     * Inicializa la escena de cambio de contraseña.
     *
     * @param clientApp referencia a la clase princial de flujo de ejecución
     */
    public void initialize(ClientApplication clientApp){
        this.clientApp = clientApp;
    }



    //*******************************************************************************************
    //* FXML METHODS
    // *******************************************************************************************

    /**
     * Función lanzada por {@code FXML}. Obtiene los datos introducidos por el usuario para cambiar
     * su contraseña.
     *
     * <p>
     * Antes de comunicarse con la clase principal para conectarse con el servidor
     * comprueba que todos los campos de la escena han sido correctamente rellenados.
     * </p>
     */
    @FXML
    public void changePassword(){
        Logger.info("Change password button pressed, trying to change user password...");
        if(usernameField.getText().isEmpty() ||
                oldPasswordField.getText().isEmpty() ||
                newPasswordField.getText().isEmpty() ||
                repeatPasswordField.getText().isEmpty()){

            Logger.info("At least one field was empty, showing error label...");
            errorLabel.setText("There are empty fields!");
            errorLabel.setVisible(true);
            return;
        }

        if(!newPasswordField.getText().equals(repeatPasswordField.getText())){

            Logger.info("Passwords dont match, showing error label...");
            errorLabel.setText("New passwords must match");
            errorLabel.setVisible(true);
            return;
        }

        Logger.info("Checking user old credentials...");
        if (clientApp.login(usernameField.getText(), oldPasswordField.getText())){

            Logger.info("Credentiales were correct, showing AuthChanges scene...");
            clientApp.showAuthChangesScene(usernameField.getText(), newPasswordField.getText());

        }else{
            Logger.info("Incorect credentials, showing error label...");
            errorLabel.setText("Incorrect Credentials");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Función lanzada por {@code FXML}. Carga la escena de login
     */
    @FXML
    public void back(){
        Logger.info("Back button pressed, going back t login scene...");
        clientApp.showLoginScene();
    }
}
