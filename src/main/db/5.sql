use chat;
select * FROM users Where id In
(select user_id FROM messages Group By 
user_id having count(user_id) > 3);
