/**
 * Created by Administrator on 2017/6/14 0014.
 */
const BASE_URL = 'http://192.168.10.201:8000/api/';

/**
 * 原生与js之间交互定义的方法名
 * @type {string}
 */
// from Js
const HEAD_TAKE_PIC_FROM_JS= "headTakePicFromJs";
const HEAD_CHOOSE_PIC_FROM_JS= "headChoosePicFromJs";
const LOGIN_SUCCESS_FROM_JS= "loginSuccessFromJs";
const EXIT_FROM_JS= "exitFromJs";
const CHANGE_UserInfo_FROM_JS= "changeUserInfoFromJs";
const ADD_DONGTAI_FROM_JS= "addDongtaiFromJs";
const MINE_SETTING_FROM_JS = "mineSettingFromJs";
const CLOSE_PAGE_FROM_JS = "closePageFromJs";
// from native
const GET_USERINFO_FROM_NATIVE= "getUserInfoFromNative";

//saveRegister key
const REGISTERPHONE = "registerphone123";
const REGISTERPWD = "registerpwd123";

//vCard {@xmlns:"",headimg:"",sex:"",address:""};


// 解决Android WebView中出现 Not allowed to load local resource
const IMG_HEADER = "http://localpath/";

const AppKey = "cb2c138cb0a1d5f7867bebbb";
const MasterSecret = "168521ff4b7acdecbaedebd9";
/**
 * 存储用户信息
 * obj
 * @param userInfo
 */
function saveUserInfo(userInfo){
    if(window.localStorage){
        var objStr = JSON.stringify(userInfo);
        // console.log("存储用户信息" + objStr);
        localStorage.setItem("userInfo",objStr);
    }else{
        alert("浏览器还不支持 web storage 功能");
    }
}
/**
 * 获取用户信息
 * @returns {Object}
 * {username ,Accesstoken, creationdate,modificationdate ,telephone , email, varCard }
 */
function getUserInfo(){
    if(window.localStorage){
        // console.log("获取用户信息" + eval('(' + localStorage.getItem("userInfo") + ')'));
        return eval('(' + localStorage.getItem("userInfo") + ')');
    }else{
        alert("浏览器还不支持 web storage 功能");
    }
}
//保存注册时填写的电话和密码
function registerUserInfoSavePhoneWithPwd(phone,pwd) {
    localStorage.setItem(REGISTERPHONE ,phone);
    localStorage.setItem(REGISTERPWD,pwd);
}
//获取注册时填写的电话和密码
function getUserInfoPhoneWithPwd() {
    var obj = {};
    obj.phone = localStorage.getItem(REGISTERPHONE);
    obj.pwd = localStorage.getItem(REGISTERPWD);
    return obj;
}
//清空注册时填写的电话和密码
function clearUserinfoPhoneWithPwd() {
    localStorage.removeItem(REGISTERPHONE);
    localStorage.removeItem(REGISTERPWD);
}
function clearuserInfo() {
    localStorage.removeItem("userInfo");
}
//解析用户名片
function getUservcard() {
    // console.log(JSON.stringify(getUserInfo().varCard));
    if(getUserInfo().varCard){
        var varCard = eval('(' + getUserInfo().varCard + ')');
        var vCard = varCard.vCard;
        // console.log(vCard);
        return vCard;
    }else
        return {};
}
function backPageFromJs() {
    window.history.go(-1);
    dsBridge.call(CLOSE_PAGE_FROM_JS,{},function () {

    });
}
// 加载中的动画效果的配置
const LOAD_OPTS = {
    lines: 13 // The number of lines to draw
    ,length: 28 // The length of each line
    , width: 14 // The line thickness
    , radius: 42 // The radius of the inner circle
    , scale: 0.25 // Scales overall size of the spinner
    , corners: 1 // Corner roundness (0..1)
    , color: '#fff' // #rgb or #rrggbb or array of colors
    , opacity: 0.25 // Opacity of the lines
    , rotate: 0 // The rotation offset
    , direction: 1 // 1: clockwise, -1: counterclockwise
    , speed: 1 // Rounds per second
    , trail: 60 // Afterglow percentage
    , fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
    , zIndex: 2e9 // The z-index (defaults to 2000000000)
    , className: 'spinner' // The CSS class to assign to the spinner
    , top: '50%' // Top position relative to parent
    , left: '50%' // Left position relative to parent
    , shadow: false // Whether to render a shadow
    , hwaccel: false // Whether to use hardware acceleration
    , position: 'absolute' // Element positioning
}



