package com.example.demo.socket;
import cn.hutool.json.JSONUtil;
import com.example.demo.pojo.SocketMsg;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

//Socket具体实现方法
@Component
//建立连接
@ServerEndpoint("/WebSocket/{username}")
public class SocketUtil {

private String username;
private Session session;

    /**
     * 用来存放每个客户端对应的MyWebSocket对象。
     **/
    private static final CopyOnWriteArraySet<SocketUtil> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 存放Session集合，方便推送消息 （javax.websocket.Session）
     */
    private static final ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();
    private static final String USER_NAME_PREFIX = "user_name_";

    //刚建立连接
    @OnOpen
    public void OnOpen(@PathParam("username") String username,Session session)
    {
        this.username = username;
        this.session = session;
        sessionMap.put(USER_NAME_PREFIX+username,session);
        webSocketSet.add(this);
        String tips = username+"进入了聊天室，当前聊天室的人数为："+webSocketSet.size();
        System.out.println(tips);
        PM(username,tips,true);
    }

    //收到消息之后
    @OnMessage
    public void OnMessage(String message)
    {
        if(JSONUtil.isTypeJSONObject(message))
        {
            SocketMsg socketMsg =JSONUtil.toBean(message,SocketMsg.class);
            //私发
            if(socketMsg.getType() == 1)
            {
                DM(socketMsg);
            }
            //群发
            else {
                PM(socketMsg.getOutputer(),socketMsg.getOutputer()+"："+socketMsg.getMsg(),false);
            }
        }
    }

    //关闭连接
    @OnClose
    public void OnClose(){
        if(sessionMap.containsKey(USER_NAME_PREFIX+username))
        {
            sessionMap.remove(USER_NAME_PREFIX+username);
            webSocketSet.remove(this);
        }
        System.out.println(username+"退出了聊天室");
    }
    //连接发生错误
    @OnError
    public void OnError(Throwable error){
        System.out.println("userName为：" + username + "，发生错误：" + error.getMessage());
        error.printStackTrace();
    }

    //私聊
    //通过SocketMsg类得到接收者的Session以及消息内容,通过Session将消息发送到前端显示
    public synchronized static void DM(SocketMsg socketMsg)
    {
        Session receiver = sessionMap.get(USER_NAME_PREFIX+socketMsg.getReceiver());
        if (receiver != null){
            System.out.println(socketMsg.getOutputer()+"向"+socketMsg.getReceiver()+"发送了一条信息："+socketMsg.getMsg());
            receiver.getAsyncRemote().sendText(socketMsg.getOutputer()+"："+socketMsg.getMsg());
        }
        //这是模拟接收者不在线的情况下才会执行，向发送者发送“不在线消息”
        else {
            System.out.println("向"+socketMsg.getReceiver()+"发送信息失败！");
            Session outputer = sessionMap.get(USER_NAME_PREFIX+socketMsg.getOutputer());
            outputer.getAsyncRemote().sendText("发送信息失败，对方不在线！");
        }
    }
    //公聊
    //利用for循环遍历所有对象的Session，通过getAsyncRemote().sendText方法将数据发送到前端，从而实现群发
    public synchronized static void PM(String username, String msg, boolean flag) {
        if (flag) {
            //将Set集合内的名字通过逗号（，）连接
            String userNames = webSocketSet.stream()
                    .map(socketUtil -> socketUtil.username)
                    .collect(Collectors.joining(","));
            //用户刚进入聊天室时会向所有人发送一条消息，并且会更新用户名列表
            for (SocketUtil socketUtil : webSocketSet) {
                Session session1 = socketUtil.session;
                if (session1.isOpen()) {
                    String messageWithUserNames = msg + "用户名列表：" + userNames;
                    session1.getAsyncRemote().sendText(messageWithUserNames);
                }
            }
        } else {
            //发送群发消息时执行的操作，除了自己以外其他人接收消息
            for (SocketUtil socketUtil : webSocketSet) {
                Session session1 = socketUtil.session;
                Session currentUser = sessionMap.get(USER_NAME_PREFIX + username);
                if (session1.isOpen() && !session1.getId().equals(currentUser.getId())) {
                    session1.getAsyncRemote().sendText(msg+"(公)");
                }
            }
        }
        System.out.println("公共频道接收了一条消息：" + msg);
    }

}
