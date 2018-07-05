/**
 * Created by Administrator on 2017/6/14 0014.
 */

$(document).ready(function(){
    $("#oldpwd").focus().select();

    $("#submit").click(function(){
        var oldpwd = $("#oldpwd").val()||'';
        var pwd = $("#pwd").val() || '';
        var pwd2 =$("#pwd2").val() || '';
        if (oldpwd.length<=0){
            mui.toast('请输入原密码');
            return;
        }
        if (pwd.length < 6 || pwd2.length < 6) {
            mui.toast('密码最短为 6 个字符');
            return;
        }
        if (pwd != pwd2) {
            mui.toast('密码不一致');
            return;
        }
        changePwd(hex_md5(pwd));
    });
});

function changePwd(pwd) {
    var oldpwd = $("#oldpwd").val()||'';
    $.ajax({
        url: BASE_URL + 'user/updatepassword',
        type:'POST', //GET
        async:true,    //或false,是否异步
        data:{
            oldpassword:hex_md5(oldpwd),
            username : getUserInfo().username,
            plainPassword: pwd,
            Accesstoken: getUserInfo().Accesstoken,
        },
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
                // 存储在localStorage

                // 通知native更新用户信息
                dsBridge.call(EXIT_FROM_JS, {}, function () {
                    clearuserInfo();
                    // alert("callback from native-->" );
                });
                // 返回上一个页面
                // window.history.go(-1);
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