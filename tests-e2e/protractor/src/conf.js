exports.config = {
	seleniumAddress: 'http://selenium:4444/wd/hub',
	specs: ['spec.js'],
	multiCapabilities: [{
		'browserName': 'firefox'
	}, {
		'browserName': 'chrome'
	}]
}

