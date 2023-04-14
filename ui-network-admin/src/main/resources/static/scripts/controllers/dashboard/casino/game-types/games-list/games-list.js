'use strict';

angular.module('lithium')
  .controller('AssignedGamesListController', ['gameTypeId', "domainName", 'rest-games','$uibModalInstance', '$scope', '$stateParams', '$translate', 'errors', 'notify', '$dt', 'DTOptionsBuilder',
      function(gameTypeId, domainName, gamesRest, $uibModalInstance, $scope, $stateParams, $translate, errors, notify, $dt, DTOptionsBuilder) {
          var controller = this;

          controller.domainName = domainName;
          gamesRest.getDomainGamesByGameType(domainName, gameTypeId).then(games => {
              controller.games = games;
          });

          controller.cancel = function() {
              $uibModalInstance.dismiss('cancel');
          };

          controller.refresh = function() {
              controller.table.instance.reloadData(function(){}, false);
          }
      }]);