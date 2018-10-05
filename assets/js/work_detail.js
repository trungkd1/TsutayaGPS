/** ヘッダテンプレート */
var titleTemplate = new TTemplate(' \
<h1 class="page-title header">作品情報</h1> \
');
/** ヘッダのテンプレート */
var headerTemplate = new TTemplate(' \
<div class="box-wrapper"> \
  <div class="jacket-image-medium-box"> \
    <img src="{loaderImage}" alt="" title="" class="jacket-image-medium" onload="loadingImage(this, \'{image}\')"/> \
  </div> \
  <div class="description-box"> \
    <p class="title">{title} \
    {:if workTypeDvd}{titleOpt}{:end} \
    {:if workTypeCd}<br><br>{titleOpt}<br><br>{:end} \
    {:if workTypeGame}<br><br>{titleOpt}<br><br>{:end} \
    {:if workTypeBook}{:if titleOpt}<br><br>{titleOpt}{:end}<br><br>{:end} \
    </p> \
    <ul> \
      <li>{lgenre}&nbsp;{:if mgenre}&gt;&nbsp;{mgenre}{:end}</li> \
      {:if country}<li>制作国&nbsp;:&nbsp;{country}</li>{:end} \
      {:if year}<li>制作年&nbsp;:&nbsp;{year}年</li>{:end} \
      {:if maker}<li>{:if workTypeGame}メーカー名{:else}販売元{:end}&nbsp;:&nbsp;{maker}</li>{:end} \
      {:if publisher}<li>出版社&nbsp;:&nbsp;{publisher}</li>{:end} \
      {:if release}<li>公開日&nbsp;:&nbsp;{release}</li>{:end} \
      {:if showTime}<li>上映時間&nbsp;:&nbsp;{showTime}分</li>{:end} \
    </ul> \
  </div> \
</div> \
');

/** SNSボタンのテンプレート */
var socialTemplate = new TTemplate(' \
<div class="social-box-wrapper"> \
  <ul class="social-box rounded"> \
     <li class="social-button" id="twitter-button" onClick=""><a href="app::safari::http://twitter.com/share?text={title}&url=http://www.tsutaya.co.jp/works/{urlCd}.html">ツイート</a></li> \
     <li class="social-button" id="facebook-button" onClick=""><a href="app::safari::http://www.facebook.com/sharer.php?u=http://www.tsutaya.co.jp/works/{urlCd}.html">シェア</a></li> \
     <li class="social-button" id="line-button" onClick=""><a href="app::safari::http://line.naver.jp/R/msg/text/?{title} http://www.tsutaya.co.jp/works/{urlCd}.html">LINEで送る</a></li> \
   </ul> \
</div> \
<div class="fav-box-wrapper"> \
  {:if urlCd}<div class="fav-box rounded btn-blue" id="works-{urlCd}" onClick="productfavButtonClick(\'{urlCd}\')">お気に入り{:if favadded}を解除{:else}に登録{:end}</div>{:end} \
</div> \
');

/** レビュー情報のテンプレート */
var reviewTemplate = new TTemplate(' \
<h3>レビューを見る</h3> \
<ul class="rounded bordered"> \
  {:if reviewEnable} \
  <li class="arrow" onclick="onReviewLinkClick(\'{urlCd}\');"> \
    <p class="detail review"><img src="{loaderImage}" alt="" title="" class="star-margin" onload="loadingImage(this, \'images/review_icon_{ratingDisp}.png\')" width="64" height="12" /> ({averageScore}点/{totalResults}件のレビュー)</p> \
  </li> \
  {:else} \
  <li> \
    <p class="detail">レビューはありません</p> \
  </li> \
  {:end} \
</ul> \
');
/** 在庫検索のテンプレート */
var stockSearchTemplate = new TTemplate(' \
<h3>在庫検索</h3> \
<ul class="rounded bordered"> \
  {:if rentalCd}<li class="arrow" onclick="onRentalStockSearchLinkClick(\'{rentalCd}\');"><div class="arrow-block"><span class="link-label">お店でレンタル在庫検索</span></div></li>{:end} \
  {:if saleCd}<li class="arrow" onclick="onSaleStockSearchLinkClick(\'{saleCd}\');"><div class="arrow-block"><span class="link-label">お店で販売在庫検索</span></div></li>{:end} \
</ul> \
');
/** 関連映像のテンプレート */
var relateMovieTemplate = new TTemplate(' \
<h3>関連映像をみる</h3> \
<ul class="rounded bordered"> \
  <li class="arrow" onclick="onYouTubeLinkClick(\'{sakuhinTitle}\');"> \
    <div class="arrow-block"> \
      <span class="link-label">関連映像<span>(YouTube検索)</span></span> \
      <p class="causion">※外部サイトへ遷移します</p> \
    </div> \
  </li> \
  {:if originalEnable} \
  <li class="arrow" onclick="onYouTubeLinkClick(\'{original}\');"> \
    <div class="arrow-block"> \
      <span class="link-label">Original Title Search<span>(YouTube)</span></span> \
      <p class="causion">※外部サイトへ遷移します</p> \
    </div> \
  </li> \
  {:end} \
</ul> \
');
/** 解説のテンプレート */
var commentTemplate = new TTemplate(' \
<h3>解説</h3> \
<ul class="rounded bordered"> \
  <li><div><p class="detail">{:if document}{document}{:else}解説はありません{:end}</p></div></li> \
</ul> \
');
/** キャスト／スタッフのテンプレート */
var castStaffTemplate = new TTemplate(' \
<h3>キャスト／スタッフ</h3> \
<ul class="rounded bordered"> \
  {castStaffList} \
</ul> \
');
var castStaffListTemplate = new TTemplate(' \
<li><p class="detail caststaff">{:if role}{role}&nbsp;:&nbsp;{artist}{:else}キャスト/スタッフはありません{:end}</p></li> \
');
/** 収録曲のテンプレート */
var tuneTemplate = new TTemplate(' \
<h3>収録曲一覧</h3> \
<ul class="rounded bordered"> \
  {:if tuneLinkEnable}<li class="arrow" onclick="onTuneLinkClick();"><div class="arrow-block"><span class="link-label">収録曲一覧</span></div></li> \
  {:else}<li><p class="detail">収録曲情報はありません</p></li>{:end} \
</ul> \
');
/** コピーライトのテンプレート */
var copyrightTemplate = new TTemplate(' \
<ul class="rounded bordered"> \
  <li><p class="copyright">{copyright}</p></li> \
</ul> \
');
/** renkei */
var renkeiFrameTemplate = new TTemplate(' \
{:if netrentallink || netsalelink} \
<h3>この商品は他のサービスでも扱っています</h3> \
<ul class="rounded bordered"> \
{:if netrentallink}<li class="arrow" onclick="app.goTo(\'app::safari::{netrentallink}\')"><div class="arrow-block"><span class="link-label">ネットで宅配レンタル<span>(TSUTAYA DISCAS)</span></span></div></li>{:end} \
{:if netsalelink}<li class="arrow" onclick="app.goTo(\'app::safari::{netsalelink}&ko=tsearch_sk\')"><div class="arrow-block"><span class="link-label">ネットで買う<span>(TSUTAYA オンラインショッピング)</span></span></div></li>{:end} \
</ul> {:end} \
');
/**
 * 項目を改行で結合する
 * @param arr
 * @returns
 */
var ageLimitFlag

function arrayToHTML(arr) {
    return arr.length > 0  ?
            TTemplate.raw(arr.join('<br />')) : '';
};
/**
 * 改行コードを\<br\>に変更
 * @param text
 * @returns
 */
function replaceBr(text) {
    if (!text) {
        return '';
    };
    return TTemplate.raw(text.replace(/\n/g, '<br />'));
}

/**
 * 作品情報を表示する
 * @param data
 * @returns
 */
function workDetailRender(data) {
    var entry = data['entry'][0];
    if (!entry) {
        app.alert('作品情報が参照できません');
        return false;
    }

    var workTypeDvd = false;
    var workTypeCd = false;
    var workTypeGame = false;
    var workTypeBook = false;
    var workType = entry['urlCd'].substring(0, 1);
    var titleOpt;
    var digest = entry['digest'];
    ageLimitFlag = entry['ageLimitDispFlag'];
    if (undefined == digest) {
        app.alert('作品情報が参照できません');
        return false;
    }
    // 映像系
    if ("1" == workType || "5" == workType || "6" == workType) {
        if (null != digest['productionYear']) {
            titleOpt = '(' + entry['digest']['productionYear'] + '年)';
        }
        workTypeDvd = true;
    }
    // 音楽
    else if ("2" == workType) {
        if (null != entry['artistName']) {
            titleOpt = entry['artistName'];
        }
        workTypeCd = true;
    }
    // ゲーム
    else if ("3" == workType) {
        if (null != entry['modelName']) {
            titleOpt = entry['modelName'];
        }
        workTypeGame = true;
    }
    // その他（本）
    else if ("4" == workType) {
        if (null != entry['artistName']) {
            titleOpt = entry['artistName'];
        }
        workTypeBook = true;
    }
    else {
        // なし
    }

    // タイトルを表示する
    $('#body').append(
        titleTemplate.render({ })
    );

    // 基本情報を表示する
    var headerItem = headerTemplate.render({
        title : entry['sakuhinTitle'],
        titleOpt : titleOpt,
        lgenre : digest['lGenreName'],
        mgenre : digest['mGenreName'],
        country : digest['productionCountry'],
        year : digest['productionYear'],
        workTypeDvd : workTypeDvd,
        workTypeCd : workTypeCd,
        workTypeGame : workTypeGame,
        workTypeBook : workTypeBook,
        maker : digest['makerName'],
        publisher : digest['publisherName'],
        release : digest['cinemaReleaseDate'],
        showTime: digest['showingTime'],
        image : entry['image']['medium'],
        loaderImage: loaderImage
    });
    $('#product-header').append(headerItem);
    $('#product-header').addClass("rounded product-header-section");
    
    // ソーシャルボタンを表示する
    var socialItem = socialTemplateRender(entry);
    if (null != socialItem) {
        $('#social-button').append(socialItem);
    }

    // レビュー情報を表示する
    var reviewItem = reviewTemplateRender(entry);
    if (null != reviewItem) {
        $('#review-link').append(reviewItem);
    }

    // 在庫検索を表示する
    var stockItem = stockSearchTemplateRender(entry);
    if (null != stockItem) {
        $('#stock-search-link').append(stockItem);
    }

    // 関連映像を表示する
    if (workTypeDvd) {
        var relateMovieItem = relateMovieTemplateRender(entry,digest);
        $('#relate-movie-link').append(relateMovieItem);
    }

    // 解説を表示する
    var commentItem = commentTemplateRender(entry);
    $('#comment-link').append(commentItem);

    // キャスト／スタッフを表示する
    if (workTypeDvd) {
        var castStaffItem = castStaffTemplateRender(digest);
        if (null != castStaffItem) {
            $('#cast-staff-link').append(castStaffItem);
        }
    }

    // 収録曲一覧を表示する
    if (workTypeCd) {
        var tuneItem = tuneTemplateRender(entry);
        if (null != tuneItem) {
            $('#tune-link').append(tuneItem);
        }
    }

    // コピーライトを表示する
    var copyrightItem = copyrightTemplateRender(entry);
    if (null != copyrightItem) {
        $('#copyright-link').append(copyrightItem);
    }
    var renkeilink = renkeiFrameTemplateRender(entry);
    if(null != renkeilink){
    	$('#renkei-frame-link').append(renkeilink);
    }
    return true;
};

/**
 * ソーシャルボタン情報を取得する
 * @param entry
 * @returns
 */
function socialTemplateRender(entry) {
	var prdList = new ProductList();
	var alreadyadded = false;
    if (prdList.isProductAdded(entry['urlCd'])) {
        alreadyadded = true;
    }
	return socialTemplate.render({
        title : entry['sakuhinTitle'],
        urlCd: entry['urlCd'],
        favadded: alreadyadded
    });
}
/**
 * お気に入り追加ボタン表示内容切り替え
 * @param urlCd
 * @param islisted  解除表示にしたい:true 登録表示したい:false
 * @returns
 */
function changefavlabel(urlCd,islisted){
	 if (islisted === true) {
		 $('div#works-' + urlCd).html('お気に入りを解除');
	 } else {
		 $('div#works-' + urlCd).html('お気に入りに登録');
	 }
}
/**
 * ボタンクリック時の状態でお気に入りの追加と削除を行う
 * 
 */
function productfavButtonClick(urlCd) {
    var favItemList = new ProductList();
    if (favItemList.isProductAdded(urlCd)) {
	    app.confirm('お気に入り商品',"お気に入りを解除しますか？", "OK", "Cancel",
		        function(buttonIndex) {
		            if (buttonIndex == 0 ) {
		            	if(favItemList.deleteProduct(urlCd)){
		            		changefavlabel(urlCd,false);
		            		app.alert('登録解除','登録を解除しました。');	            		
		            	} else {
		            		app.alert('登録解除','解除に失敗しました。');
		            	}
		                return true;
		            }
		        }
		    );    	
    } else {
	    app.confirm('お気に入り商品',"お気に入りに追加しますか？", "OK", "Cancel",
	        function(buttonIndex) {
	            if (buttonIndex == 0 ) {
	                if(addfavlist(urlCd)){
	                	app.alert('お気に入り商品',"登録しました");
	                	changefavlabel(urlCd,true);	                	
	                	return true;
	                } else {
	                    app.alert('お気に入り商品',"登録に失敗しました");
	                	return false;
	                }
	            }
	        }
	    );
   }
}

/**
 * お気に入り登録
 * @returns {Boolean}
 */
function addfavlist(worksId) {

    // 登録可能チェック
    var productList = new ProductList();
    var urlCd = worksId;
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
            adultAuthOK: 1
//            adultAuthOK: AgeLimitAuth.isAuthOK()
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
                  return true;
              } else {
                  return false;
              };
          }
        }
    });
    return true;
}
/**
 * レビュー情報を取得する
 * @param entry
 * @returns
 */
function reviewTemplateRender(entry) {
    var review = entry['review'];
    if (undefined != review) {
        return reviewTemplate.render({
            urlCd: entry['urlCd'],
            reviewEnable: entry['urlCd'],
            averageScore: review.averageScore,
            ratingDisp: review.ratingDisp,
            totalResults: review.totalResults,
            loaderImage: loaderImage
        });
    }
    else {
        return reviewTemplate.render({
            reviewEnable: null,
        });
    }
    return null;
}
/**
 * 在庫情報を取得する
 * @param entry
 * @returns
 */
function stockSearchTemplateRender(entry) {
    var renkeiframe = entry['renkeiframe'];
    var rental = renkeiframe['storeRentalFrameInfo'];
    var rentalCd;
    if (undefined != rental) {
        rentalCd = rental['cccSakuhinCd'];
    }
    var sale = renkeiframe['storeSaleFrameInfo'];
    var saleCd;
    if (undefined != sale) {
        saleCd = sale['cccSakuhinCd'];
    }
    if (null != rentalCd || null != saleCd) {
        return stockSearchTemplate.render({
            rentalCd: rentalCd,
            saleCd: saleCd
        });
    }
    return null;
}
/**
 * 関連映像情報を取得する
 * @param entry
 * @param digest
 * @returns
 */
function relateMovieTemplateRender(entry, digest) {
    return relateMovieTemplate.render({
        sakuhinTitle : entry['sakuhinTitle'],
        original : digest['original'],
        originalEnable : digest['original']
    });
}
/**
 * 解説情報を取得する
 * @param entry
 * @returns
 */
function commentTemplateRender(entry) {
    var description = entry['descriptionDocInfo'];
    var document = undefined != description ? description['document'] : null;
    if (null == document) {
        document = '解説はありません';
    }
    return commentTemplate.render({
        document : replaceBr(document)
    });
}
/**
 * キャスト／スタッフ情報を取得する
 * @param digest
 * @returns
 */
function castStaffTemplateRender(digest) {
    var artistInfoList = digest['artistInfoList'];
    var castStaffList = [];
    if (undefined != artistInfoList) {
        for (var i = 0; i < artistInfoList['artistInfo'].length; i++) {
            var artistInfo = artistInfoList['artistInfo'][i];
            castStaffList.push(
                castStaffListTemplate.render({
                    role: artistInfo['roleName'],
                    artist: artistInfo['artistName']
                })
            );
        }
    }
    // キャスト／スタッフが存在しない場合
    if (0 == castStaffList.length) {
        castStaffList.push(
            castStaffListTemplate.render({
                role: null,
                artist: null
            })
        );
    }
    return castStaffTemplate.render({
        castStaffList : TTemplate.raw(castStaffList.join(''))
    });
}
/**
 * 収録曲情報を取得する
 * @param entry
 * @returns
 */
function tuneTemplateRender(entry) {
    var tuneList = entry['tune'];
    if (undefined != tuneList) {
        var tuneLinkEnable = false;
        if (0 < tuneList['entry'].length) {
            tuneLinkEnable = true;
        }
        return tuneTemplate.render({
            tuneLinkEnable : tuneLinkEnable
        });
    }else{
        var tuneLinkEnable = false;
        return tuneTemplate.render({
        tuneLinkEnable : tuneLinkEnable
        });
}
    return null;
}
/**
 * コピーライト情報を取得する
 * @param entry
 * @returns
 */
function copyrightTemplateRender(entry) {
    var copyright = entry['copyright'];
    if (undefined != copyright) {
        var copyrightList = [];
        undefined != copyright['copyright1'] ? copyrightList.push(copyright['copyright1']) : false;
        undefined != copyright['copyright2'] ? copyrightList.push(copyright['copyright2']) : false;
        undefined != copyright['copyright3'] ? copyrightList.push(copyright['copyright3']) : false;
        undefined != copyright['note1'] ? copyrightList.push(copyright['note1']) : false;
        undefined != copyright['jkPhotoNoteSection'] ? copyrightList.push(copyright['jkPhotoNoteSection']) : false;
        undefined != copyright['scenePhotoNoteSection'] ? copyrightList.push(copyright['scenePhotoNoteSection']) : false;
        undefined != copyright['note5'] ? copyrightList.push(copyright['note5']) : false;
        return copyrightTemplate.render({
            copyright : arrayToHTML(copyrightList)
        });
    }
    return null;
}
/**
 * 連携リンク
 */
function renkeiFrameTemplateRender(entry){
	var renkeiframe = entry['renkeiframe'];
	if(undefined != renkeiframe){
		if(undefined != renkeiframe['netSaleFrameInfo']){
			var netSalesInfo = renkeiframe['netSaleFrameInfo'];
			var sakuhinCd = netSalesInfo['cccSakuhinCd'];
			var tolshoplink = String(netSalesInfo['link'][0]['href']).replace("shop","sp");
			var discas = undefined;
			if(undefined != sakuhinCd && undefined != renkeiframe['storeRentalFrameInfo']){
				var stype = String(sakuhinCd).substring(0,1);
    			if(stype == 1 || stype == 2){
				   discas = 'http://www.discas.net/d/d/entry_site.pl?SITE=ccc_p_tol_tsutayasearchsakuhin&AP=ggd&GID=' + sakuhinCd;
    			}
			}
			return renkeiFrameTemplate.render({
				netrentallink : discas,
			    netsalelink : tolshoplink
			});
		}
	}
	return null;
}

/**
 * 初回起動
 */
$(function() {
    getWorkDetailInfo();
    window.refreshView = function () {
        var params = getICSQueryParameters();
        var urlCd = params['urlCd'];
        addMenu(urlCd);
    };
});

function getWorkDetailInfo(){
    var params = getICSQueryParameters();
    var urlCd = params['urlCd'];

    addMenu(urlCd);

    // 作品情報を取得する
    var query = {
            urlCd: urlCd,
            fieldSet: 'digest;review;description;tune;renkeiframe',
            'review.dispNums': 5,
            'review.dispPageNo': 1,
            adultAuthOK: AgeLimitAuth.isAuthOK()
        };

    app.startLoading();

    TWSClient.sendRequest('/media/v0/works/detail.json', {
        data: query,
        complete: function() {
            app.stopLoading();
        },
        error: function(xhr) {
            app.alert('エラー', Messages.networkError, app.goBack );
        },
        success : function(data) {
            var errMsg = '';
            if (data.error) {
                if (data.error.code == 401
                        && data.error.id == 'age_limit' ) {
                        app.confirm('年齢確認', Messages.ageLimitAlert, 'いいえ', 'はい',
                            function(buttonIndex) {
                                if (buttonIndex == 1 ) {
                                    AgeLimitAuth.setAuthOK();
                                    getWorkDetailInfo();
                                } else {
                                    app.goBack();
                                };
                            }
                        );
                        return;
                } else if (data.error.code == 503) {
                    errMsg = data.error.message;
                } else {
                    errMsg = Messages.serverError;
                }
                app.alert('エラー', errMsg, app.goBack );
                return;
            };
            var reslut = workDetailRender(data);
            $("img.reflect").reflect({/* Put custom options here */});

            // 表示用データを保持する
            if (reslut) {
                var reviewData = JSON.stringify(data['entry'][0]['review']);
//                sessionStorage["reviewData"] = reviewData;
                localStorage["reviewData"] = reviewData;
                var tuneData = JSON.stringify(data['entry'][0]['tune']);
//                sessionStorage["tuneData"] = tuneData;
                localStorage["tuneData"] = tuneData;
            }
        }
    })
}


/**
 * レビュー情報へ遷移する
 * @param param
 * @returns
 */
function onReviewLinkClick(param) {
    app.goTo("review_detail.html?urlCd=" + param);
}
/**
 * YouTubeリンク
 * @param param
 * @returns
 */
function onYouTubeLinkClick(param) {
    document.location.href = "http://m.youtube.com/results?client=mv-google&gl=JP&hl=ja&search_type=by_search&submit=%E6%A4%9C%E7%B4%A2&q=" + encodeURIComponent(param);
}
/**
 * レンタル在庫検索リンク
 * @param param
 * @returns
 */
function onRentalStockSearchLinkClick(param) {
    onStockSearchLinkClick('0', param);
}
/**
 * セル在庫検索リンク
 * @param param
 * @returns
 */
function onSaleStockSearchLinkClick(param) {
    onStockSearchLinkClick('1', param);
}
/**
 * 在庫検索
 * @param saleRentalCd
 * @param cccSakuhinCd
 * @returns
 */
function onStockSearchLinkClick(saleRentalCd, cccSakuhinCd) {
	if(ageLimitFlag == 1){
	    localStorage['safeSearch'] = 0;
	}
    app.goTo("search_result.html?saleRentalCd=" + saleRentalCd + "&cccSakuhinCd=" + cccSakuhinCd);
}
/**
 * 収録曲一覧
 * @returns
 */
function onTuneLinkClick() {
    app.goTo("tune_list.html");
}
