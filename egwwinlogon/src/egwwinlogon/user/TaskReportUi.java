package egwwinlogon.user;

import com.sun.jna.platform.win32.Netapi32Util.Group;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;



/**
 * dialog to request current task from user
 */
public class TaskReportUi extends JFrame {
    // login name
    protected String _login;
    // panel
    //protected JPanel _panel;
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
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        
        GroupLayout.SequentialGroup sequential_horizontal_group = layout.createSequentialGroup();
        GroupLayout.SequentialGroup sequential_vertical_group = layout.createSequentialGroup();
        //Group vertical_group = new Group();
        
        // ComboBox
        this._ui_select = new JComboBox();
        
        this.addSelectionOptions();
        //panel.add(this._ui_select);
        sequential_horizontal_group.addGroup(layout.createParallelGroup().addComponent(this._ui_select));
        layout.setHorizontalGroup(sequential_horizontal_group);
        sequential_vertical_group.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this._ui_select));     
        layout.setVerticalGroup(sequential_vertical_group);
        
        
        //this.getContentPane().add(panel);
        //this.setMinimumSize(new Dimension(700,300));
        //this.pack();
        this.setVisible(true);
    }
    
    
    public void addSelectionOptions() {
        
        this._ui_select.addItem("transwrarp-Antrieb");
        this._ui_select.addItem("Ki-Gehirn-Implantat");
        this._ui_select.addItem("holografische Konstruktion");
        
        
        
    }
    
}
