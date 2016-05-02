/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import egwwinlogon.service.EgroupwarePGina;
import egwwinlogon.service.EgwWinLogonUltis;
import egwwinlogon.winapi.PInvokes;
import egwwinlogon.winapi.PInvokes.CredentialData;
import javax.swing.JOptionPane;

/**
 * EgwWinPromptCredentials
 * @author Stefan Werfling
 */
public class EgwWinPromptCredentials {
	
	/**
     * main
     * @param args String[]
     */
    public static void main(String[] args) {
		boolean _stop	= false;
		int maxround	= 3;
		int countround	= 0;
		
		while( !_stop ) {
			try {
				CredentialData data = PInvokes.getCredentials("EGroupware", 
					"Session ist abgelaufen, bitte loggen Sie sich erneut mit Ihren EGroupware Daten ein.");

				String curUser = EgwWinLogonUltis.getCurrentSystemUser();
				
				if( EgroupwarePGina.isUseEmulator() ) {
					curUser = data.username.trim();
				}

				if( data.username.trim().equals(curUser) ) {
					EgwWinLogonClient client = new EgwWinLogonClient();

					String url = null;
					
					if( EgroupwarePGina.isUseEmulator() ) {
						url = "http://localhost:8109/";
					}
					
					_stop = client.setRelogin(curUser, data.password.trim(), url);
				}
				else {
					throw new Exception("Benutzername stimmt nicht mit dem Systembenutzer überein!");
				}
			}
			catch( Exception e ) {
				 JOptionPane.showMessageDialog(null, e.getMessage());
			}
			
			countround++;
			
			if( maxround <= countround ) {
				break;
			}
		}
	}
}
