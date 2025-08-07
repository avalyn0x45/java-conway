// This code is in the public domain. Original author Avalyn Baldyga

package com.avalyn.CGOL;
import java.util.*;
import java.nio.charset.Charset;
import java.io.*;

public class App {
    public static boolean[][] cells;
    public static Integer seed;
    public static void main(String[] args) throws IOException {
        seed = Integer.parseInt(args[0]);

        System.out.print(ControlCodes.moveTo(5000,5000));
        System.out.print(ControlCodes.reqpos);
        
        byte[] response = new byte[16];
        Integer bytes_read = System.in.read(response);
        String response_str = new String(response, Charset.forName("ISO-8859-1"));
        
        System.out.print(ControlCodes.moveTo(0,0));

        String[] split = response_str.split(";");

        String x_str = split[0].substring(2);
        String y_str = split[1].substring(0,3);

        System.out.println(x_str+":"+y_str);
        
        Integer cols = Integer.parseInt(x_str);
        Integer rows = Integer.parseInt(y_str);

        cells = new boolean[cols][rows];
        randgen();        

        while (true) {
           try {
                Thread.sleep(1000 / Integer.parseInt(args[1]));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } 
            frame();
        }
    }
    static void randgen() {
        Random rand = new Random(seed);
        for (int i = 0; i < cells.length; i++) {
            for (int k = 0; k < cells[i].length; k++) {
                cells[i][k] = rand.nextBoolean();
            }
        }
    }
    static void frame() throws IOException {
        BufferedWriter buffer = new BufferedWriter(
            new OutputStreamWriter(System.out)
        );

        buffer.write(ControlCodes.clear);
        calculate();
        for (boolean[] row : cells) {
            buffer.write("\n");
            for(boolean cell: row) {
                if (cell) {
                    buffer.write("@");
                } else {
                    buffer.write(".");
                }
            }
        }
        buffer.flush();
        Random rand = new Random(seed);
    }
    static boolean shouldExist(Integer x, Integer y) {
        Integer active = 0;

        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0) continue;
            if (i >= cells.length) continue;
            for (int k = y - 1; k <= y + 1; k++) {
                if (k < 0) continue;
                if (k >= cells[i].length) continue;
                if (cells[i][k]) {
                    active++;
                }
            }
        }

        if (active == 3) {
            return true;
        } else if (active == 2) {
            return cells[x][y];
        }
        return false;
    }
    static void calculate() {
        for (int i = 0; i < cells.length; i++) {
            for (int k = 0; k < cells[i].length; k++) {
                cells[i][k] = shouldExist(i, k);
            }
        }
    }
}
