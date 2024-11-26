package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class FriendViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    // *******************************************************************************************

    MenuViewController menuViewController;

    @FXML
    Label usernameLabel;
    @FXML
    HBox friendHbox;
    @FXML
    ImageView iconView;



    //*******************************************************************************************
    //* INITIALIZERS
    // *******************************************************************************************

    /**
     * Setter para el atributo {@code username}
     *
     * @param username nombre de usuario que mostrar
     */
    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    /**
     * Setter para el atributo {@code menuViewController}
     *
     * <p>
     *     Es la referencia a la clase principal de manejo de  la GUI
     * </p>
     */
    public void setMenuViewController(MenuViewController menuViewController) {
        this.menuViewController = menuViewController;
    }



    //*******************************************************************************************
    //* FXML METHODS
    // *******************************************************************************************

    /**
     * Función lanzada por {@code FXML} al pulsar un usuario de la lista de amigos.
     * <p>
     *     Actualiza la lista de amigos y el chat para poder mostrarlo por pantalla
     * </p>
     */
    @FXML
    public void loadChat()
    {
        menuViewController.showFriends();
        menuViewController.setLoadedChatUser(usernameLabel.getText());
        menuViewController.loadChat();
    }
}
