$$('input[type=text]').addEvent('keyup',function(e){
	if(e.code==13){
		var obj = e.target;
		var elements  = document.forms[0].elements;
		for(var i=0; i<elements.length-1; i++){   
             if($(elements[i])==obj) { 
				 $(elements[i+1]).focus();
                 break;
              }
         }   
	}
});
