/** ヘッダテンプレート */
var headerTemplate = new TTemplate(' \
<h1 class="page-title header">「{title}」の収録曲一覧</h1> \
');
/** 収録曲のテンプレート */
var tuneTemplate = new TTemplate(' \
{:if discEnable}<h1>Disc.{diskNo}</h1>{:end} \
<ul class="rounded bordered"> \
  {tuneList} \
</ul> \
');
var tuneNameTemplate = new TTemplate(' \
<li><p class="detail">{tuneNo}.{tuneName}{lyricist}</p></li> \
');
var castStaffListTemplate = new TTemplate(' \
<br/>{role}&nbsp;:&nbsp;{artist} \
');

/**
 * 収録曲情報を取得する
 * @param tune
 * @returns
 */
function tuneListRender(tune) {

    var discEnable = false;
    if (1 < tune['allDiscNums']) {
        discEnable = true;
    }
    // 作品タイトル
    $('#tune-link').append(
        headerTemplate.render({
            title: tune['sakuhinTitle']
        })
    );

    for (var i = 0; i < tune['entry'].length; i++) {
        var tuneList = [];

        var diskNum;
        for (; i < tune['entry'].length; i++) {

            var entry = tune['entry'][i];
            diskNo = entry['discNo'];

            // 作詞者情報
            var lyricistInfo = entry['lyricistInfo'];
            var lyricist = [];
            if (undefined != lyricistInfo) {
                for (var j = 0; j < lyricistInfo.length; j++) {
                    var artistInfo = lyricistInfo[j];
                    lyricist.push(
                        castStaffListTemplate.render({
                            role: artistInfo['roleName'],
                            artist: artistInfo['artistName']
                        })
                    );
                }
            }
            // 曲名
            tuneList.push(
                tuneNameTemplate.render({
                    tuneNo: entry['tuneNo'],
                    tuneName: entry['tuneName'],
                    lyricist: TTemplate.raw(lyricist.join(''))
                })
            );

            // 一番最後または、ディスクが変わる時点でBreakする
            if ((i + 1) == tune['entry'].length
                    || diskNo != tune['entry'][i + 1]['discNo']) {
                break;
            }
        }
        // ディスク情報
        if (0 < tuneList.length) {
            $('#tune-link').append(
                tuneTemplate.render({
                    discEnable: discEnable,
                    diskNo : diskNo,
                    tuneList : TTemplate.raw(tuneList.join(''))
                })
            );
        }
    }
    return null;
}

/**
 * 初期起動
 */
$(function() {
//    var tune = sessionStorage["tuneData"];
    var tune = localStorage["tuneData"];
    if (undefined == tune) {
        // あり得ないが念のため
        app.alert('エラー', Messages.serverError, app.goBack );
        return null;
    }
    var tuneData = JSON.parse(tune);
    tuneListRender(tuneData);
});
