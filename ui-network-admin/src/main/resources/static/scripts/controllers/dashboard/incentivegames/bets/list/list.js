'use strict'

angular.module('lithium').controller('IncentiveGamesBetsListController', ['user', '$stateParams', 'periodService',
    function(user, $stateParams, periodService) {
        var controller = this;

        console.debug("user", user);
        console.debug("$stateParams", $stateParams);

        controller.data = {user: user};

        var domainName = $stateParams.domainName; // We aren't really filtering by domain in this list
        var granularity = $stateParams.granularity;
        var offset = $stateParams.offset;
        var tranType = $stateParams.tranType;

        if (granularity !== undefined && granularity !== null &&
            offset !== undefined && offset !== null && offset !== -1) {
                controller.data.betTimestampRangeStart = new Date(periodService.getDateByGranularityAndOffsetAndType(granularity, offset, 'start'));
                controller.data.betTimestampRangeEnd = new Date(periodService.getDateByGranularityAndOffsetAndType(granularity, offset, 'end'));
        }

        if (tranType !== undefined && tranType !== null) {
            switch (tranType) {
                case "ALL":
                    controller.data.isSettled = 1;
                    break;
                case "WIN":
                    controller.data.isSettled = 1;
                    controller.data.result = "WIN";
                    break;
                case "VOID":
                    controller.data.isSettled = 1;
                    controller.data.result = "VOID";
                    break;
                default:;
            }
        }
    }
]);