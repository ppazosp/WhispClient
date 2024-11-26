package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import whisp.utils.Logger;

public class RequestViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    //*******************************************************************************************

    MenuViewController menuViewController;

    @FXML
    Label usernameLabel;



    //*******************************************************************************************
    //* INITIALIZERS
    //*******************************************************************************************

    /**
     * Setter para el texto del label {@code username}
     *
     * @param username nombre de usuario de la solicitud a mostrar
     */
    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    /**
     * Setter para la referencia a la clase principal de GUI
     *
     * @param menuViewController nueva referencia a la clase principal de GUI
     */
    public void setMenuViewController(MenuViewController menuViewController) {
        this.menuViewController = menuViewController;
    }



    //*******************************************************************************************
    //* FXML METHODS
    //*******************************************************************************************

    @FXML
    public void acceptResquest(){
        Logger.info("Accept resquest button pressed, trying to add friend...");
        menuViewController.addFriend(usernameLabel.getText());
    }

    @FXML
    public void cancelResquest(){
        Logger.info("Cancel resquest button pressed, informing server... ");
        menuViewController.cancelRequest(usernameLabel.getText());
    }
}
