
function init() {
	if (initialTabId) {
		selectTab(initialTabId);
	} else {
		selectTab(1);
	}
}
function doSelectTab(tabId) {
	selectTab(tabId);
}
function doSubmit(method, tabId) {
	document.forms.monitoringForm.action = method+".do";
	document.forms.monitoringForm.submit();
}

