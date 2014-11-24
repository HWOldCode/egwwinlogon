/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_2;

import winapi.io.PipeProcess;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author test
 */
public class Test_2 extends Thread {
    
    static protected PipeProcess _pipe_2 = null;
    static protected int _counter = 0;
    static protected InputStream _in; 

    protected boolean _stop = false;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            if(Test_2._pipe_2 == null) {
                try {
                    Test_2._pipe_2 = new PipeProcess("test_pipe");
                    Test_2._in = Test_2._pipe_2.getInputStream();
                } catch (IOException ex) {
                    Logger.getLogger(Test_2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            Test_2 t2 = new Test_2();
            t2.start();
            
            Thread.sleep(1000000000);
            //t2._stop = true;
            System.out.println("stop");
        } catch (InterruptedException ex) {
            Logger.getLogger(Test_2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public void run() {
       
        try {
            while( !this._stop ) {
                
                //int i = Test_2._counter++;
                int i = Test_2._in.read();
                
                if(i != -1) {
                    char c = (char) i;
                    
                    System.out.print(i);
                    System.out.print(" : ");
                    
                    System.out.println(c);
                }
                else {
                    System.out.print("test 2 : ?");
                }
                
                
                
                //Test_2._counter++;
                Thread.sleep(1000);
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
