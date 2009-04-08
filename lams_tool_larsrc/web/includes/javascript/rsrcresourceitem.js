	/*
	 This is Resource Item instrcution area.
	 */
	var instructionTargetDiv = "instructionArea";
    var itemAttachmentTargetDiv = "itemAttachmentArea";
// Please set these 2 variables in JSP file for using tag reason:
//    var removeInstructionUrl = "<c:url value='/authoring/removeInstruction.do'/>";
//    var addInstructionUrl = "<c:url value='/authoring/newInstruction.do'/>";
	function removeInstruction(idx){
		//var id = "instructionItem" + idx;
		//Element.remove(id);
 		var url= removeInstructionUrl;
	    var reqIDVar = new Date();
	   
	   var param = $("instructionForm").serialize(true); // Form.serialize('instructionForm');
	   // param('instructionItemDesc' + idx) = 
	    param.removeIdx = idx;
	    param.reqID = reqIDVar.getTime();
	    
	    removeInstructionLoading();
	    var myAjax = new Ajax.Updater(
		    	instructionTargetDiv,
		    	url,
		    	{
		    		method:'post',
		    		parameters:param,
		    		onComplete:removeInstructionComplete,
		    		evalScripts:true,
		    		contentType: 'application/x-www-form-urlencoded; charset=UTF-8'
		    	}
	    );
	}
	function removeItemAttachment(idx){
		//var id = "instructionItem" + idx;
		//Element.remove(id);
 		var url= removeItemAttachmentUrl;
	    var reqIDVar = new Date();
	    var param = "reqID="+reqIDVar.getTime();
	    removeItemAttachmentLoading();
	    var myAjax = new Ajax.Updater(
		    	itemAttachmentTargetDiv,
		    	url,
		    	{
		    		method:'post',
		    		parameters:param,
		    		onComplete:removeItemAttachmentComplete,
		    		evalScripts:true
		    	}
	    );
	}
	function addInstruction(){
		var url= addInstructionUrl;
	    var reqIDVar = new Date();
	    var param = Form.serialize("instructionForm")+"&reqID="+reqIDVar.getTime();
		addInstructionLoading();
	    var myAjax = new Ajax.Updater(
		    	instructionTargetDiv,
		    	url,
		    	{
		    		method:'post',
		    		parameters:param,
		    		onComplete:addInstructionComplete,
		    		evalScripts:true 
		    	}
	    );
	}
	function upItem(itemIdx){
		if(itemIdx == 0)
			return;
		var currId = "instructionItemDesc" + itemIdx;
		var repId = "instructionItemDesc" + (--itemIdx);
		switchValue(currId,repId);
	}
	function downItem(itemIdx,maxSize){
		if(itemIdx == (maxSize -1))
			return;
		var currId = "instructionItemDesc" + itemIdx;
		var repId = "instructionItemDesc" + (++itemIdx);
		switchValue(currId,repId);
	}
	function switchValue(currId,repId){
		var temp = document.getElementById(repId).value;
		document.getElementById(repId).value = document.getElementById(currId).value;
		document.getElementById(currId).value = temp;
	}
	function removeInstructionLoading(){
		showBusy(instructionTargetDiv);
	}
	function removeInstructionComplete(){
		hideBusy(instructionTargetDiv);
	}
	function removeItemAttachmentLoading(){
		showBusy(itemAttachmentTargetDiv);
	}
	function removeItemAttachmentComplete(){
		hideBusy(itemAttachmentTargetDiv);
	}
	function addInstructionLoading(){
		showBusy(instructionTargetDiv);
	}
	function addInstructionComplete(){
		hideBusy(instructionTargetDiv);
	
	}
	
	function submitResourceItem(){
		//$("instructionList").value = Form.serialize("instructionForm");
		document.getElementById("instructionList").value = $("#instructionForm").serialize();
		//$("#resourceItemForm").value = $("#instructionForm").serialize();
		$("#resourceItemForm").submit();
		// after submit, it direct to itemlist.jsp, 
		// then refresh "basic tab" resourcelist and close this window.
	}
	function cancelResourceItem(){
		window.top.hideMessage();
	}
