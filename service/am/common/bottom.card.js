function initCard(){
  var cards = $$('input[name^=card_init_]');
  if(cards){
   	for(var i=0;i<cards.length;i++){
		  var gid = cards[i].value;
      if(gid){
        var unitId = cards[i].get('name').substring(10);
        $fCard(unitId,gid)
      }
	  }
  }
}
initCard();