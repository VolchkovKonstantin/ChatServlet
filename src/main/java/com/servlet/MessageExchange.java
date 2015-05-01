package com.servlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class MessageExchange {
    private JSONParser jsonParser = new JSONParser();

    public String getToken(int index) {
        Integer number = index * 8 + 11;
        return "TN" + number + "EN";
    }

/*    public int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }
*/
    public String getServerResponse(List<Message> messages, int numberToken) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < messages.size(); i++) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", messages.get(i).getID());
            jsonObject1.put("user", messages.get(i).getUser());
            jsonObject1.put("message", messages.get(i).getMessage());
            jsonArray.add(jsonObject1);
        }
        jsonObject.put("messages", jsonArray);
        jsonObject.put("token", getToken(numberToken));
        return jsonObject.toJSONString();
    }

   /* public String getClientSendMessageRequest(long id, String username, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("user", username);
        jsonObject.put("message", message);
        return jsonObject.toJSONString();
    }
*/
    /*
    public String getErrorMessage(String text) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("error", text);

        return jsonObject.toJSONString();
    }
*/
    public JSONObject getClientMessage(String input) throws ParseException {
        JSONObject jsonObject = getJSONObject(input);
        return jsonObject;
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

  /*  public String inputStreamToString(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(baos.toByteArray());
    }
    */
}