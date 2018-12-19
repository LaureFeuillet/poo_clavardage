
public class PseudoView extends javax.swing.JFrame {

    /* Attributes */
    protected Controller controller;
    protected String pseudo;
    
    /* Constructors */
    public PseudoView(Controller controller) {
        this.controller = controller;
    }
    
    // controller.setPseudo(pseudo);
    
    public void printMsgError(){
        //error.setText("Error, this pseudo is already used !");
        //newPseudo.setText("");
    }
}
