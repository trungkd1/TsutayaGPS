var COUPON_ORANGE_PAGE = "http://www.tsutaya.co.jp/smartphone/ap/search/cp_login.html";
var COUPON_TOP_PAGE = "https://c.tsutaya.co.jp/mfl";
var COUPON_REDIRECT_PAGE = "http://www.tsutaya.co.jp/tm/clm.html?rk=mfl";

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

function resetCouponStatus() {
	app.setLoginStatus("0");
	app.setAppMyfcMergeStatus("0");
}

/** Dispatch Pages */
function dispatch() {
	var lctolid;
	var storeMerged;

	lctolid = localStorage.loginStatus;

	if (lctolid != null && lctolid != undefined) {
		if (app.getUserLoginStatus() == "0") {
			checkLoginStatus();
		}
	} else if (lctolid == null || lctolid == undefined) {
		checkLoginStatus();
	}

	storeMerged = localStorage.mergedMyfc;

	lctolid = localStorage.loginStatus;
	if (lctolid == null || lctolid == undefined) {
		/* go to Orange Page. */
		resetCouponStatus();
		localStorage.removeItem("mergedMyfc");
		app.stopLoading();
		app.goTo(COUPON_ORANGE_PAGE);
	} else { // already Login.
		app.setLoginStatus("1");
		if (app.isRedirectSettingPage()) {
			app.setRedirectSettingPage(false);
			app.goToSettingMenu();
			return;
		}
		var schk = new StoreList();
		var appStoreList = schk.getAppStoreIdList();
		/* MY TSUTAYA BOXとお気に入り店舗をまとめます */
		if (!appStoreList || appStoreList.length == 0) {
			localStorage.setItem("mergedMyfc", "merged");
			app.setAppMyfcMergeStatus("1");
			schk.syncMyFcAndAppFc();
		} else {
			app.setAppMyfcMergeStatus("1");
			// 店舗まとめ済みであれば、同期を行う
			if (storeMerged != null && storeMerged != '') {
				// console.log("sync");
				schk.syncMyFcAndAppFc(); // Syncing StoreList between
											// applocal and tolmyfc...
			} else { // 店舗のおまとめが済んでいない場合は、おまとめページへ
				// console.log("omatome");
				app.stopLoading();
				localStorage.mergedMyfc = "merged";
				app.goTo("https://www.tsutaya.co.jp/SpTsutayaSearch/app.psgi?app_fcid="
						+ appStoreList.join(',')
						+ "&_="
						+ (new Date()).getTime());
				return;
			}
		}
		storeMerged = localStorage.mergedMyfc;
		if (storeMerged != null && storeMerged != '') {
			/** クーポンリストページへ */
			app.stopLoading();
			// app.goTo(COUPON_REDIRECT_PAGE);
			// app.goTo(COUPON_TOP_PAGE + "?_=" + (new Date()).getTime() );
			app.setCouponCheckService(lctolid);
			app.goTo(COUPON_REDIRECT_PAGE + "&_=" + (new Date()).getTime());
		}
	}

}

function refreshView() {
	// app.startLoading();
	// dispatch();
}

$(function() {
	app.startLoading();
	dispatch();
});
