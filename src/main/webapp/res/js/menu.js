$(function() {
	//加载菜单
	loadTree();
	
	//选项卡
	window.mainTabs = $('#mainTabs').tabs({
	    fit : true,
	    border : false,
	    tools : "#tabTools",
	    onContextMenu : function(e, title) {
	        e.preventDefault();
	        tabsMenu.menu('show', {
	            left : e.pageX,
	            top : e.pageY
	        }).data('tabTitle', title);
	    }
	});

	// 选项卡菜单
	window.tabsMenu = $('#tabsMenu').menu({
	    onClick : function(item) {
	        var curTabTitle = $(this).data('tabTitle');
	        var type = $(item.target).attr('type');

	        if (type === 'refresh') {
	            var iframe = mainTabs.tabs('getSelected').panel('panel').find('iframe');
	            iframe.attr("src", iframe.attr("src"));
	            return;
	        }

	        if (type === 'close') {
	            var t = mainTabs.tabs('getTab', curTabTitle);
	            if (t.panel('options').closable) {
	                mainTabs.tabs('close', curTabTitle);
	            }
	            return;
	        }

	        var allTabs = mainTabs.tabs('tabs');
	        var closeTabsTitle = [];

	        $.each(allTabs, function() {
	            var opt = $(this).panel('options');
	            if (opt.closable && opt.title != curTabTitle
	                    && type === 'closeOther') {
	                closeTabsTitle.push(opt.title);
	            } else if (opt.closable && type === 'closeAll') {
	                closeTabsTitle.push(opt.title);
	            }
	        });

	        for ( var i = 0; i < closeTabsTitle.length; i++) {
	            mainTabs.tabs('close', closeTabsTitle[i]);
	        }
	    }
	});
});

/**
 * 加载菜单
 */
function loadTree() {
	$.post(basePath + "/login/getMenu", function(data) {
		var menulist = [];
		$.each(data, function(i, item) {
			menulist.push({
				id: item.id,
				text: item.menu_name,
				attributes: basePath + item.menu_url, 
				parent_id: item.parent_id,
				iconCls: item.icon
			});
		});
		
		for(var i = 0; i < menulist.length; i++) {
			var children = menulist[i].children || [];
			for(var j = 0; j < menulist.length; j++) {
				if(menulist[j].parent_id == menulist[i].id) {
					children.push(menulist[j]);
				}
			}
			menulist[i].children = children;
		}
		
		var treeData = [];
		$.each(menulist, function(i, item) {
			if(item.parent_id == 0) {
				treeData.push(item);
			}
		});
		$("#menuTree").tree({
			data: treeData,
			onBeforeSelect: treeBeforeSelect, 
			onSelect: treeSelect
		});
	});
}

/**
 * 菜单事件
 */
function treeBeforeSelect(node) {
    if(node.children && node.children.length > 0) {
        if(node.state == "open") {
            $(this).tree("collapse", node.target);
        }else {
            $(this).tree("expand", node.target);
        }
        return false;
    } 
}
/**
 * 菜单事件
 */
function treeSelect(node) {
	addMainTab(node.text, node.attributes);
}

/**
 * 增加一个主窗口
 */
function addMainTab(text, url) {
	if(!mainTabs.tabs("exists", text)) {
		mainTabs.tabs('add',{
            title: text,
            content:'<iframe src="' + url + '" class="easyui-panel" data-options="fit:true,border:false" frameborder="0"></iframe>',
            closable:true
        });
    }else {
    	mainTabs.tabs("select", text);
    	//刷新
    	mainTabs.tabs('getSelected').panel('panel').find('iframe').attr("src", url)
    }
}

/**
 * 全屏
 */
function fullScreen() {
	if($("#fullScreen").find(".glyphicon-screenshot").length > 0) {
		$("#layout").layout('expand', 'west').layout('expand', 'north');
		$("#fullScreen").find(".l-btn-icon").addClass("glyphicon-fullscreen").removeClass("glyphicon-screenshot");
	}else {
		$("#layout").layout('collapse', 'north').layout('collapse', 'west');
		$("#fullScreen").find(".l-btn-icon").addClass("glyphicon-screenshot").removeClass("glyphicon-fullscreen");
		$(".layout-expand").hide();
	}
	
}