/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.protocol;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import egwwinlogon.service.EgwWinLogonUltis;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * EgwWinLogonProtocol
 * 
 * @author Stefan Werfling
 */
public class EgwWinLogonProtocol {
   
    /**
     * setup
     */
    public static void setup() {
        try {
            Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, "", "egwwinlogon");
            Advapi32Util.registrySetStringValue(
                WinReg.HKEY_CLASSES_ROOT,
                "egwwinlogon",
                "URL Protocol",
                "");

            Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, "egwwinlogon", "shell");
            Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, "egwwinlogon\\shell", "open");
            Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, "egwwinlogon\\shell\\open", "command");

            Advapi32Util.registrySetStringValue(
                WinReg.HKEY_CLASSES_ROOT,
                "egwwinlogon\\shell\\open\\command",
                "",
                EgwWinLogonUltis.getProtocolAppCmd() + " \"--action=%1\"");
        }
        catch( Exception e ) {
            // none
        }
    }
    
    /**
     * main
     * @param args String[]
     */
    public static void main(String[] args) {
        if( (args.length > 0) && (args[0] != "") ) {
            for( String s: args ) {
                String[] tmp = s.split("=");
                
                if( tmp.length == 2 ) {
                    if( tmp[0].contains("--action") ) {
                        // init
                        new EgwWinLogonProtocol(tmp[1]);
                    }
                }
            }
        }
    }

    /**
     * EgwWinLogonProtocol
     * @param arg 
     */
    private EgwWinLogonProtocol(String action) {
        String cleanAction = action.replaceFirst("egwwinlogon://", "");
        
        if( cleanAction.endsWith("/") ) {
            cleanAction = cleanAction.substring(0, cleanAction.length() - 1);
        }
        
        String type = "";
        String[] tmp = cleanAction.split(">", 2);
        
        String source = "";
        
        if( tmp.length == 2 ) {
            type = tmp[0].replace("<", "");
            source = tmp[1];
        }
        
        if( "dialog".equals(type) ) {
            JOptionPane.showMessageDialog(null, source);
        }
        else if( "explorer".equals(type) ) {
            try {
                Runtime.getRuntime().exec("Explorer /e," + source);
            } catch (IOException ex) {
                Logger.getLogger(EgwWinLogonProtocol.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if( "explorer-select".equals(type) ) {
            try {
                Runtime.getRuntime().exec("Explorer /e,/select," + source);
            } catch (IOException ex) {
                Logger.getLogger(EgwWinLogonProtocol.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}