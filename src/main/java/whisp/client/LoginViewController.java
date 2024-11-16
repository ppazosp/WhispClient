package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import whisp.Logger;

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
        if (clientApp.login(usernameField.getText(), passwordField.getText())){
            try{
                clientApp.showMenuStage(usernameField.getText());
                System.out.println("name");
            }catch (Exception e){
                Logger.error("Cannot connect to mainApp");
            }
        }else{
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
