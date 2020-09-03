package com.tmallspringboot.demo.util;

import lombok.val;

import javax.swing.*;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

public class PortUtil {
    public static boolean testPort(int port) {
        try {
            val ss = new ServerSocket(port);
            ss.close();
            return false;
        } catch (BindException e) {
            return true;
        } catch (IOException e) {
            return true;
        }
    }

    public static void checkPort(int port, String server, boolean shutdown) {
        if (!testPort(port)) {
            if (shutdown) {
                val msg = String.format("the server %s isn't run on port:%d%n", server, port);
                JOptionPane.showMessageDialog(null, msg);
                System.exit(1);
            } else {
                val msg = String.format("the server %s doesn't run on port: %s, continue anyway?%n",
                        server, port);
                if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null, msg)) {
                    System.exit(1);
                }
            }
        }
    }
}
