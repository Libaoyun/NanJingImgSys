package com.rdexpense.manager.util;

import java.io.*;

public class WaitFileUnlock {
    public void waitFileUnlock(File f) {
        RandomAccessFile raf = null;
        while (raf == null) {
            try {
                raf = new RandomAccessFile(f, "r");
            } catch (FileNotFoundException e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ;//x.printStackTrace();
                }
            }
        }

        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
