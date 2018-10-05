/*!
	reflection.js for jQuery v1.03
	(c) 2006-2009 Christophe Beyls <http://www.digitalia.be>
	MIT-style license.
*/
/*edit start*/var reflectMaxWidth = 90;/*edit end*/
(function($) {

$.fn.extend({
	reflect: function(options) {
		options = $.extend({
			height: 1/3,
			opacity: 0.5
		}, options);

		return this.unreflect().each(function() {

			var img = this;
			if (/^img$/i.test(img.tagName)) {
				function doReflect() {
					//var imageWidth = img.width,
					/*edit start*/
					var imageWidth=img.width > reflectMaxWidth ? reflectMaxWidth : img.width;
//					var imageHeight = img.width > reflectMaxWidth ? img.height * reflectMaxWidth / img.width : img.height;
					var imageHeight = img.height;
					//ランキング画像の為の微調整
					options.height = imageHeight > 15 ? options.height : 1/2;
					var reflection, reflectionHeight, wrapper, context, gradient;
					/*edit end*/
					reflectionHeight = Math.floor((options.height > 1) ? Math.min(imageHeight, options.height) : imageHeight * options.height);

					if ($.browser.msie) {
						reflection = $("<img />").attr("src", img.src).css({
							width: imageWidth,
							height: imageHeight,
							marginBottom: reflectionHeight - imageHeight,
							filter: "flipv progid:DXImageTransform.Microsoft.Alpha(opacity=" + (options.opacity * 100) + ", style=1, finishOpacity=0, startx=0, starty=0, finishx=0, finishy=" + (reflectionHeight / imageHeight * 100) + ")"
						})[0];
					} else {
						reflection = $("<canvas />")[0];
						if (!reflection.getContext) return;

						context = reflection.getContext("2d");
						try {
							$(reflection).attr({width: imageWidth, height: reflectionHeight});
							context.save();

							/*edit start*/
							if(app.isNeedModifyCanvas && app.isNeedModifyCanvas()) {
								//OS2.1Canvasバグ対応
								var zoom = Math.sqrt(app.MEDIUM_SCREEN / screen.width);
								context.scale(zoom, zoom);

								context.translate(0, imageHeight-1);
								context.scale(1, -1);
//								console.log("img.width:" + img.width + " img.height:" + img.height + " imageWidth:" + imageWidth + " imageHeight:"+imageHeight);
								context.drawImage(img, 0, 0, imageWidth, imageHeight, 0, imageHeight / 6, imageWidth, imageHeight);
								context.restore();
								context.globalCompositeOperation = "destination-out";
							} else {
								//OS2.2以上は正常
								context.translate(0, imageHeight-1);
								context.scale(1, -1);
								context.drawImage(img, 0, 0, imageWidth, imageHeight);
								context.restore();
								context.globalCompositeOperation = "destination-out";
							}
							/*edit end*/

							gradient = context.createLinearGradient(0, 0, 0, reflectionHeight);
							gradient.addColorStop(0, "rgba(255, 255, 255, " + (1 - options.opacity) + ")");
							gradient.addColorStop(1, "rgba(255, 255, 255, 1.0)");
							context.fillStyle = gradient;
							context.rect(0, 0, imageWidth, reflectionHeight);
							context.fill();
						} catch(e) {
							return;
						}
					}
					$(reflection).css({display: "block", border: 0});

					wrapper = $(/^a$/i.test(img.parentNode.tagName) ? "<span />" : "<div />").insertAfter(img).append([img, reflection])[0];
					wrapper.className = img.className;
					$.data(img, "reflected", wrapper.style.cssText = img.style.cssText);
					$(wrapper).css({width: imageWidth, height: imageHeight + reflectionHeight, overflow: "hidden"});
					img.style.cssText = "display: block; border: 0px";
					img.className = "reflected";
				}

				if (img.complete) doReflect();
				else $(img).load(doReflect);
			}
		});
	},

	unreflect: function() {
		return this.unbind("load").each(function() {
			var img = this, reflected = $.data(this, "reflected"), wrapper;

			if (reflected !== undefined) {
				wrapper = img.parentNode;
				img.className = wrapper.className;
				img.style.cssText = reflected;
				$.removeData(img, "reflected");
				wrapper.parentNode.replaceChild(img, wrapper);
			}
		});
	}
});

})(jQuery);