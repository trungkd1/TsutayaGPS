/**
 * お気に入り追加確認
 */
function productAddButtonClick() {
    // 追加確認
    app.confirm('お気に入り商品',"お気に入りに追加しますか？", "OK", "Cancel",
        function(buttonIndex) {
            if (buttonIndex == 0 ) {
                return productAdd();
            }
        }
    );
}

/**
 * お気に入り登録共通処理
 * @returns {Boolean}
 */
function productAdd() {

    // 登録可能チェック
    var productList = new ProductList();
    var urlCd = getICSQueryParameters()['urlCd'];
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
//            fieldSet: encodeURIComponent('review;description;tune;renkeiframe'),
            adultAuthOK: AgeLimitAuth.isAuthOK()
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
                  app.alert('お気に入り商品',"登録しました");

                  // ボタン表示フラグをOFFにする
                  app.setAddFavBtnEnable(false);
                  return true;
              } else {
                  app.alert('お気に入り商品',"登録に失敗しました");
                  return false;
              };
          }
        }
    });
    return false;
}

/**
 * お気に入り削除確認
 * （お気に入り情報画面より呼び出される）
 * @param urlCd
 */
function productDelButtonClick(urlCd) {
 app.confirm('お気に入り商品','登録解除します\nよろしいですか','登録解除','キャンセル',
     function(buttonIndex) {
         if (buttonIndex == 0 ) {
             if (null == urlCd || "" == urlCd) {
                 app.alert('お気に入り商品',"登録解除に失敗しました\nはじめからやり直してください");
                 return false;
             }
             // お気に入り削除へ
             productDel(urlCd);

             // 再読み込み
             reload();
         }
         else {
             return false;
         }
     }
 );
}


/**
 * お気に入り削除共通処理
 * @param urlCd
 * @returns {Boolean}
 */
function productDel(urlCd) {

    // お気に入り店舗の削除
    var productList = new ProductList();

    if (productList.isProductAdded(urlCd)) {
        if (productList.deleteProduct(urlCd)) {
            app.alert('お気に入り商品', '登録を解除しました');

            // ボタン表示フラグをONにする
            app.setAddFavBtnEnable(true);
            return true;
        } else {
            app.alert('お気に入り商品','登録解除に失敗しました');
        };
    }
    else {
        app.alert('お気に入り商品','登録解除済みです');

        // ボタン表示フラグをONにする
        app.setAddFavBtnEnable(true);
        return false;
    }
    return false;
}

/**
 * お気に入り追加メニュー
 * @param urlCd
 */
function addMenu(urlCd){
	// お気に入り情報確認
    productList = new ProductList();
    if (productList.isProductAdded(urlCd) == false) {
        // ボタン表示フラグをONにする
        app.setAddFavBtnEnable(true);
    }
}

/**
 * お気に入り追加確認(CCC作品コードバージョン)
 * 商品検索結果にお気に入り追加機能を付けるためのテスト実装
 */
function productfavButtonClick(cccSakuhinCd) {
    // 追加確認
    app.confirm('お気に入り商品',"お気に入りに追加しますか？", "OK", "Cancel",
        function(buttonIndex) {
            if (buttonIndex == 0 ) {
                return productAdd();
            }
        }
    );
}
