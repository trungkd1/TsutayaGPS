(function() {
//	var STORE_REGIST_BUTTON_MESSAGE='ご利用店舗に登録';
//	var STORE_DELETE_BUTTON_MESSAGE='登録を解除する';
	
var SearchStoreViewTemplate = new TTemplate(
   '<h1>「{k}」の検索結果<span>({totalResults}件中{itemsPerPage}件)</span></h1>'
 + '<ul id="search-result-items" class="list bordered">'
 + '{searchItems}'
 + '</ul>'
 + '{:if hasMoreItems}'
 + '<div id="more">検索結果の表示は最大20件までです。<br />見つからない場合は条件を変更して検索してください</div>'
 + '{:end}'
);

var StoreSearchItemTemplate = new TTemplate(
   '<li class="store-search-item rounded arrow">'
 +   '<div id="store-item-{storeId}" class="store-list-item">'
 +     '<h3 id="store-name-{storeId}">{storeName}</h3>'
 +     '<p>〒{zipCode1}-{zipCode2}</p>'
 +     '<p>{todofuken}{commune}{houseNumber}</p>'
 +     '<p class="add-store-button"><input type="submit" id="add-{storeId}" class="submit-button btn-blue" value="'
 +     '{:if storeAdded}' + STORE_DELETE_BUTTON_MESSAGE + '{:else}'
 +     STORE_REGIST_BUTTON_MESSAGE + '{:end}"></input>'
 +    '<input type="submit" id="map-{storeId}" class="map-button btn-blue" value="マップ表示" />'
 +   '</p></div>'
 +   '</div>'
 + '</li>'
);
var StoreSearchItemView = function(data) {
    this.data = data;
};
StoreSearchItemView.prototype.render = function() {
    var data = this.data;
    if (data['error']) {
        var result = SearchStoreViewTemplate.render( {
            k : data['searchKeyword'],
            searchItems : "error"
        })
        return result;
    };

    var items = [];
    var storeList = new StoreList();
    for (var i = 0; i < data['entry'].length; i++) {
        var e = data['entry'][i];
        var storeAdded = storeList.isStoreAdded(e['storeId']);
        var item = StoreSearchItemTemplate.render( {
            storeName : e['storeName'],
            zipCode1 : e['zipCode1'],
            zipCode2 : e['zipCode2'],
            todofuken : e['todofuken'],
            commune : e['commune'],
            houseNumber : e['houseNumber'],
            storeAdded: storeAdded,
            storeId : e['storeId']
        });
        items.push( item );
    };

    var result = SearchStoreViewTemplate.render( {
        k : data['searchKeyword'],
        itemsPerPage: data['itemsPerPage'],
        totalResults: data['totalResults'],
        hasMoreItems: parseInt(data['totalResults']) > parseInt(data['itemsPerPage']),
        searchItems : TTemplate.raw(items.join(''))
    })
    return result;
};

var globalViewInstance;

var storeItemClick = function(e) {
    var elem = $(e);
    var storeId = elem.attr('id').replace('store-item-','');
    app.goTo("store_detail.html?storeId=" + storeId);
}

//window.refreshView = function() {
var refreshViewStore = function() {
    var html = globalViewInstance.render();
    $('#search-result').html(html);
    $('.submit-button').click( function(event) {
        event.preventDefault();
        event.stopPropagation();
        storeAddButtonClick( event.target );
    });
    $('.map-button').click(function(event) {
        event.preventDefault();
        event.stopPropagation();
        var storeId = $(event.target).attr('id').replace('map-','');
        app.goTo("app::storemap::" + storeId);
    });
    $('.store-list-item').each(function() {
        var elem = this;
        $(elem).click( function() {
            storeItemClick( elem );
        });
    });
}

$(function() {
    var params = getICSQueryParameters();
/*    $('#search-result-items').children().remove(); */
    app.startLoading();
    TWSClient.sendRequest(
        '/store/v0/store/search.json', {
        data: {
            dispNums: 20,
            searchKeyword: params['k']
        },
        complete: function() {
            app.stopLoading();
        },
        error: function(xhr) {
            app.alert('エラー', Messages.networkError, app.goBack );
        },
        success : function(data) {
            if ( data.error ) {
                if (data.error.code == 404) {
                    app.alert('検索結果', Messages.searchNotFoundError, app.goBack );
                } else if (data.error.code == 400) {
                    app.alert('エラー', '検索条件が正しくありません', app.goBack );
                } else if ( data.error.code == 503 ) {
                    app.alert('エラー', data.error.message, app.goBack );
                } else {
                    app.alert('エラー', Messages.serverError, app.goBack );
                }
                return;
            }
            data['searchKeyword'] = params['k'];
  /* render
            var html = new StoreSearchItemView(data).render();
            $('#search-result').html(html);
            $('.submit-button').click( function(event) {
                event.preventDefault();
                event.stopPropagation();
                storeAddButtonClick( event.target );
            });
            $('.map-button').click(function(event) {
                event.preventDefault();
                event.stopPropagation();
                var storeId = $(event.target).attr('id').replace('map-','');
                app.goTo("app::storemap::" + storeId);
            });
            $('.store-list-item').each(function() {
                var elem = this;
                $(elem).click( function() {
                    storeItemClick( elem );
                });
            });
 */            
            globalViewInstance = new StoreSearchItemView(data);
            refreshViewStore();
        }
    })
});
})();
