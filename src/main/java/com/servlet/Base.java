package com.servlet;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Xaker on 28.04.2015.
 */
public class Base {
    public Map<Integer, Message> history = new TreeMap<Integer, Message>();
    private static final String PATH_HOME = System.getProperty("user.home") + "\\history.xml";
    private static SimpleDateFormat timeForm = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    public ArrayList<Message> giveArrayMessages() {
        ArrayList<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < history.size(); i++) {
            Message mes = history.get(i);
            if (mes != null) {
                messages.add(mes);
            }
        }
        return messages;
    }

  /*  private void sendResponse(HttpExchange httpExchange, String response) {
        try {
            byte[] bytes = response.getBytes();
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            if ("OPTIONS".equals(httpExchange.getRequestMethod())) {
                headers.add("Access-Control-Allow-Methods", "PUT, DELETE, POST, GET, OPTIONS");
            }
            httpExchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
   /* private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
*/
    public static synchronized void startXML() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("messages");
        doc.appendChild(root);
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(PATH_HOME));
    }
    public static synchronized void createPartXML(Message temp) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(PATH_HOME);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        Element message = doc.createElement("message");
        root.appendChild(message);
        Element author = doc.createElement("author");
        author.appendChild(doc.createTextNode(temp.getUser()));
        message.appendChild(author);
        Element text = doc.createElement("text");
        text.appendChild(doc.createTextNode(temp.getMessage()));
        message.appendChild(text);
        Element date = doc.createElement("date");
        Date textTime = temp.getDate();
        date.appendChild(doc.createTextNode(timeForm.format(textTime)));
        message.appendChild(date);
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(PATH_HOME));
    }

    public synchronized Map<Integer, Message> readXML() throws SAXException, IOException, ParserConfigurationException {
        Map<Integer, Message> messages = new TreeMap<Integer, Message>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(PATH_HOME);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        NodeList ndlist = root.getElementsByTagName("message");
        for (int i = 0; i < ndlist.getLength(); i++) {
            Element message = (Element) ndlist.item(i);
            String author = message.getElementsByTagName("author").item(0).getTextContent();
            String text = message.getElementsByTagName("text").item(0).getTextContent();
            Date date = timeForm.parse(message.getElementsByTagName("date").item(0).getTextContent(), new ParsePosition(0));
            messages.put(i,new Message(i, author, text, date));
        }
        history.putAll(messages);
        return messages;
    }
    public static synchronized boolean thereXML() {
        File file = new File(PATH_HOME);
        return file.exists();
    }
}