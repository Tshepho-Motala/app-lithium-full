'use strict'

angular.module('lithium').controller('InboxViewController', ['inbox', '$translate',
	function(inbox, $translate) {
		var controller = this;
		
		controller.model = inbox;
	}
]);