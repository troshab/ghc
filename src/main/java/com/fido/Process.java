package com.fido;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Process {

    private static String FILENAME = Config.filename + ".bin";

    private static Map<String, BitSet> slides = new HashMap<>();

    public static void main(String[] args) {
    }
}
