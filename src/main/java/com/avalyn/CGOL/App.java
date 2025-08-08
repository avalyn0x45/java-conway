// This code is in the public domain. Original author Avalyn Baldyga

package com.avalyn.CGOL;
import java.util.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.regex.Pattern;

public class App {
    public static Boolean[][] cells;
    public static Integer seed;
    public static void main(String[] args) throws IOException {
        Pattern numeric = Pattern.compile("-?\\d+(\\.\\d+)?");
        
        Boolean is_numeric = numeric.matcher(args[0]).matches();

        if (is_numeric) { 
            seed = Integer.parseInt(args[0]);

            System.out.print(ControlCodes.moveTo(5000,5000));
            System.out.print(ControlCodes.reqpos);
            
            byte[] response = new byte[16];
            Integer bytes_read = System.in.read(response);
            String response_str = new String(response, Charset.forName("ISO-8859-1"));
            String[] split = response_str.split(";");

            String x_str = split[0].substring(2);
            String y_str = split[1].substring(0,3);

            System.out.println(x_str+":"+y_str);
            
            Integer cols = Integer.parseInt(x_str);
            Integer rows = Integer.parseInt(y_str);

            cells = new Boolean[cols][rows];
            randgen();
        } else {
            ArrayList<ArrayList<Boolean>> lines = new ArrayList();
            File board = new File(args[0]);
            Scanner reader = new Scanner(board);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                ArrayList<Boolean> cols = new ArrayList();
                for (char c : line.toCharArray()) {
                    cols.add(c == '@');
                }
                lines.add(cols);
            }
            reader.close();

            cells = new Boolean[lines.size()][lines.get(0).size()];
            for (int i = 0; i < lines.size(); i++) {
                ArrayList<Boolean> row = lines.get(i);
                cells[i] = row.toArray(new Boolean[row.size()]);
            }
        }

        System.out.print(ControlCodes.moveTo(0,0)); 

        while (true) {
            frame();
            calculate();
            try {
                Thread.sleep(Double.valueOf(1000 /  Double.parseDouble(args[1])).longValue());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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
        for (Boolean[] row : cells) {
            buffer.write("\n");
            for(Boolean cell: row) {
                if (cell) {
                    buffer.write("@");
                } else {
                    buffer.write(".");
                }
            }
        }
        buffer.flush();
    }
    static Boolean shouldExist(int x, int y) {
        int active = 0;

        // WHY ARE YOU MISBEHAVING
        for (int i = x - 1; i < x + 2; i++) {
            if (i < 0) continue;
            //for (Boolean bool : Arrays.copyOfRange(cells[i], Math.max(y-1, 0), Math.min(y+1, cells[i].length - 1))) {
            //    System.err.print(bool.booleanValue() + ", ");
            //}
            //System.err.print("\n");
            if (i >= cells.length) continue;
            for (int k = y - 1; k < y + 2; k++) {
                if (k < 0) continue;
                if (k >= cells[i].length) continue;
                if (i == x && k == y) continue;
                if (cells[i][k]) {
                    active++;
                }
            }
        } 

        //System.err.println("Active: " + active + "\n");

        if (active == 3) {
            return true;
        } else if (active == 2) {
            return cells[x][y];
        }

        return false;
    }
    static void calculate() {
        Boolean[][] newcells = new Boolean[cells.length][cells[0].length];
        for (int i = 0; i < cells.length; i++) {
            for (int k = 0; k < cells[i].length; k++) {
                newcells[i][k] = shouldExist(i, k);
            }
        }
        cells = newcells;
    }
}
