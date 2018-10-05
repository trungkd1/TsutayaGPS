var Pager = function(){
    this.page = 0;
    this.isLoading = false;
    this.totalResults = -1;
    this.itemsPerPage = 20;
    this.loadedItemCount = 0;
    this.maxItemsPerPage = 300;

    this.currentPage = function() {
        return this.page;
    };
    this.loadNextPage = function() {
        if (!this.isLoading) {
            this.isLoading = true;
            loadSearchResult(++this.page, this.itemsPerPage);
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
};
var pager = new Pager();

function loadSearchResult(page, dispNums) {
 //   var params = getQueryParameters();
    var params = getICSQueryParameters();
    if (pager.currentPage() == 1) {
        app.startLoading();
    }

    var query = {
        page: page,
        dispNums: dispNums
    };
    if (params['k']) {
        query['searchKeyword'] =params['k'];
    };
    if (params['cccSakuhinCd']) {
        query['cccSakuhinCd'] = params['cccSakuhinCd'];
    };
    if (params['item']) {
        query['storeProductItemCd'] = params['item'];
    };
    if (params['saleRentalCd']) {
        query['saleRentalCd'] = params['saleRentalCd'];
    };
    query['adultAuthOK'] = AgeLimitAuth.isAuthOK();
    
    if(localStorage.safeSearch == null || localStorage.safeSearch == undefined){
    	localStorage['safeSearch'] = '1';
    }
    
    if(localStorage['safeSearch'] == '1'){
    	query['adultFlag'] = '1';
    }

    TWSClient.sendRequest('/store/v0/products/search.json', {
        data : query, 
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
        },
        success : function(data) {
          if (data['error']) {
              if (data['error']['code']==404) {
                  app.alert('検索結果', Messages.searchNotFoundError, app.goBack );
              } else if (data['error']['code']==400) {
                  app.alert('エラー', '検索条件が正しくありません', app.goBack );
              } else {
                  app.alert('エラー', Messages.serverError, app.goBack );
              }
          } else {
              pager.setTotalResults( data['totalResults'] );
              pager.addLoadedItemCount( data['itemsPerPage'] );
              data['keyword'] = params['k'];
              new SearchResultView().render(data);
          }
        }
    });
};

$(function() {
    $('#search-result-items').children().remove();
    pager.loadNextPage();
//    var w_height = $(window).height();
    var w_height = app.getDisplayInfo(0);
    $(window).scroll(function(evt) {
        if (!pager.isLoading) {
//            if (pager.canLoadMoreItem() && $(evt.target).scrollTop() + w_height + 100 > $(evt.target).height()) {
    if (pager.canLoadMoreItem() && $(evt.target).scrollTop() + w_height + 80 > $(evt.target).height()) {
                // 再描画のためにsetTimeoutする
                setTimeout(function() {
                    $('#loading-more').show();
                    $('#footer-message').hide();
                    pager.loadNextPage();
                }, 100); 
            };
        };
    });
});

var storeItemCd2IconName = {
    '011' : 'レンタルDVD',
    '012' : 'レンタルBlu-ray',
    '013' : 'レンタルVHS',
    '020' : 'レンタルCD',
    '030' : 'レンタルコミック',
    '111' : '販売DVD',
    '112' : '販売Blu-ray',
    '113' : '販売VHS',
    '120' : '販売CD',
    '130' : '販売本',
    '140' : '販売ゲーム',
}

var searchTypeCd2DispName = {
		'201' : 'すべての商品名',
		'202' : 'すべての人名',
		'211' : 'レンタルDVD/商品名',
		'212' : 'レンタルDVD/人物名',
		'213' : 'レンタルCD/商品名',
		'214' : 'レンタルCD/人物名',
		'215' : 'レンタルCD/曲名',
		'216' : 'レンタルコミック/商品名',
		'217' : 'レンタルコミック/人物名',
		'221' : '販売DVD/商品名',
		'222' : '販売DVD/人物名',
		'223' : '販売CD/商品名',
		'224' : '販売CD/人物名',
		'225' : '販売CD/曲名',
		'226' : '販売本/商品名',
		'227' : '販売本/人物名',
		'228' : '販売ゲーム/商品名'
}


var ProductListTemplate = new TTemplate(
   '<div>'
+  '<li id="product-{productKey}" class="arrow longclick rounded" onClick="onProductListItemClick(this)">'
+  '<div class="box-wrapper">'
+  '<div class="jacket-image-small-box"><img src="{image}" class="lazyloadimage"/>'
+  '</div>'
+  '<div class="search-result-product-info">' 
+  '<h3>{productTitle}&nbsp;</h3>'
+  '<dl>'
+  '{:if artistName1}<dt>{roleName1}</dt><dd>{artistName1}</dd>{:end}'
+  '{:if artistName2}<dt>{roleName2}</dt><dd>{artistName2}</dd>{:end}'
+  '</dl>'
+  '<div class="tag wide">'
+  '<span class="sell-rental-disp">{itemIconName}</span>'
+  '&nbsp;<span class="genre-disp">{lGenreName}</span>'
+  '</div>'
+  '<dl>'
+  '{:if saleDate.date}<dt>{saleDate.label}</dt><dd>{saleDate.date}</dd>{:end}'
+  '{:if isSell && priceTax}<dt>定価（税込）</dt><dd>{priceTax}円<br /><span class="small">※価格は店舗によって異なります。<span></dd>{:end}'
+  '</dl>'
+  '{:if isAdult}<p class="causion">※18歳未満アクセス禁止</p>{:end}' 
+  '</div>'
+  '</div>'
+  '</li>'
+  '</div>'
);

var SearchResultHeader = new TTemplate(
  '<h1 class="page-title">{:if searchItemType}<span>{searchItemType}</span><br />{:end}{:if keyword}「{keyword}」の{:end}検索結果</h1>'
+ '<div id=left-search></div>'
+ '<div id=right-search></div>'
+ '<div id=safesearch></div>'
+ '<p>1-{itemsCount}件/全{totalResults}件</p>'
);

function onSafeSearchClick(){
	if(localStorage.safeSearch == '1'){
		localStorage.safeSearch = '0';
	} else {
		localStorage.safeSearch = '1';
	}
	
	location.reload();
}
function onProductListItemClick(elem) {
    var e = $(elem);
    var productKey = e.attr('id').replace('product-','');

    app.goTo( 'product_detail.html?productKey='
        + productKey
        );
}

function getProductTitle( e ) {
    var ret = e['productName'];

    var pd = e['storeProductItemCd'].substr(0,2);
    if (pd == '01') {
        if (e['lmdName']) {ret += '（' + e['lmdName'] + '）'};
        if (e['handlingName']) {ret += '（' + e['handlingName'] + '）'};
        if (e['releaseName']) {ret += '（' + e['releaseName'] + '）'};
    } else if (pd == '02' || pd == '12') {
        if (e['lmdName']) {ret += '（' + e['lmdName'] + '）'};
        if (e['attachmentDiscSection']) {ret += '（' + e['attachmentDiscSection'] + '）'};
        if (e['handlingName']) {ret += '（' + e['handlingName'] + '）'};
    } else if (pd == '11') {
        if (e['captionDubSection'] && e['storeProductItemCd'] == '113') {ret += '（' + e['captionDubSection'] + '）'};
        if (e['lmdName']) {ret += '（' + e['lmdName'] + '）'};
        if (e['releaseName']) {ret += '（' + e['releaseName'] + '）'};
        if (e['handlingName']) {ret += '（' + e['handlingName'] + '）'};
    } else if ( pd == '13' ) {
        if (e['kansu']) {ret += '（' + e['kansu'] + '）'};
    } else if ( pd == '14' ) {
        if (e['lmdName']) {ret += '（' + e['lmdName'] + '）'};
        if (e['releaseName']) {ret += '（' + e['releaseName'] + '）'};
    }
    return ret;
}

function getSaleDate( e, saleDate ) {
    var ret = {
        label: e['saleRentalCd'] == '0' ? 'レンタル開始日' : '発売日',
        date: saleDate
    };
    if (!ret['date']) {
        return {};
    }
    var pd = e['storeProductItemCd'].substr(0,2);

    if (pd == '13') {
        ret['label'] = '出版年月'
    } else if ( pd == '02' && e['rentalStartDateFlag'] == '1') {
        if ( ret['date'].match(/(\d+)年(\d+)月(\d+)日/) ) {
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
            ret['date'] = year + '年' + month + '月' + day;
        }
    }

    return ret;
}

var search_labels ={
		'201' : ['202','215','artists','songs'],
		'202' : ['201','215','products','songs'],
		'211' : ['212','#','artists','songs'],
		'212' : ['211','#','products','songs'],
		'213' : ['214','215','artists','songs'],
		'214' : ['213','215','products','songs'],
		'215' : ['214','213','artists','products'],
		'216' : ['217','#','artists','songs'],
		'217' : ['216','#','products','songs'],
		'221' : ['222','#','artists','songs'],
		'222' : ['221','#','products','songs'],
		'223' : ['224','225','artists','songs'],
		'224' : ['223','225','products','songs'],
		'225' : ['226','223','artists','products'],
		'226' : ['227','#','artists','songs'],
		'227' : ['226','#','products','songs'],
		'228' : ['#','#','artists','songs'],
}

var btn_labels = {
		'products' :'商品名',
		'artists' :'人物名',
		'songs' : '曲名',
}

function resetSearch(storeProductItem,searchKeyword) {

    var url = 'search_result.html?k=' + searchKeyword + '&item=' + storeProductItem;
 //   app.setRealUrl(url);
    app.goTo( url );  
 
};

function buildHeaderArea(p){
	var searchItem = p.itemCode;
	var sk = p.keyword;
	var leftItem,rightItem;
	var llabel,rlabel;
	var lsearch = '<div class="resarch-title"><a class="btnText btn-func" href="#" ';
	var rsearch = '<div class="resarch-name"><a class="btnText btn-func" href="#" ';

	if(search_labels[searchItem]){
		leftItem = search_labels[searchItem][0];
		rightItem  = search_labels[searchItem][1];
		llabel  = btn_labels[search_labels[searchItem][2]];
		rlabel  = btn_labels[search_labels[searchItem][3]];
		if(leftItem !== '#'){
			lsearch = lsearch + 'onClick="resetSearch(\'' + leftItem + '\',\'' + sk + '\')">' + llabel + 'で再検索</a></div>';
		} else {
			lsearch = lsearch + '><span style="color:#9f9f9f;">' + llabel + 'で再検索</span></a></div>';
		}
		
		if(rightItem !== '#'){
			rsearch = rsearch + 'onClick="resetSearch(\'' + rightItem + '\',\'' + sk + '\')">' + rlabel + 'で再検索</a></div>';
			$('#right-search').attr('disabled','');
		} else {
			rsearch = rsearch +'><span style="color:#9f9f9f;">' + rlabel + 'で再検索</span></a></div>';
			$('#right-search').attr('disabled','true');
		}
		
		if(localStorage.safeSearch == '1'){
			$('#safesearch').html('<div class="adult-check off"><a class="btnText btn-func" href="#" onClick="onSafeSearchClick()">アダルト<br>非表示</a></div>');
		} else {
			$('#safesearch').html('<div class="adult-check on"><a class="btnText btn-func" href="#" onClick="onSafeSearchClick()">アダルト<br>表示中</a></div>');
		}

		$('#left-search').html( lsearch );
		$('#right-search').html( rsearch );
	}
}

var SearchResultView = function() {};
SearchResultView.prototype.render = function(data) {
    var p = {
        keyword: data['keyword'],
        itemsCount: pager.getLoadedItemCount(),
        totalResults: pager.getTotalResults(),
        searchItemType: searchTypeCd2DispName[data['query']['storeProductItemCd']],
        itemCode : data['query']['storeProductItemCd']
    }; 
    var headerHTML = SearchResultHeader.render( p ); 
    $('#header').html( headerHTML );
    buildHeaderArea(p);
    for (var i = 0; i < data['entry'].length; i++) {
        var e = data['entry'][i];
        var iconName = storeItemCd2IconName[e['storeProductItemCd']];
        var params = {
            itemIconName: iconName,
            storeProductItemName: e['storeProductItemName'],
            lGenreName: e['lGenreName'],
            image: e['image']['small'],
            productTitle: getProductTitle(e),
            isSell: e['saleRentalCd'] == '0' ? 0 : 1,
            saleDate: getSaleDate(e, formatDate(e['saleDate'])),
            priceTax: formatPrice(e['priceTax']),
            productKey: e['productKey'],
            cccSakuhinCd: e['cccSakuhinCd']       // お気に入り対応 2012.02.08
        };
        var artistList = e['artistList'];
        if (artistList) {
            for (var j = 0; j < artistList.length; j++) {
                var a = artistList[j];
                params['artistName' + (j+1)] = a['artistName'];
                params['roleName' + (j+1)] = a['roleName'];
            };
        };
        if (e['adultFlag']=='1') {
            params['isAdult']=1;
        };
        var elem = ProductListTemplate.render( params );
        $('#search-result-items').append( elem );
    };
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


