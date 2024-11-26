package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import whisp.client.ClientApplication;
import whisp.utils.Logger;

public class LoginViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    // *******************************************************************************************

    ClientApplication clientApp;

    @FXML
    Label errorLabel;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;



    //*******************************************************************************************
    //* INITIALIZERS
    // *******************************************************************************************

    /**
     * Inicializa el controlador de la escena de Login.
     *
     * @param clientApp referencia a la clase princial de flujo de ejecución
     */
    public void initialize(ClientApplication clientApp){
        this.clientApp = clientApp;

        Logger.info("Login view initialized correctly");

    }



    //*******************************************************************************************
    //* FXML METHODS
    // *******************************************************************************************

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de Login.
     *
     * <p>
     *     Recoge usuario y contraseña de los campos de la escena y se los pasa a la clase principal
     *     para conectarse al servidor y comprobar si las credenciales de inicio de sesión son válidas.
     *     Si lo son muestra la escena de validación, si no muestra un label de error.
     * </p>
     */
    @FXML
    public void login(){
        Logger.info("Login button pressed, trying to login user...");
        if (clientApp.login(usernameField.getText(), passwordField.getText())){
            Logger.info("Username and Password are correct, proceeding to Authentification window");
            clientApp.showAuthScene(usernameField.getText());
        }else{
            Logger.info("Username or Password incorrect, showing error label...");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de cambiar contraseña.
     * <p>
     *     Muestra la escena de cambio de contraseña
     * </p>
     */
    @FXML
    public void changePassword(){
        try {
            clientApp.showChangePasswordScene();
        }catch (Exception e){
            Logger.error("Cannot load Change Password scene");
        }
    }

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de crear cuenta.
     * <p>
     *     Muestra la escena de registro
     * </p>
     */
    @FXML
    public void register(){
        try {
            clientApp.showRegisterScene();
        }catch (Exception e){
            Logger.error("Cannot load Register Scene ");
            e.printStackTrace();
        }
    }

}
