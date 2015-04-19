package checkers.main;

import checkers.gui.MainView;
import checkers.logic.Controller;
import checkers.logic.Logic; 
import checkers.model.Model;

/**
 * Start Checkers Game
 * 
 * @author Cristian Tardivo
 */
public class Start {    
    public static void main(String[] args){                
        Model model = new Model();
        Logic logic = new Logic(model);
        MainView view = new MainView();
        Controller controller = new Controller(logic,view);
        controller.init();
    }
}