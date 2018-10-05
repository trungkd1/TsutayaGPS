/* TSUTAYA LOG List */
var Pager = function(){
    this.page = 0;
    this.isLoading = false;
    this.totalResults = -1;
    this.itemsPerPage = 20;
    this.loadedItemCount = 0;
    this.maxItemsPerPage = 1000;
    this.startindex = 1;
    this.ym = '197001';

    this.currentPage = function() {
        return this.page;
    };
    this.loadNextPage = function() {
        if (!this.isLoading) {
            this.isLoading = true;
            loadUserLog(++this.page, this.itemsPerPage);
        };
    };
    this.resetPage = function(){
    	this.page = 0;
    };
    this.loadFinished = function() {
        this.isLoading = false;
    };
    this.getLoadedItemCount = function() {
        return this.loadedItemCount;
    };
    this.setTotalResults = function(num) {
        this.totalResults = num;
    };
    this.addLoadedItemCount = function(num) {
        this.loadedItemCount += num;
    };
    this.hasNextPage = function() {
        return this.loadedItemCount < this.totalResults ? true : false;
    };
    // ページ内で更にロード可能かどうか。
    this.canLoadMoreItem = function() {
        return this.hasNextPage() && this.loadedItemCount < this.maxItemsPerPage ? true : false;
    };
    this.isLoadItemMaxOver = function() {
        return this.loadedItemCount >= this.maxItemsPerPage ? true :false;
    };
    this.getTotalResults = function() {
        return this.totalResults;
    };
    this.getcurrentYM= function(){ return this.ym;};
    this.setcurrentYM = function(ym) { this.ym = ym;};
};

var pager = new Pager();
var ProductListTemplate = null;

var rsrTypeCd2DispName = {
	    '1': 'レンタル',
	    '2': '販売',
	    '3': 'リサイクル',
}

var productListThumbnail = 
	   '{:if showym}<h2><span>{sy}年{sm}月</span></h2><ul>{:end}'
	+  '<li class="{:if odd}item_odd{:else}item_evn{:end}" id="product-{productKey}" onClick="onProductListItemClick(this)">' 
	+  '<a href="#">'
	+  '<img src="{image}" class="lazyloadimage"/>'
	+  '<span class="infoblock">'
	+  '<span class="categorie"><span>{itemSearchType}</span><span>{itmnm}</span></span>'
	+  '<span class="title">{productTitle}</span>' 
	+  '<span class="author">{:if artistName}{artistName}{:else}&nbsp;{:end}</span>' 
	+  '<span class="day">{:if sellrentdate}{tradeType}：{sellrentdate}{:else}&nbsp;{:end}</span>'
	+  '<span class="store">{:if tenponm}ご利用店舗:{tenponm}{:else}&nbsp;{:end}</span>'
	+  '</span>'
	+  '</a>'
	+  '</li>{:if ulc}</ul>{:end}';

/*     役割名非表示対応
 * 	+  '<span class="author">{:if roleName}{roleName}:{:end}{:if artistName}{artistName}{:else}&nbsp;{:end}</span>' 
 *  +  '{:if artistName}<span class="author">{:if roleName}{roleName}:{:end}{artistName}</span>{:end}' 
 */

var productListNoThumbnail = 
	   '{:if showym}<h2><span>{sy}年{sm}月</span></h2><ul>{:end}'
	+  '<li class="{:if odd}item_odd{:else}item_evn{:end}" id="product-{productKey}" onClick="onProductListItemClick(this)">' 
	+  '<a href="#">'
	+  '<span class="categorie"><span>{itemSearchType}</span><span>{itmnm}</span></span>'
	+  '<span class="title">{productTitle}</span>' 
	+  '{:if artistName}<span class="author">{artistName}</span>{:end}' 
	+  '{:if sellrentdate}<span class="day">{tradeType}：{sellrentdate}</span>{:end}'
	+  '{:if tenponm}<span class="store">ご利用店舗:{tenponm}</span>{:end}'
	+  '</a>'
	+  '</li>{:if ulc}</ul>{:end}';

var productThumbnailView =
	   '{:if showym}<h2><span>{sy}年{sm}月</span></h2><div class="item_list"></div><ul>{:end}'
	+  '<li id="product-{productKey}" onClick="onProductListItemClick(this)">' 
	+  '<a href="#">'
	+  '<p class="image"><img src="{image}" class="lazyloadimage"/></p>'
	+  '<p class="title">{productTitle}</p>'
	+  '</a>'
	+  '</li>{:if ulc}</div></ul>{:end}';

/* viewtype 
 *  1: 画像なしリスト
 *  2: ジャケット写真ありリスト
 *  3: サムネイルリスト
 */
function getListType(viewtype) {
	if(viewType == 1){
		listType = '#log-list_text';		
	}else if(viewType == 2){
		listType = '#log-list_thumb';
	}else{
		listType = '#log-thumbnail-view';
	}
	return listType;
}

function loadUserLog(page, dispNums) {

    var params = getICSQueryParameters();
    if (pager.currentPage() == 1) {
        app.startLoading();
    } 
    var viewtype = params['viewtype'];
    if(viewtype == 1){
    	ProductListTemplate = new TTemplate(productListNoThumbnail);
    } else if(viewtype == 2){
        ProductListTemplate = new TTemplate(productListThumbnail);
    } else if(viewtype == 3){
        ProductListTemplate = new TTemplate(productThumbnailView);
    } else {
        ProductListTemplate = new TTemplate(productListThumbnail);
    }
    var sidx = (page -1) * dispNums +1;
    	pager.startindex = sidx;
    var eidx = sidx + dispNums -1;
    var options = {
		async: false,
		cache: false,
		timeout: 15000,
		url: 'https://www.tsutaya.co.jp/TsutayaLog/get_history.psgi?startindex='+ sidx + '&endindex=' + eidx,
        dataType: 'json',
        success: function(data) {
        	if(data == null || data == undefined){
        		app.alert('エラー', Messages.serverError, app.goBack );
        	} else {
	            if (data['errCode']) {
	                if (data['errCode']==404) {
	                    app.alert('検索結果', Messages.searchNotFoundError, app.goBack );
	                } else if (data['error']['code']==400) {
	                    app.alert('エラー', '指定条件が正しくありません。', app.goBack );
	                } else {
	                    app.alert('エラー', Messages.serverError, app.goBack );
	                }
	            } else {
	                pager.setTotalResults( data['header']['totalcount'] );
	                if(data['datalist']){
	                	pager.addLoadedItemCount( data['datalist'].length );
	            	} else {
	            		pager.addLoadedItemCount(0);
	            	}
	                data['viewtype'] = params['viewtype'];
	                new TsutayaLogResultView().render(data);
	            }
        	}
        },
        complete : function(){
            //通信終了時の処理
            if (pager.currentPage == 1) {
                app.stopLoading();
            }
            $('#loading-more').hide();
            $('#footer-message').show();
            pager.loadFinished();
        },
        error: function(xhr) {
            app.alert("エラー",  Messages.networkError);
        }
    }
   $.ajax(options);
}


/* ヘッダ部分テンプレート  */
var SearchResultHeader = new TTemplate('<ul>'
+ '<li class="{:if b1}off{:else}on{:end}" onClick="onClickViewSwitch(1)"><a href="#"><img src="img/rireki/{ltx_btn}" border="0" /></a></li>'
+ '<li class="{:if b2}off{:else}on{:end}" onClick="onClickViewSwitch(2)"><a href="#"><img src="img/rireki/{lth_btn}" border="0" /></a></li>'
+ '<li class="{:if b3}off{:else}on{:end}" onClick="onClickViewSwitch(3)"><a href="#"><img src="img/rireki/{thb_btn}" border="0" /></a></li>'
+ '</ul>'
);

function buildHeaderArea(p){  /* 表示切替ボタン作成 */
	var viewstyle = p.viewtype;
	var q = {};
	if(viewstyle == 1){
		q ={
				ltx_btn: 'btnToggle_01_on.png',
				lth_btn: 'btnToggle_02_off.png',
				thb_btn: 'btnToggle_03_off.png',
				b1 : false,
				b2 : true,
				b3: true,		
				sidx : p['sidx'],
				eidx : p['eidx'],				
		}
	} else if(viewstyle == 2){
		q ={
				ltx_btn: 'btnToggle_01_off.png',
				lth_btn: 'btnToggle_02_on.png',
				thb_btn: 'btnToggle_03_off.png',
				b1 : true,
				b2 : false,
				b3: true,	
				sidx : p['sidx'],
				eidx : p['eidx'],				
		}				
	} else if(viewstyle == 3){
		q ={
				ltx_btn: 'btnToggle_01_off.png',
				lth_btn: 'btnToggle_02_off.png',
				thb_btn: 'btnToggle_03_on.png',
				b1 : true,
				b2 : true,
				b3: false,	
				sidx : p['sidx'],
				eidx : p['eidx'],			
		}		
	}
	return q;
}

function onClickViewSwitch(viewtype){
	var url_;
	if(viewtype == 1){
		url_ = "tlog_list.html?viewtype=1&startindex=1&endindex=20";
	} else if(viewtype == 2){
		url_ = "tlog_list_thumb.html?viewtype=2&startindex=1&endindex=20";
	} else if(viewtype == 3){
		url_ = "tlog_thumb.html?viewtype=3&startindex=1&endindex=20";
	} else {
		url_ = "tlog_list.html?viewtype=1&startindex=1&endindex=20";
	}
	if(url_ != undefined && url_ != null){
		app.goTo(url_ +"&_="+ (new Date()).getTime());
	}
}

function onProductListItemClick(elem) {
    var e = $(elem);
    var productKey = e.attr('id').replace('product-','');
    app.goTo( 'product_detail.html?productKey=' + productKey );
}

function getProductTitle( e ) {
    var ret = e['titlenm'];
    var kansu = e['kansu'];
    var stype = String(e['urlcd']).substr(0,1);
    if(kansu != undefined && kansu != null){
    	if(stype == '4'){
    		kansu = "(" + String(kansu) + ")";
    		ret += kansu;
    	}
    }
    return ret;
}

function getSaleDate( e ) {
	var ymlabel = rsrTypeCd2DispName[e['sellrentkbn']];
    var ret = {
        label: ymlabel,
        date: e['sellrentdate']
    };
    if (!ret['date']) {
        return {};
    }    
    if ( ret['date'].match(/(\d{4})(\d{2})(\d{2})/) ) {
        var year  = RegExp.$1;
        var month = RegExp.$2;
        var day   = RegExp.$3;
        ret['date'] = year + '/' + month + '/' + day;
    }
    return ret['date'];
}

function getym(ym){
	if(ym.length > 0){
		return String(ym).substr(0,6);
	} else {
		return '';
	}
}

function getSearchTypeCode(urlCd){
	if(urlCd == null || urlCd == undefined) return '1';
	var stype = String(urlCd).substr(0,1);
	return String(stype);
}

function getSearchTypeName(urlCd){
	if(urlCd == null || urlCd == undefined) return '';
	var stype = String(urlCd).substr(0,1);
	var ret = '';
	if(stype == '1' || stype == '5' || stype == '6' ){
		ret = '映像';
	} else if(stype == '2'){
		ret = '音楽／CD';
	} else if(stype == '3'){
		ret = 'ゲーム';
	} else if(stype == '4'){
		ret = '本';
	}
	return ret;
}

var TsutayaLogResultView = function() {};
TsutayaLogResultView.prototype.render = function(data) {
	var viewtype = data['viewtype'];
    var listType ='';
	if(viewtype == 1){
		listType = '#log-list_text';
	}else if(viewtype == 2){
		listType = '#log-list_thumb';
	}else{
		listType = '#log-thumbnail-view';
	}
  /* ヘッダ部分の描画 */
	var hparam = {
	  sidx: data['header']['startindex'],
	  eidx: data['header']['endindex'],
	  viewtype : data['viewtype'],
	}
    var p =  buildHeaderArea(hparam);
    var headerHTML = SearchResultHeader.render( p ); 
    $('#header').html( headerHTML );
  /* 履歴リストの描画 */
    var currentYM = pager.getcurrentYM();
    var uls = false;
    var ulc = false;
    var showym = false;
    
    var oddidx = true;
    var dcount = data['datalist']?data['datalist'].length:0;
    var logList = "";
    for (var i = 0; i <  dcount ; i++) {
        var e = data['datalist'][i];
        var srym = getym(e['sellrentdate']);
        var year,month;
        if(srym != currentYM){
        	if(currentYM === '197001') {
        		uls = false;
        	} else {
        		uls = true;
        	}
        	 if ( srym.match(/(\d{4})(\d{2})/) ) {
        	        year  = RegExp.$1;
        	        month = RegExp.$2;
        	 }
        	 pager.setcurrentYM(srym);
        	 showym = true;
        }else {
        	 showym = false;
        }
        currentYM = pager.getcurrentYM();
        
        var productTitle = String(getProductTitle(e));
        if(viewtype==3){
        	if(productTitle.length > 38){
        		productTitle = String(productTitle).substr(0,38) + '…';
        	}
        }
        if(i == dcount-1) { ulc = true; } else {ulc = false;}
        var jacketImage = e['jacketurl'];
        if(jacketImage == undefined || jacketImage == null){
        	if( '2' != getSearchTypeCode(e['urlcd'])){
        		jacketImage = 'img/rireki/dvd_s.png';
        	} else {
        		jacketImage = 'img/rireki/music_s.png';
        	}
        }
        var item_params = {
        	uls: uls,
        	ulc: ulc,
        	sy: year,
        	sm: month,
        	showym : showym,
            itemSearchType: getSearchTypeName(e['urlcd']),
            itmnm: e['itmnm'],
            productTitle: productTitle,
            image: jacketImage,
            artistName: e['cccartnm1'],
            roleName: e['cccartyakunm1'],
            tradeType: rsrTypeCd2DispName[e['sellrentkbn']],
            tenponm: e['tenponm'],
            sellrentdate: getSaleDate(e),
            productKey: e['sellrentkbn']=='1'?e['rentshohincd']:e['jan'],
            cccSakuhinCd: e['cccSakuhincd'],
            urlCd: e['urlcd'],
            odd: oddidx,           
        };
        
        logList += ProductListTemplate.render( item_params );
        if(oddidx == true){ oddidx = false;} else { oddidx = true;}
    };
    $(listType).append( logList );
    if (pager.hasNextPage()) {
        var msg = '';
        if ( !pager.canLoadMoreItem()) {
            msg = '一度に読み込めるのは' + pager.maxItemsPerPage  + '件までです<br />検索条件を変更して再度検索してください</div>';
            $('#footer').html('<div id="footer-message">' + msg + '</div>');
        }
    } else {
        $('#loading-more').remove();
        $('#footer-message').remove();
    };

}
/* Load Results */
$(function() {
	var params = getICSQueryParameters();
	var viewType = params['viewteype'];
	var listType;
	if(viewType == 1){
		listType = '#log-list_text';
	}else if(viewType == 2){
		listType = '#log-list_thumb';
	}else{
		listType = '#log-thumbnail-view';
	}
    $(listType).children().remove();
    pager.loadNextPage();

    var w_height = app.getDisplayInfo(0);

    $(window).scroll(function(evt) {
        if (!pager.isLoading) {
    if (pager.canLoadMoreItem() && $(evt.target).scrollTop() + w_height + 80 > $(evt.target).height()) {
                setTimeout(function() {
                    $('#loading-more').show();
                    $('#footer-message').hide();
                    pager.loadNextPage();
                }, 100); 
            };
        };
    });
});


