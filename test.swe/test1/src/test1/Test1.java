/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test1;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import winapi.io.PipeProcess;

/**
 *
 * @author swe
 */
public class Test1 extends Thread {

    /**
     * PipeProcess
     */
    protected PipeProcess _pipeServer = null;

    protected boolean _stop = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Test1 tmp = new Test1();
        tmp.start();


    }

    public void run() {
        System.out.println("Start Test1");

        if( this._pipeServer == null ) {
            this._pipeServer = new PipeProcess("egroupware", "reconnect"/*, "out"*/);
        }

        while( !this._stop ) {
            try {
                this._pipeServer.getOutputStream().write(
                        new String("SessionLogon").getBytes());
                Thread.sleep(1000);

                System.out.println("Output:");
            } catch (IOException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
            }

            /*byte[] buf = new byte[8192];
            int size;

            try {
                size = this._pipeServer.getInputStream().read(buf);

                if( size > 0 ) {
                    System.out.println("Input:");
                    System.out.println(new String(buf));
                }
            } catch (IOException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }

        System.out.println("End Test2");
    }
}
