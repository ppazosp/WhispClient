package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import whisp.Logger;

public class LoginViewController {

    ClientApplication clientApp;

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField;

    public void initialize(ClientApplication clientApp){
        this.clientApp = clientApp;
    }

    @FXML
    public void login(){
        if (clientApp.login(usernameField.getText(), passwordField.getText())){
            try{
                clientApp.showMenuStage(usernameField.getText());
            }catch (Exception e){
                Logger.error("Cannot connect to mainApp");
            }
        }
    }

}
