function window.onbeforeunload()   
{  
  if(event.clientX > document.body.clientWidth || event.clientY < 0 || event.altKey)   
  { 
		try
		{ 
		CloseSave(); 
		}
		catch(ee)
		{
		}    
  }   
}