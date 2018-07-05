/**
 * 通用组件类
 * Created by Administrator on 2017/6/24 0024.
 */

// 分隔符 高度12px
Vue.component('space', {
    template: '<div class="space"></div>',
});

// 分隔符 高度1px
Vue.component('divider', {
    template: '<div class="divider"></div>',
})


/**
 * font
 */
Vue.component('font-26-333', {
    props: ['text'],
    template: '<span class="font font-26-333">{{ text }}</span>',
})
Vue.component('font-36-000', {
    props: ['text'],
    template: '<span class="font font-36-000">{{ text }}</span>',
})
Vue.component('font-26-aaa', {
    props: ['text'],
    template: '<span class="font font-26-aaa">{{ text }}</span>',
})

// 评论
Vue.component('comment', {
    props: {
        comment:Object,
    },
    data: function () {
        var str = "";
        if(this.comment && this.comment.contect){
            str = this.comment.contect;
            if(str.indexOf("[")!=-1){
                str = str.replace(/\[/g,"<img class='emoji-size' src='img/emoji/").replace(/\]/g,".gif'/>");
            }
        }

        return{
            commentStrHtml: str,
        }
    },

    template: '<div>' +
    '<span class="font font-26-blue" style="margin-right: 6px">{{comment.name}}:</span>' +
    '<span class="font font-26-333" v-html="commentStrHtml"></span>' +
    '</div>'
})

Vue.component('dongtai-head', {
    props: {
        vcard:String,
    },
    data: function () {
        var str = 'img/register/touxiang.png';
        console.log(this.vcard);
        if(this.vcard){
            var obj = eval('(' + this.vcard + ')');
            if(obj.vCard && obj.vCard.headimg){
                str = 'data:image/jpeg;base64,' + obj.vCard.headimg;
            }
        }

        return{
            head: str,
        }
    },
    template: '<img class="head" v-bind:src="head">',
})

//时间戳转当地时间
Vue.component('local-time', {
    props: {
        timestr:String,
    },
    data: function () {
        var timeChuoInt = Number(this.timestr);
        var newDate = new Date();
        newDate.setTime(timeChuoInt * 1000);
        console.log(newDate.toLocaleString());
        return{
            time: newDate.toLocaleString(),
        }

    },
    template: '<span class="font font-26-aaa">{{ time }}</span>',
})


Vue.component('dongtai-like', {
    props: {
        thumbsups:Array,
    },
    data: function () {
        var str = '';

        this.thumbsups.map(function (item) {
            str += item.name + ",";
        });
        str = str.substring(0, str.length-1);
        return{
            persons: str,
            count: this.thumbsups.length,
        }
    },
    template: '<span class="font font-26-blue dongtai-like">{{ persons }}等{{count}}人觉得很赞</span>',
})

