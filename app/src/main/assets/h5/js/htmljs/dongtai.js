/**
 * Created by Administrator on 2017/6/26 0026.
 */
$(document).ready(function() {

    // $('.head').jqthumb({
    //     classname: 'head-container',
    //     width: 44,
    //     height: 44,
    // });
    // $('.img-4').jqthumb({
    //     classname: 'img-container',
    //     width: $(window).width() <= 320? 65:80,
    //     height: $(window).width() <= 320? 65:80,
    // });

    // dropload
    var dropload = $('.mui-content').dropload({
        scrollArea: window,
        autoLoad: false,
        domUp: {
            domClass: 'dropload-up',
            domRefresh: '<div class="dropload-refresh">↓下拉刷新</div>',
            domUpdate: '<div class="dropload-update">↑释放更新</div>',
            domLoad: '<div class="dropload-load"><span class="loading"></span>加载中...</div>'
        },
        domDown: {
            domClass: 'dropload-down',
            domRefresh: '<div class="dropload-refresh">↑上拉加载更多</div>',
            domLoad: '<div class="dropload-load"><span class="loading"></span>加载中...</div>',
            domNoData: '<div class="dropload-noData">暂无数据</div>'
        },
        loadUpFn: function (me) {
            console.log("up");
            dongtai.isLoad = false;
            getAllTrends(dongtai);
        },
        loadDownFn: function (me) {
            if(dongtai.page < dongtai.pageCount){
                console.log("down");
                dongtai.isLoad = true;
                getAllTrends(dongtai);
            }
        },
    });
    dongtai.dropload = dropload;
})


var dongtai = new Vue({
    el: '#dongtai',

    mounted: function () {
        $('.dropload-down').hide();
        getAllTrends(this);
    },

    computed: {

    },
    data: {
        dropload: undefined,
        isLoad: false,             // 是否是加载更多
        page : 1,                  // 当前页
        size : 5,                  // 请求条目数
        total: 0,                  // 总item数量
        pageCount: 1,              // 总页数
        title: '全部动态',
        seenType: false,
        seenComment: false,
        seenEmoji: false,
        commentStr: '',
        emojis:[
            {path: 'img/emoji/草泥马.gif', value: '[草泥马]'},
            {path: 'img/emoji/神马.gif', value: '[神马]'},
        ],
        rows: [],                  // items信息[{zan, imgs, imgsAll,...自定义的一些字段}]
        selectedItem:undefined,    // 选中的item
    },
    methods:{
        toggleType: function () {
            this.seenType = !this.seenType;
        },
        toggleEmoji: function () {
            this.seenEmoji = !this.seenEmoji;
        },
        // 选择动态的type
        changeType: function (title, type) {
            this.title = title;
            this.seenType = false;
        },
        addDongtai: function () {
            // 通知native跳转界面
            dsBridge.call(ADD_DONGTAI_FROM_JS, {}, function (responseData) {
                console.log("callback from native-->" + responseData);
                var obj = eval('(' + responseData + ')');
                if(obj.status == 100){
                    // 刷新列表
                    console.log("刷新列表");
                    dongtai.isLoad = false;
                    getAllTrends(dongtai, dongtai.dropload);
                }
            });
        },
        chat: function (id) {
            this.seenComment = false;
            alert(id);
        },
        toggleComment: function (item) {
            this.seenComment = !this.seenComment;
            this.selectedItem = item;
        },
        chooseEmoji:function (emojiVaule) {
            this.commentStr += emojiVaule;
        },
        sendComment: function () {
            console.log(this.commentStr);
            if(this.commentStr && this.commentStr.length > 0){
                this.seenComment = false;
                addCommentOrLike(this, 1);
                return;
            }
            mui.toast('请输入评论')
        },
        zan: function (item) {
            this.seenComment = false;
            this.selectedItem = item;
            if(item.zan){
                //do unzan
                console.log('unzan');
                cancleCommentOrLike(this, 0)
            }else {
                // do zan
                console.log('zan');
                addCommentOrLike(this, 0);
            }
        },



    },
});


/**
 * 全部动态获取
 */
function getAllTrends(dongtai) {
    var page = dongtai.page;
    page = dongtai.isLoad ? ++page : 1;

    $.ajax({
        url: BASE_URL + 'Trends/getAllTrends',
        type:'POST', //GET
        async:true,    //或false,是否异步
        data:{
            page : page,
            rows: dongtai.size,
            q: getUserInfo().Accesstoken,
            // type: type,
        },
        timeout:5000,    //超时时间
        dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
        beforeSend:function(xhr){
            console.log(xhr);
            console.log('发送前');
            // 显示加载中
            showLoading();
        },
        success:function(data,textStatus,jqXHR){
            console.log(data.results);
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);

            resetDpload();
            // 100 ：成功 101：失败 102:输入错误
            if(data.status == 100){
                // 解析接口返回的数据
                parseData(data);
                // 加载更多
                if(dongtai.isLoad){
                    ++dongtai.page;
                    data.results.rows.map(function (row) {
                        dongtai.rows.push(row);
                    });
                    if(dongtai.page < dongtai.pageCount){
                        // 解锁loadDownFn里锁定的情况
                        showDpBottomLoad();
                    }else {
                        showDpNodata();
                    }
                }
                // 下拉刷新
                else {
                    // 计算总页数
                    dongtai.total = data.results.total;
                    dongtai.pageCount = (dongtai.total == 0 ? 1 : Math.ceil(dongtai.total/dongtai.size));
                    // 重置页数，重新获取loadDownFn的数据
                    dongtai.page = 1;
                    dongtai.rows = data.results.rows;
                    // resetDpload();

                    if(dongtai.pageCount > 1){
                        // 解锁loadDownFn里锁定的情况
                        showDpBottomLoad();
                    }else {
                        showDpNodata();
                    }
                }

            }else
                mui.toast(data.message);
        },
        error:function(xhr,textStatus){
            console.log('错误');
            console.log(xhr);
            console.log(textStatus);
            // 即使错误，必须重置
            resetDpload();
        },
        complete:function(){
            console.log('结束');
            hideLoading();

        }
    });
}

/**
 * 解析接口返回的数据
 * @param data
 */
function parseData(data) {
    if(data.results.rows && data.results.rows.length > 0){
        data.results.rows.map(function (row) {
            // 解析动态图片
            if(row.imagestr && row.imagestr.length>0){
                if(row.imagestr.lastIndexOf(",") == (row.imagestr.length -1)){
                    row.imagestr = row.imagestr.substring(0, row.imagestr.length -1);
                    // console.log(row.imagestr);
                }
                var arr = row.imagestr.split(",");
                row.imgsAll = arr;
                console.log(arr);
                if(arr.length > 4){
                    arr.splice(4, arr.length-4);
                }
                row.imgs = arr;
            }
            // 判断是否点赞过
            row.zan = false;
            if(row.thumbsups && row.thumbsups.length>0){
                row.thumbsups.map(function (thumbsup) {
                    if(thumbsup.token == getUserInfo().Accesstoken){
                        row.zan = true;
                    }
                })
            }
        })
    }
}

function showLoading() {
    $('.load').show();
    $('body').spin(LOAD_OPTS);
}

function hideLoading() {
    $('.load').hide();
    $('body').spin(false);
}

// 底部显示加载中
function showDpBottomLoad() {
    console.log('showDpBottomLoad');
    if(dongtai.dropload){
        dongtai.dropload.unlock();
        dongtai.dropload.noData(false);
        $('.dropload-down').show();
    }
}
function showDpNodata() {
    console.log('showDpNodata');
    if(dongtai.dropload){
        dongtai.dropload.lock('down');
        dongtai.dropload.noData();
    }
    $('.dropload-down').hide();
}
// 每次数据加载完，必须重置
function resetDpload() {
    if(dongtai.dropload){
        console.log('resetload');
        dongtai.dropload.resetload();
        console.log(dongtai.dropload)
    }
}


/**
 * 添加评论和点赞
 * @param dongtai  动态
 * @param type   0评论 1 点赞
 */
function addCommentOrLike(dongtai, type) {

    console.log('type--->'+type);

    var name = '';
    if(getUserInfo().name)
        name = getUserInfo().name;
    else
        name = getUserInfo().username;

    $.ajax({
        url: BASE_URL + 'thumbsup/add',
        type:'POST', //GET
        async:true,    //或false,是否异步
        data:{
            trendsid : dongtai.selectedItem.trendsid,
            type: type,
            token: getUserInfo().Accesstoken,
            contect: dongtai.commentStr,
            name: name,
        },
        timeout:5000,    //超时时间
        dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
        beforeSend:function(xhr){
            console.log('发送前');
            console.log(xhr);
            // 显示加载中
            $('.load').show();
            $('body').spin(LOAD_OPTS);
        },

        success:function(data,textStatus,jqXHR){
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            mui.toast(data.message);
            // 100 ：成功 101：失败 102:输入错误
            if(data.status == 100){
                if(type == 0){
                    dongtai.selectedItem.zan = true;
                }
            }
            // 调用最新数据，刷新
            getCommentOrLike(dongtai, type);
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
/**
 * 取消评论和点赞
 * @param dongtai  动态
 */
function cancleCommentOrLike(dongtai, type) {
    console.log('type--->'+type);
    var name = '';
    if(getUserInfo().name)
        name = getUserInfo().name;
    else
        name = getUserInfo().username;

    $.ajax({
        url: BASE_URL + 'thumbsup/unthumbsup',
        type:'POST', //GET
        async:true,    //或false,是否异步
        data:{
            trendsid : dongtai.selectedItem.trendsid,
            type: type,
            token: getUserInfo().Accesstoken,
            contect: dongtai.commentStr,
            name: name,
        },
        timeout:5000,    //超时时间
        dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
        beforeSend:function(xhr){
            console.log('发送前');
            // 显示加载中
            $('.load').show();
            $('body').spin(LOAD_OPTS);
        },

        success:function(data,textStatus,jqXHR){
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);
            mui.toast(data.message);
            // 100 ：成功 101：失败 102:输入错误
            if(data.status == 100){
                if(type == 0){
                    dongtai.selectedItem.zan = false;
                }
            }
            getCommentOrLike(dongtai, type);
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
/**
 * 获取评论和点赞
 * @param dongtai  动态
 */
function getCommentOrLike(dongtai, type) {

    $.ajax({
        url: BASE_URL + 'thumbsup/getthumbsups',
        type:'GET', //GET
        async:true,    //或false,是否异步
        data:{
            trendsid : dongtai.selectedItem.trendsid,
            type: type,
        },
        timeout:5000,    //超时时间
        dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
        beforeSend:function(xhr){
            console.log('发送前');
            // 显示加载中
            $('.load').show();
            $('body').spin(LOAD_OPTS);
        },

        success:function(data,textStatus,jqXHR){
            console.log("textStatus:  " +　textStatus);
            console.log(jqXHR);

            // 100 ：成功 101：失败 102:输入错误
            if(data.status == 100){
                if(type == 0){ //点赞
                    dongtai.selectedItem.thumbsups = data.results;
                }else {
                    dongtai.selectedItem.comment = data.results;
                }
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


