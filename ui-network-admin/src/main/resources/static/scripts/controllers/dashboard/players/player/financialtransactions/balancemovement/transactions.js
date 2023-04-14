'use strict';

angular.module('lithium')
.controller('PlayerBalanceMovementController', ['user', '$scope', '$stateParams', function(user, $scope, $stateParams) {
    var controller = this;
    controller.selectedUser = user;
}]);
