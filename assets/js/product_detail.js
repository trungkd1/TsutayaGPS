var ProductDetailTemplate = new TTemplate(
    '<h1 class="page-title">商品情報</h1>'
+   '<div id="product-header-section" class="rounded">'
+    '<div class="box-wrapper">'
+     '<div class="jacket-image-medium-box">'
+       '<img src="{jacketImage}" alt="" title="" class="jacket-image-medium" />'
+     '</div>'
+     '<div class="description-box">'
+      '<h2 class="title-long">{productName}{:if kansu}({kansu}){:end}&nbsp;'
+       '{:if captionDubSection}（{captionDubSection}）{:end}'
+       '{:if lmdName}（{lmdName}）{:end}{:if attachmentDiscSection}（{attachmentDiscSection}）{:end}'
+       '{:if handlingCd}（{handlingCd}）{:end}{:if releaseName}（{releaseName}）{:end}'
+       '{:if subtitle || seriesName} '
+         '<br /><span>{subtitle}　{seriesName}</span>'
+       '{:end}'
+      '</h2>'
+      '<div class="tag">'
+        '<span class="sell-rental-disp">{:if isRental}レンタル{:else}販売{:end}</span> <span class="genre-disp">{itemNameDisp}</span>'
+      '</div>'
+      '<div class="genre-path">{lGenreName} {:if mGenreName} &gt; {mGenreName}{:end}{:if sGenreName} &gt; {sGenreName}{:end}</div>'
+      '<div>{mediaFormatSection}</div>'
+       '<dl>'
+         '{:if saleDate}<dt>発売日</dt>{saleDate}</dd>{:end}'
+         '{:if rentalStartDate}<dt>レンタル開始日</dt><dd>{rentalStartDate}</dd>{:end}'
+         '{:if publisherYearMonth}<dt>発売年月</dt><dd>{publisherYearMonth}</dd>{:end}'
+         '{:if artistName1}<dt>{roleName1}</dt><dd>{artistName1}</dd>{:end}'
+         '{:if artistName2}<dt>{roleName2}</dt><dd>{artistName2}</dd>{:end}'
+         '{:if priceTax}<dt>定価（税込）</dt><dd>{priceTax}円<br /><span class="small">※価格は店舗によって異なります。</span></dd>{:end}'
+       '</dl>'
+     '</div>'
+    '</div>'
+   '</div>'
+   '<div class="social-box-wrapper">'
+     '<ul class="social-box rounded">'
+       '<li class="social-button" id="twitter-button"><a href="app::safari::http://twitter.com/share?text={productName}{:if kansu}({kansu}){:end}&url=http://www.tsutaya.co.jp/works/{urlCd}.html">ツイート</a></li>'
+       '<li class="social-button" id="facebook-button"><a href="app::safari::http://www.facebook.com/sharer.php?u=http://www.tsutaya.co.jp/works/{urlCd}.html">シェア</a></li>'
+       '<li class="social-button" id="line-button"><a href="app::safari::http://line.naver.jp/R/msg/text/?{productName}{:if kansu}({kansu}){:end}http://www.tsutaya.co.jp/works/{urlCd}.html">LINEで送る</a></li>'
+     '</ul>'
+   '</div>'
+	'<div class="fav-box-wrapper">'
+	'{:if urlCd}<div class="fav-box rounded btn-blue" id="works-{urlCd}" onClick="productfavButtonClick(\'{urlCd}\')">お気に入り{:if favadded}を解除{:else}に登録{:end}</div>{:end}'
+	'</div>'
+	'</div>'
+   '<h3>在庫情報</h3>'
+   '<div id="stock-info-area">'
+   '<p class="stock-causion">※在庫状況は現時点のものではありません。必ずお店に在庫状況をご確認ください。</p>'
+   '{:if stockInfoList}'
+     '<ul id="stock-info-list" class="bordered rounded">'
+       '{stockInfoList}'
+     '</ul>'
+   '{:else}<div id="store-list" class="default-padding rounded" onClick="app.goToStoreMenu()">'
+     '<br />在庫を検索するには、よく使うお店を登録しておくと便利です。'
+     '店舗検索メニューからお店を検索して、お店を登録してください。<br /><br /></div>'
+   '{:end}'
+   '</div>'
+   '<h3>マップで在庫検索</h3>'
+   '<div id="stock-map-link">'
+   '<ul class="rounded"><li class="arrow" onclick="mapLinkClick(\'{productKey}\')">'
+     '<div class="arrow-block"><span class="link-label">お店で{:if isRental}レンタル{:else}販売{:end}在庫検索<span></div></li></ul>'
+   '</div>'
+   '<h3>レビュー</h3>'
+   '<div id="work-detail-link" class="rounded">'
+   '<ul><li class="arrow" onclick="workLinkClick(\'{urlCd}\')">'
+     '<div class="arrow-block"><span class="link-label">作品情報<span></div></li>'
+   '</ul>'
+   '</div>'
+   '{:if discasLink || tolshopLink}'
+   '<h3 id="renkei">この商品は他のサービスでも扱っています</h3>'
+   '<div id="other-service-link">'
+   '<ul class="bordered rounded">'
+     '{:if discasLink}<li class="arrow" onclick="app.goTo(\'app::safari::{discasLink}\')"><div><span class="link-label">ネットで宅配レンタル <span>(TSUTAYA DISCAS)</span></span></div></li>{:end}'
+     '{:if tolshopLink}<li class="arrow" onclick="app.goTo(\'app::safari::{tolshopLink}&ko=tsearch_sh\')"><div><span class="link-label">ネットで買う <span>(TSUTAYA オンラインショッピング)</span></span></div></li>{:end}'
+   '</ul>'
+   '</div>'
+   '{:end}'

);
var ProductStockItemTemplate = new TTemplate(
   '<li id="store-item-{storeId}" class="arrow store-list-item">'
+    '<h3>{storeName}</h3>'
+    '<dl class="store-stock-info">'
+      '<dt>電話番号</dt><dd><a class="tel-link" href="tel:{tel}">{tel}</a></dd>'
+      '<dt class="stock-status">在庫状況</dt><dd class="stock-status"><span class="stock-symbol"><span class="stock-symbol-mark">{symbol}</span></span>{:if lastUpdate}&nbsp;({lastUpdate}頃)<br />{:end}'
+      '{:if message}<span class="stock-message">({message})<br /></span>{:end}'
+      '{:if rentalPossibleDay}　　{rentalPossibleDay}返却予定{:end}</dd>'
+    '</dl>'
+  '</li>'
);

function getRentalStartDate( e ) {
    var date = formatDate(e['rentalStartDate']);
    if (date && e['rentalStartDateFlag'] == '1') {
        if ( date.match(/(\d+)年(\d+)月(\d+)日/) ) {
            var year  = RegExp.$1;
            var month = RegExp.$2;
            var day   = RegExp.$3;
            if (day < 11) {
                day = '上旬';
            } else if ( day > 20 ) {
                day = '下旬';
            } else {
                day = '中旬';
            }
            date = year + '年' + month + '月' + day;
        }
    };
    return date;
}

var ProductDetailView = function() {};
ProductDetailView.prototype.render = function(data) {
    var entry = data['entry'];
    var html = '';
    var stockItems = '';
    var stockInfo = entry['stockInfo'] || [];
    for (var i = 0; i < stockInfo.length; i++) {
        var info = stockInfo[i];
        var telno = '';
        if (info['telephoneNumber'] ) {
            var tel1 = info['telephoneNumber'][0];
            telno += tel1['outAreaNumber'] + '-'
                  + tel1['areaNumber'] + '-'
                  + tel1['affiliateNumber'];
        };
        if(info['storeName']){
	        var params = {
	          'symbol': info['stockStatus']['symbol'] == '－' ? '-' :info['stockStatus']['symbol'] ,
	          'tel': telno,
	          'lastUpdate': formatDateTime(info['lastUpDate']),
	          'rentalPossibleDay': formatDate(info['rentalPossibleDay']),
	          'message': info['stockStatus']['message'],
	          'storeName': info['storeName'],
	          'storeId': info['storeId']
	        };
	        stockItems += ProductStockItemTemplate.render( params );
        }
    };
    
    var prdList = new ProductList();
    var alreadyadded = false;
    if (prdList.isProductAdded(entry['urlCd'])) {
        alreadyadded = true;
    }

    var params = {
        'urlCd': entry['urlCd'],
        'productKey': entry['productKey'],
        'jacketImage': entry['image']['medium'],
        'productName': entry['productName'],
        'lGenreName': entry['lGenreName'],
        'mGenreName': entry['mGenreName'],
        'sGenreName': entry['sGenreName'],
        'captionDubSection' : entry['captionDubSection'],
        'lmdName' : entry['lmdName'],
        'attachmentDiscSection' : entry['attachmentDiscSection'],
        'handlingCd' : entry['handlingCd'],
        'releaseName' : entry['releaseName'],
        'subtitle' : entry['subtitle'],
        'seriesName' : entry['seriesName'],
        'mediaFormatSection': entry['mediaFormatSection'],
        'publisherYearMonth': formatDate(entry['publisherYearMonth']),
        'saleDate': formatDate(entry['saleDate']),
        'priceTax': formatPrice(entry['priceTax']),
        'rentalStartDate': getRentalStartDate( entry ),
        'kansu': entry['kansu'],
        'isRental': entry['isRental'],
        'itemNameDisp': entry['itemNameDisp'],
        'stockInfoList': stockItems != '' ? TTemplate.raw(stockItems) : '',
        'favadded': alreadyadded
    };
    var artistList = entry['artistInfo'];
    if (artistList) {
        for (var j = 0; j < artistList.length; j++) {
            var a = artistList[j];
            params['artistName' + (j+1)] = a['artistName'];
            params['roleName' + (j+1)] = a['roleName'];
        };
    };
    // TSUTAYA.com連携リンク
    var renkeiList = entry['renkeiframe_dk'];
    if(renkeiList){
    	if(renkeiList['netRentalLink']){
    		var cccSakuhinCd = getGid(renkeiList['netRentalLink']['href']);
    		if(cccSakuhinCd != null){
    			var stype = String(cccSakuhinCd).substring(0,1);
    			if(stype == 1 || stype == 2 || stype == 4){
    			   if(stype == 4){ // rental comic only
    				   if(params['isRental'] == 1){
        			       params['discasLink'] = "http://www.discas.net/d/d/entry_site.pl?SITE=ccc_p_tol_tsutayasearchzaiko&AP=hgd&GID=" + cccSakuhinCd;      			       
    				   }
    			   }else{
    			       params['discasLink'] = "http://www.discas.net/d/d/entry_site.pl?SITE=ccc_p_tol_tsutayasearchzaiko&AP=ggd&GID=" + cccSakuhinCd;
    			   }
    			}
    		}else{
    			params['discasLink'] = renkeiList['netRentalLink']['href'];
    		}
    	}
       	if(renkeiList['netSaleLink']){
       		var sparams = getShopID(renkeiList['netSaleLink']['href']);
       		if(sparams.length == 2){
    		   params['tolshopLink'] = "http://shop.tsutaya.co.jp/" + sparams[0] + "/product/" + sparams[1] + "/?ko=tsearch_sh";
       		} else {
       			params['tolshopLink'] = renkeiList['netSaleLink']['href'];
       		}
    	}
    }
    var html = ProductDetailTemplate.render(params);

    $('#body').html(html);
 //   $("img.reflect").reflect({/* Put custom options here */});
};

function getGid(dlink){
	if(undefined != dlink){
		var ltmp = String(dlink).split("?")[1].split('&');
		if(ltmp.length == 2){
			var gid = String(ltmp[1]).split('=');
			if(gid.length == 2){
				return gid[1];
			}
		}
	}
	return null;
}
function getShopID(slink){
	if(undefined != slink){
		var r = [];
		var stmp = String(slink).split("?")[1].split('&');
		if(stmp.length == 2){
			var t = String(stmp[0]).split('=');
			var j = String(stmp[1]).split('=');
			if(t.length == 2) r[0] = t[1];
			if(j.length == 2) r[1] = j[1];
			return r;
		}
	}
	return null;
}

function workLinkClick(urlCd) {
    app.goTo("http://www.tsutaya.co.jp/works/" + urlCd + ".html");
}

function mapLinkClick(productKey) {
    app.goTo("app::areastock::" + productKey);
}

function storeItemClick(e) {
    var elem = $(e);
    var storeId = elem.attr('id').replace('store-item-','');
    app.goTo("store_detail.html?storeId=" + storeId);
}

// 設定画面で店を追加して戻ってきたときに、リストが変わったかどうかを判定するために
// 一時的に保存するための変数
var loadedStoreId;

function getStoreIDList() {
    var params = getICSQueryParameters();
    var storeId = params['storeId'];

    if (!storeId) {
        var storeList = new StoreList();
        var storeIdList = storeList.getStoreIdList();
        if (storeIdList && storeIdList.length > 0) {
            storeId = storeIdList.join(',');
        }
    }
    return storeId;
};

// 画面が再表示された際に呼び出される
function refreshView() {
    var newStoreId = getStoreIDList();
    if (loadedStoreId != newStoreId) {
        getProductInfo();
    };
}

function getProductInfo() {
    var params = getICSQueryParameters();

    var productKey = params['productKey'];
    var storeId = getStoreIDList();
    loadedStoreId = storeId;

    app.startLoading();
    TWSClient.sendRequest('/store/v0/products/detail.json', {
        data: {
            fieldSet : 'stock;renkeiframe_dk',
            productKey: productKey,
            adultAuthOK: AgeLimitAuth.isAuthOK(),
            storeId: storeId
        },
        complete: function() {
            app.stopLoading();
        },
        error: function(xhr) {
            app.alert('エラー',  Messages.networkError, app.goBack);
        },
        success : function(data) {
            var errMsg='';
            if (data.error) {
                if (data.error.code == 401
                    && data.error.id == 'age_limit' ) {
                    app.confirm('年齢確認', Messages.ageLimitAlert, 'いいえ', 'はい',
                        function(buttonIndex) {
                            if (buttonIndex == 1 ) {
                                AgeLimitAuth.setAuthOK();
                                getProductInfo();
                            } else {
                                app.goBack();
                            };
                        }
                    );
                    return;
                } else if ( data.error.code == 503 ) {
                    errMsg = data.error.message;
                } else {
                    errMsg = Messages.serverError;
                };
                app.alert('エラー', errMsg, app.goBack);
                return;
            } else {
                new ProductDetailView().render( data );
                $('.tel-link').click(function(event) {
                    event.stopPropagation();
                });
                $('.store-list-item').each(function() {
                    var elem = this;
                    $(elem).click( function() {
                        storeItemClick( elem );
                    });
                });
            };
        }
    })
}

/**
 * お気に入り追加ボタン表示内容切り替え
 * @param urlCd
 * @param islisted  解除表示にしたい:true 登録表示したい:false
 * @returns
 */
function changefavlabel(urlCd,islisted){
	 if (islisted === true) {
		 $('div#works-' + urlCd).html('お気に入りを解除');
	 } else {
		 $('div#works-' + urlCd).html('お気に入りに登録');
	 }
}
/**
 * ボタンクリック時の状態でお気に入りの追加と削除を行う
 * 
 */
function productfavButtonClick(urlCd) {
    var favItemList = new ProductList();
    if (favItemList.isProductAdded(urlCd)) {
	    app.confirm('お気に入り商品',"お気に入りを解除しますか？", "OK", "Cancel",
		        function(buttonIndex) {
		            if (buttonIndex == 0 ) {
		            	if(favItemList.deleteProduct(urlCd)){
		            		changefavlabel(urlCd,false);
		            		app.alert('登録解除','登録を解除しました。');	            		
		            	} else {
		            		app.alert('登録解除','解除に失敗しました。');
		            	}
		                return true;
		            }
		        }
		    );    	
    } else {
	    app.confirm('お気に入り商品',"お気に入りに追加しますか？", "OK", "Cancel",
	        function(buttonIndex) {
	            if (buttonIndex == 0 ) {
	                if(addfavlist(urlCd)){
	                	app.alert('お気に入り商品',"登録しました");
	                	changefavlabel(urlCd,true);	                	
	                	return true;
	                } else {
	                    app.alert('お気に入り商品',"登録に失敗しました");
	                	return false;
	                }
	            }
	        }
	    );
   }
}

/**
 * お気に入り登録
 * @returns {Boolean}
 */
function addfavlist(worksId) {

    // 登録可能チェック
    var productList = new ProductList();
    var urlCd = worksId;
    if (undefined == urlCd) {
        app.alert('お気に入り商品',"登録に失敗しました\nはじめからやり直してください");
        return false;
    }
    if (!productList.canAdd()) {
        app.alert('お気に入り商品',"登録件数は最大" + productList.maxListSize + "件です");
        return false;
    }
    if (productList.isProductAdded(urlCd)) {
        app.alert('お気に入り商品','登録済みです');
        return false;
    }

    // 作品情報を取得する
    var query = {
            urlCd: urlCd,
            adultAuthOK: 1
//            adultAuthOK: AgeLimitAuth.isAuthOK()
        };

    TWSClient.sendRequest('/media/v0/works/detail.json', {
        data : query,
        complete : function(){
            //通信終了時の処理
            app.stopLoading();
        },
        error: function(xhr) {
            app.alert("エラー",  Messages.networkError);
        },
        success : function(data) {
          if (data['error']) {      	  
              app.alert('お気に入り商品',"登録に失敗しました");
              return false;
          } else {
              var e = data['entry'][0];
              var urlCd = e['urlCd'];
              var image = e['image']['small'];
              var title = e['sakuhinTitle'];
              var saveDate = Date.now();
              if (productList.addProduct(urlCd,image,title,saveDate)){
                  return true;
              } else {
                  return false;
              };
          }
        }
    });
    return true;
}

function refreshView() {
	getProductInfo();
}

$( function() {
    getProductInfo();
});
