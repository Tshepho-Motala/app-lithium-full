'use strict'

angular.module('lithium').controller('UserMissionViewController', ['usermission', 'gamesList', '$translate',
	function(usermission, gamesList, $translate) {
		var controller = this;
		
		controller.model = usermission.plain();
		controller.gamesList = gamesList.plain();
		
	}
]);