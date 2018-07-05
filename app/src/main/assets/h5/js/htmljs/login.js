/**
 * Created by Administrator on 2017/6/14 0014.
 */
$(document).ready(function(){
    // alert(getUservcard().address);
    // getUservcard();
    $("#login").click(function(){
        var username = $("#account").val() || '';
        var plainPassword =$("#password").val() || '';

        if (username.length <= 0) {
            mui.toast('请输入账号');
            return;
        }
        if (plainPassword.length < 6) {
            mui.toast('密码最短为 6 个字符');
            return;
        }
        login(username, hex_md5(plainPassword));
    });
});

// function login(username, plainPassword) {
//     $.ajax({
//         url: BASE_URL + 'user/login',
//         type:'POST', //GET
//         async:true,    //或false,是否异步
//         data:{
//             username :username,
//             plainPassword:plainPassword,
//             // username :'dingd',plainPassword:'e10adc3949ba59abbe56e057f20f883e'
//         },
//         timeout:5000,    //超时时间
//         dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
//         beforeSend:function(xhr){
//             console.log(xhr);
//             console.log('发送前');
//             // 显示加载中
//             $('.load').show();
//             $('body').spin(LOAD_OPTS);
//         },
//
//         success:function(data,textStatus,jqXHR){
//             console.log(data.results);
//             console.log("textStatus:  " +　textStatus);
//             console.log(jqXHR);
//             $('.load').hide();
//             $('body').spin(false);
//             mui.toast(data.message);
//             // 100 ：成功 101：失败 102:输入错误
//             if(data.status == 100){
//                 // 存储在localStorage
//                 data.results.plainPassword = plainPassword;
//                 saveUserInfo(data.results);
//                 // 通知native存储用户信息
//                 dsBridge.call(LOGIN_SUCCESS_FROM_JS, {'userInfo': data.results}, function (responseData) {
//                     alert("callback from native-->" + responseData);
//                 });
//             }
//         },
//         error:function(xhr,textStatus){
//             console.log('错误');
//             console.log(xhr);
//             console.log(textStatus);
//             $('.load').hide();
//             $('body').spin(false);
//         },
//         complete:function(){
//             console.log('结束');
//             $('.load').hide();
//             $('body').spin(false);
//         }
//     });
// }