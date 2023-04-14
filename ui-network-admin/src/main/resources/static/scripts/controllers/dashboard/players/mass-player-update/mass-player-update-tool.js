'use strict';

angular.module('lithium').controller('MassPlayerUpdateToolController', ['statuses', 'statusReasons', 'tags', 'domainName', '$state', 'currencySymbol', 'excludeStatusReasons',
    function(statuses, statusReasons, tags, domainName, $state, currencySymbol, excludeStatusReasons) {

        var controller = this;

        controller.data = {
            domainName: domainName,
            statuses: statuses,
            statusReasons: statusReasons,
            tags: tags,
            currencySymbol: currencySymbol,
            excludeStatusReasons: excludeStatusReasons
        };
    }]);
