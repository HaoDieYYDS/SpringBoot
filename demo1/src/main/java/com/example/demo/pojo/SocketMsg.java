package com.example.demo.pojo;
//一个消息类所包含的内容，包括（接收者，发送者，消息内容，类型（是公聊还是私聊））
public class SocketMsg {
    private String receiver;
    private String outputer;
    private String msg;
    private int type;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getOutputer() {
        return outputer;
    }

    public void setOutputer(String outputer) {
        this.outputer = outputer;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
