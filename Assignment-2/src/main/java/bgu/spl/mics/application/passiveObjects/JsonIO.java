package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class JsonIO {
    public static Input getInputFromJson(String filePath) throws IOException {
        Reader reader = new FileReader(filePath);
        return new Gson().fromJson(reader, Input.class);
    }

    public static void getJsonFromDiary(String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        new GsonBuilder().create().toJson(Diary.getInstance(), writer);
        writer.close();
    }
}
