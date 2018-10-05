/** ランキング検索コード */
var rankingConcentrationCd = {
        daily : 'D045,D002,D055,D050,D033,D060',
        weekly : 'W057,W002,W078,W066,W033,W090,W083',
        monthly : 'M066,M002,M086,M074,M034,M098,M091'
}
/** ランキングタイトル */
var rankingTitle = {
        daily : "デイリー",
        weekly : "週間",
        monthly : "月間"
}

/**
 * ランキング表示データ読み込み処理
 */
function rankingDataLoader(reloadFlg) {

    var rankingType = localStorage["rankingType"];
    var storageName = "rankingTop" + rankingType;

    // キャッシュを使用する
    var rank = localStorage[storageName];
    var rankData = null;
    if (undefined != rank) {
        rankData = JSON.parse(rank);
        rankingRender(rankData);
    }
    if (reloadFlg) {
        //ロード中
        $('#page-date').html(Messages.loading);
    }

    app.startLoading();

    TWSClient.sendRequest('/media/v0/works/tsutayarankingsummary.json', {
        data: {
            rankingConcentrationCdList: rankingConcentrationCd[rankingType]
        },
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

                // 画面表示
                displyChange(rankingType);
                rankingRender(data);

                // データの保持
                var rankingData = JSON.stringify(data);
                localStorage[storageName] = rankingData;
            }
        }
    })
}

var totalingPeriod;

/**
 * ランキングデータ表示処理
 * @param data
 */
function rankingRender(data){

    var rankingType = localStorage["rankingType"];
    totalingPeriod = data['totalingPeriod'];

    $('#page-date').html(data['totalingPeriod']);

    var i = 0;
    // レンタルランキング
    $('#dvd011').attr('src', data['entry'][i++]['productImage']['small']);
    $('#cd021').attr('src', data['entry'][i++]['productImage']['small']);
    $('#commic041').attr('src', data['entry'][i++]['productImage']['small']);
    // 販売ランキング
    $('#dvd012').attr('src', data['entry'][i++]['productImage']['small']);
    $('#cd022').attr('src', data['entry'][i++]['productImage']['small']);
    if ('daily' != rankingType) {
        // デイリーは表示しない（ゲーム）
        $('#game032').attr('src', data['entry'][i++]['productImage']['small']);
    }
    $('#book042').attr('src', data['entry'][i++]['productImage']['small']);
}

/**
 * 「ランキングジャンル一覧」画面へ遷移する
 * @param elem
 */
function onRankingConcentrationClick(elem){
    var e = $(elem).attr('id').split("-");
    var rankingType = localStorage["rankingType"];
    undefined != rankingType ? e.push(rankingType) : false;
    if (3 != e.length) {
        app.alert('エラー', Messages.serverError, app.goBack );
    }
    app.goTo('ranking_genre_list.html?categoryCd=' + e[0] + '&rentalSalesSection=' + e[1] + '&rankingType=' + rankingType);
}

/**
 * ランキングタイプ選択時
 * デイリー、週間、月間
 * @param rankingType
 */
function onRankingTypeClick(rankingType) {
    localStorage["rankingType"] = rankingType;
    // 一旦初期化
    displyChange(rankingType);
    rankingDataLoader();
}

/**
 * ランキングリストの初期表示を制御する
 * @param rankingType
 */
function displyChange(rankingType) {

    // ボタンの色を初期化
    var daily = document.getElementById('rankingDaily');
    var weekly = document.getElementById('rankingWeekly');
    var monthly = document.getElementById('rankingMonthly');
    daily.className="cssButton-left cssButton-noSelect";
    weekly.className="cssButton-center cssButton-noSelect";
    monthly.className="cssButton-light cssButton-noSelect";

    // ボタンの表示
    if ('daily' == rankingType) {
        daily.className="cssButton-left cssButton-select";
    }
    else if ('weekly' == rankingType) {
        weekly.className="cssButton-center cssButton-select";
    }
    else if ('monthly' == rankingType) {
        monthly.className="cssButton-light cssButton-select";
    }

    // ヘッダの表示
    $('#page-title > p').html(rankingTitle[rankingType]);
    $('#page-date').html(Messages.loading);

    // ランキングの表示
    if ('daily' == rankingType) {
        $("#game-sale-ranking").parent().css("display", "none");
        $("#book-sale-ranking").removeClass("search-result-list1");
        $("#book-sale-ranking").addClass("search-result-list0");
    }
    else {
        $("#game-sale-ranking").parent().css("display", "");
        $("#book-sale-ranking").removeClass("search-result-list0");
        $("#book-sale-ranking").addClass("search-result-list1");
    }

    // 画像の初期化
    var img = $('li img');
    img.each(
        function() {
            $(this).attr('src', loaderImage);
        }
    );
}

/**
 * 再読み込み
 * localStorageのクリア
 */
function reloadRanking() {

    $('#page-date').html(Messages.loading);

    var rankingType = localStorage["rankingType"];
    var storageName = "rankingTop" + rankingType;

    // キャッシュを削除する
    rankingDataLoader(true);
}

/**
 * 初回読み込み
 * ※デイリー
 */
$(function() {
    //APコールバックを設定
    app._stopLoadingCallback = function (){
        //バナー書き換え
        $('#page-date').html(totalingPeriod);
    };
    var params = getICSQueryParameters();
    var rankingType = params['rankingType'] ? params['rankingType'] : 'daily'; 
    onRankingTypeClick(rankingType);
    margin('#ranking-top');

});