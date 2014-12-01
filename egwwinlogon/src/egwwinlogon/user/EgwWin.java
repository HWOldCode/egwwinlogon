/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import com.jegroupware.egroupware.Egroupware;
//import com.jegroupware.egroupware.EgroupwareBrowser;
import com.jegroupware.egroupware.EgroupwareConfig;
import egwwinlogon.http.LogonHttpClient;
import java.util.HashMap;
import java.util.Map;

//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 *
 * @author swe
 */
public class EgwWin {



    public EgwWin() {


    }


    /**
     * main
     * @param args String[]
     */
    public static void main(String[] args) {
        try {
            /*LogonHttpClient client = new LogonHttpClient();
            String buffer = client.sendGET("http://127.0.0.1:8101/session?user=" +
                System.getProperty("user.name"));

            System.out.println(buffer);

            Egroupware egw = Egroupware.getInstance(new EgroupwareConfig(
                "http://dev.hw-softwareentwicklung.de/egroupware/",
                "default",
                "admin2",
                "test"
                ));

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
            //EgroupwareBrowser.open(egw);

            // tray icon
            Tray tray = new Tray(egw);

            // request user's current task through Dialog
            EgroupwareConfig egw_config = egw.getConfig();*/
            //TaskReportUi task_report_ui = new TaskReportUi(egw_config.getUser());
            TaskReportUi task_report_ui = new TaskReportUi("kwa");
            
            Map<String, String> map = new HashMap<String, String>();
            map.put("1", "transwarp Antrieb");
            map.put("2", "holografische Konstruktion");
            map.put("3", "ki-Gehirn-implanatat");
            task_report_ui.addSelectOptions(map);
        }
        catch( Exception e ) {
            System.out.println("Fehler:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("ende");
    }
}
