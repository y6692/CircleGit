/**
 * Created by Administrator on 2017/6/14 0014.
 */

$(document).ready(function(){

    $("#phone").focus().select();

    var user = getUserInfo();
    // alert(user.varCard)


    $('#phone').val(user.telephone);
    $('#email').val(user.email);


    //验证电话号码
    $("#phone").blur(function () {
        var userPhone =  $(this).val() || "";
        if (userPhone.length > 0 && userPhone != user.telephone){
            if (!/(^1[3|5|8][0-9]{9}$)/.test(userPhone)) {//值工符合手机的格式
                mui.toast("请输入正确的手机号");
                return;
            }
            checkDataIsExist(userPhone,"user/repeattel?telephone=");
        }
    });
    //验证邮箱
    $("#email").blur(function () {
        var userEmail =  $(this).val() || "";
        if (userEmail.length > 0 && userEmail != user.email) {
            if (!(/@/.test(userEmail))){
                mui.toast("请输入正确的邮箱");
                return;
            }
            checkDataIsExist(userEmail,"user/repeatemail?email=");
        }
    });

    $("#submit").click(function(){
        var phone =$("#phone").val() || '';
        var email =$("#email").val() || '';

        if (phone.length == 0){
            mui.toast("请输入电话");
            return;
        }
        if (email.length == 0){
            mui.toast("请输入邮箱");
            return;
        }

        var obj = {};
        obj.username = user.username;
        obj.plainPassword = user.plainPassword;
        obj.Name = user.name;
        obj.Email = email;
        obj.telephone = phone;
        // obj.varCard = user.varCard;
        changeInfo(obj);
    });
});

function checkDataIsExist(userData,urlstr) {
    $.ajax({
        url:BASE_URL + urlstr + userData,
        type:"get",
        async:true,
        timeout:5000,
        dataType:"json",
        beforeSend:function(xhr){
            console.log(xhr);
            console.log("发送前");
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
            console.log("错误");
            console.log(xhr);
            console.log(textStatus);
        },
        complete:function () {
            console.log("结束");
        }
    });
}

function checkSpan(value,flag) {
    var errSpan = $("#errorSpan");
    if (flag == false){
        errSpan.text(value);
    }else {
        errSpan.text("");
    }
}

function changeInfo(obj) {
    console.log(JSON.stringify(obj));
    $.ajax({
        url: BASE_URL + 'user/updateuser',
        type:'POST', //GET
        async:true,    //或false,是否异步
        data: obj,
        timeout:5000,    //超时时间
        dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
        beforeSend:function(xhr){
            console.log(xhr);
            console.log('发送前');
            // 显示加载中
            $('.load').show();
            $('body').spin(LOAD_OPTS);
        },

        success:function(data,textStatus,jqXHR){
            console.log(data.results);
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            mui.toast(data.message);

            $('.load').hide();
            $('body').spin(false);
            // 100 ：成功 101：失败 102:输入错误
            if(data.status == 100){
                var user = getUserInfo();
                user.telephone = obj.telephone;
                user.email = obj.Email;
                saveUserInfo(user);
                // 返回上一个页面
                window.history.go(-1);
            }
        },
        error:function(xhr,textStatus){
            console.log('错误');
            console.log(xhr);
            console.log(textStatus);
            $('.load').hide();
            $('body').spin(false);
        },
        complete:function(){
            console.log('结束');
            $('.load').hide();
            $('body').spin(false);
            $('.load').hide();
            $('body').spin(false);
        }
    });
}