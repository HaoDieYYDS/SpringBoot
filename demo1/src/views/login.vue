<template>
   <div class="login">
     <div class="mylogin" align="center">
       <h4>登录</h4>
       <el-form
         :model="loginForm"
         :rules="loginRules"
         ref="loginForm"
         label-width="0px"
       >
         <el-form-item label="" prop="username" style="margin-top: 10px">
           <el-row>
             <el-col :span="2">
               <span class="el-icon-s-custom"></span>
             </el-col>
             <el-col :span="22">
               <el-input
                 class="inps"
                 placeholder="账号"
                 v-model="loginForm.username"
               >
               </el-input>
             </el-col>
           </el-row>
         </el-form-item>
         <el-form-item label="" prop="password">
           <el-row>
             <el-col :span="2">
               <span class="el-icon-lock"></span>
             </el-col>
             <el-col :span="22">
               <el-input
                 class="inps"
                 type="password"
                 placeholder="密码"
                 v-model="loginForm.password"
               ></el-input>
             </el-col>
           </el-row>
         </el-form-item>
         <el-form-item style="margin-top: 55px">
           <el-button type="primary" round class="submitBtn" @click="submitForm">登录
           </el-button>
         </el-form-item>
      
       </el-form>
     </div>
   </div>
 </template>

<script>
 
export default {
  name: "Login",
  data: function () {
    return {
      loginForm: {
        username: "",
        password: "",
      },
      loginRules: {
         username: [{ required: true, message: "请输入账号", trigger: "blur" }],
         password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      },
    };
  },
 
  methods: {
    submitForm:function() {
      this.axios.post("http://localhost:8088/user/login",this.loginForm).then((resp)=>{
         console.log(resp.data);
         var data = resp.data
         if(data && data.username !='' && data.username != null){
            this.loginForm = {};
            this.$message({
                 message: '成功登录！！！',
                 type: 'success'
                 })
            
            this.$router.push({path:"/main",query:{username:data.username}})
         }
         else
            { 
              
              this.$router.push({path:"/"})
              this.$message({
                message:'用户密码错误，请重新登录！！',
                type:'false'
              })
            }
      })
    },
  },
};
</script>

<style>
.login {
   width: 100vw;
   padding: 0;
   margin: 0;
   height: 100vh;
   font-size: 16px;
   background-position: left top;
   background-color: #242645;
   color: #fff;
   font-family: "Source Sans Pro";
   position: relative;
 }

 .mylogin {
   width: 240px;
   height: 280px;
   position: absolute;
   top: 0;
   left: 0;
   right: 0;
   bottom: 0;
   margin: auto;
   padding: 50px 40px 40px 40px;
   box-shadow: -15px 15px 15px rgba(6, 17, 47, 0.7);
   opacity: 1;
   background: linear-gradient(
     230deg,
     rgba(53, 57, 74, 0) 0%,
     rgb(0, 0, 0) 100%
   );
 }

 .inps input {
   border: none;
   color: #fff;
   background-color: transparent;
   font-size: 12px;
 }

 .submitBtn {
   background-color: transparent;
   color: #39f;
   width: 200px;
 }
</style>