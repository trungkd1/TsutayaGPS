(function() {
	var STORE_REGIST_BUTTON_MESSAGE='ご利用店舗に登録';
	var STORE_DELETE_BUTTON_MESSAGE ='登録を解除する';
	
	var curl = 'img/store_registered.png';
    var uurl = '';
    
var InfoTemplate = new TTemplate(
'<li id="store-info-{storeId}-{order}" class="arrow store-info"><h4>{title}</h4> \
{:if infoUpdateTime}<p class="last-update">(更新日:{infoUpdateTime})</p>{:end}'
);

var TelTemplate = new TTemplate(
'<a href="tel:{telNo}">{telNo}</a>'
);

var StoreDetailViewTemplate = new TTemplate(
     '<h1 class="page-title" id="store-name-{storeId}">{storeName}</h1>'
+    '<div id="product-header-section" class="rounded">'
+     '<div id="store-image-area">'
+      '<div class="store-completedimage">'
+      '{:if storeAdded}<img src="' + curl + '" style="position:absolute; width:100px; height:100px; top: 37px; right: 6px;"/>' + '{:end}'
+		'</div>'
+      '<ul>'
+      '<a href="#iconInfo">'
+      '{:if TLogo}<li><img src="img/ico_kamei_01.png" alt="TSUTAYAレンタル加盟店です" /></li>{:end}'
+      '{:if RLogo}<li><img src="img/ico_kamei_02.png" alt="TSUTAYA RECORDS加盟店です" /></li>{:end}'
+      '{:if GLogo}<li><img src="img/ico_kamei_03.png" alt="GAME TSUTAYA加盟店です" /></li>{:end}'
+      '{:if BLogo}<li><img src="img/ico_kamei_04.png" alt="TSUTAYA BOOKS加盟店です" /></li>{:end}'
+      '{:if RCLogo}<li><img src="img/ico_kamei_05.png" alt="リサイクルTSUTAYA加盟店です" /></li>{:end}'
+      '{:if ELogo}<li><img src="img/ico_kamei_06.png" alt="TSUTAYA ecobooks加盟店です" /></li>{:end}'
+      '</a>'
+      '</ul>'
+      '<div id="store_icon_container" style="position: relative; width:0px; ">'
+      '<p><img id="store_icon" style="width:200px; " src="{storeFilm}" class="lazyloadimage" alt="{storeName}" title="{storeName}" /></p>'
+     '</div>'

+     '</div>'
+   '</div>'
+   '<div>'
+     '<p class="add-store-button"><input type="submit" id="add-{storeId}" class="submit-button btn-blue" value="'
+     '{:if storeAdded}' + STORE_DELETE_BUTTON_MESSAGE + '{:else}'
+     STORE_REGIST_BUTTON_MESSAGE + '{:end}"></input></p>'
+   '</div>'
+   '<h3>お店からのお得情報</h3>'
+   '<ul id="information-list" class="rounded bordered">'
+   '{:if infoList} '
+   '{infoList}'
+   '{:else}'
+   '<li>お得情報は現在登録されておりません。</li>'
+   '{:end}'
+   '</ul>'
+      '<h3>基本情報</h3>'
+      '<dl class="rounded bordered">'
+          '<dt>住所</dt>'
+          '<dd class="arrow store-address" id="store-address-{storeId}"><div class="address-block">〒{zipCode1}-{zipCode2}<br />{todofuken}{commune}{houseNumber}</div></dd>'
+          '<dt>電話番号</dt>'
+          '<dd>{telno}</dd>'
+        '{:if faxno}'
+          '<dt>FAX番号</dt>'
+          '<dd>{faxno}</dd>'
+        '{:end}'
+          '<dt>営業時間</dt>'
+          '<dd>{bussinessHours}'
+          '{:if regularHoliday}<br />{regularHoliday}{:end}</dd>'
+          '{:if returnBoxFlag}<dt>店外返却BOX</dt>'
+          '<dd>{:if returnBoxAriFlag}あり{:else}なし{:end}'
+          '{:if returnBoxExplanation}({returnBoxExplanation}){:end}</dd>{:end} '
+      '</dl>'
+  '<h3>取扱い商品</h3>'
+  '<ul id="service-list" class="rounded">'
+  '{:if handlingInfo.rentalDVD.handlingFlag || handlingInfo.rentalCD.handlingFlag || handlingInfo.rentalComic.handlingFlag}'
+   '<li>'
+    '<h4>レンタル</h4>'
+     '<table><tbody>'
+    '{:if handlingInfo.rentalDVD.handlingFlag}'
+      '<tr><td class="name">DVD＆ブルーレイ＆ビデオ</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.rentalDVD.tPoint} '
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+    '{:if handlingInfo.rentalCD.handlingFlag}'
+      '<tr><td class="name">CD</td>'
+      '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.rentalCD.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+    '{:if handlingInfo.rentalComic.handlingFlag}'
+      '<tr><td class="name">コミック</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.rentalComic.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+     '</tbody></table>'
+    '</li>'
+  '{:end}'
+  '{:if handlingInfo.sellDVD.handlingFlag || handlingInfo.sellCD.handlingFlag || handlingInfo.sellGame.handlingFlag || handlingInfo.sellBook.handlingFlag}'
+    '<li>'
+    '<h4>新品販売</h4>'
+     '<table><tbody>'
+    '{:if handlingInfo.sellDVD.handlingFlag}'
+      '<tr><td class="name">DVD＆ブルーレイ＆ビデオ</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.sellDVD.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+    '{:if handlingInfo.sellCD.handlingFlag}'
+      '<tr><td class="name">CD</td>'
+        '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.sellCD.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+    '{:if handlingInfo.sellBook.handlingFlag}'
+      '<tr><td class="name">本・コミック</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.sellBook.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+    '{:if handlingInfo.sellGame.handlingFlag}'
+      '<td class="name">ゲーム</td>'
+        '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.sellGame.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+     '</tbody></table>'
+    '</li>'
+  '{:end}'
+  '{:if handlingInfo.usedSellDVD.handlingFlag || handlingInfo.usedSellBook.handlingFlag || handlingInfo.usedSellCD.handlingFlag || handlingInfo.usedSellGame.handlingFlag}'
+    '<li>'
+    '<h4>中古販売</h4>'
+    '<table><tbody>'
+    '{:if handlingInfo.usedSellDVD.handlingFlag}'
+      '<tr><td class="name">DVD＆ブルーレイ＆ビデオ</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedSellDVD.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      "</td></tr>"
+    '{:end}'
+    '{:if handlingInfo.usedSellCD.handlingFlag}'
+      '<tr><td class="name">CD</td>'
+        '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedSellCD.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      "</td></tr>"
+    '{:end}'
+    '{:if handlingInfo.usedSellBook.handlingFlag}'
+      '<tr><td class="name">本・コミック</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedSellBook.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+    '{:if handlingInfo.usedSellGame.handlingFlag}'
+      '<tr><td class="name">ゲーム</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedSellGame.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+    '{:end}'
+    '</tbody></table>'
+    '</li>'
+    '{:end}'
+    '{:if handlingInfo.usedBuyDVD.handlingFlag ||handlingInfo.usedBuyCD.handlingFlag ||handlingInfo.usedBuyBook.handlingFlag ||handlingInfo.usedBuyGame.handlingFlag}'
+    '<li>'
+    '<h4>中古買取</h4>'
+      '<table><tbody>'
+     '{:if handlingInfo.usedBuyDVD.handlingFlag}'
+      '<tr><td class="name">DVD＆ブルーレイ＆ビデオ</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedBuyDVD.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+     '{:end}'
+     '{:if handlingInfo.usedBuyCD.handlingFlag}'
+      '<tr><td class="name">CD</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedBuyCD.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+     '{:end}'
+     '{:if handlingInfo.usedBuyBook.handlingFlag}'
+      '<tr><td class="name">本・コミック</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedBuyBook.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+     '{:end}'
+     '{:if handlingInfo.usedBuyGame.handlingFlag}'
+      '<tr><td class="name">ゲーム</td>'
+       '<td class="l-text">○</td>'
+      '<td>'
+      '{:if handlingInfo.usedBuyGame.tPoint}'
+        '<img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />'
+      '{:end}'
+      '</td></tr>'
+     '{:end}'
+     '</tbody></table>'
+   '</li>'
+  '{:end}'
+    '{:if additionalItemsInfo}'
+    '<li>'
+    '<h4>その他の商品</h4>'
+      '<table><tbody>'
+       '{additionalItemsInfo}'
+      '</tbody></table>'
+    '</li>'
+  '{:end}'
+  '<div class="description">'
+  '<ul>'
+  '<li>｢<span>○</span>｣：お取扱いあり</li>'
+  '<li><img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" />：Ｔポイントがご利用いただけるサービスです</li>'
+  '<li>※ブルーレイ、ビデオの取扱いは店舗により異なります。詳細は店舗にご確認ください。</li>'
+  '<li>※中古販売・買取商品は店舗により異なります。詳細は店舗にご確認ください。</li>'
+  '</ul>'
+  '</div>'
+  '</ul>'
+  '{:if handlingServiceList || serviceInfo.shopReceiptPropriety || serviceInfo.mailDeliveryFlg}'
+  '<h3>取扱いサービス</h3>'
+  '<ul id="service-list" class="rounded">'
+   '<li>'
+   '<table class="list2">'
+   '<tbody>'
+   '{:if serviceInfo.shopReceiptPropriety}'
+     '<tr><td  class="name"><a href="http://www.tsutaya.co.jp/tty_spcont/apli_uketori.html">通販店頭受取り</a></td></tr>'
+   '{:end}'
+   '{:if serviceInfo.mailDeliveryFlg}'
+    '<tr><td  class="name"><a href="http://www.tsutaya.co.jp/cp/w2010/post/sp/index.html">郵便返却</a></td></tr>'
+   '{:end}'
+   '{additionalServiceInfo}'
+   '</tbody></table>'
+   '</li>'
+  '</ul>'
+  '{:end}'
+  '<h3 id="iconInfo">アイコンの説明</h3>'
+      '<ul id="icon-description" class="rounded">'
+        '<li><img src="img/ico_kamei_tpoint.png" alt="Tポイント" title="Tポイント" />'
+          'Tポイントがご利用いただけるサービスです</li>'
+        '<li><img src="img/ico_kamei_01.png" alt="TSUTAYAレンタル" title="TSUTAYAレンタル" />'
+          'TSUTAYAレンタル加盟店です</li>'
+        '<li><img src="img/ico_kamei_02.png" alt="TSUTAYA RECORDS" title="TSUTAYA RECORDS" />'
+          'TSUTAYA RECORDS加盟店です</li>'
+        '<li><img src="img/ico_kamei_03.png" alt="GAME TSUTAYA" title="GAME TSUTAYA" />'
+          'GAME TSUTAYA加盟店です</li>'
+        '<li><img src="img/ico_kamei_04.png" alt="TSUTAYA BOOKS" title="TSUTAYA BOOKS" />'
+          'TSUTAYA BOOKS加盟店です</li>'
+        '<li><img src="img/ico_kamei_05.png" alt="リサイクルTSUTAYA加盟店です" title="リサイクルTSUTAYA"/>'
+           'リサイクルTSUTAYA加盟店です</li>'
+        '<li><img src="img/ico_kamei_06.png" alt="TSUTAYA ecobooks加盟店です" title="TSUTAYA ecobooks"/>'
+           'TSUTAYA ecobooks加盟店です</li>'
+      '</ul>'
+  '{:if storeIntroduction}'
+   '<h3>お店紹介</h3>'
+     '<div class="rounded">'
+        '<div id="storeIntroduction">{storeIntroduction}</div>'
+      '</div>'
+   '{:end}'
+      '<div>'
+         '<p class="add-store-button"><input type="submit" id="add-{storeId}" class="submit-button btn-blue" value="'
+         '{:if storeAdded}' + STORE_DELETE_BUTTON_MESSAGE + '{:else}'
+         STORE_REGIST_BUTTON_MESSAGE + '{:end}"></input></p></div>'
);

var CompletedTemplate = new TTemplate('<img src={CURL} Border="0" Width="70" Height="70">');
var StoreDetailView = function() {};
StoreDetailView.prototype.arrayToHTML = function(arr) {
    return arr.length > 0  ?
            TTemplate.raw(arr.join('<br />')) : '';
};
StoreDetailView.prototype.replaceBr = function(text) {
    if (!text) {
        return '';
    };
    return TTemplate.raw(TTemplate.escapeHTML(text).replace(/\n/g, '<br />'));
}
StoreDetailView.prototype.render = function(data) {
    var entry = data['entry'];
    if (!entry) {
        alert('お店情報が参照できません')
    };
    var telnoList = [];
    if (entry['telephoneNumberList']) {
        for (var i = 0; i < entry['telephoneNumberList'].length; i++) {
            var tel = entry['telephoneNumberList'][i];
            telnoList.push(
                [tel['outAreaNumber'], tel['areaNumber'], tel['affiliateNumber']].join('-'));
        };
    };

    var bussinessHourList = [];
    if (entry['bussinessTimeList']) {
        for (var i = 0; i < entry['bussinessTimeList'].length; i++) {
            var e = entry['bussinessTimeList'][i];
            bussinessHourList.push(
                [
                    e['bussinessTimeStartAmPm'],
                    e['bussinessTimeStart'],
                    '-',
                    e['bussinessTimeEndAmPm'],
                    e['bussinessTimeEnd'],
                    e['bussinessTimeType'] ? '(' + e['bussinessTimeType'] + ')' : ''
                ].join('')
            );
        };
    };

    var regularHolidayList = [];
    if (entry['regularHolidayList']) {
        for (var i = 0; i < entry['regularHolidayList'].length; i++) {
            var e = entry['regularHolidayList'][i]
            regularHolidayList.push( e['regularHoriday'] );
        };
    };

    var infoListHTML = '';
    if (entry['registrationStoreInfo']) {
        var ret = [];
        var infoList = entry['registrationStoreInfo'][0]['profitInfo'];
        for (var i = 0; i < infoList.length; i++) {
            ret.push(
                InfoTemplate.render( {
                    storeId: entry['storeId'],
                    order: infoList[i]['order'],
                    title: infoList[i]['title'],
                    infoUpdateTime: infoList[i]['infoUpdateTime']
                        ? formatDate(infoList[i]['infoUpdateTime']) : ''
                })
            );
        };
        if (ret.length > 0) {
            infoListHTML = ret.join('');
        };
    };

    var regularHoliday = this.arrayToHTML(regularHolidayList);
    var handlingInfo = entry['storeHandlingItemInfo'];
    var storeList = new StoreList();

    var tel=[];
    for (i = 0; i <  telnoList.length; i++) {
         tel.push(
             TelTemplate.render({
                  telNo:telnoList[i]
             })
         );
    };
   var handlingServices = entry['handlingServiceList'];
   var additionalServices = [];
   if(handlingServices){
	   var sent;	   
	   for (i=0; i < handlingServices.length; i++ ) {
		   sent = handlingServices[i];
		   if(sent.useFlg=='1'){
		   additionalServices.push(
		     '<tr><td class="name">'
		+    '<a href="' + sent.serviceLinkSp +'">'
		+    sent.serviceName + '</a></td></tr>'		   
		   );
		   }
	   }
   }
   var svcList = additionalServices.join('');
 /*  取り扱いアイテム対応 */
   var handlingItems = entry['handlingItemList'];
   var additionalItems = [];
   if(handlingItems){
	   var otherItem;
	   for(i=0; i < handlingItems.length; i++ ){
		   otherItem = handlingItems[i];

		   if(otherItem.useFlg == '1'){
			   additionalItems.push(
			      '<tr><td class="name">'
			+     otherItem.serviceName + '</td>'
			+     '<td class="l-text">○</td><td></td></tr>'
			   );
		   } else if(otherItem.useFlg == '2'){
			   additionalItems.push(
					      '<tr><td class="name">'
					+     otherItem.serviceName + '</td>'
					+     '<td class="l-text">○</td>'
					+     '<td><img src="img/ic_tolstcstsvc01.png" alt="Tポイント" title="Tポイント" /></td></tr>'
			   );
		   }
	   }
   }
   var additionalItemsList = additionalItems.join('');

    var params = {
    	BLogo: entry['BLogo'],
    	GLogo: entry['GLogo'],
    	RLogo: entry['RLogo'],
    	TLogo: entry['TLogo'],
    	ELogo: entry['ELogo'],
    	RCLogo: entry['RCLogo'],
        bussinessHours: this.arrayToHTML( bussinessHourList ),
        telno: this.arrayToHTML(tel),
        storeIntroduction: this.replaceBr(entry['storeIntroduction']),
        regularHoliday: regularHoliday,
        zipCode1: entry['zipCode1'],
        zipCode2: entry['zipCode2'],
        todofuken: entry['todofuken'],
        commune: entry['commune'],
        houseNumber: entry['houseNumber'],
        returnBoxFlag: entry['returnBoxFlag'] == '0' ? '' : '1',
        returnBoxAriFlag: entry['returnBoxFlag'] == '1' ? '1' : '',
        returnBoxExplanation: entry['returnBoxExplanation'],
        storeFilm: entry['storeFilm'],
        storeName: entry['storeName'],
        storeAdded: storeList.isStoreAdded(entry['storeId']),
        handlingInfo: handlingInfo,
        serviceInfo: entry['storeHandlingServiceInfo'],
        additionalServiceInfo: svcList !== '' ? TTemplate.raw(svcList) : '',
        additionalItemsInfo: additionalItemsList !== '' ? TTemplate.raw(additionalItemsList) : '',
        infoList: infoListHTML !== '' ? TTemplate.raw(infoListHTML) : '',
        storeId: entry['storeId']
    };

    return StoreDetailViewTemplate.render(params);
};


function CompletedImageLoad(){
		 var html = [];
		 html.push(CampaignTemplate.render({CURL: curl}));
		 $('#store-completedimage').html(html.join(''));
 }

function CompletedImageLoad(e) {

	 //var id = "store-completedimage";
	 var className = ".store-completedimage";
	 //app.goTo("app::mes::" + e.value + "::");
	 //登録を解除する
	 //img/store_completed.png
 
    if(e.value == STORE_DELETE_BUTTON_MESSAGE ){
        $(className).html("<img src=\""+ curl + "\" style=\"position:absolute; width:100px; height:100px; top: 37px; right: 6px;\"/>");
        //document.all.item(id).innerHTML("<img src=\""+ curl + "\" Border=\"0\" Width=\"70\" Height=\"70\">");
        //window.location.reload();
    }else{
        $(className).html('');
        
    }
}


$(function() {
    var params = getICSQueryParameters();

    var storeId = params['storeId'];

    app.startLoading();

    TWSClient.sendRequest('/store/v0/store/detail.json', {
        data: {
            fieldSet: 'registrationStoreInfo',
            'registrationStoreInfo.processSection': 1,
            storeId: storeId

        },
        complete: function() {
            app.stopLoading();

            $('img.lazyloadimage').lazyload({
                placeholder : 'img/loading.gif',
                threshold : 0,
                effect : "fadeIn"
            });
        },
        error: function(xhr) {
            app.alert('エラー', Messages.networkError, app.goBack );
        },
        success : function(data) {
            var errMsg = '';
            if (data.error) {
                if (data.error.code == 503) {
                    errMsg = data.error.message;
                } else {
                    errMsg = Messages.serverError;
                }
                app.alert('エラー', errMsg, app.goBack );
                return;
            };
            var html = new StoreDetailView().render(data);
						
            $('#store-detail').html(html);
			
			var winWidth = $(window).width();
			var iconWidth = $("#store_icon").width();
			if(iconWidth == 0)
			{
				iconWidth = 200;
			}
			var marginLeft = (winWidth - iconWidth - 22)/2;
			$("#store_icon_container").css("margin-left", marginLeft);
	  
            $('.store-address').click( function(event) {
                var storeId = this.id.replace("store-address-","");
                app.goTo("app::storemap::" + storeId);
            });
            $('.submit-button').click( function(event) {
                event.preventDefault();
                event.stopPropagation();
                storeAddButtonClick( event.target );
                CompletedImageLoad(event.target);
                $('.submit-button').val($(this).val());
            });
            $('.store-info').click( function(event) {
                var storeId, item;
                if (this.id.match(/store-info-(\d+)-(\d+)/)) {
                    storeId = RegExp.$1;
                    item = RegExp.$2;
                }
                app.goTo('store_info.html?storeId=' + storeId + '&item=' + item );
            });
        }
    })
});

})();

