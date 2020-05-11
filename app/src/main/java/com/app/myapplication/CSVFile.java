package com.app.myapplication;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVFile {
    InputStream in;

    public CSVFile(InputStream in){
        this.in = in;
    }
    public BufferedReader read() throws Exception {
//        String[] row = null;
        JSONObject results = new JSONObject();
//        ArrayList<String> results = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        try {
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            throw new Exception();
//        }
//        finally{
//            try {
//                in.close();
//            }
//            catch (IOException e){
//                throw new RuntimeException("Error closing inputstream " + e);
//            }
//        }

        return reader;
    }
}
