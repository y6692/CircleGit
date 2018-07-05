/**
 * Created by Administrator on 2017/6/23.
 */
var base64image ="";
var pwd;
var phone;
var userdata = {};
$(document).ready(function () {
    var obj = getUserInfoPhoneWithPwd();
    phone = obj.phone;
    pwd = obj.pwd;
    changeuerimage();
    clickInput();
    //注册
    $("#submitclick").click(function () {
        clearUserinfoPhoneWithPwd();
        var varCard = {};
        var vCard = {};

        varCard.vCard = vCard;
        vCard["@xmlns"] = "vcard-temp";
        vCard.headimg = base64image || '';
        vCard.sex = $("#usersex").val() ||"";
        vCard.address = $("#useraddress").val() || "";
         userdata.username = phone;
         userdata.plainPassword = hex_md5(pwd);
         userdata.name = $("#username").val()||phone;
         userdata.telephone = phone;
        vCard.Name = $("#username").val()||phone;
        vCard.telephone = phone;
        vCard.Email = '';
        vCard.username = phone;
        userdata.varCard = JSON.stringify(varCard);
         // console.log(JSON.stringify(userdata));
         clickRegister(userdata);
    });
     //跳过填写信息
    $("#headrightskip").click(function () {
        clearUserinfoPhoneWithPwd();
        var varCard = {};
        var vCard = {};
        varCard.vCard = vCard;
        userdata.username = phone;
        userdata.name = phone;
        userdata.plainPassword = hex_md5(pwd);
        userdata.telephone = phone;
        vCard["@xmlns"] = "vcard-temp";
        vCard.headimg = base64image || '';
        vCard.Name = '';
        vCard.sex = '';
        vCard.telephone = '';
        vCard.Email = '';
        vCard.address = '';
        vCard.username = '';
        userdata.varCard = JSON.stringify(varCard);
        // console.log(JSON.stringify(userdata));
        clickRegister(userdata);

    });

});
function changeuerimage() {
    mui('body').on('tap', '.mui-popover-action li>a', function() {

        var a = this,
            parent;
        //根据点击按钮，反推当前是哪个actionsheet
        for (parent = a.parentNode; parent != document.body; parent = parent.parentNode) {
            if (parent.classList.contains('mui-popover-action')) {
                break;
            }
        }
        //关闭actionsheet
        mui('#' + parent.id).popover('toggle');
        console.log("你刚点击了\"" + a.innerHTML + "\"按钮");

        if(a.innerHTML == '拍照'){
            dsBridge.call(HEAD_TAKE_PIC_FROM_JS, {}, function (responseData) {
                // var obj = eval('(' + responseData + ')');
                // $('#info').text(obj.base64String);
                $("#head").attr('src','data:image/jpeg;base64,' + responseData);
            })
        }else if(a.innerHTML == '相册'){
            dsBridge.call(HEAD_CHOOSE_PIC_FROM_JS, {}, function (responseData) {
                // var obj = eval('(' + responseData + ')');
                // base64image = obj.base64String;
                // alert(responseData);
                // $('#info').text(obj.base64String);
                $("#head").attr('src','data:image/jpeg;base64,' + responseData);
            })
        }
    });
}

 function clickInput() {
     $("#usersex").focus(function () {
         // alert(localStorage.getItem("phone"));
         $("#selectsex").slideDown()
     });

     // $("#xiala-a").click(function () {
     //     $("#selectsex").slideDown()
     // });

     $("#man").click(function () {
         $("#usersex").val("男");
         $("#selectsex").slideUp();
     });
     $("#woman").click(function () {
         $("#selectsex").slideUp();
         $("#usersex").val("女");
     });

 }

function clickRegister(jsonData) {
    $.ajax({
        url:BASE_URL + "user/reg",
        url:BASE_URL + "user/reg",
        type:"POST",
        async:false,
        data:jsonData,
        timeout:5000,
        dataType:"json",
        beforeSend:function(xhr){
            console.log(xhr)
            console.log("发送前")
            // 显示加载中
            $('.load').show();
            $('body').spin(LOAD_OPTS);
        },
        success:function (data,textStatus,jqXHR) {
            console.log(data);
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            if(data.status != 100){
                mui.toast(data.message);
            }else {
                // mui.toast("注册成功");
                    if(data.status == 100){
                        login(phone,hex_md5(pwd));
                        // saveUserInfo(data.results);
                        // 通知native存储用户信息
                        // dsBridge.call(LOGIN_SUCCESS_FROM_JS, {'userInfo': data.results}, function (responseData) {
                        //     alert("callback from native-->" + responseData);
                        // });
                    }

                // window.history.go(-1);
            }
        },
        error:function (xhr,textStatus) {
            console.log("错误")
            console.log(xhr)
            console.log(textStatus)


        },
        complete:function () {
            console.log("结束")
            $('.load').hide();
            $('body').spin(false);
        }
    });
}