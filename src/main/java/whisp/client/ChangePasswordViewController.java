package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import whisp.Logger;

import java.io.IOException;

public class ChangePasswordViewController {

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

    public void initialize(ClientApplication clientApp){
        this.clientApp = clientApp;
    }

    @FXML
    public void changePassword(){
        if(usernameField.getText().isEmpty() ||
                oldPasswordField.getText().isEmpty() ||
                newPasswordField.getText().isEmpty() ||
                repeatPasswordField.getText().isEmpty()){
            errorLabel.setText("There are empty fields!");
            errorLabel.setVisible(true);
            return;
        }

        if(!newPasswordField.getText().equals(repeatPasswordField.getText())){
            errorLabel.setText("New passwords must match");
            errorLabel.setVisible(true);
            return;
        }

        if (clientApp.login(usernameField.getText(), oldPasswordField.getText())){

            try {
                clientApp.showAuthChangesScene(usernameField.getText(), newPasswordField.getText());
            } catch (IOException e) {
                Logger.error("Cannot load Auth Changes Scene");
            }

        }else{
            errorLabel.setText("Incorrect Credentials");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void back(){
        try {
            clientApp.showLoginScene();
        }catch (Exception e){
            Logger.error("Cannot load Login Scene");
        }
    }
}
