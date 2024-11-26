package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import whisp.client.ClientApplication;
import whisp.utils.Logger;

public class LoginViewController {

    ClientApplication clientApp;

    @FXML
    Label errorLabel;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;

    public void initialize(ClientApplication clientApp){
        this.clientApp = clientApp;
    }

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

    @FXML
    public void changePassword(){
        try {
            clientApp.showChangePasswordScene();
        }catch (Exception e){
            Logger.error("Cannot load Change Password scene");
        }
    }

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
