
// お気に入り登録数（履歴）
var befProductListSize;

// お気に入り登録最終URLコード（履歴）
var befLastUrlCd;

/**
 * 初回読み込み
 */
$(function() {
    var productList = new ProductList();
    productListRender(productList);

    // 履歴情報を更新
    setBefProductListInfo(productList);

    //お気に入りをリロード
    window.refreshView = function () {
        reload();
    };

});

/**
 * 画面リロード処理
 */
function reload() {

    var productList = new ProductList();

    // お気に入り登録数が異なっている場合、画面リロード
    if(isChangedProductList(productList)){

        // 履歴情報を更新
        setBefProductListInfo(productList);
        // 情報をクリア
        $('#body').html("");
        productListRender(productList);
    }
}

/**
 * お気に入り変更判定
 * @param productList localstorageから取得したデータ
 */
function isChangedProductList(productList){

	var size = productList.getProductListSize();

    // お気に入り登録数が異なっている場合は更新あり
    // お気に入り登録数が0の場合は常に変更あり
    // 最終登録URLコードが異なっている場合は更新あり
    if(befProductListSize != size
        || size == 0
    	|| befLastUrlCd != productList.list[size - 1]['urlCd']){
        return true;
    }
    return false;
}


/**
 * お気に入り情報履歴登録
 * @param productList localstorageから取得したデータ
 */
function setBefProductListInfo(productList){

    var size = productList.getProductListSize();

    // お気に入り登録数（履歴）を更新
    befProductListSize = size;

    // お気に入り登録最終URLコード（履歴）を更新
    if(size == 0){
        befLastUrlCd = '';
    } else {
        befLastUrlCd = productList.list[size - 1]['urlCd'];
    }

}

/**
 * お気に入り一覧表示処理
 * @param productList localstorageから取得したデータ
 */
function productListRender(productList) {
    // お気に入りタイトル
    $('#body').append(
        headerTemplate.render({ })
    );
    // お気に入り内容部の作成
    var size = productList.getProductListSize();
    var tmplArray = [];
    for (var i = size; 0 < i; i--) {
        var info = productList.list[i - 1];
        tmplArray.push(
            tmplList.render({
                urlCd: info['urlCd'],
                delUrlCd: info['urlCd'],
                loaderImage: loaderImage,
                image: info['image'],
                title:  info['title'],
                saveDate: info['saveDate'],
                border: ((size - i) % 2),
                genre: getFavoritesGenre(info['urlCd'])
            })
        );
    }
    if (0 < tmplArray.length) {
        $('#body').append(
            tmpl.render({
                favoriteList: TTemplate.raw(tmplArray.join(''))
            })
        );
    }else{
    	$('#body').append('<h3>現在登録されているお気に入りはありません。</h3>');
    }

    // 長押し対応
    lcNS.init();
}

/**
 * お気に入り表示ジャンル取得
 * @param urlCd
 * @return お気に入り一覧表示時のジャンル名称
 */
function getFavoritesGenre(urlCd){

    genre = urlCd.charAt(0);
    switch(genre){
        case "1":
        case "5":
        case "6":
            return 'DVD';
        case "2":
            return 'CD';
        case "3":
            return 'ゲーム';
        case "4":
            return '本';
        default:
            break;
    }
    return '';
}

/**
 * 「作品情報」画面へ遷移する
 * @param elem
 * @returns {Boolean}
 */
function onFavoriteProductClick(elem){
    // longclick.jsの制御
    if (lcNS.longTapFlag) {
        // ロングタップされたら画面遷移はしない
        lcNS.longTapFlag = false;
        return false;
    }
    var id = $(elem).attr('id');
    app.goTo('work_detail.html?urlCd=' + id);
}

/** ヘッダテンプレート */
var headerTemplate = new TTemplate(' \
<h1 class="page-title header">お気に入り</h1> \
');

/**
 * お気に入り行のテンプレート
 * 枠を1つ1つ作る：ul属性をループさせる
 * 1つの枠にリストを作る：ulのli属性をループさせる
 */
var tmpl = new TTemplate(' \
<h3>お客様が登録されたお気に入り一覧です。</h3> \
<ul id="" class="rounded bordered"> \
  {favoriteList} \
</ul> \
');
var tmplList = new TTemplate(' \
<li id="{urlCd}" class="arrow longclick" onclick="onFavoriteProductClick(this);" onmouseover="productDelButtonClick({delUrlCd});"> \
  <div class="box-wrapper"> \
    <div class="jacket-image-small-box"><img src="{loaderImage}" alt="" title="" class="lazyloadimage" onload="loadingImage(this, \'{image}\')"/></div> \
    <div class="search-result-product-info"> \
      <div class="tag"><span class="genre-disp">{genre}</span></div> \
      <h2>{title}</h2> \
    </div> \
  </div> \
</li> \
');