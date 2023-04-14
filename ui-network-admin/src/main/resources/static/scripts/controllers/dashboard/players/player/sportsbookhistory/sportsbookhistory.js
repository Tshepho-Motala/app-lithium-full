'use strict';

angular.module('lithium').controller('SportsbookHistoryController', ['user', function (user) {
    var controller = this;
    controller.selectedUser = user;
}]);
