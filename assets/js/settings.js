/**
 * (C)Culture Convenience Club Co.,Ltd.
 */
var TLOG_LOGIN_PAGE = "http://www.tsutaya.co.jp/smartphone/ap/search/cp_login.html";
function refreshView() {
	renderPageContents();
}

function checkLoginStatus() {
	var ret, options = {
		async : false,
		cache : false,
		url : 'https://www.tsutaya.co.jp/SpTsutayaSearch/app.psgi?c=api_getLoginStatus',
		dataType : 'json',
		success : function(data) {
			if (data == null || data == undefined || data["tolid"] == null) {
				localStorage.removeItem("loginStatus");
				localStorage.removeItem("mergedMyfc");
				ret = null;
			} else {
				localStorage.setItem("loginStatus", data["tolid"]);
				ret = data['tolid'];
			}
			// ret = data['tolid'];
		}
	};
	$.ajax(options);
	// return ret;
}

function doLogOut() {
	var ret, options = {
		async : false,
		cache : false,
		url : 'https://ssl.tsutaya.co.jp/tm/logout.html',
		dataType : 'html',
		jsonp : 'callback',
		success : function(data) {
			localStorage.removeItem("loginStatus");
			localStorage.removeItem("mergedMyfc");
			localStorage.removeItem("safeSearch");
			app.alert("TSUTAYA", "ログアウトいたしました。");
			app.clearAllCookies();
			app.setLoginStatus("0");
			app.setAppMyfcMergeStatus("0");
			app.cancelCouponCheckService();
			ret = data;
			renderPageContents();
		}
	};
	$.ajax(options);
}

function doLogIn() {
	var lctolid;
	var storeMerged;

	lctolid = localStorage.loginStatus;
	if (lctolid == null || lctolid == undefined) {
		app.setLoginStatus("0");
		app.setAppMyfcMergeStatus("0");
		app.stopLoading();
		localStorage.removeItem("mergedMyfc");
		app.setRedirectSettingPage(true);
		app.goTo(TLOG_LOGIN_PAGE);
	}
}

function logoutConfirm() {
	app.confirm('ログアウト', "本当にログアウトしますか？", "OK", "Cancel",
			function(buttonIndex) {
				if (buttonIndex == 0) {
					doLogOut();
					app.alert("TSUTAYA", "ログアウトいたしました。");
				}
				return true;
			});
}
function renderPageContents() {

	var lstat = localStorage.loginStatus;

	if (lstat != null && lstat != undefined) {
		if (app.getUserLoginStatus() == "0") {
			checkLoginStatus();
		}
	} else if (lstat == null || lstat == undefined) {
		checkLoginStatus();
	}
	lstat = localStorage.loginStatus;
	if (lstat == null || lstat == undefined) {
		$('ul#logoutbtn').html(
				'<li class="rounded btn-blue" onclick="doLogIn()">ログイン</li>');
	} else {
		$('ul#logoutbtn').html(
				'<li class="rounded btn-blue" onclick="doLogOut()">ログアウト</li>');
	}
}

$(function() {
	renderPageContents();
	$('#version-str').html(app.getVersion());
});
