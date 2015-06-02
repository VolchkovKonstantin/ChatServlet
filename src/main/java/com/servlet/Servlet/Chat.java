package com.servlet.Servlet;

import com.servlet.Repository.Message;
import com.servlet.Repository.MessageExchange;
import com.servlet.Repository.Pairs;
import com.servlet.Repository.Storage;
import com.servlet.dao.DataBase;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@WebServlet(urlPatterns = "/Chat", asyncSupported = true)
public class Chat extends HttpServlet {
    private MessageExchange messageExchange;
    private DataBase base;
    static Logger logger = Logger.getLogger(Chat.class.getName());
    private static List<AsyncContext> ascont = Collections.synchronizedList(new ArrayList<AsyncContext>());
    @Override
    public void init() throws ServletException {
        messageExchange = new MessageExchange();
        base = new DataBase();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        final PrintWriter out = resp.getWriter();
        boolean flag = Boolean.parseBoolean(req.getParameter("flag"));
        if (flag) {
            String messages = "";
            synchronized (messages) {
                    firstHistory();
                    messages = formResponse();
                    Storage.deleteAll();
            }
            out.print(messages);
            out.flush();
        }else{
                AsyncContext asc = req.startAsync(req, resp);
                asc.setTimeout(300000);
                ascont.add(asc);
                asc.addListener(new AsyncListener() {
                    public void onComplete(AsyncEvent asyncEvent) throws IOException {

                    }

                    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                        HttpServletResponse resp = (HttpServletResponse) asyncEvent.getAsyncContext().getResponse();
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        PrintWriter out = resp.getWriter();
                        out.print("{\"messages\" : []}");
                        out.flush();
                        ascont.remove(asyncEvent.getAsyncContext());
                    }

                    public void onError(AsyncEvent asyncEvent) throws IOException {

                    }

                    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

                    }
                });
            }
        } catch (Exception e){
            logger.error(e);
        }
    }
    private void doResponse(List<AsyncContext> context) {
        for (AsyncContext asc : context) {

            HttpServletResponse response = (HttpServletResponse) asc.getResponse();
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(formResponse());
                out.flush();
                asc.complete();
            } catch (IOException e) {
                logger.error(e);
            }
        }
        Storage.deleteAll();
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
            base.createPartBase(structOfMessage);
            resp.setStatus(HttpServletResponse.SC_OK);
            List<AsyncContext> ascont = new ArrayList<AsyncContext>(this.ascont);
            this.ascont.clear();
            doResponse(ascont);
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
            Message message = base.replacePartBase(id, "DeleteMessage"); //base.history.get(i);

            if ((message != null) && (message.getFlag())) {
                Storage.addMessage("DELETE", message);
                Date curTime = new Date();
                DateFormat dtfrm = DateFormat.getDateTimeInstance();
                String dateTime = dtfrm.format(curTime);
                logger.info("Delete " + dateTime + " " + message.getUser() + " : " + message.getMessage());
                resp.setStatus(HttpServletResponse.SC_OK);
                List<AsyncContext> ascont = new ArrayList<AsyncContext>(this.ascont);
                this.ascont.clear();
                doResponse(ascont);
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

            Message message = base.replacePartBase(id, putMessage);

            if ((message != null) && (message.getFlag())) {
                Storage.addMessage("PUT", message);
                Date curTime = new Date();
                DateFormat dtfrm = DateFormat.getDateTimeInstance();
                String dateTime = dtfrm.format(curTime);
                    logger.info("PUT " + dateTime + " " + message.getUser() + " : " + message.getMessage());
                resp.setStatus(HttpServletResponse.SC_OK);
                List<AsyncContext> ascont = new ArrayList<AsyncContext>(this.ascont);
                this.ascont.clear();
                doResponse(ascont);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.error("Need correct message(Not null)");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    private String formResponse() {

        List<Pairs> history = Storage.getHistory(0);
        return  messageExchange.getServerResponse(history);
    }

    private void firstHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
            Storage.addAll(base.readBase());
    }

}