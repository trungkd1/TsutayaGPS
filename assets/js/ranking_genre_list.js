/** タイトルのテンプレート */
var titleTemplate = new TTemplate(' \
<div id="ranking-top"> \
  <h1 id="page-title">TSUTAYA ランキング<p>{rankingTitle}</p></h1> \
  <p class="h3" id="page-date">{totalingPeriod}</p> \
</div> \
');
/** ヘッダのテンプレート */
var headTemplate = new TTemplate(' \
<h3>{headName}</h3> \
');
/** リストのテンプレート */
var listTemplate = new TTemplate(' \
<li> \
<ul id="" class="search-result-list{listNo}"> \
  <li id="{rankingConcentrationCd}" class="arrow" onclick="onRankingConcentrationClick(this)"> \
    <div class="box-wrapper"> \
      <div class="jacket-image-small-box"><img src="{loaderImage}" alt="" title="" class="lazyloadimage" onload="loadingImage(this, \'{image}\')"/></div> \
      <div class="search-result-product-info"><h2>{genreName}</h2></div> \
    </div> \
  </li> \
</ul> \
</li> \
');

/**
 * ランキングジャンル一覧のデータ取得処理
 */
function rankingGenreLoader(reloadFlg){

    var params = getICSQueryParameters();
    var storageName = "rankingGenre" + params['categoryCd'] + params['rentalSalesSection'] + params['rankingType'];

    // キャッシュを使用する
    var rank = localStorage[storageName];
    var rankData = null;
    if (undefined != rank) {
        rankData = JSON.parse(rank);
        rankingListRender(rankData);
    }
    if (reloadFlg) {
        //ロード中
        $('#page-date').html(Messages.loading);
    }

    app.startLoading();

    TWSClient.sendRequest('/media/v0/works/tsutayarankingsummary.json', {
        data: params,
        complete: function() {
            app.stopLoading();
        },
        error: function(xhr) {
            app.alert('エラー', Messages.networkError, app.goBack );
        },
        success : function(data) {
            var errMsg = '';
            if (data.error
                || !data.entry) {
                if (data.error.code == 503) {
                    errMsg = data.error.message;
                } else {
                    errMsg = Messages.serverError;
                }
                app.alert('エラー', errMsg, app.goBack );
                return;
            };

            // 初回表示または、次回更新時刻を過ぎた場合
            if (null == rankData || rankData['entry'][0]['nextUpDateEpoch'] < new Date().getTime()
                && rankData['entry'][0]['nextUpDateEpoch'] < data['entry'][0]['nextUpDateEpoch']) {

                // 画面を表示する
                rankingListRender(data);

                // データの保持
                var rankingData = JSON.stringify(data);
                localStorage[storageName] = rankingData;
            }
        }
    })
}

var totalingPeriod;

/**
 * ランキング一覧画面表示
 * @param data
 */
function rankingListRender(data) {

    var entryList = data['entry'];
    var numberOfGroups = data['numberOfGroups'];
    totalingPeriod = data['totalingPeriod'];

    // 項目初期化
    $('#rankList').html('');

    var rtitle;
    if(data['rankingTitle'] == "DVDセルランキング"){
       rtitle = data['rankingTitle'].replace("DVDセル", "販売DVD");
    }else if(data['rankingTitle'] == "CDセルランキング"){
       rtitle = data['rankingTitle'].replace("CDセル", "販売CD");
    }else if(data['rankingTitle'] == "ゲームセルランキング"){
       rtitle = data['rankingTitle'].replace("ゲームセル", "販売ゲーム");
    }else if(data['rankingTitle'] == "ブックセルランキング"){
       rtitle = data['rankingTitle'].replace("ブックセル", "販売本");
    }else{
       rtitle = data['rankingTitle'];
    }
    rtitle = rtitle.replace("ランキング", "");
    
    // タイトル表示
    $('#rankList').append(
        titleTemplate.render({
            rankingTitle: rtitle,
            totalingPeriod: data['totalingPeriod']
        })
    );

    var index = 0;
    var headName = "";
    var headerTitle = "";
    var rankingList = "";
    for (var i = 0; i < entryList.length; i++) {
        var entry = entryList[i];

        if (undefined == entry['genreInfo']) {
            continue;
        }

        if (0 < numberOfGroups && headName != entry['genreInfo']['groupName']) {
            if (rankingList != ""){
                headerTitle = headTemplate.render({
                    headName: headName
                });
                $('#rankList').append(headerTitle + '<ul class="rounded bordered">' + rankingList + '</ul>');
                rankingList = "";
            }
            // リストの色を初期化
            index = 0;
        }
        headName = entry['genreInfo']['groupName'];

        // リストを表示する
        var genreName = entry['genreInfo']['genreName'];
        var image = entry['productImage']['small'];
        
        rankingList +=  listTemplate.render({
            genreName: genreName,
            image: image,
            loaderImage: loaderImage,
            listNo: (index % 2),
            rankingConcentrationCd: entry['rankingConcentrationCd']
        });
        
        index++;
    }
    headerTitle = headTemplate.render({
        headName: headName
    });
    $('#rankList').append(headerTitle + '<ul class="rounded bordered">' + rankingList + '</ul>');
}

/**
 * 「ランキング」画面へ遷移する
 * @param elem
 */
function onRankingConcentrationClick(elem) {
    var e = $(elem).attr('id');
    app.goTo('ranking.html?rankingConcentrationCd=' + e);
}

/**
 * 再読み込み
 * localStorageのクリア
 */
function reloadRanking() {

    $('#page-date').html(Messages.loading);

    var params = getQueryParameters();
    var storageName = "rankingGenre" + params['categoryCd'] + params['rentalSalesSection'] + params['rankingType'];

    rankingGenreLoader(true);
}

/**
 * 初回アクセス
 */
$(function() {

    //APコールバックを設定
    app._stopLoadingCallback = function (){
        //バナー書き換え
        $('#page-date').html(totalingPeriod);
    };

    rankingGenreLoader();
    margin('#rankList');

});