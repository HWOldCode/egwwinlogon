package egwwinlogon.user;

import com.sun.jna.platform.win32.Netapi32Util.Group;
import java.awt.GridBagLayout;
//simport java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;




/**
 * dialog to request current task from user
 */
public class TaskReportUi extends JFrame implements ActionListener {
    // login name
    protected String _login;
    // selection options map
    protected Map<String, String> _options;
    // panel
    protected JPanel _ui_panel;
    // select box element for project-options
    protected JComboBox _ui_select;
    // informations to selected projects
    protected JPanel _ui_panel_info;
    protected JButton _ui_button_link;
    protected TextArea _ui_textarea_description;
    // notes by user
    protected TextArea _ui_textarea_notes;
    // terminate dialog
    protected JPanel _ui_panel_terminate;
    protected JButton _ui_button_submit;
    protected JButton _ui_button_cancel;
    
    
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
        
        ui_panel_select.add(this._ui_select);
        this._ui_panel.add(ui_panel_select);
        
        // dialog termination button
        this._ui_panel_terminate = new JPanel();
        this._ui_panel_terminate.setLayout(new GridBagLayout());
        this._ui_button_cancel = new JButton("Rechner sperren");
        this._ui_panel_terminate.add(this._ui_button_cancel);
        this._ui_panel.add(this._ui_panel_terminate);
        
        
        //this._addSelectionOptions(); // test
        
        this.getContentPane().add(this._ui_panel);
        //this.setMinimumSize(new Dimension(700,300));
        this.pack();
        this.setVisible(true);
    }
    
    
    private void _setSelected() {
        // the main panel only contains the combo box with project-list 
        // & cancel button before first selection remaining ui-components have 
        // to be inserted now
        if(this._ui_panel.getComponentCount() == 2) {
            this._ui_select.removeItemAt(0);
            this._ui_panel.remove(this._ui_panel_terminate);
            this._ui_panel_terminate.remove(this._ui_button_cancel);
            // hyperlink-link
            this._ui_panel_info = new JPanel();
            this._ui_panel_info.setLayout(new GridBagLayout());
            this._ui_panel_info.add(new Label("Link:"));
            this._ui_button_link = new JButton();
            this._ui_panel_info.add(this._ui_button_link);
            this._ui_panel.add(this._ui_panel_info);
            // description
            this._ui_panel.add(new Label("Beschreibung:"));
            this._ui_textarea_description = new TextArea();
            this._ui_textarea_description.setEnabled(false);
            this._ui_panel.add(this._ui_textarea_description);
            // user notes
            this._ui_panel.add(new Label("Notizen:"));
            this._ui_textarea_notes = new TextArea();
            this._ui_panel.add(this._ui_textarea_notes);
            // dialog termination buttons
            this._ui_button_submit = new JButton("Report abschicken");
            this._ui_panel_terminate.add(this._ui_button_submit);
            this._ui_panel_terminate.add(this._ui_button_cancel);
            this._ui_panel.add(this._ui_panel_terminate);
        }
        
        this._ui_button_link.setText("neuer text 1");
        this._ui_textarea_description.setText("Ich bin die Projectbeschreibung...");
        //this.repaint();
        this.pack();
    }
    
    
    private void _addSelectionOptions() {
        this._ui_select.addItem("Bitte auswählen");
        this._ui_select.addItem("transwrarp-Antrieb");
        this._ui_select.addItem("Ki-Gehirn-Implantat");
        this._ui_select.addItem("holografische Konstruktion");
        this._ui_select.addActionListener(this);
    }
    
    
    /**
     * fill options to combo box
     * 
     * @param options Map<String, String>
     */
    public void addSelectOptions(Map<String, String> options) {
        this._ui_select.removeAllItems();
        
        if(!options.isEmpty()) {
            this._options = options;
            
            if(this._options.size() > 1) {
                this._ui_select.addItem("Bitte auswählen");
            }
       
            Set set = this._options.entrySet();
            Iterator iterator = set.iterator();

            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                String option = (String) entry.getValue();
                this._ui_select.addItem(option);
            }

            this._ui_select.addActionListener(this);
            this.pack();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println(e.getSource());
        this._setSelected();
    }
}
