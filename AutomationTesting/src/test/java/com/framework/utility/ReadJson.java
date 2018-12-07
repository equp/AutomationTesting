package com.framework.utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

public class ReadJson {


    BufferedReader reader;
    JSONObject jsonObject = null;
    JSONObject screenResPerPlatform = null;

    public JSONObject ReadJsonFromFile(String fileName, String id) {
        try {
            reader = new BufferedReader(new FileReader(fileName));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            jsonObject = new JSONObject(responseStrBuilder.toString());
            screenResPerPlatform = jsonObject.getJSONObject(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return screenResPerPlatform;
    }
}
