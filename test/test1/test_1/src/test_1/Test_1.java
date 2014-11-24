/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_1;



import winapi.io.PipeProcess;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author test
 */
public class Test_1 extends Thread {
    
    static protected PipeProcess _pipe_1 = null;
    static protected int _counter = 0;
    static protected OutputStream _out;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if(Test_1._pipe_1 == null) {
            try {
                Test_1._pipe_1 = new PipeProcess("test_pipe");
                Test_1._out = Test_1._pipe_1.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(Test_1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Test_1 t1 = new Test_1();
        t1.start();
    }
    
    
    
    
    
    public void run() {
        
        try {
            while(Test_1._counter < 177) {
                System.out.print("test 1 : ");
                System.out.println(Test_1._counter);
                Test_1._out.write(Test_1._counter);
                Test_1._counter++;
                Thread.sleep(1000);
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
