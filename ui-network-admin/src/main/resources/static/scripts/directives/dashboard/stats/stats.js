'use strict';

angular.module('lithium')
    .directive('stats', function() {
        return {
            templateUrl: 'scripts/directives/dashboard/stats/stats.html',
            scope: {
                data: "=",
                granularity: "@",
                selectedDomains: "=?"
            },
            restrict: 'E',
            replace: true,
            controller: ['$q', '$scope', '$http', '$interval', function ($q, $scope, $http, $interval) {
                var completed = false;

                $scope.refresh = function() {
                    var me = this;

                    if (angular.isUndefined($scope.selectedDomains)) {
                        $scope.selectedDomains = [];
                        $scope.selectedDomains = $scope.data.domains;
                    }

                    $scope.data.countcurrent = 0;
                    $scope.data.countlast1 = 0;
                    $scope.data.countlast2 = 0;
                    $scope.data.counttotal = 0;

                    me.domains = [];
                    var promises = [];

                    for (var i in $scope.selectedDomains) {
                        var domainName = $scope.selectedDomains[i];
                        var url = "services/service-stats/backoffice/dashboard/stats"
                        url += "/" + $scope.selectedDomains[i];
                        url += "/" + $scope.data.statType;
                        url += "/" + $scope.data.event;
                        url += "/" + $scope.granularity;

                        promises.push($http.get(url)
                            .success((function(domainName) { return function(response, status) {
                                $scope.data.countcurrent += response.data.countcurrent;
                                $scope.data.countlast1 += response.data.countlast1;
                                $scope.data.countlast2 += response.data.countlast2;
                                $scope.data.counttotal += response.data.counttotal;
                            }})(domainName))
                        );
                    }
                }

                $scope.$watch("granularity", function() {
                    $scope.refresh();
                });
                $scope.$watch("selectedDomains", function(newValue, oldValue) {
                    if (newValue != oldValue) {
                        $scope.refresh();
                    }
                });

                if ($scope.data.updateSeconds !== undefined && $scope.data.updateSeconds !== null) {
                    var intervalPromise = null;
                    intervalPromise = $interval(function() {
                        if (completed) {
                            console.debug("stats | completed, refreshing");
                            completed = false;
                            $scope.refresh();
                        } else {
                            console.debug("stats | not completed, cannot refresh yet");
                        }
                    }, $scope.data.updateSeconds * 1000);
                    $scope.$on("$destroy",function(){
                        if (intervalPromise)
                            $interval.cancel(intervalPromise);
                    });
                }
            }]
        }
    });