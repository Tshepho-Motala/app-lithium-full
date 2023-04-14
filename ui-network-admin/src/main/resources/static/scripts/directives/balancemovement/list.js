'use strict';

angular.module('lithium')
    .directive('balancemovementlist', function () {

            return {
                templateUrl: 'scripts/directives/balancemovement/list.html',
                scope: {
                    inputPlayer: "=?"
                },
                restrict: 'E',
                replace: true,
                controllerAs: 'controller',
                controller: [
                    '$state', '$filter', '$dt', 'UserRest', 'rest-accounting-internal', '$translate', '$compile', 'DTOptionsBuilder', '$scope', "notify", "$stateParams", '$rootScope',
                    function ($state, $filter, $dt, userRest, accountInternalRest, $translate, $compile, DTOptionsBuilder, $scope, notify, $stateParams, $rootScope, ) {
                        $rootScope.provide.quickActionProvider['user'] = $scope.inputPlayer
                        window.VuePluginRegistry.loadByPage("BalanceMovement")
                    }
                ]
            };
        }
    );
