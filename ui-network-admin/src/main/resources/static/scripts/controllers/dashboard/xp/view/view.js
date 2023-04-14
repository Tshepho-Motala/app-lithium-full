'use strict'

angular.module('lithium').controller('XPSchemesViewController', ['scheme', '$translate',
	function(scheme, $translate) {
		var controller = this;
		
		controller.model = scheme;
	}
]);