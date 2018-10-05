/* This script requires jQuery.
 *
 * The MIT License
 *
 * Copyright (c) 2010 James Edgington <root[@]rel2.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

// delay is in ms
var lcNS = {
	tags : [ 'li' ],
	className : 'longclick',
	delay : 750,
	timers : [],
	oldOnclicks : [],
	getOldOnclick : function(id) {
		return this.oldOnclicks[id]
	},
	longTapFlag : false,
	beforePageY : 0,
	beforePageX : 0
};

// UUID function lifted from aivopass @ jquery forums
// function init() {
(function($) {
	var uid = 0;
	$.UUID = function() {
		uid++;
		return 'uuid-' + uid;
	};
	$.fn.UUID = function() {
		if (!this.length)
			return 0;
		var fst = this.first(), id = fst.attr('id');
		if (!id) {
			id = $.UUID();
			fst.attr('id', id);
		}
		return id;
	}
})(jQuery);
// }

// touchstartイベント（タイムアウト開始）
var startLongClick = function(event) {
	lcNS.longTapFlag = false;
	lcNS.beforePageY = event.originalEvent.touches[0].pageY;
	lcNS.beforePageX = event.originalEvent.touches[0].pageX;
	var id = $(this).attr('id');
	// イベントが実行されたら、ロングタップフラグをtrueとする
	lcNS.timers[id] = setTimeout("eval(lcNS.getOldOnclick('" + id
			+ "'));lcNS.longTapFlag = true;", lcNS.delay);
	return true;
};
// touchendイベント（タイムアウト終了）
var cancelLongClick = function(event) {
	clearTimeout(lcNS.timers[$(this).UUID()]);
	if (!lcNS.longTapFlag) {
		return true;
	} else {
	}
	return false;
};
// touchMoveイベント（）
var cancelLongClickMove = function(event) {

	// Y軸に一定以上変異があった場合と、X軸が右方向にスワイプされた場合（sidemenuが開くイベント）はキャンセル
	if ((lcNS.beforePageY != 0 && Math.abs(event.originalEvent.touches[0].pageY - lcNS.beforePageY) > 10)) {
		lcNS.beforePageY = 0;
		lcNS.beforePageX = 0;
		clearTimeout(lcNS.timers[$(this).UUID()]);
		if (!lcNS.longTapFlag) {
			return true;
		} else {
		}
		return false;
	} else {
		lcNS.beforePageY = event.originalEvent.touches[0].pageY;
		lcNS.beforePageX = event.originalEvent.touches[0].pageX;
	}

};
var nullEventHandler = function(event) {
	event.preventDefault();
	return true;
};

lcNS.init = function() {
	var $els = $(lcNS.tags.join("." + lcNS.className + ", ") + "."
			+ lcNS.className);

	$els.each(function(i, el) {
		/*
		 * god this is dumb, but inline onclicks seem to always fire when
		 * clicked, no matter what you do to cancel the event. you have to strip
		 * the onclick attribute to stop it from firing, but i have to store its
		 * value so i can use it as a kind of callback for long click.
		 * 
		 * also, $.attr('onclick') [incorrectly?] returns a Function instead of
		 * a String, which means when evaluated it runs in a different context
		 * than the "real" onclick would. so I have to convert it to a string
		 * and then strip jQuery's function wrapper off of it. i.e. <div
		 * onclick="alert('hi')"> passed thru .attr('onclick') becomes function
		 * onclick(event) {alert('hi');})
		 * 
		 * OH and if you just say var x = $(y).attr('onclick'), jQuery just
		 * fires the onclick event for you right then. so that's another reason
		 * i have to use toString().
		 */
		$(el).UUID();
		lcNS.oldOnclicks[$(el).UUID()] = $(el).attr('onmouseover').toString()
				.replace(/[\r\n]/g, "").replace(
						/^function onmouseover\(event\)..\s+/, "").replace(
						/;}\s*$/, "");
		$(el).removeAttr('onmouseover');
	});

	// android用
	$els.bind('touchstart', startLongClick);
	$els.bind('touchmove', cancelLongClickMove);
	$els.bind('touchend', cancelLongClick);
	$els.bind('tap', nullEventHandler);
	// els.bind('mousedown', startLongClick);
	// els.bind('mouseout', cancelLongClick);
	// els.bind('mouseup', cancelLongClick);
	// els.bind('click', nullEventHandler);
};
// 表示項目を作成後に実施するので不要
// $(document).ready(function(){ lcNS.init(); });
