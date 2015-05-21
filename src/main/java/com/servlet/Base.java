package com.servlet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Xaker on 28.04.2015.
 */
public class Base {
    private static final String PATH_HOME = System.getProperty("user.home") + "\\history.xml";
    private static SimpleDateFormat timeForm = new SimpleDateFormat("dd-MM-yyyy HH:mm");

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
        message.setAttribute("id", Long.toString(temp.getID()));
        root.appendChild(message);

        Element author = doc.createElement("author");
        author.appendChild(doc.createTextNode(temp.getUser()));
        message.appendChild(author);

        Element text = doc.createElement("text");
        text.appendChild(doc.createTextNode(temp.getMessage()));
        message.appendChild(text);

        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(Long.toString(temp.getID())));
        message.appendChild(id);

        Element date = doc.createElement("date");
        Date textTime = temp.getDate();
        date.appendChild(doc.createTextNode(timeForm.format(textTime)));
        message.appendChild(date);

        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(PATH_HOME));
    }

    public static synchronized List<Pairs> readXML(int index) throws SAXException, IOException, ParserConfigurationException {
        List<Pairs> messages = new ArrayList<Pairs>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(PATH_HOME);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        NodeList ndlist = root.getElementsByTagName("message");
        for (int i = index; i < ndlist.getLength(); i++) {
            Element block = (Element) ndlist.item(i);
            String author = block.getElementsByTagName("author").item(0).getTextContent();
            String text = block.getElementsByTagName("text").item(0).getTextContent();
            String id = block.getElementsByTagName("id").item(0).getTextContent();
            Date date = timeForm.parse(block.getElementsByTagName("date").item(0).getTextContent(), new ParsePosition(0));

            Message message = new Message(Long.parseLong(id), author, text, date);
            messages.add(new Pairs("POST",message));
        }
        return messages;
    }

    /*  public static synchronized void deletePartXML(Message deleteTemp ) throws ParserConfigurationException, IOException, SAXException, TransformerException, XPathExpressionException {
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document doc = db.parse(PATH_HOME);
          doc.getDocumentElement().normalize();
          XPath xpath = XPathFactory.newInstance().newXPath();
          XPathExpression expr = xpath.compile("//" +"message"+ "[@id='" + Long.toString(id) + "']");
          Node nd = (Node) expr.evaluate(doc, XPathConstants.NODE);
  /*        Element root = doc.getDocumentElement();

          NodeList ndlist = root.getElementsByTagName("message");
          String findId = Long.toString(deleteTemp.getID());
          for (i = 0; i < ndlist.getLength(); i++) {
              Element message = (Element) ndlist.item(i);
              String realId = message.getElementsByTagName("id").item(0).getTextContent();
              if (findId.equals(realId)) {
                 // message.
                  //message.replaceChild("", ((Element) ndlist.item(i)).getElementsByTagName("messege").item(0).getTextContent())
                  //ndlist.item(i).replaceChild((Node)deleteTemp,message);
                  /
                  nd.getParentNode().removeChild(nd);
                  Transformer t = TransformerFactory.newInstance().newTransformer();
                  t.setOutputProperty(OutputKeys.INDENT, "yes");
                  t.transform(new DOMSource(doc), new StreamResult(PATH_HOME));
      }
  */
    public static synchronized boolean thereXML() {
        File file = new File(PATH_HOME);
        return file.exists();
    }

    public static synchronized int sizeXML() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(PATH_HOME);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();

        NodeList ndlist = root.getElementsByTagName("message");
        return ndlist.getLength();
    }

    /*public static synchronized Message deletePartXML(long id) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(PATH_HOME);
        doc.getDocumentElement().normalize();
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//" + "message" + "[@id='" + Long.toString(id) + "']");
        Node nd = (Node) expr.evaluate(doc, XPathConstants.NODE);
        Element el = (Element) nd;
        String author = el.getElementsByTagName("author").item(0).getTextContent();
        String text = el.getElementsByTagName("text").item(0).getTextContent();
        //Element root = doc.getDocumentElement();
        //String findId = Long.toString(id);
        /*NodeList ndlist = root.getElementsByTagName("message");
        int i;
        for (i = 0; i < ndlist.getLength(); i++) {
            Element message = (Element) ndlist.item(i);
            String realId = message.getElementsByTagName("id").item(0).getTextContent();
            if (findId.equals(realId)) {
                break;
            }
        }
        Element el = (Element)ndlist.item(i);
        String author = el.getElementsByTagName("author").item(0).getTextContent();
        String text = el.getElementsByTagName("text").item(0).getTextContent();
        Date date = timeForm.parse(el.getElementsByTagName("date").item(0).getTextContent(), new ParsePosition(0));
        nd.getParentNode().removeChild(nd);
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(PATH_HOME));
        return new Message(id, author, text, date);
    }
    */
    public  static synchronized int getIndex(long id) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(PATH_HOME);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        String findId = Long.toString(id);
        NodeList ndlist = root.getElementsByTagName("message");

        int i;
        for (i = 0; i < ndlist.getLength(); i++) {
            Element message = (Element) ndlist.item(i);
            String realId = message.getElementsByTagName("id").item(0).getTextContent();
            if (findId.equals(realId)) {
                break;
            }
        }
        return i;
    }
    public static synchronized Message replacePartXML(long id, String message) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(PATH_HOME);
        doc.getDocumentElement().normalize();

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//" + "message" + "[@id='" + Long.toString(id) + "']");
        Node nd = (Node) expr.evaluate(doc, XPathConstants.NODE);
        Node newNode = nd;
        Element newEl = (Element) newNode;

        newEl.getElementsByTagName("text").item(0).setTextContent(message);
        newNode = (Node) newEl;
        Element el = (Element) nd;
        String author = el.getElementsByTagName("author").item(0).getTextContent();
        Date date = timeForm.parse(el.getElementsByTagName("date").item(0).getTextContent(), new ParsePosition(0));

        nd.getParentNode().replaceChild(newNode, nd);

        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(PATH_HOME));
        return new Message(id, author, message, date);
    }
}