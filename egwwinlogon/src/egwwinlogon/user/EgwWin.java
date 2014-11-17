/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;
import com.jegroupware.egroupware.EgroupwareConfig;

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
            Egroupware egw = Egroupware.getInstance(new EgroupwareConfig(
            "http://dev.hw-softwareentwicklung.de/egroupware/",
            "default",
            "admin2",
            "test"
            ));

            try {
                System.out.println(egw.getLoginDomains());
                egw.login();
                System.out.println(egw.getSession().getLastLoginId());
                EgroupwareBrowser.open(egw);
            }
            catch( Exception e ) {
                System.out.println(e.getMessage());
            }

            System.out.println("test");
        }
}
