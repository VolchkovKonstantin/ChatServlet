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
    MessageExchange messageExchange;
    Base base;
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
        PrintWriter out = resp.getWriter();
        String token = (String)req.getParameter("token");
        if (token != null && !"".equals(token)) {
            //int index = messageExchange.getIndex(token);
            out.print(messageExchange.getServerResponse(base.giveArrayMessages(), base.history.size()));//username,history.subList(index, history.size()));
        }
    }
@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    try {
        BufferedReader in = req.getReader();
        String temp = in.readLine();
        JSONObject jsonObject = messageExchange.getClientMessage(temp);
        long id = Long.parseLong(jsonObject.get("id").toString());
        String user = jsonObject.get("user").toString();
        String message = jsonObject.get("message").toString();
        Date curTime = new Date();
        DateFormat dtfrm = DateFormat.getDateTimeInstance();
        String dateTime = dtfrm.format(curTime);
        System.out.println(dateTime + " " + user + " : " + message);
        Message structOfMessage = new Message(id, user, message, curTime);
        base.history.put(base.history.size(), structOfMessage);
        base.createPartXML(structOfMessage);
    }
    catch (Exception e) {
    }
}
    private void getfistHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException  {
        if (base.thereXML()) {
            base.readXML();
        }
        else {
           base.startXML();
        }
    }
}