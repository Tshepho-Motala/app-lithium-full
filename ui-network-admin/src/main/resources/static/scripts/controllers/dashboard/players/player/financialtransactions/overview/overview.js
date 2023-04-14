'use strict';

angular.module('lithium')
.controller('PlayerFinancialTransOverviewController', ['user', '$scope', '$stateParams', function(user, $scope, $stateParams) {
    var controller = this;
    controller.data = { user: user };
}]);
