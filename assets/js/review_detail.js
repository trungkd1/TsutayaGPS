var Pager = function(){
    this.page = 0;
    this.isLoading = false;
    this.totalResults = -1;
    this.itemsPerPage = 5;
    this.loadedItemCount = 0;
    this.maxItemsPerPage = 200;

    this.currentPage = function() {
        return this.page;
    };
    this.loadNextPage = function() {
        if (!this.isLoading) {
            this.isLoading = true;
            loadSearchResult(++this.page, this.itemsPerPage);
        };
    };
    this.loadFinished = function() {
        this.isLoading = false;
    }
    this.getLoadedItemCount = function() {
        return this.loadedItemCount;
    }
    this.setTotalResults = function(num) {
        this.totalResults = num;
    }
    this.addLoadedItemCount = function(num) {
        this.loadedItemCount += num;
    }
    this.hasNextPage = function() {
        return this.loadedItemCount < this.totalResults ? true : false;
    }
    // ページ内で更にロード可能かどうか。
    this.canLoadMoreItem = function() {
        return this.hasNextPage() && this.loadedItemCount < this.maxItemsPerPage ? true : false;
    }
    this.isLoadItemMaxOver = function() {
        return this.loadedItemCount >= this.maxItemsPerPage ? true :false;
    }
    this.getTotalResults = function() {
        return this.totalResults;
    }
};
var pager = new Pager();

// 初回読み込み
$(function() {

//    var review = sessionStorage["reviewData"];
    var review = localStorage["reviewData"];
    if (undefined == review) {
        pager.loadNextPage();
    }
    // セッションにデータがある場合
    else {
        var reviewData = JSON.parse(review);
        // ページ送り
        pager.page++;
        reviewTmplRender(reviewData, pager.getLoadedItemCount());
    }
 // お気に入り情報確認
    var urlCd = getICSQueryParameters()['urlCd'];
    addMenu(urlCd);

    window.refreshView = function () {
    	var urlCd = getICSQueryParameters()['urlCd'];
    	addMenu(urlCd);
    };
});

// 表示データ作成
function loadSearchResult(page, dispNums) {
    var sendRequest = function() {
        var params = getICSQueryParameters();
        if (pager.currentPage() == 1) {
            app.startLoading();
        }

        var query = {
            dispPageNo: page,
            dispNums: dispNums
        };
        if (params['urlCd']) {
            query['urlCd'] = params['urlCd'];
        };

        // 表示順用
        var loadedNum = pager.getLoadedItemCount();

        TWSClient.sendRequest('/media/v0/works/review.json', {
            data: query,
            complete: function() {
                //通信終了時の処理
                if (pager.currentPage == 1) {
                    app.stopLoading();
                }
                pager.loadFinished();
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
                // 画面表示
                reviewTmplRender(data, loadedNum);
            }
        })
    };
    sendRequest();
}

// レビュー情報を表示する
function reviewTmplRender(data, loadedNum) {

    // ページャ情報の更新
    pager.setTotalResults( data['totalResults'] );
    pager.addLoadedItemCount( data['entry'].length );

    // タイトル部の作成
    $('#title').html( reviewTmpl.render( {
        sakuhinName: data.sakuhinName,
        averageScore: data.averageScore,
        ratingDisp: data.ratingDisp,
        totalResults: data.totalResults,
        loadedItemCount : pager.loadedItemCount,
        loaderImage: loaderImage
    } ) );

    // レビュー内容部の作成
    for (var i = 0; i < data['entry'].length; i++) {
        var info = data['entry'][i];
        var item = tmpl.render({
//            order: info['order'] + pager.loadedItemCount,
            order: (++loadedNum),
            netabareFlag: info['netabareFlag'],
            reviewTitle: info['reviewTitle'],
            eachRatingDisp: info['eachRatingDisp'],
            contributorName: info['contributorName'],
            contributeDate: info['contributeDate'],
            commentText: transCRLF2Br(info['commentText']),
            loaderImage: loaderImage
        });
        $('#review-list').append(item);
    }

    // もっと表示するボタン制御
    if (pager.hasNextPage()) {
        $('#loading-more').html( load );
    }
    else {
        $('#loading-more').remove();
    }
    // 最大表示件数オーバー時のメッセージ制御
    if (pager.isLoadItemMaxOver()) {
        var msg = '一度に読み込めるのは' + pager.maxItemsPerPage  + '件までです';
        $('#footer').html('<div id="footer-message">' + msg + '</div>');
    }
}

// もっと読み込むボタン押下時
function reviewNextPage() {
    $('#load-more').html('読み込み中...');
    pager.loadNextPage();
}

// レビュー概要
var reviewTmpl = new TTemplate( ' \
<h1 class="page-title">「{sakuhinName}」のレビュー</h1> \
<div class="review-top rounded"> \
<div class="title">評価点：{averageScore}</div> \
<div class="rate"><img src="{loaderImage}" alt="" title="" class="" onload="loadingImage(this, \'images/review_icon_{ratingDisp}.png\')"/></div> \
</div> \
<h3>{totalResults}件中{loadedItemCount}件のレビュー</h3>\
' );

// レビュー内容
var tmpl = new TTemplate(' \
<ul class="rounded"> \
{:if netabareFlag} <li><p class="netabare">ネタバレ</p></li> {:end} \
<li><div><p class="title">{order}.{reviewTitle}</p></div></li> \
<li><div class="review-image-star"><img src="{loaderImage}" alt="" title="" class="" onload="loadingImage(this, \'images/review_icon_{eachRatingDisp}.png\')"/></div></li> \
<li><div class="review-contributor">投稿者：{contributorName}&nbsp;&nbsp;{contributeDate}</div></li> \
<li><div><p class="detail">{commentText}</p></div></li> \
</ul> \
');

// もっと見るリンク
var load = ' \
<ul class="rounded"> \
  <li onclick="reviewNextPage();"><div><p id="load-more" class="load-more">もっと読み込む</p></div></li> \
</ul> \
';