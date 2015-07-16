/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

/**
 * EgwWinLogonException
 * @author Stefan Werfling
 */
public class EgwWinLogonException extends Exception {
    
    public static final int EC_SERVER_CONNECTION    = 1;
    public static final int EC_LOGIN_FALSE          = 2;
    
    
    public static final String MSG_SERVER_CONNECTION    = "Server connection can`t open";
    public static final String MSG_UNKNOW_ERROR         = "Unknow Error";
    public static final String MSG_LOGIN_FALSE          = "Login return false";
    
    /**
     * error code
     */
    protected int _ec = 0;
    
    /**
     * constructor
     * @param ec 
     */
    public EgwWinLogonException(int ec) {
        super(EgwWinLogonException.getMessage(ec));
        this._ec = ec;
    }
    
    /**
     * getErrorCode
     * @return 
     */
    public int getErrorCode() {
        return this._ec;
    }
    
    /**
     * getMessage
     * @param ec
     * @return 
     */
    static public String getMessage(int ec) {
        String msg = EgwWinLogonException.MSG_UNKNOW_ERROR;
        
        switch( ec ) {
            case EgwWinLogonException.EC_SERVER_CONNECTION:
                msg = EgwWinLogonException.MSG_SERVER_CONNECTION;
                break;
                
            case EgwWinLogonException.EC_LOGIN_FALSE:
                msg = EgwWinLogonException.MSG_LOGIN_FALSE;
                break;
        }
        
        return msg;
    }
}
