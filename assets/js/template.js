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
    var pos = 0;
    function createToken(parentToken, tokenList) {
        while( pos < tokenList.length ) {
            var val = tokenList[pos];
            if (val.match(/^\{[: \w.]+\}$/)) {
                if ( val.match(/^{:if /) ) {
                    var t = new TokenIf(val);
                    pos++;
                    parentToken.addChild( t );
                    var ifthen = new TokenIfThen();
                    t.addChild( ifthen );
                    createToken( ifthen,tokenList );
                } 
                else if ( val.match(/^{:else}/) ) {
                    var t = new TokenIfElse();
                    pos++;
                    parentToken.parentToken.addChild( t );
                    createToken( t,  tokenList );
                } else if ( val.match(/^{:end}/)  ) {
                    parentToken = parentToken.parentToken.parentToken;
                    pos++;
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
            pos++;
        };
    };

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
            var key = this.varName.split(/\./);
            var val = params;
            for (var i = 0; i < key.length; i++) {
                var k = key[i];
                val = val[k];
                if (val === undefined) { return '' };
            };
              
            // デフォルトはHTMLエスケープ
            if (val && typeof val == "object" && val.raw ) {
                // エスケープしない場合は[str]で入っている
                val = val.val;
            } else {
                val = TTemplate.escapeHTML( '' + val );
            };
            return val;
        }
    };
    $.extend( TokenVariable.prototype, Token.prototype );

    var TokenIf = function(str) {
        this.children = [];
        this.value = str;
        this.type='if';
        if (str.match(/^{:if (\w+)\}/)) {
            this.varName = RegExp.$1;
        } else {
            throw("unknown condition");
        };
    };
    TokenIf.prototype = {
        condition: function( tmpl, params ) {
            return params[this.varName];
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
        return str.replace(/&/g, '&amp;')
           .replace(/</g, '&lt;')
           .replace(/>/g, '&gt;')
           .replace(/"/g, '&quot;');
    };

    TTemplate.prototype = {
        parse: function( tmpl ) {
            var t = new TokenString();
            var tokens = tmpl.split(/(\{[:. \w]+\})/);
            var rootToken = new TokenNil();
            createToken( rootToken, tokens );
            this.rootToken = rootToken;
        },
        render: function( params ) {
            var html = this.rootToken.traverse( this, params );

            return html;
        }
    };
    window.TTemplate = TTemplate;
})();
