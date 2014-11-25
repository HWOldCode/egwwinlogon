/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;
import com.jegroupware.egroupware.EgroupwareConfig;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author swe
 */
public class EgwWin {
	/**
	 * main
	 * @param args String[]
	 */
	public static void main(String[] args) {
        try {
            Egroupware egw = Egroupware.getInstance(new EgroupwareConfig(
                    "http://dev.hw-softwareentwicklung.de/egroupware/",
                    "default",
                    "admin2",
                    "test"
            ));

            try {
                System.out.println("getLoginDomains()");
                egw.getLoginDomains();

                //System.out.println(egw.getLoginDomains());
                System.out.println("login()");
                egw.login();

                if( egw.isLogin() ) {
                    System.out.println("isLogin");
                }
                else {
                    System.out.println("not isLogin");
                }
                //System.out.println(egw.getSession().getLastLoginId());
                EgroupwareBrowser.open(egw);
            }
            catch( Exception e ) {
                System.out.println("Fehler:");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            System.out.println("ende");

            PipeProcess tmp = new PipeProcess("egroupware");
            tmp.getOutputStream().write(new String("userclient").getBytes());
        }
        catch( IOException ex ) {
            Logger.getLogger(EgwWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
