var thisImageIndex=0;
	
var getImageIndex=function(isLeft)
{
	var rValue=0;
	
	//t.debug("getImageIndex() thisImageIndex=" + thisImageIndex);
	
	if(isLeft)
	{
		if(thisImageIndex===0)
			thisImageIndex=3;
		else
			thisImageIndex--;
	}
	else
	{
		if(thisImageIndex===3)
			thisImageIndex=0;
		else
			thisImageIndex++;
	}
	
	rValue=thisImageIndex;
	
	return rValue;
};

	var imageList={
		0 : "<img src='./static/photos/001.jpg'/>"
		,1:"<img src='./static/photos/002.jpg'/>"
		,2:"<img src='./static/photos/003.jpg'/>"
		,3:"<img src='./static/photos/004.jpg'/>"
	};

var action={
	name : "test.action.help"
	,show : function(ac)
	{
		ac.Tool.debug('我是远程方法v02...');
		ac.$("#am_frame_help_imgs").html(imageList[2]).trigger("create");
	}
	,event : function(ac)
	{
		ac.$("#am_frame_help_imgs").on("tap",function()
		{
			ac.Tool.debug("You swiped tap!");

			ac.$("#am_frame_help_imgs").html(imageList[getImageIndex(true)]);//.trigger("create");
		});

		ac.$("#am_frame_help_imgs").on("swiperight",function()
		{
			ac.Tool.debug("You swiped right!");

			ac.$("#am_frame_help_imgs").html(imageList[getImageIndex(false)]);//.trigger("create");
		});

		ac.$("#am_frame_help_imgs").on("swipeleft",function()
		{
			ac.Tool.debug("You swiped left!");

			ac.$("#am_frame_help_imgs").html(imageList[getImageIndex(true)]);//.trigger("create");
		});
	}

}