'use strict';

angular.module('lithium').directive('incentivegamesbet', function() {
    return {
        templateUrl:'scripts/directives/incentivegames/bet/bet.html',
        scope: {
            bet: "="
        },
        restrict: 'E',
        replace: true,
        controller: ['$scope',
            function($scope) {
            }
        ]
    }
});
