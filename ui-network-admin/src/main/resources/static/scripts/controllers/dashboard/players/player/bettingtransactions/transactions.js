'use strict';

angular.module('lithium')
.controller('PlayerBettingTransController', ['user', '$scope', '$stateParams', function(user, $scope, $stateParams) {
    var controller = this;
    controller.data = {allowUserSearch: false, allowAddManualTran: false, selectedUser: user};
}]);
