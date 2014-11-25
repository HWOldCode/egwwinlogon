/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import java.io.IOException;
import java.util.Scanner;

/**
 * Wmic
 * @author swe
 */
public class Wmic {

    /**
     * getBiosSerialNumber
     * @return
     * @throws IOException
     */
    static public String getBiosSerialNumber() throws IOException {
        Process process = Runtime.getRuntime().exec(
            new String[] {
                "wmic",
                "bios",
                "get",
                "serialnumber" });

        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        String property = sc.next();
        String serial = sc.next();

        return serial;
    }
}
