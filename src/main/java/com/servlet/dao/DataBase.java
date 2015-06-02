package com.servlet.dao;

import com.servlet.Repository.Message;
import com.servlet.Repository.Pairs;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.servlet.Servlet;
import java.sql.Connection;

/**
 * Created by Xaker on 30.05.2015.
 */
public class DataBase {
    private static Logger logger = LogManager.getLogger(Servlet.class.getName());
    private Integer userID = 0;
    public void createPartBase(Message block){
        Connection connection = null;
        PreparedStatement idCol = null;
        PreparedStatement user = null;
        PreparedStatement message = null;
        ResultSet resultSet = null;
        int id;
        try {
            connection = ConnectionPool.getConnection();
            idCol = connection.prepareStatement("SELECT * FROM users WHERE NAME =?");
            idCol.setString(1,block.getUser());
            resultSet = idCol.executeQuery();
            if (resultSet.next()){
                id=resultSet.getInt("id");
            }
            else {
                user = connection.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)");
                user.setInt(1, userID);
                user.setString(2, block.getUser());
                user.executeUpdate();
                id = userID;
                synchronized (userID) {
                    userID++;
                }
            }
            message = connection.prepareStatement("INSERT INTO messages (id, text, date,user_id) values (?, ?, ?, ?)");
            message.setLong(1, block.getID());
            message.setString(2, block.getMessage());
            message.setTimestamp(3, new java.sql.Timestamp(block.getDate().getTime()));
            message.setInt(4, id);
            message.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (idCol != null) {
                try {
                    idCol.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (user != null) {
                try {
                    user.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (message != null) {
                try {
                    message.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            }
        }
    public Message replacePartBase(long id,String newMessage) {
        Connection connection = null;
        PreparedStatement message = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            message = connection.prepareStatement("UPDATE messages SET text = ? WHERE id = ?");
            message.setString(1, newMessage);
            message.setLong(2, id);
            message.executeUpdate();
            message = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
            message.setLong(1,id);
            resultSet = message.executeQuery();
            resultSet.next();
            Date date = resultSet.getDate("date");
            int user_id = resultSet.getInt("user_id");
            message = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            message.setInt(1,user_id);
            resultSet = message.executeQuery();
            resultSet.next();
            String user = resultSet.getString("name");
            return new Message(id,user,newMessage,date);
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (message != null) {
                try {
                    message.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return new Message();
    }
    public List<Pairs> readBase(){
        List<Pairs> req = new ArrayList<Pairs>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM messages INNER JOIN users ON messages.user_id = users.id order by date ASC ");
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String user = resultSet.getString("name");
                String message = resultSet.getString("text");
                Date date = resultSet.getDate("date");
                req.add(new Pairs("POST", new Message(id, user, message, date)));
            }
            resultSet = statement.executeQuery("SELECT * FROM users ORDER BY id DESC limit 1");
            resultSet.next();
                userID = resultSet.getInt("id")+1;
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return req;
    }
    }
