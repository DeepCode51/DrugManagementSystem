function log(obj) {
	console.log(obj);
}

//获得参数
function zcurdGetParam() {
	var param = {};
	$("#tb :input[name]").each(function(i, item) {
		if($(item).val()) {
			param[$(item).attr("name")] = $(item).val();	
		}
	});
	return param;
}