/**
 * (C)Culture Convenience Club Co.,Ltd.
 */
/* product */
TWS_API_KEY = '26fe50f1-8ca5-4660-b1aa-58afe27b2ab5';
TWS_API_HOST = 'http://tws.tsutaya.co.jp';
/* product */
//TWS_API_KEY = '0c5f1227-cf3f-44ee-94bd-801348fa7087'; // test
/* Staging 
TWS_API_KEY = '54b0722e-ebc7-433e-bf48-b95a13866154'; // staging
TWS_API_HOST = 'http://stg.tws.tsutaya.co.jp'; // staging
 Staging */
/*---------------------------------------------------------------
 TWS呼び出しインタフェース。
-----------------------------------------------------------------*/
(function() {
    var TWSClient = function(){};
    TWSClient.prototype = {
        sendRequest: function(path, opt) {
            var data = opt.data || {};
            var options = opt;
            if (data.tolPlatformCode === undefined) data.tolPlatformCode =  '00';
            if (data.api_key === undefined) data.api_key = TWS_API_KEY;
            if (data._version === undefined) data._version = window.app.getVersion();
            options.url = TWS_API_HOST + path;
            options.data = data;
            if (options.dataType === undefined) options.dataType = 'jsonp';
            if (options.jsonp === undefined) options.jsonp = 'callback';

            $.ajax( options );
        }
    };

    window.TWSClient = new TWSClient();
})();

/*------------------------------------------------------------------
 簡易テンプレートエンジン

 {varName} 変数埋め込み
 {:if varName} {:else} {:end}だけの仕様

 (例)
  var tmpl = new TTemplate('{:if if1}if1 {:if if2} if2 {:else} if2 else{:end} if2 end {:end}if1 end {hoge} {tag}');
  var ret = tmpl.render( {
      if1: true,
      if2: true,
      hoge: 'aaaa<br />aaa', // 自動エスケープされる
      tag: TTemplate.raw('<b>bold</b>')  // エスケープされない
  });
  $('#result').html(ret);
-----------------------------------------------------------------*/
(function() {
    var debugIndent = 0;
    var Token = function() {
    };
    Token.prototype = {
        addChild: function( child ) {
            if (!child) {
                return;
            };
            child.parentToken = this;
            this.children.push( child );
        },
        traverse: function(tmpl, params) {
            var result = this.process( tmpl, params );
            for (var i = 0; i < this.children.length; i++) {
                var ret = this.children[i].process( tmpl, params );
                result += ret;
            };
            return result;
        },
        debug: function(tmpl, params) {
            var result = debugIndent + ':' + this.type + ':' + this.varName + "\n";
            debugIndent++;
            for (var i = 0; i < this.children.length; i++) {
                var ret = '';
                for (var j = 0; j < debugIndent; j++) {
                    ret += '  ';
                };
                ret += this.children[i].debug(tmpl, params);
                result += ret;
            };
            debugIndent--;
            return result;
        },
        toStr:function() {
            return this.type + ':' + this.value;
        }
    }

    var TokenNil = function() {
        this.children = [];
    }
    TokenNil.prototype = {
        process: function( tmpl, params ) {
            return '';
        }
    };
    $.extend( TokenNil.prototype, Token.prototype );

    var TokenString = function(str) {
        this.children = [];
        this.value = str;
        this.type = 'string';
    };
    TokenString.prototype = {
        process: function( tmpl, params ) {
            return this.value;
        }
    };
    $.extend( TokenString.prototype, Token.prototype );

    var TokenVariable = function(str) {
        this.children = [];
        this.value = str;
        this.type = "variable";

        if (this.value.match(/\{([.\w]+)\}/)) {
            this.varName = RegExp.$1;
        } else {
            throw("unknown variable");
        };
    };
    TokenVariable.prototype = {
        process: function( tmpl, params ) {
            return tmpl.getParam( this.varName, params );
        }
    };
    $.extend( TokenVariable.prototype, Token.prototype );

    var TokenIf = function(str) {
        this.children = [];
        this.value = str;
        this.type='if';
        if (str.match(/^{:if ([\w. |&]+)\}/)) {
            this.varName = RegExp.$1;
        } else {
            throw("unknown condition");
        };
    };
    TokenIf.prototype = {
        condition: function( tmpl, params ) {
            var c =  this.varName.split(/\s*(\|\||&&)\s*/);
            if (c === undefined) {
                return false;
            };
            var ret = true;
            for (var i = 0; i < c.length; i++) {
                var k = c[i];
                if (k == '&&' || k == '||') {
                    if (!ret && c[i] == '&&' ) {
                        break;
                    }
                    if (ret && c[i] == '||' ) {
                        break;
                    }
                } else {
                    var v = tmpl.getRawParam( k, params );
                    if (v && v != false && v != "0") {
                        ret = true;
                    } else {
                        ret = false;
                    }
                };
            };

            return ret;
        },
        process: function( tmpl, params ) {
            if (this.condition(tmpl, params)) {
                return this.children[0].traverse( tmpl, params );
            } else if (this.children[1]) {
                return this.children[1].traverse( tmpl, params );
            } else {
                // elseがないとき
                return '';
            };
        }
    };
    $.extend( TokenIf.prototype, Token.prototype );

    var TokenIfThen = function() {
        this.children = [];
        this.type='ifthen';
        this.value = 'ifthen';
    };
    TokenIfThen.prototype = {
        process: function( tmpl, params ) {
            return '';
        }
    };
    $.extend( TokenIfThen.prototype, Token.prototype );

    var TokenIfElse = function() {
        this.children = [];
        this.type='ifelse';
        this.value = 'else';
    };
    TokenIfElse.prototype = {
        process: function( tmpl, params ) {
            return '';
        }
    };
    $.extend( TokenIfElse.prototype, Token.prototype );

    var TTemplate = function(tmpl) {
        this._template = tmpl;
        this.parse( tmpl );
    };
    TTemplate.raw = function(str) {
        return {raw:1, val:str}; // オブジェクトの場合エスケープしないようにする
    };
    TTemplate.escapeHTML = function(str) {
        if (str === undefined || str === null) {
            return ''
        };
        str = str + ''; // 文字列化
        return str.replace(/&/g, '&amp;')
           .replace(/</g, '&lt;')
           .replace(/>/g, '&gt;')
           .replace(/"/g, '&quot;');
    };
    TTemplate.prototype = {
        getRawParam: function(k, params) {
            var key = k.split(/\./);
            var val = params;
            for (var i = 0; i < key.length; i++) {
                var k = key[i];
                val = val[k];
                if (val === undefined) { return '' };
            };
            return val;
        },
        getParam: function(k, params) {
            var val = this.getRawParam( k, params );

            // デフォルトはHTMLエスケープ
            if (val && typeof val == "object" && val.raw ) {
                // エスケープしない場合は[str]で入っている
                val = val.val;
            } else {
                val = TTemplate.escapeHTML( val );
            };
            if (val === undefined || val === null) {
                val = '';
            };
            return val;
        },
        createToken: function(parentToken, tokenList) {
            while( this.pos < tokenList.length ) {
                var val = tokenList[this.pos];
                if (val.match(/^\{[:. &|\w]+\}$/)) {
                    if ( val.match(/^{:if /) ) {
                        var t = new TokenIf(val);
                        this.pos++;
                        parentToken.addChild( t );
                        var ifthen = new TokenIfThen();
                        t.addChild( ifthen );
                        this.createToken( ifthen,tokenList );
                    } else if ( val.match(/^{:else}/) ) {
                        var t = new TokenIfElse();
                        this.pos++;
                        parentToken.parentToken.addChild( t );
                        this.createToken( t,  tokenList );
                    } else if ( val.match(/^{:end}/)  ) {
                        parentToken = parentToken.parentToken.parentToken;
                        this.pos++;
                        continue;
                    } else if ( val.match(/^\{:/) ) {
                        //return new TTokenValue( val );
                        parentToken.addChild( new TokenNil() );
                    } else {
                        parentToken.addChild( new TokenVariable( val ) );
                    }
                } else {
                    parentToken.addChild( new TokenString(val) );
                }
                this.pos++;
            };
        },
        parse: function( tmpl ) {
            var t = new TokenString();
            var tokens = tmpl.split(/(\{[:. |&\w]+\})/);
            var rootToken = new TokenNil();
            this.pos = 0;
            this.createToken( rootToken, tokens );
            this.rootToken = rootToken;
        },
        render: function( params ) {
            var html = this.rootToken.traverse( this, params );

            return html;
        }
    };
    window.TTemplate = TTemplate;
})();

/* ------------------------------------------------------------------------
 *  登録店舗を扱うオブジェクト
 * ----------------------------------------------------------------------*/
var StoreList = function() {	
    this.maxListSize = 5;
    this.API_URL='https://www.tsutaya.co.jp/SpTsutayaSearch/app.psgi';
    // 予めアプリ内の店舗を読み込んでおく。
    var data = localStorage['storeList'];
    this.list = data ? JSON.parse( data ) : [];
    this.errMsgStr = '';
    
    this.errMsg = function() {
    	return this.errMsgStr;
    };
    
    this.canAdd = function() {
        return this.list.length < this.maxListSize;
    };
 
    // ローカルストレージに蓄える
    this.save = function() {
        localStorage['storeList'] = JSON.stringify(this.list);
    };
 
    this.addStore = function(storeId,storeName) {
    	localStorage.removeItem("addMyfcStatus");
    	if (this.canAdd() && !this.isStoreAdded(storeId) ) {
    		if (this.isLogined()) {
                var ret = this.addMyFc(storeId);
                //if (ret.status !== 'SUCCESS') {
            /*
                if(!this.isStoreAdded(storeId)){
                    return false;
                };
            
                if (ret.errMsg) {
                    this.errMsg = ret.errMsg;
                    return false;
                };
            */
             }
        	
            // ローカルの店舗リストに追加する。
            this.list.push( {
                storeId: storeId,
                storeName: storeName
            } );
            this.save();
            return true;
        };
        return false;
    };
    
    this.deleteStore = function(id) {
        var newList = [];
        var deleted = false;
 //       localStorage.removeItem("deleteMyfcStatus");
        // ローカルデータの削除
        for (var i = 0; i < this.list.length; i++) {
            var e = this.list[i];
            if (e['storeId'] != id) {
                newList.push( e );
            } else {
                deleted = true;
            }
        };
        if( this.isLogined() && deleted){
        	var ret = this.deleteMyFc(id);
        }
        // 失敗した場合はリストに反映されない。
        this.list = newList;
        this.save();
        return deleted;
    };
    
    this.getStoreListSize = function() {
        return this.list.length;
    };
 
    this.addMyFc = function(fcid) {
		// FIXME JSONPなので非同期呼び出しできない
        var ret,
            options = {
                async: false,
                url: this.API_URL + '?c=api_addmyfc&fcid=' + fcid,
                dataType: 'jsonp',
                jsonp: 'callback',
                success: function(data) {
                    ret = data;
//                    localStorage["addMyfcStatus"] = JSON.stringify(ret);
                }
            };
        $.ajax(options);
        return ret;
    };

    this.deleteMyFc = function(fcid) {
		// FIXME JSONPなので非同期呼び出しできない
        var ret,
            options = {
                async: false,
                url:  this.API_URL + '?c=api_delmyfc&fcid=' + fcid,
                dataType: 'jsonp',
                jsonp: 'callback',
                success: function(data) {
                    ret = data;
 //                   localStorage["deleteMyfcStatus"] = JSON.stringify(ret);
                }
            };
        $.ajax(options);
        return ret;
    };
    
    this.dupCheck = function(id){
    	var ret = false;
    	var j=0,fc;
    	for(j; j< this.list.length; j++){
    		fc = this.list[j];
    		if(fc['storeId'] === id){
    			ret = true;
    			break;
    		}
    	};
    	return ret;
    }
    // アプリーMYツタ店舗の同期処理
    this.syncStoreList = function() {
        var i = 0, e;
        var ret = false;
        var data = localStorage['tmpstoreList'];    // MYツタ店舗リスト
        var myfclist = data ? JSON.parse( data ) : [];
        if (myfclist && myfclist.length > 0) {  // MYツタ登録店舗をアプリ内に取り込む
        	var fcid,fcname;
        	this.list = [];
            for (i; i < myfclist.length; i++ ) {
                e = myfclist[i];
                fcid = e['fcid'];
                fcname = e['fnam'];
               // ret = this.addStore(fcid,fcname);
                // ローカルの店舗リストに追加する。
	                this.list.push( { storeId: fcid,storeName: fcname } );
            }
            this.save();
            localStorage['tmpstoreList'] = '';
        } else {  // アプリ登録店舗をMYツタ登録店舗に同期
        	if(this.list && this.isStoreListMerged()){
        		for (i; i < this.list.length; i++ ) {
        			e = this.list[i];
                    fcid = e['fcid'];
                    fcname = e['fnam'];
        			ret = this.addStore(fcid,fcname);
        		}
        	}
        }
    };

    this.checkMyfcUpdateAsync = function() {
        var ret,
        	options = {
        	async:false,
        	cache:false,
            url: this.API_URL + '?c=api_myfc',
            dataType: 'json',
            jsonp: 'callback',
            success: function(data) {
                var i = 0, e;
                this.list = [];     // 登録用テンポラリリスト
                if (data && data.tenpoList) {
                	var fcid,fcname;
                    for (i; i < data.tenpoList.length; i++ ) {
                        e = data.tenpoList[i];
                        this.list.push({
                        	fcid: e['fcid'],
                        	fnam: e['fnam']
                        }); 
                        //ret = this.addStore(fcid,fcname);
                    }
                    localStorage['tmpstoreList'] = JSON.stringify(this.list);         
                };
            }
        };
        $.ajax(options);
    };
  
    this.syncMyFcAndAppFc = function(){
    	this.checkMyfcUpdateAsync();   // MYツタ店舗リスト抽出
    	this.syncStoreList();          // アプリローカルとの同期処理
    };

    this.setTOLID = function( tolid ) {
        localStorage.loginStatus = tolid ? tolid : '';
    };
    this.setLogout = function() {
        localStorage.loginStatus = '';
    };
    this.getTOLID = function() {
        return localStorage.loginStatus || '';
    };
    this.isLogined = function() {   	
 //       return (!localStorage.loginStatus || localStorage.loginStatus === '' ) ? false : true;
        if(!localStorage.loginStatus || localStorage.loginStatus === ''){
        	return false;
        } else {
        	if(!localStorage.mergedMyfc || localStorage.mergedMyfc === ''){
        		return false;
        	}
        	return true;
        }
    }; 
    this.isStoreListMerged = function(){
    	return (!localStorage.mergedMyfc || localStorage.mergedMyfc === '' ) ? false : true;
    };

    this.getStoreIdList = function() {
    	 if (this.isLogined() && this.isStoreListMerged()) {
    		 this.syncMyFcAndAppFc();
             //this.checkMyfcUpdateAsync();
         }
        return this.list.map(function(e) { return e['storeId'] } );
    };
    
    this.getStoreIdList_NoUpdate = function(){
    	var data = localStorage['storeList'];
        var appStoreList = data ? JSON.parse( data ) : [];
        return appStoreList.map(function(e) { return e['storeId'] } );
    };
   
    this.getStoreIdListQuery = function() {
    	window.alert('storeIdListQuery');
        var list = this.getStoreIdList();
        return "app_fcid=" + list.join(',');
    };
  
    this.getAppStoreIdList = function() {
    	return this.list.map(function(e) { return e['storeId'] } );
    };
      
    this.isStoreAdded = function(id) {
        return this.getStoreIdList().some(function(e) { return e == id });
    };
  
    this.isMyFcAdded = function(id) {
      var data = localStorage['tmpStoreList'];
      var myFcStoreList = data ? JSON.parse( data ) : [];
      return myFcStoreList.some(function(e) { return e == id });
    };
 
    this.getStoreName = function(id) {
        for (var i = 0; i < this.list.length; i++) {
            var e = this.list[i];
            if (e['storeId'] == id) {
                return e['storeName'];
            };
        };
        return null;
    };
// ログイン状態チェック
    this.checkLogin = function(){
        var ret,
        options = {
            async: false,
            cache: false,
            url: 'https://www.tsutaya.co.jp/SpTsutayaSearch/app.psgi?c=api_getLoginStatus',
            dataType: 'json',
            success: function(data) {
              if(data) {
            	if(data["tolid"] == null){
            		localStorage.removeItem("loginStatus");
            		localStorage.removeItem("mergedMyfc");
            		app.setLoginStatus("0");
            		app.setAppMyfcMergeStatus("0");
            	} else {
            	    localStorage.setItem("loginStatus",data["tolid"]);
            	    app.setLoginStatus("1");
            	}
            	ret = data['tolid'];
              }
            }
        };
    	$.ajax(options);
    };
};

//--------------------------------------------------------------
//登録商品を扱うオブジェクト
//--------------------------------------------------------------
var ProductList = function() {
//  this.maxListSize = 200;
  this.maxListSize = 1000;
  var data = localStorage['productList'];
  this.list = data ? JSON.parse( data ) : [];
  this.canAdd = function() {
      return this.list.length < this.maxListSize;
  };
  this.save = function() {
      localStorage['productList'] = JSON.stringify(this.list);
  };
  this.addProduct = function(urlCd,image,title,saveDate) {
      if (this.canAdd() && !this.isProductAdded(urlCd) ) {
          this.list.push( {
              urlCd: urlCd,
              image: image,
              title: title,
              saveDate: saveDate
          } );
          this.save();
          return true;
      };
      return false;
  };
  this.getProductListSize = function() {
      return this.list.length;
  };
  this.getProductIdList = function() {
      return this.list.map(function(e) { return e['urlCd'] } );
  };
  this.isProductAdded = function(id) {
      return this.getProductIdList().some(function(e) { return e == id });
  };
  this.getProductName = function(id) {
      for (var i = 0; i < this.list.length; i++) {
          var e = this.list[i];
          if (e['urlCd'] == id) {
              return e['title'];
          };
      };
      return null;
  };
  this.deleteProduct = function(id) {
      var newList = [];
      var deleted = false;
      for (var i = 0; i < this.list.length; i++) {
          var e = this.list[i];
          if (e['urlCd'] != id) {
              newList.push( e );
          } else {
              deleted = true;
          }
      };
      this.list = newList;
      this.save();
      return deleted;
  }
};

//--------------------------------------------------------------
// 年齢認証保存用オブジェクト
//--------------------------------------------------------------
var AgeLimitAuth = {
    ageLimitAuthPeriod: 24 * 60 * 60 * 1000,
    setAuthOK: function() {
        localStorage['ageLimitAuthedTime'] = Date.now();
    },
    isAuthOK: function() {
        var lastTime = localStorage['ageLimitAuthedTime'];
        var nowTime = Date.now();
        var diff = nowTime - lastTime;
        if ( lastTime && diff < this.ageLimitAuthPeriod ) {
            localStorage['ageLimitAuthedTime'] = Date.now();
            return 1;
        }
        return 0;
    }
};

//--------------------------------------------------------------
// Utility functions
//--------------------------------------------------------------
// クエリーパラメータをハッシュ形式にして返す
function getQueryParameters() {
    var href = document.location.href;
    var query = href.split('?')[1];
    if(query){
	    query = query.replace(/#.+/,'');
	    var params = {};
	    var keyvalues = query.split('&');
	    for (var i = 0; i < keyvalues.length; i++) {
	        var key_value = keyvalues[i].split('=');
	        params[key_value[0]] = decodeURI(key_value[1]);
	    };
	    return params;
    } else {
    	return {};
    }
}

/** for ICS */
function getICSQueryParameters() {
    var href = app.getRealUrl();
    var query = String(href).split('?')[1]; 
    if(query){
    	query = query.replace(/#.+/,'');
	    var params = {};
	    var keyvalues = query.split('&');
	    for (var i = 0; i < keyvalues.length; i++) {
	        var key_value = keyvalues[i].split('=');
	        params[key_value[0]] = decodeURI(key_value[1]);
	    };
	    return params;
    } else {
    	return {};
    }
}

function formatDate(str) {
    if (!str) {
        return '';
    };
    var match = str.match(/(\d{4})\/?(\d{2})\/?(\d{2})?/);
    if (match) {
        var dateStr = RegExp.$1 + '年' + RegExp.$2 + '月';
        if ( RegExp.$3 ) {
            dateStr += RegExp.$3 + '日';
        }
        return dateStr;
    };
    return '';
}

function formatDateTime(str) {
    if (!str) {
        return '';
    };
    var match = str.match(/(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})/);
    if (match) {
        return RegExp.$1 + '年' + RegExp.$2 + '月' + RegExp.$3 + '日 '
            + RegExp.$4 + '時' + RegExp.$5 + '分';
    };
    return str;
}

function formatPrice( price ) {
    if (price === undefined || price === null){
        return "";
    };
    price = price + "";
    price = price.replace(/(\d{1,3})(?=(?:\d\d\d)+(?!\d))/g,
            function(val) { return RegExp.$1 + "," } );
    return price;
}

function  margin(name) {
    //解像度により微妙にバナー位置がずれるので調整
    var w = screen.width;
    var h = screen.height;
    if(w == 0 || h ==0){
        setTimeout("margin('"+name+"')", 50 );
    } else {
        var dis = -3.1 * (h/w);
        $(name).css('margin-top',dis);
    }
}

// delegate Method
function createDelegate(func, thisObj){

	var del = function() {
					return func.apply(thisObj, arguments);
				};

	//情報は関数のプロパティとして定義する
    del .func = func;
	del .thisObj = thisObj;

	return del; 

}

//--------------------------------------------------------------
// Message系
//--------------------------------------------------------------
var Messages = {
    ageLimitAlert: "ここからは成人向け商品を取り扱いしているページです。"
+ "18歳未満の方のアクセスは堅く禁じております。<br>閲覧はご遠慮ください。<br><br>"
+ "あなたは１８歳以上ですか？",
    networkError: "通信エラーが発生しました。電波状況をご確認ください",
    searchNotFoundError: "検索結果は0件でした。",
    pageNotFoundError: "存在しないページです。",
    serverError: "ただいまサーバが混み合っております。ご迷惑をおかけして申し訳ございません",
    loading: "読み込み中..."
};


//--------------------------------------------------------------
// ネイティブアプリケーションブリッジ(iPhone用)
//--------------------------------------------------------------
(function() {
    var APP_VERSION='3.2';
    var AppIphone = function() {
        var self = this;
        this.alert = function( title, message, callback ) {
            this.callback_ = callback;
            document.location.href = ["app::alert", title, message, "了解"].join('::');
        };
        this.alertCallback = function(buttonIndex) {
            if (this.callback_) {
                this.callback_(buttonIndex);
            };
        };
        this.confirm = function( title, message, noTitle, yesTitle, callback ) {
            this.callback_ = callback;

            document.location.href = [
                "app::alert",
                title, message,
                'いいえ','はい'
            ].join('::');
        };
        this.startLoading = function() {
            document.location.href = '#ajax_loading';
            //self.gotTo('#ajax_loading');
        };
        this.stopLoading = function() {
            document.location.href = '#ajax_complete';
            //self.gotTo('#ajax_complete');
        };
        this.goBack = function() {
            document.location.href="app::goback";
        };
        this.goTo = function(l) {
            document.location.href = l;
        }
        this.log = function(msg) {
            document.location.href="app::log::" + msg;
        }
        this.getVersion = function() {
            return APP_VERSION;
        }
        this.setAddFavBtnEnable = function() {
        }
    };

    //--------------------------------------------------------------
    // ネイティブアプリケーションブリッジ(Android用)
    //--------------------------------------------------------------
    var AppAndroid = function() {
        this.alert = function( title, message, callback ) {
            //コールバック関数のコールはAndroid側で行う
            //(android.で実行するスレッドがUIスレッドと別になってしまう為)
            this.callback_ = callback;
            android.alert(title, message);
        };
        this.confirm = function( title, message, yesTitle, noTitle, callback ) {
            //コールバック関数のコールはAndroid側で行う
        	this.callback_=callback;
            android.confirm(title, message, yesTitle, noTitle);
        };
        this.alertCallback = function(buttonIndex) {
            if (this.callback_) {
                this.callback_(buttonIndex);
            };
        }
        this.startLoading = function() {
            //ネイティブにブリッジしない
            if (this._startLoadingCallback) {
                this._startLoadingCallback();
            }
        }
        this.stopLoading = function() {
            if (this._stopLoadingCallback) {
            	this._stopLoadingCallback();
            }
        }
        this.goBack = function() {
            android.browserBack();
        }

        this.goTo = function(l) {

            //ネイティブ側で実行
            if (l.lastIndexOf('app::', 0) == 0) {
                if (l.lastIndexOf('app::areastock::', 0) == 0) {
                    //在庫検索
                    android.startMap('areastock',l.replace('app::areastock::',''));
                    return;
                } else if (l.lastIndexOf('app::storemap::', 0) == 0) {
                    //店舗検索
                    android.startMap('storemap',l.replace('app::storemap::',''));
                    return;
                }
 //               return;
            }

            var urlstr = "http://www.tsutaya.co.jp/works/";
            var currentStr = "#";

            //作品詳細の遷移をネイティブ内で行う
            if (l.indexOf(urlstr) == 0) {
                var afterstr = l.replace(urlstr,"");
                //console.log(afterstr);
                var urlCd = afterstr.split(".");

                if (urlCd!=null) {
                    android.goToUrl('work_detail.html?urlCd='+ urlCd[0]);
//                    document.location.href='work_detail.html?urlCd='+ urlCd[0];
                }
                return;
            } else if (l.indexOf(currentStr) == 0) {
                document.location.href = l;
            } else {
                //document.location.href = l;
            	console.log("main::goToUrl " + l);
                android.goToUrl(l);
            }
        }
        this.log = function(msg) {
            console.log( msg );
        }
        this.getVersion = function() {
            return APP_VERSION;
        }
        this.setAddFavBtnEnable = function(flg) {
            android.setAddFavBtnEnable(flg);
        }

        /** android 特有定数 */
        this.MEDIUM_SCREEN = 320;

        this.isNeedModifyCanvas = function() {
            //OS2.1、2.0でかつDIP=240の端末が上手く表示できない為その対応
            return android.isNeedModifyCanvas() && !(screen.width % app.MEDIUM_SCREEN == 0);
        }

        this.goToSettingMenu = function() {
            android.goToSettingMenu();
        }

        this.goToStoreMenu = function() {
            android.goToStoreMenu();
        }
        
        this.sendCouponNotify = function(nums) {
        	android.notifyCouponArrivals(nums);
        	return;
        }
        /** Clear All Cookies from CookeStore */
        this.clearAllCookies = function(){
        	android.clearAllCookies();
        }
        /** Change Login Status */
        this.setLoginStatus = function(stat){
        	android.changeLoginStatus(stat);
        }
        this.getUserLoginStatus = function(){
        	return android.getUserLoginStatus();
        }
        /** Change RegisterdShopMergedStatus */
        this.setAppMyfcMergeStatus = function(stat){
        	android.changeAppMyfcMergeStatus(stat);
        }
        this.getAppMyfcMergeStatus = function(){
        	return android.getAppMyfcMergeStatus();
        }
        this.getDisplayInfo = function(hw){
        	return android.getDispInfo(hw);
        }
        this.getRealUrl = function(){
        	return android.getRealUrl();
        }
        this.setRealUrl = function(rurl){
        	android.setRealUrl(rurl);
        }
        this.setCouponCheckService = function(tolid){
        	android.setAlarm(tolid);
        }
        this.cancelCouponCheckService = function(){
        	android.resetCouponAlarmService();
        }
        this.setRedirectSettingPage = function(isRedirect){
        	android.setRedirectSettingPage(isRedirect);
        }
        this.isRedirectSettingPage = function(){
        	return android.isRedirectSettingPage();
        }
		this.updateRegisteredStore = function(storeId, state){
			android.updateRegisteredStore(storeId, state);
		}
		
        //submitもアプリに投げる
        $(function() {
            $("form").submit(function() {
                app.goTo($("form").attr("action")+'?'+$("form").serialize());
                return false;
            });
        });
        
        
    };

    //--------------------------------------------------------------
    // ネイティブアプリケーションブリッジ(PC用)
    //--------------------------------------------------------------
    var AppPC = function() {
    	//Android環境のエミュレートの際使用
        this.MEDIUM_SCREEN = 320;
        this.alert = function( title, message, callback ) {
            this.callback_ = callback;
            alert(message);
            this.alertCallback();
        };
        this.confirm = function( title, message, yesTitle, noTitle, callback ) {
            this.callback_ = callback;

            if (window.confirm( message ) ) {
                this.alertCallback(0);
            } else {
                this.alertCallback(1);
            }
        };
        this.alertCallback = function(buttonIndex) {
            if (this.callback_) {
                this.callback_(buttonIndex);
            };
        }
        this.startLoading = function() {
        }
        this.stopLoading = function() {
        }
        this.goBack = function() {
            history.back();
        }
        this.goTo = function(l) {
            document.location.href = l;
        }
        this.log = function(msg) {
            console.log( msg );
        }
        this.getVersion = function() {
            return APP_VERSION;
        }
        this.setAddFavBtnEnable = function() {
        }
    };

    if (navigator.userAgent.match(/iPhone|iPad/)) {
        window.app = new AppIphone();
    } else if (navigator.userAgent.match(/Android/)) {
        window.app = new AppAndroid();
    } else {
        window.app = new AppPC();
    };
})();

/**
 * 画像読み込み中共通処理
 * @param elem
 * @param image
 */
function loadingImage(elem, image) {
    var img = new Image();
    img.onload = function() {
        $(elem).attr('src', img.src);
        var className = $(elem).attr('className');
        if (null != className && null != className.match(/reflect/)) {
            $(elem).reflect({/* Put custom options here */});
        }
    };
    img.src = image;
}
/** ロード中画像 */
var loaderImage = "img/loading.gif";

function transCRLF2Br (target) {
	return TTemplate.raw(target ? target.replace(/\r\n/g,'<br/>').replace(/(\n|\r)/g, "<br/>") : '')
}
