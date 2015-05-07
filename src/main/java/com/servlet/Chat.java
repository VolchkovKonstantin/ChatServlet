package com.servlet;

import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.lang.Override;
import java.lang.String;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@WebServlet(urlPatterns = "/Chat")
public class Chat extends HttpServlet {
    private MessageExchange messageExchange;
    private Base base;

    @Override
    public void init() throws ServletException {
        messageExchange = new MessageExchange();
        base = new Base();
        try {
            getfistHistory();
            super.init();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        String token = (String) req.getParameter("token");
        try {
            if (token != null && !"".equals(token)) {

                //int index = messageExchange.getIndex(token);
                out.print(messageExchange.getServerResponse(base.readXML()/*giveArrayMessages()*/, base.sizeXML())); //base.history.size()));//username,history.subList(index, history.size()));
                out.flush();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("Need correct token");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    // else resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Need token");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        BufferedReader in = req.getReader();
        String temp = in.readLine();
        try {
            JSONObject jsonObject = messageExchange.getClientMessage(temp);
            long id = Long.parseLong(jsonObject.get("id").toString());
            String user = jsonObject.get("user").toString();
            String message = jsonObject.get("message").toString();
            Date curTime = new Date();
            DateFormat dtfrm = DateFormat.getDateTimeInstance();
            String dateTime = dtfrm.format(curTime);
            System.out.println("POST " + dateTime + " " + user + " : " + message);
            Message structOfMessage = new Message(id, user, message, curTime);
            //base.history.put(base.history.size(), structOfMessage);
            base.createPartXML(structOfMessage);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            System.out.print(e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setCharacterEncoding("UTF-8");
            BufferedReader in = req.getReader();
            String temp = in.readLine();
            JSONObject jsonObject = messageExchange.getClientMessage(temp);
            long id = Long.parseLong(jsonObject.get("id").toString());
            // int i;
            /*for(i = 0; i < base.history.size(); i++ ) {
                if(id == base.history.get(i).getID()) {
                    break;
                }
            }
            */
            Message message = base.deletePartXML(id); //base.history.get(i);
            if ((message != null) && (message.getFlag())) {
                DateFormat dtfrm = DateFormat.getDateTimeInstance();
                String dateTime = dtfrm.format(message.getDate());
                System.out.println("Delete " + dateTime + " " +/*base.history.get(i)*/message.getUser() + " " + message.getMessage());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("Need correct message(Not null)");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /* @Override
     protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         BufferedReader in = req.getReader();
         String temp = in.readLine();
         try {
             JSONObject jsonObject = messageExchange.getClientMessage(temp);
             long id = Long.parseLong(jsonObject.get("id").toString());
             Message message = base.findInXML(id);
             if ((message != null) && (message.getFlag())) {
                 Date curTime = new Date();
                 DateFormat dtfrm = DateFormat.getDateTimeInstance();
                 String dateTime = dtfrm.format(curTime);
                 System.out.println("PUT "+ dateTime + " " +/*base.history.get(i)**message.getUser() + " " + message.getMessage());
                 message.setMessage("");
                 base.deletePartXML(message);
                 message.setFlag(false);
             }
         } catch (Exception e) {
             resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
         }
     }
 */
    private void getfistHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (base.thereXML()) {
            base.readXML();
        } else {
            base.startXML();
        }
    }

}