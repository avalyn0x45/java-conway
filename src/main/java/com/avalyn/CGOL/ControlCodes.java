// This code is in the public domain. Original author Avalyn Baldyga

package com.avalyn.CGOL;

public class ControlCodes {
    public static final String clear = "\033[2J";
    public static final String savepos = "\033[s";
    public static final String reqpos = "\033[6n";
    public static final String resetpos = "\033[u";
    public static String moveTo(Integer x, Integer y) {
        return "\033["+x+";"+y+"H"; // Java's format syntax is absolutely ridiculous, so no.
    }
}
