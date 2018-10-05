/** ヘッダのテンプレート */
var headTemplate = new TTemplate(' \
<div id="ranking-top-nobaner"> \
  <h1>TSUTAYA {rankingTitle}<p>{genreName}</p></h1>\
  <p class="h3" id="page-date">{totalingPeriod}</p> \
</div> \
');
/** リストのテンプレート */
var listTemplate = new TTemplate(' \
<li> \
<ul id="" class="list search-result-list{listNo}"> \
<li id="{urlCd}" class="arrow" onclick="onRankingConcentrationClick(this)"> \
  <div class="box-wrapper"> \
    <div class="jacket-image-medium-box"><img src="{loaderImage}" alt="" title="" class="lazyloadimage" onload="loadingImage(this, \'{image}\')"/><p class="rank-{rankState}">{rankSign}&nbsp;{:if lastRankNo}({lastRankNo}){:end}</p></div> \
      <ul id="search-result-list-child" class="list"> \
        <li> \
          <div class="box-wrapper"> \
            <div class="ranking-{crown}"><p class="ranking">{rankNo}&nbsp;位</p></div> \
            <ul id="search-result-list-child"> \
              <li><p class="rank-title">{productTitle}</p></li> \
            </ul> \
          </div> \
        </li> \
        <li> \
          <table class="artist-list-table"> \
            {artistList} \
          </table> \
        </li> \
      </ul> \
  </div> \
</li> \
</ul> \
</li> \
');
/** 出演者リスト */
var artistTemplate = new TTemplate(' \
<tr> \
<td><p class="role-name">{roleName}</p></td> \
<td><p class="artist-name">&nbsp;{artistName}</p></td> \
</tr> \
');

/**
 * 1位から3位は背景を変更する
 * @param rankNo
 * @returns
 */
function rankingCheck(rankNo) {
    if (1 == rankNo) {
        return 'gold';
    }
    else if (2 == rankNo) {
        return 'silver';
    }
    else if (3 == rankNo) {
        return 'bronze';
    }
    return 'none';
}
/**
 * 前回順位の表示
 * @param transition
 * @returns
 */
function rankingCompare(transition) {
    if (0 == transition) {
        return '↑';
    }
    else if (1 == transition) {
        return '→';
    }
    else if (2 == transition) {
        return '↓';
    }
    return '初';
}

var totalingPeriod;

/**
 * ランキング一覧出力
 */
function rankingListRender(data) {

    // 項目初期化
    $('#rank-body').html('');

    /*******************
    //タイトルフォントの調整(18はh1のデフォルトフォントサイズ)
    if( (data['rankingTitle'].length + (data['genreName'] ? data['genreName'].length : 0)) * 18 >= app.MEDIUM_SCREEN ) {
        // タイトル + ジャンル名称 + 接続文字『-』
        data['rankingTitleFontsize'] = Math.floor(app.MEDIUM_SCREEN / (data['rankingTitle'].length + data['genreName'].length + 1));
    }
    ********************/

    //期間を保存しておく
    totalingPeriod = data['totalingPeriod'];

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

    // ヘッダ情報を出力
    $('#rank-body').append(
        headTemplate.render({
            rankingTitle: rtitle,
            genreName: data['genreName'],
            totalingPeriod: data['totalingPeriod']
        })
    );

    var rankingList = "";
    for (var i = 0; i < data['entry'].length; i++) {
        var entry = data['entry'][i];

        // 1位から3位は画像を背景に選択する
        var rankNo = entry['rankNo'];
        var rankingCrown = rankingCheck(rankNo);

        // 前回との比較
        var transition = entry['transition']
        var rankSign = rankingCompare(transition);
        if(transition !=0 && transition !=1 && transition !=2){
        	transition = 3
        }

        if(undefined != entry['urlCd']){
            var workType = entry['urlCd'].substring(0, 1);
        }

        // 出演者リストを作成
        var artistInfoList = entry['artistInfoList']
        var artistList = [];

        // ゲームの場合
        if (3 == workType) {
            // メーカ
            artistList.push(
                artistTemplate.render({
                    roleName: 'メーカー',
                    artistName: entry['makerName']
                })
            );
            // 機種名
            artistList.push(
                artistTemplate.render({
                    roleName: '機種',
                    artistName: entry['modelName']
                })
            );
        }
        // その他
        else {
            // アーティスト情報がある場合のみ
            if (undefined != artistInfoList
                    && undefined != artistInfoList['artistInfo']) {

                // アーティスト情報が複数ある場合（リスト）
                for (var j = 0; j < artistInfoList['artistInfo'].length; j++) {
                    if (j == 2) {
                        break;
                    }
                    var artistInfo = artistInfoList['artistInfo'][j];
                    artistList.push(
                        artistTemplate.render({
                            roleName: artistInfo['roleName'],
                            artistName: artistInfo['artistName']
                        })
                    );
                }
                // アーティスト情報が複数ない場合（非リスト）
                if (0 == artistList.length
                        && !(artistInfoList['artistInfo'] instanceof Array)) {
                    var artistInfo = artistInfoList['artistInfo'];
                    artistList.push(
                        artistTemplate.render({
                            roleName: artistInfo['roleName'],
                            artistName: artistInfo['artistName']
                        })
                    );
                }
            }
        }

        // ボディー部の作成
        rankingList += listTemplate.render({
            rankState: transition,
            rankSign: rankSign,
            urlCd: entry['urlCd'],
            image: entry['productImage']['small'],
            loaderImage: loaderImage,
            listNo: (i % 2),
            lastRankNo: entry['lastRankNo'],
            crown: rankingCrown,
            rankNo: rankNo,
            productTitle: addBrTag(entry['productTitle'], app.MEDIUM_SCREEN - 150, 13, 2),
            artistList: TTemplate.raw(artistList.join(''))
        });
    }
    $('#rank-body').append('<ul class="rounded bordered">' + rankingList + '</ul>');
}

/**
 * 長い文字列を短く見せます。
 * （HTMLの属性を付けると横スクロールしてしまう為）
 *
 * @param target
 * @returns
 */
function addBrTag(target, width, fontsize) {
	return addBrTag(target, width, fontsize, 1);
}

/**
 * 長い文字列を短く見せます。
 * （HTMLの属性を付けると横スクロールしてしまう為）
 *
 * @param target
 * @returns
 */
function addBrTag(target, width, fontsize, line) {
	if(target.length * fontsize <= width * line) {
		return target;
	} else {
		//固定でうしろ7文字残す
		var beforeLen = Math.floor(width * line / fontsize) - 8;
		return TTemplate.raw(target.slice(0,beforeLen) + "..." + target.slice(target.length-7));
	}
}

/**
 * ランキングデータ取得処理
 * @returns
 */
function rankingLoader(reloadFlg){

    var params = getICSQueryParameters();
    var storageName = "ranking" + params['rankingConcentrationCd'];

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

    var query = {
        rankingConcentrationCd: params['rankingConcentrationCd'],
        dispNums: 50
    };

    app.startLoading();

    TWSClient.sendRequest('/media/v0/works/tsutayarankingresult.json', {
        data: query,
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
                if(!data['genreName']) {
                    data['genreName'] = data['modelName'];
                }

                rankingListRender(data);

                // データの保持
                var rankingData = JSON.stringify(data);
                localStorage[storageName] = rankingData;
            }
        }
    })
}

/**
 * 「作品情報」画面へ遷移する
 * @param elem
 * @returns
 */
function onRankingConcentrationClick(elem) {
    var e = $(elem).attr('id');
    app.goTo('work_detail.html?urlCd=' + e);
}

/**
 * 再読み込み
 * localStorageのクリア
 */
function reloadRanking() {

    var params = getICSQueryParameters();
    var storageName = "ranking" + params['rankingConcentrationCd'];
    $('#page-date').html(Messages.loading);

    rankingLoader(true);
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

    rankingLoader();
});