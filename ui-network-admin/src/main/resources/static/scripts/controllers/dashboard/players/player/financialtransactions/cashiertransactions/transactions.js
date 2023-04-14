'use strict';

angular.module('lithium')
.controller('PlayerCashierTransController', ['user', '$scope', '$stateParams', function(user, $scope, $stateParams) {
    var controller = this;
    controller.data = {allowUserSearch: false, allowAddManualTran: false, selectedUser: user};
}]);
