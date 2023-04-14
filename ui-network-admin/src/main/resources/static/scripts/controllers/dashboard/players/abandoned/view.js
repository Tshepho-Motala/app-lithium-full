'use strict';
console.log("AbandonedPlayersViewController");
angular.module('lithium')
	 .controller('AbandonedPlayersViewController', ["user","domain", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify", "UserRest", "$scope",
 function(user,domain, $uibModal, $translate, $log, $dt, $state, $rootScope, notify, UserRest, $scope) {
	 	
		 console.log("user",this.user);
		 
		var controller = this;
 		 controller.referenceId = "AbandonedPlayersViewController_"+(Math.random()*1000);
		controller.user = user;
		controller.domain=domain;
		 controller.randomthing = 12;
		 //console.log("user",user);
		 
		 
		 
}]);
		
