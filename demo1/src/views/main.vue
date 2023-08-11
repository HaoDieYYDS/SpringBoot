<template>
    <div id="main">
      <el-row>
        <el-col :span="8">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>{{ username }}</span>
            </div>
            <h3>用户名列表：</h3>
            <ul>
                <li v-for="item in filterList" :key="item">
                    {{ item }}
                    <el-button type="text" @click="Open(item)">私聊</el-button>
                </li>
            </ul>
          </el-card>
        </el-col>
        <el-col :span="16">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>公共聊天室</span>
            </div>
            <div id="msgList"></div>
            <div>
              <el-input type="text" id="msg" placeholder="请输入消息" v-model="message" ></el-input>
              <el-button type="primary" @click="send">发送</el-button>
                <el-popover placement="top" title="选择表情" width="200" trigger="click" >
                    <div>
                        <el-button type="text" @click="selectEmoji('\u{1F60A}')">😊</el-button>
                        <el-button type="text" @click="selectEmoji('👍')">👍</el-button>
                        <!-- 添加其他表情按钮 -->
                    </div>
                    <el-button slot="reference" icon="el-icon-smile">表情</el-button>
                </el-popover>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <el-dialog :title="receiver" :visible.sync="dialogVisible" width="60%" :before-close="handleClose">
        <div id="PmsgList"></div>
        <el-input placeholder="请输入内容" v-model="input" clearable></el-input>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="Psend">发 送</el-button>
          <el-popover placement="top" title="选择表情" width="200" trigger="click" >
                    <div>
                        <el-button type="text" @click="PselectEmoji('😊')">😊</el-button>
                        <el-button type="text" @click="PselectEmoji('👍')">👍</el-button>
                        <!-- 添加其他表情按钮 -->
                    </div>
                    <el-button slot="reference" type="primary" icon="el-icon-user">表情</el-button>
            </el-popover>
        </span>
      </el-dialog>
    </div>
  </template>
  
  <style scoped>
  #main {
    padding: 20px;
  }
  
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  
  .clearfix:after {
    clear: both;
  }
  
  .box-card {
    margin-bottom: 20px;
  }
  
  .time {
    font-weight: bold;
    color: #409EFF;
  }
  
  .message {
    margin-top: 5px;
    color: #303133;
  }
  </style>
  
  <script>
  export default {
    data() {
      return {
        username: '',
        list: [],
        websocket: null,
        message: '',
        dialogVisible: false,
        receiver: '',
        input: '',
        selectedEmoji: "", // 新增的属性，用于存储选中的表情
      }
    },
    computed: {
      filterList() {
        return this.list.filter(item => item !== this.username)
      }
    },
    created() {
      this.username = this.$route.query.username
      this.connection()
    },
    methods: {
      connection() {
        if (typeof WebSocket === 'undefined') {
          console.log("你的浏览器不支持WebSocket")
          return
        } else {
          this.websocket = new WebSocket('ws://localhost:8088/WebSocket/' + this.username);
          this.websocket.onopen = () => {
            alert('连接成功！')
            console.log('连接成功！！！')
          }
  
          this.websocket.onerror = () => {
            alert('连接失败！')
          }
  
          window.onbeforeunload = () => {
            this.closeConnection()
          };
  
          this.websocket.onmessage = resp => {
            if (resp.data.includes("用户名列表：")) {
              const separator = "用户名列表："
              const parts = resp.data.split(separator)
              const msg = parts[0].trim()
              const usernames = parts[1].trim()
              this.list = usernames.split(',')
              this.show('', msg)
            } else if (resp.data.includes('公')) {
              this.show('', resp.data)
            } else {
              this.Pshow('', resp.data)
            }
          }
          this.websocket.onclose = evt => {
            console.log("websocket关闭：", evt)
          }
        }
      },
      closeConnection() {
        this.websocket.close()
      },
      show(id, msg) {
        var msgList = document.getElementById("msgList")
        var currentTime = new Date().toLocaleTimeString(); // 获取当前时间
        if (id === "") {
          msgList.innerHTML += "<div class='time'>" + currentTime + "</div><div class='message'>" + msg + "</div><br>";
        } else {
          msgList.innerHTML += "<div class='time'>" + currentTime + "</div><div class='message'>" + id + ": " + msg + "</div><br>";
        }
      },
      Pshow(id, msg) {
        var msgList = document.getElementById("PmsgList");
        var currentTime = new Date().toLocaleTimeString(); // 获取当前时间
        if (id === "") {
          msgList.innerHTML += "<div class='time'>" + currentTime + "</div><div class='message'>" + msg + "</div><br>";
        } else {
          msgList.innerHTML += "<div class='time'>" + currentTime + "</div><div class='message'>" + id + ": " + msg + "</div><br>";
        }
      },
      mapToJson(map) {
        var obj = Object.create(null)
        for (var [k, v] of map) {
         obj[k] = v
        }
        return JSON.stringify(obj)
      },
      send() {
        var m = new Map()
        var outputer = this.username
        var msg = this.message
        if (msg === '') {
          alert('请输入消息！')
          return
        }
        m.set('outputer', outputer)
        m.set('receiver', '')
        m.set('msg', msg)
        m.set('type', 0)
        var json = this.mapToJson(m)
        this.websocket.send(json)
        this.message = ''
        this.show('我', msg)
      },
      Psend() {
        var m = new Map()
        m.set('receiver', this.receiver)
        m.set('outputer', this.username)
        if (this.input === '') {
          alert('请输入消息！')
          return
        }
        m.set('msg', this.input)
        m.set('type', 1)
        var json = this.mapToJson(m)
        this.websocket.send(json)
        this.Pshow('我', this.input)
        this.input = ''
      },
      handleClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {});
      },
      Open(item) {
        this.receiver = item
        this.dialogVisible = true
      },
      selectEmoji(emoji) {
        this.selectedEmoji = emoji;
        this.message = this.message+this.selectedEmoji
      },
      PselectEmoji(emoji) {
        this.selectedEmoji = emoji;
        this.input = this.input+this.selectedEmoji
      },
    }
  }
  </script>