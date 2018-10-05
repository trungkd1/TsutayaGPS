(function(){
$(function() {
    var pageTmpl = new TTemplate( ' \
<h1 id="page-title">{pageTitle}</h1> \
<h3>お得情報</h3> \
<ul id="information-list" class="bordered rounded"> \
{infoList} \
</ul> \
' );
    var tmpl = new TTemplate(' \
<li> \
<a name="item-{order}"></a> \
<h4>{title}</h4> \
<p>{text}</p> \
{:if infoUpdateTime}<p class="last-update">更新日：{infoUpdateTime}</p>{:end} \
</li>');

    var replaceTextLink = function(text) {
        var i= 0,
            tokens = [],
            result = [];

        if (text === undefined) {return ''};
        tokens = text.split(/(https?:\/\/[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+)/);
        for (i=0; i < tokens.length; i++) {
            if (tokens[i].match(/(https?:\/\/[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+)/)) {
                var matched = RegExp.$1;
                result.push( '<a href="app::safari::' + TTemplate.escapeHTML(matched) + '">'
                       + TTemplate.escapeHTML(tokens[i]) + '</a>' );
            } else {
                result.push( TTemplate.escapeHTML(tokens[i]) );
            }
        };
        return result.join('');
    }

    var sendRequest = function() {
        var params = getICSQueryParameters();

        var storeId = params['storeId'];

        app.startLoading();

        TWSClient.sendRequest('/store/v0/store/registrationstoreinfo.json', {
            data: {
                'processSection': 1,
                storeId: storeId
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
                var entry = data['entry'][0];
                var infoHTML = '';
                if (entry['profitInfo']) {
                    var tmplist = [];
                    for (var i = 0; i <entry['profitInfo'].length; i++) {
                        var info = entry['profitInfo'][i];
                        var item = tmpl.render({
                            order: info['order'],
                            title: info['title'],
                            text: TTemplate.raw(replaceTextLink( info['text'] )),
                            infoUpdateTime: info['infoUpdateTime']
                        });
                        tmplist.push(item);
                    };
                    if (tmplist.length > 0) {
                        infoHTML = tmplist.join('');
                    };
                };
                $('#home').html( pageTmpl.render( {
                    pageTitle: entry.storeName,
                    infoList: TTemplate.raw( infoHTML )
                } ) );
                app.goTo('#item-' + params['item']);
            }
        })
    };
    sendRequest();
});
})();

