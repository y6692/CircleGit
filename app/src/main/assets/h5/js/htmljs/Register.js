/**
 * Created by Administrator on 2017/6/15.
 */
var InterValObj;
var curCount;
var msgidStr = "";
$(document).ready(function () {
    $("#userPhone").focus().select();
//验证账号
  /*  $("#userAccount").blur(function () {
        var userAccount =  $("#userAccount").val() || "";
        if (userAccount.length > 0){
            checkDataIsExist(userAccount,"user/repeatusername?Username=");
        }
    });*/
    //验证邮箱
  /*  $("#userEmail").blur(function () {
        var userEmail =  $("#userEmail").val() || "";
        if (userEmail.length>0){
            if (!(/@/.test(userEmail))){
                mui.toast("请输入正确的邮箱");
                return;
            }
            checkDataIsExist(userEmail,"user/repeatemail?email=");
        }
    });*/
    //验证电话号码
    $("#userPhone").blur(function () {
        var userPhone =  $("#userPhone").val() || "";
        if (userPhone.length>0){
            if (!/(^1[3|5|8][0-9]{9}$)/.test(userPhone)) {//值工符合手机的格式
                checkSpan("请输入正确的手机号",false);
                return;
            }
            checkSpan("",true);
            checkDataIsExist(userPhone,"user/repeattel?telephone=");
        }
    });
    //点击注册
    $("#register").click(function () {
        var phone =  $("#userPhone").val().trim();
        var pwd = $("#pw1").val().trim();
        var passwd = $("#pw2").val().trim();
        var code = $("#code").val().trim();
        if (phone.length <= 0){
            mui.toast("请输入手机号");
            return;
        }
        if (pwd.length<6){
            mui.toast("请输入6位字符密码");
            return;
        }
        if (pwd != passwd){
            mui.toast("两次密码输入不一致");
            return;
        }
        if ($("#errorSpan").html() != ""){
            mui.toast($("#errorSpan").html());
            return ;
        }
        if (code.length <=0){
            mui.toast("请输入验证码");
            return;
        }
        registerUserInfoSavePhoneWithPwd(phone,$("#pw1").val());
        checkcodeRequest(code);
        return;
    });
    //获取短信验证码
    $("#get_code").click(function () {
        checkPhoneNum();
    });
    $("#get_Recode").click(function () {
        checkPhoneNum();
    });
});
function checkPhoneNum() {
    var phone = $("#userPhone").val();
    if (phone.trim().length<=0){
        mui.toast("请输入电话号码");
        return;
    }else {
        if (!/(^1[3|5|8][0-9]{9}$)/.test(phone)) {//值工符合手机的格式
            checkSpan("请输入正确的手机号",false);
            return;
        }
        getcodeRequest(phone);
    }
}
/*
 * 计时器
 */
function getCode() {
    if (InterValObj)
    window.clearInterval(InterValObj);//停止计时器
    curCount = 60;
    InterValObj = window.setInterval(setRemainTime, 1000); //启动计时器，1秒执行一次
}
/*
 * 更新剩余时间
 */
function setRemainTime() {
    if (curCount == 1) {
        window.clearInterval(InterValObj);//停止计时器
        // alert("验证码过期,请重新获取");
        $('#remainTime').hide();
        $('#get_Recode').show();
    } else {
        $('#remainTime').show();
        $('#get_Recode').hide();
        $('#get_code').hide();
        curCount--;
        $("#remainTime").html(curCount + "s"+"重新获取");
    }
}

function checkSpan(value,flag) {
    var errSpan = $("#errorSpan");
    if (flag == false){
        errSpan.text(value);
    }else {
        errSpan.text("");
    }
}

function checkDataIsExist(userData,urlstr) {
    $.ajax({
        url:BASE_URL + urlstr + userData,
        type:"get",
        async:true,
        timeout:5000,
        dataType:"json",
        beforeSend:function(xhr){
            console.log(xhr)
            console.log("发送前")
        },
        success:function (data,textStatus,jqXHR) {
            console.log(data);
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            if(data.status == 103){
                checkSpan(data.message,false);
            }else {
                checkSpan(data.message,true);
            }

        },
        error:function (xhr,textStatus) {
            console.log("错误")
            console.log(xhr)
            console.log(textStatus)
        },
        complete:function () {
            console.log("结束")
        }
    });
}

function clickRegister(data) {
    $.ajax({
        url:BASE_URL + "user/reg",
        type:"POST",
        async:true,
        data:data,
         timeout:5000,
        dataType:"json",
        beforeSend:function(xhr){
            console.log(xhr)
            console.log("发送前")
        },
        success:function (data,textStatus,jqXHR) {
            console.log(data);
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            if(data.status != 100){
                mui.toast(data.message);
            }else {
                mui.toast("注册成功");
                window.history.go(-1);
            }
        },
        error:function (xhr,textStatus) {
            console.log("错误")
            console.log(xhr)
            console.log(textStatus)


        },
        complete:function () {
            console.log("结束")
        }
    });
}

function getcodeRequest(phone) {
    $.ajax({
        url:BASE_URL+"message/getSMS",
        type:"GET",
        async:true,
        timeout:5000,
        dataType:"json",
        data:{
          Phone:phone,
        },
        beforeSend:function(xhr){
            console.log(xhr)
            console.log("发送前")
        },
        success:function (data,textStatus,jqXHR) {
            console.log(data);
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            if (data.status == 100) {
                var obj = eval('(' + data.message + ')')
                if (obj.msg_id) {
                    mui.toast("发送成功！");
                    msgidStr = obj.msg_id;
                    getCode();
                } else {
                    mui.toast("发送失败！");
                }
            }else {
                mui.toast("发送失败!");
            }
        },
        error:function (xhr,textStatus) {
            console.log("错误")
            console.log(xhr)
            console.log(textStatus)
        },
        complete:function () {
            console.log("结束")
        }
    });

}

function checkcodeRequest(code) {
    $.ajax({
        url:BASE_URL+"message/checkSMS",
        type:"GET",
        async:true,
        timeout:5000,
        dataType:"json",
        data:{
            checknum :code,
            msg_id:msgidStr,
        },
        beforeSend:function(xhr){
            console.log(xhr)
            console.log("发送前")
        },
        success:function (data,textStatus,jqXHR) {
            console.log(data);
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            if(data.status = 100){
                var obj = eval('('+ data.message +')');
                if (obj.is_valid == true){
                    window.location.href = "userInfo.html";
                }else {
                    mui.toast("验证码失败");
                }
            }else {
                mui.toast("验证码失败");
            }
        },
        error:function (xhr,textStatus) {
            console.log("错误")
            console.log(xhr)
            console.log(textStatus)
        },
        complete:function () {
            console.log("结束")
        }
    });

}