var TLOG_ORANGE_PAGE = "http://www.tsutaya.co.jp/smartphone/ap/search/tlog_login.html";
var TLOG_ACCOUNT_PAGE = "http://www.tsutaya.co.jp/tm/tsutayalog.html";

function checkLoginStatus(){
    var ret,
    options = {
        async: false,
        cache: false,
        url: 'https://www.tsutaya.co.jp/SpTsutayaSearch/app.psgi?c=api_getLoginStatus',
        dataType: 'json',
        success: function(data) {
        	if(data["tolid"] == null){
        		localStorage.removeItem("loginStatus");
        		localStorage.removeItem("mergedMyfc");
        	} else {
        	    localStorage.setItem("loginStatus",data["tolid"]);
        	}
        	ret = data['tolid'];
        }
    };
	$.ajax(options);
}

function resetAccountStatus() {
	app.setLoginStatus("0");
	app.setAppMyfcMergeStatus("0");
}

/** Dispatch Pages */
function dispatch() {
	var lctolid;
	var storeMerged;

	lctolid = localStorage.loginStatus;
	
	if(lctolid != null && lctolid != undefined){
		if(app.getUserLoginStatus() == "0"){
			checkLoginStatus();
		}
	} else if(lctolid == null || lctolid == undefined){
		checkLoginStatus();
	}
	
	lctolid = localStorage.loginStatus;
    if( lctolid == null || lctolid == undefined){
		/* go to Orange Page. */
    	resetAccountStatus();
    	localStorage.removeItem("mergedMyfc");
    	app.stopLoading();
    	app.goTo(TLOG_ORANGE_PAGE);
	} else {  // already Login.
		app.setLoginStatus("1");
		app.stopLoading();
		app.goTo(TLOG_ACCOUNT_PAGE + "?_=" + (new Date()).getTime() );
	}
	
}

function refreshView() {
/* nop */
}

$(function() {
	app.startLoading();
	dispatch();
});

