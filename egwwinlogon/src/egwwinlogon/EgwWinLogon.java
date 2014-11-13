/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon;

/**
 * EgwWinLogon
 * @author Stefan Werfling
 */
public class EgwWinLogon {

	static protected  String _error = "";

	/**
	 * main
	 * @param args String[]
	 */
	public static void main(String[] args) {

	}

	public int egwAuthenticateUser(String username, String password) {
		EgwWinLogon._error = "JUsername: " + username + " JPassword; " + password;

		if( (username.compareTo("test") == 0) && (password.compareTo("test") == 0) ) {
			return 1;
		}
        else {
            EgwWinLogon._error = "Egroupware User not found!";
        }

		return 0;
	}

	public String egwGetError() {
        String terror = EgwWinLogon._error;

        EgwWinLogon._error = "";

		return terror;
	}

	public int egwIsError() {
        if( !"".equals(EgwWinLogon._error) ) {
            return 1;
        }

		return 0;
	}

	public String[] egwGetConfig() {
		return null;
	}

	public void egwSetConfig(String[] config) {

	}

	public void egwStarting() {

	}

	public void egwStopping() {

	}

	public String[] egwGetLogs() {
		return null;
	}

	public String egwGetDescription() {
		return "ich bin EGW Plugin aus Java!";
	}

	public String egwGetName() {
		return "Egroupware";
	}

	public String egwGetVersion() {
		return "14.1";
	}

	public int egwAuthenticatedUserGateway() {
		return 0;
	}

	public int egwSessionChange() {
		return 0;
	}
}
