package egwwinlogon.user;

import javax.swing.JComboBox;
import javax.swing.JFrame;



/**
 * dialog to request current task from user
 */
public class TaskReportUi extends JFrame {
    // login name
    protected String _login;
    // select box element for project-options
    protected JComboBox _ui_select;
    
    
    /**
     * create a TaskReportUi-Instance
     */
    public TaskReportUi(String login) {
        this._login = login;
        String title = "Report - ";
        title = title.concat(this._login);
        this.setTitle(title);
        
        // create the ui elements
        this._ui_select = new JComboBox();
        this.add(this._ui_select);
    }
    
}
