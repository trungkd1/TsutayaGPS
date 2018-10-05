
var STORE_REGIST_BUTTON_MESSAGE='ご利用店舗に登録';
var STORE_DELETE_BUTTON_MESSAGE ='登録を解除する';

function storeAddButtonClick(e) {
    var storeList = new StoreList();
    var elem = $(e);
    var storeId = elem.attr('id').replace('add-','');
    if (storeList.isStoreAdded(storeId)) {
        if (storeList.deleteStore(storeId)) {
        	app.updateRegisteredStore(storeId, 0);
            $(e).attr('value', STORE_REGIST_BUTTON_MESSAGE);
            app.alert('ご利用店舗', '登録を解除しました');
        } else {
            app.alert('ご利用店舗','登録解除に失敗しました');
        };
        return false;
    };

    if (!storeList.canAdd()) {
        app.alert('ご利用店舗',"登録件数は最大" + storeList.maxListSize + "件です");
        return false;
    };
    var storeName = $('#store-name-' + storeId).html();
    if (storeList.addStore( storeId, storeName) ){
        app.updateRegisteredStore(storeId, 1);
        app.alert('ご利用店舗',"登録しました");
        $(e).attr('value', STORE_DELETE_BUTTON_MESSAGE);
        return false;
    } else {
        app.alert('ご利用店舗',storeList.errMsg() || "登録に失敗しました"); //ありえる？
        return false;
    };
    return false;
}

