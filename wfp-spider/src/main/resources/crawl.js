var system = require('system');
const fs = require('fs')
var jsonFile = system.args[1];
params = JSON.parse(fs.read(jsonFile))
url = params.url
var resources = [];
var page = require('webpage').create();
page.settings.loadImages = false;
page.settings.resourceTimeout = params.timeOut==0?5000:params.timeOut;
page.open(url, function (status) {
	console.log('statusCode:'+resources[0].status);
	console.log('redirectURL:'+resources[0].redirectURL);
	console.log(">----------------------<")
    if (status != 'success') {
        console.log("HTTP request failed!"+status);
    } else {
    	
        console.log(page.content);
    }

    page.close();
    phantom.exit();
});
page.onResourceReceived = function(response) {
    // check if the resource is done downloading 
    if (response.stage !== "end") return;
    // apply resource filter if needed:
    if (response.headers.filter(function(header) {
    	// header.value
        if (header.name == 'Content-Type') {
            return true;
        }
        return false;
    }).length > 0)
        resources.push(response);
};
page.onError = function(msg, trace) {};
