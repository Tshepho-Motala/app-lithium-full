'use strict'

angular.module('lithium').controller('GoGameWinDistributionsWinDistributionController', ['winDistribution', 'winDistributionBuckets', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', '$dt', 'DTOptionsBuilder',
    function(winDistribution, winDistributionBuckets, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest, $dt, DTOptionsBuilder) {
        var controller = this;

        controller.winDistribution = winDistribution;
        controller.winDistributionBuckets = winDistributionBuckets;

        controller.drawWinDistributionGraph = function() {
            console.log('Drawing Win Distribution Graph');

            var data = controller.winDistributionBuckets;
            var xColumnValues = "";
            var xDataPoints = "";


            for (var i = 0; i < data.length; i++) {
                var item = data[i];
                if (xColumnValues.length > 0) xColumnValues += ',';
                xColumnValues += item.comparator.symbol + " " + item.multiplier + " x Bet";
                if (xDataPoints.length > 0) xDataPoints += ',';
                xDataPoints += item.rtp;
            }

            controller.winDistributionData = {
                xColumnValues: xColumnValues,
                xDataPoints: xDataPoints
            }
        }

        controller.refresh = function() {
            gogameGamesRest.findWinDistributionTestById(controller.winDistribution.id).then(function(response) {
                controller.winDistribution = response.plain();
            });
            gogameGamesRest.findWinDistributionDataById(controller.winDistribution.id).then(function(response) {
                controller.winDistributionBuckets = response.plain();

                // FIXME - data is not plotted on refresh
                controller.drawWinDistributionGraph();
            });
        }

        controller.downloadxls = function() {
            console.log("Downloading XLS");
            window.location = 'services/service-casino-provider-gogame/admin/windistribution/'+winDistribution.id+'/xls';
        }

        controller.drawWinDistributionGraph();
    }
]);