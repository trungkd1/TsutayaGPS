$(function() {
    $('#search-button').attr('disabled','true');
    var createInputChecker = function( buttonId, inputId ) {
        return function() {
            var v = $('#' + inputId).val();
            v = v.replace(/^[ 　]+/g,'').replace(/[ 　]+$/g, '');
            var button = $('#' + buttonId );
            if ( v != '' ) {
                button.attr('disabled','');
            } else {
                button.attr('disabled','true');
            }
        };
    };
    var check1 = createInputChecker('search-button', 'search-keyword');
    var checkInput = function() {
        check1();
        setTimeout( checkInput, 200 );
    }
    setTimeout( checkInput, 200 );

    var storeItemClick = function(e) {
        var elem = $(e);
        var storeId = elem.attr('id').replace('store-item-','');
        app.goTo("store_detail.html?storeId=" + storeId);
    }

    var itemTmpl = new TTemplate(
      '<li id="store-item-{storeId}" class="arrow store-list-item"> \
  <h4>{storeName}</h4><p>{title}</p> \
  {:if infoUpdateTime}（更新日：{infoUpdateTime}）{:end}');

    var loadedStoreId = '';

    var getStoreIDList = function() {
        var storeList = new StoreList();
        // 毎回Ajax呼び出しを行わないように
        var lst = localStorage.loginStatus;
    	if(lst != null && lst != undefined){
    		if(app.getUserLoginStatus() == "0"){
    			storeList.checkLogin();
    		}
    	} else if(lst == null || lst == undefined){
    		if(app.getUserLoginStatus() != "0"){
    			storeList.checkLogin();
    		}
    	}   
//        	storeList.checkLogin();
        var storeIdList = storeList.getStoreIdList();
        if (storeIdList && storeIdList.length > 0) {
            storeId = storeIdList.join(',');
        }else{
            storeId=storeIdList;
        }
        return storeId;
    };

    var sendRequest = function() {
        var storeId = getStoreIDList();
        $('#information-list').html('<img src="'+ loaderImage + '" />');

        if(storeId.length == 0){
            $('#information-list').html('');
            return;
        }
        TWSClient.sendRequest('/store/v0/store/registrationstoreinfo.json', {
            data: {
                'processSection': 1,
                storeId: storeId
            },
            error: function(xhr) {
                $('#information-list').html(Messages.networkError);
            },
            success : function(data) {
                var i,
                    errMsg,
                    entry,
                    info,
                    infoHTML = '',
                    tmplist = [];
                if (data.error
                    || !data.entry) {
                    if (data.error.code == 503) {
                        errMsg = data.error.message;
                    } else {
                        errMsg = Messages.serverError;
                    }
                    $('#information-list').html(errMsg);
                    return;
                };
                for (i = 0; i < data['entry'].length; i++) {
                    entry = data['entry'][i];
                    info = entry['profitInfo'][0];

                    tmplist.push( itemTmpl.render({
                        storeId: entry['storeId'],
                        storeName: entry['storeName'],
                        title: info['title'],
                        infoUpdateTime: info['infoUpdateTime']
                    }) );
                };
                if (tmplist.length > 0) {
                    infoHTML = tmplist.join('');
                };
                $('#information-list').html( infoHTML );
                // ここでスタイルを付け直さないと上手く反映してくれない
                $('#information-list').attr('class', 'bordered rounded');
                $('.store-list-item').each(function() {
                    var elem = this;
                    $(elem).click( function() {
                        storeItemClick( elem );
                    });
                });
            }
        })
    };

    window.refreshView = function() {
        var newStoreId = getStoreIDList();
        if (loadedStoreId != newStoreId) {
            setTimeout( function() {
                sendRequest();
                loadedStoreId = newStoreId;
            },100
            )
        };
    };

    // XXX 一旦ローディングを切るために少し間を置いてからお得情報を読み込む
    setTimeout(
        sendRequest, 100
    );
});
