package com.servlet;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


@WebServlet(urlPatterns = "/Chat")
public class Chat extends HttpServlet {
    private MessageExchange messageExchange;
    private Base base;
    final static Logger logger = Logger.getLogger(Chat.class);

    @Override
    public void init() throws ServletException {
        messageExchange = new MessageExchange();
        base = new Base();
        try {
            getfistHistory();
            super.init();
        } catch (SAXException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (ParserConfigurationException e) {
            logger.error(e);
        } catch (TransformerException e) {
            logger.error(e);
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
                int index = messageExchange.getIndex(token);
                if (formResponse(index).equals("NO")) {
                    resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                } else {
                    out.print(formResponse(index));
                    out.flush();
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.error("Need correct token");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Something wrong");
        }
    }

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

            logger.info("POST " + dateTime + " " + user + " : " + message);

            Message structOfMessage = new Message(id, user, message, curTime);
            Storage.addMessage("POST", structOfMessage);
            base.createPartXML(structOfMessage);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            logger.error(e);
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

            Message message = base.replacePartXML(id, "DeleteMessage"); //base.history.get(i);

            if ((message != null) && (message.getFlag())) {
                Storage.addMessage("DELETE", message);
                Date curTime = new Date();
                DateFormat dtfrm = DateFormat.getDateTimeInstance();
                String dateTime = dtfrm.format(curTime);
                logger.info("Delete " + dateTime + " " + message.getUser() + " : " + message.getMessage());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.error("Need correct message(Not null)");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setCharacterEncoding("UTF-8");
            BufferedReader in = req.getReader();
            String temp = in.readLine();
            JSONObject jsonObject = messageExchange.getClientMessage(temp);
            long id = Long.parseLong(jsonObject.get("id").toString());
            String putMessage = jsonObject.get("message").toString();

            Message message = base.replacePartXML(id, putMessage);

            if ((message != null) && (message.getFlag())) {
                Storage.addMessage("PUT", message);
                Date curTime = new Date();
                DateFormat dtfrm = DateFormat.getDateTimeInstance();
                String dateTime = dtfrm.format(curTime);
                if (!message.getMessage().equals("User Change message")) {
                    logger.info("PUT " + dateTime + " " + message.getUser() + " : " + message.getMessage());
                }
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.error("Need correct message(Not null)");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    private String formResponse(int index) {
        List<Pairs> history = Storage.getHistory(index);
        if (history.size() != 0) {
            return messageExchange.getServerResponse(history, Storage.getSize());
        }
        return "NO";
    }

    private void getfistHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (base.thereXML()) {
            Storage.addAll(base.readXML(0));

        } else {
            base.startXML();
        }
    }

}