package egwwinlogon.user;

import com.sun.jna.platform.win32.Netapi32Util.Group;
import java.awt.GridBagLayout;
//simport java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;




/**
 * dialog to request current task from user
 */
public class TaskReportUi extends JFrame {
    // login name
    protected String _login;
    // panel
    protected JPanel _ui_panel;
    // select box element for project-options
    protected JComboBox _ui_select;
    // informations to selected projects
    protected JPanel _ui_panel_info;
    protected JButton _ui_button_link;
    //protected JPanel _ui_panel_description;
    protected TextArea _ui_textarea_description;
    
    
    /**
     * create a TaskReportUi-Instance
     * 
     * @param login String
     */
    public TaskReportUi(String login) {
        this._login = login;
        String title = "Report - ";
        title = title.concat(this._login);
        this.setTitle(title);
        
        // create the ui elements
        this._ui_panel = new JPanel();
        this._ui_panel.setLayout(new BoxLayout(this._ui_panel, BoxLayout.Y_AXIS));
        
        // ComboBox
        JPanel ui_panel_select = new JPanel();
        ui_panel_select.setLayout(new GridBagLayout());
        
        ui_panel_select.add(new Label("Auftrag:"));
        this._ui_select = new JComboBox();
        this._addSelectionOptions();
        ui_panel_select.add(this._ui_select);
        this._ui_panel.add(ui_panel_select);
        
        // info
        this._ui_panel_info = new JPanel();
        this._ui_panel_info.setLayout(new GridBagLayout());
        this._ui_panel_info.add(new Label("Link:"));
        this._ui_panel_info.add(new JButton("click me"));
        
        this._ui_panel.add(this._ui_panel_info);
        //this._ui_panel.remove(this._ui_panel_info);
        this._ui_panel.add(new Label("Beschreibung:"));
        //this._ui_panel_description = new JPanel();
        //this._ui_panel_description.setLayout(new GridBagLayout());
        this._ui_textarea_description = new TextArea();
        //this._ui_panel_description.add(this._ui_textarea_description);
        //this._ui_panel.add(this._ui_panel_description);
        this._ui_panel.add(this._ui_textarea_description);
        
        this.getContentPane().add(this._ui_panel);
        //this.setMinimumSize(new Dimension(700,300));
        this.pack();
        this.setVisible(true);
    }
    
    
    private void _addSelectionOptions() {
        
        this._ui_select.addItem("transwrarp-Antrieb");
        this._ui_select.addItem("Ki-Gehirn-Implantat");
        this._ui_select.addItem("holografische Konstruktion");
    }
}
