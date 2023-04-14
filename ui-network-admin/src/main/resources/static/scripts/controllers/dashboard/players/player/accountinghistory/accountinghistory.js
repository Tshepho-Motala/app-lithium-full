'use strict';

angular.module('lithium').controller('PlayerAccountingHistoryController', ['user', function (user) {
    var controller = this;
    controller.selectedUser = user;
}]);
