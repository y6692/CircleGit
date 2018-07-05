/**
 * Created by Administrator on 2017/7/5.
 */
$(document).ready(function () {
    if(getUservcard().headimg)
        $("#head").attr('src','data:image/jpeg;base64,' + getUservcard().headimg);
    var user = getUserInfo();
    $('#name').text(user.username);

});