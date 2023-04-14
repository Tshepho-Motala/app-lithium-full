'use strict';

angular.module('lithium').controller('PlayerKycRecordsController', ['user', '$scope',
    function (user, $scope) {
    var controller = this;
    controller.selectedUser = user;
}]);