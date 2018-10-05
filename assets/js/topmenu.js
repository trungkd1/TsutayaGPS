$(function() {
	/* Search check */
	$('#SbSbmtBtn').attr('disabled', 'true');
	var createInputChecker = function(buttonId, inputId) {
		return function() {
			var v = $('#' + inputId).val();
			if (v != undefined) {
				v = v.replace(/^[ 　]+/g, '').replace(/[ 　]+$/g, '');
				var button = $('#' + buttonId);
				if (v != '') {
					button.attr('disabled', '');
				} else {
					button.attr('disabled', 'true');
				}
			}
		};
	};
	couponService();

	var check1 = createInputChecker('SbSbmtBtn', 'sea');
	var checkInput = function() {
		check1();
		setTimeout(checkInput, 200);
	}
	setTimeout(checkInput, 200);

	var selitemId = localStorage.searchItemId;
	if (selitemId == null || selitemId == undefined) {
		$("#stype").val('211');
	} else {
		$("#stype").val(selitemId);
	}

	$("#stype").change(function() {
		var sid = $("#stype option:selected").val();
		if (sid != null && sid != undefined) {
			localStorage.searchItemId = sid;
		}
	}).change();

});

function refreshView() {
	couponService();
}

function couponService() {
	var lctolid;
	var storeMerged;
	lctolid = localStorage.loginStatus;
	storeMerged = localStorage.mergedMyfc;

	if (lctolid != null && lctolid != '') {
		if (storeMerged != null && storeMerged != '') {
			app.setCouponCheckService(lctolid);
		}
	}
}
