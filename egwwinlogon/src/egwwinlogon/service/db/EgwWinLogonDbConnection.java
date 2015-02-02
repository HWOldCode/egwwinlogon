/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service.db;

import egwwinlogon.service.EgwWinLogon;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * EgwWinLogonDbConnection
 * @author Stefan Werfling
 */
public class EgwWinLogonDbConnection {

    protected Connection _cn = null;
    protected Statement _st = null;
    protected ResultSet _res = null;
    protected Boolean _eof = true;
    protected ResultSetMetaData _rsmd = null;
    protected int _fieldCount = 0;

    /**
     * EgwWinLogonDbConnection
     */
    public EgwWinLogonDbConnection(String server, String db) {
        try {
            Class.forName("org.hsqldb.jdbcDriver");

            this._cn = DriverManager.getConnection("jdbc:hsqldb:hsql://" + server + "/" + db, "sa", ""); // can through sql exception
            this._st = this._cn.createStatement();
        }
        catch( Exception ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean query(String Query) {
        try {
            this._res = this._st.executeQuery(Query);
            this._rsmd = this._res.getMetaData();
            this._fieldCount = this._rsmd.getColumnCount();
            this.next();
            return true;
        }
        catch( SQLException ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void next() {
        try {
            this._eof = !this._res.next();
        }
        catch( SQLException ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isEOF() {
        return this._eof;
    }

    public boolean update(String Update) {
        try {
            this._st.executeUpdate(Update);
            return true;
        }
        catch( SQLException ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public String getFieldNameByID(int i) {
        String columname = "";

        try {
            if((i <= this._fieldCount) && (i > 0)) {
                columname = this._rsmd.getColumnName(i);
            }
        }
        catch (SQLException ex) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
        }

        return columname;
    }

    public String getValueByFieldName(String fieldname) {
        int index = 0;
        String back = "";

        for(int i = 1; i <= this._fieldCount; i++) {
            String name = this.getFieldNameByID(i);

            if( fieldname.compareTo(name) == 0 ) {
                index = i;
            }
        }

        try {
            back = this._res.getString(index);
        }
        catch (SQLException ex) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
        }

        return back;
    }

    public boolean insert(String query) {
        try {
            this._st.executeUpdate(query);
            return true;
        }
        catch( SQLException ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
