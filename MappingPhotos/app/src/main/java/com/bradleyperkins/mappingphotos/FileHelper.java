package com.bradleyperkins.mappingphotos;

// Date 10/24/18
// Bradley Perkins
// MDF# - 1811
// FileHelper.Java

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHelper {


    public static final String FILENAME = "mapitems.txt";

    public static void writeData(ArrayList<MapItem> mapList, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mapList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<MapItem> readData(Context context) {
        ArrayList<MapItem> mapList = null;
        try {
            FileInputStream is = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(is);

            mapList = (ArrayList<MapItem>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //In case there is nothing in mapList, catch it
            mapList = new ArrayList<>();
            e.printStackTrace();
        }

        return mapList;
    }

}
