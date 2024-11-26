package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import whisp.client.ClientApplication;
import whisp.utils.Logger;


public class RegisterViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    //*******************************************************************************************

    ClientApplication clientApp;

    @FXML
    Label errorLabel;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatPasswordField;



    //*******************************************************************************************
    //* INITIALIZERS
    //*******************************************************************************************

    public void initialize(ClientApplication clientApp){
        this.clientApp = clientApp;

        Logger.info("Register view initialized correctly");
    }



    //*******************************************************************************************
    //* FXML METHODS
    //*******************************************************************************************

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de registrar.
     *
     * <p>
     *      Intenta registrar al usuario conectándose a la clase principal para llegar al servidor.
     *      Comprueba primero si el nombre de usuario está disponible. Si consigue registrarlo llama
     *      a {@code back}, si no muestra una label de error.
     * </p>
     *
     * <p>
     *      Antes de comunicarse comprueba que los campos
     *      de la escena han sido correctamente rellenados
     * </p>
     */
    @FXML
    public void register(){
        Logger.info("Register button pressed, trying to register user...");

        if(usernameField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                repeatPasswordField.getText().isEmpty()){

            Logger.info("At least one field was empty, showing error label...");
            errorLabel.setText("There are empty fields!");
            errorLabel.setVisible(true);
            return;
        }

        if(!passwordField.getText().equals(repeatPasswordField.getText())){
            Logger.info("Passwords must much, showing error label...");
            errorLabel.setText("Passwords must match");
            errorLabel.setVisible(true);
            return;
        }

        Logger.info("Checking username availability...");
        if(clientApp.checkUsernameAvailability(usernameField.getText())) {
            Logger.info("Username available, proceeding to register user...");
            clientApp.register(usernameField.getText(), passwordField.getText());
        }else{
            Logger.info("Username not available, showing error label...");
            errorLabel.setText("Username is taken");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de retroceder.
     *
     * <p>
     *     Carga la escena de login
     * </p>
     */
    @FXML
    public void back(){
        clientApp.showLoginScene();
    }
}
