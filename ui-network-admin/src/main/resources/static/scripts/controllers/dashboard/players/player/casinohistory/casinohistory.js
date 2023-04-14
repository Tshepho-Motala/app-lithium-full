'use strict';

angular.module('lithium').controller('CasinoHistoryController', ['user', 'domainInfo', function (user, domainInfo) {
  var controller = this;
  controller.selectedUser = user;
  controller.domain = domainInfo;
}]);
