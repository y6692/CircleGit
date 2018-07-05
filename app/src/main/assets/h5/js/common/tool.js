/**
 * Created by Administrator on 2017/6/26 0026.
 */
/**
 * 压缩 & base64编码
 * @param img  Dom对象（勿传jquery对象）
 * @param size  最大尺寸
 * @returns {string} base64编码后的字符串
 */
function getBase64Image(img, size) {
    var canvas = document.createElement("canvas");

    var square = size || 700;
    canvas.width = square;
    canvas.height = square;

    var ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, square, square);

    console.log('开始压缩图片');
    var imageWidth;
    var imageHeight;
    var offsetX = 0;
    var offsetY = 0;
    if(img.width < square && img.height < square){
        imageWidth = img.width;
        imageHeight = img.height;
    } else if (img.width > img.height) {
        imageWidth = Math.round(square * img.width / img.height);
        imageHeight = square;
        offsetX = - Math.round((imageWidth - square) / 2);
    } else {
        imageHeight = Math.round(square * img.height / img.width);
        imageWidth = square;
        offsetY = - Math.round((imageHeight - square) / 2);
    }
    ctx.drawImage(img, offsetX, offsetY, imageWidth, imageHeight);
    console.log('压缩图片成功');

    console.log('开始base64编码');
    try
    {
        var dataURL = canvas.toDataURL("image/png");
        console.log('base64编码成功');
        return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
    }
    catch (e)
    {
        alert(e.message);
        alert(e.description)
        alert(e.number)
        alert(e.name)
    }
}

/**
 *
 * @param username
 * @param plainPassword
 */
function login(username, plainPassword) {
    $.ajax({
        url: BASE_URL + 'user/login',
        type:'POST', //GET
        async:true,    //或false,是否异步
        data:{
            username :username,
            plainPassword:plainPassword,
            // username :'dingd',plainPassword:'e10adc3949ba59abbe56e057f20f883e'
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
            $('.load').hide();
            $('body').spin(false);
            mui.toast(data.message);
            // 100 ：成功 101：失败 102:输入错误
            if(data.status == 100){
                // 存储在localStorage
                data.results.plainPassword = plainPassword;
                saveUserInfo(data.results);
                // 通知native存储用户信息
                dsBridge.call(LOGIN_SUCCESS_FROM_JS, {'userInfo': data.results}, function (responseData) {
                    alert("callback from native-->" + responseData);
                });
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
        }
    });
}

/**
 * 我的动态获取
 */
function getMyTrends(page, rows) {
    $.ajax({
        url: BASE_URL + 'Trends/getmyTrends',
        type:'POST', //GET
        async:true,    //或false,是否异步
        data:{
            page :page,
            rows: rows,
            q: getUserInfo().Accesstoken,
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
            // 100 ：成功 101：失败 102:输入错误
            if(data.status == 100){
                console.log(data.results.rows);
                return data.results.rows;
            }else
                mui.toast(data.message);
        },
        error:function(xhr,textStatus){
            console.log('错误');
            console.log(xhr);
            console.log(textStatus);
        },
        complete:function(){
            console.log('结束');
            $('.load').hide();
            $('body').spin(false);
        }
    });
}

