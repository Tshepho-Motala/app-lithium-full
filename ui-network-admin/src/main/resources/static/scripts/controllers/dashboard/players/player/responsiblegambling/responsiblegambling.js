'use strict'

angular.module('lithium').controller('ResponsibleGamblingController', ['user', 'domain', 'userData', '$translate', '$dt', 'DTOptionsBuilder', '$filter', 'domainSettings',
    function(user, domain, userData, $translate, $dt, DTOptionsBuilder, $filter, domainSettings) {
        var controller = this;

        controller.domain = domain;
        controller.user = user;
        controller.userData = userData;
        controller.domainSettings = domainSettings;

        var baseUrl = 'services/service-limit/backoffice/reality-check/v1/'+user.domain.name+'/audit';
        var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
        controller.RealityCheckTable = $dt.builder()
            .column($dt.columnformatdatetime('date').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.RESPONSIBLE_GAMBLING.DATE')))
            .column($dt.column('action').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.RESPONSIBLE_GAMBLING.ACTION')))
            .options({url: baseUrl, type: 'GET', data: user}, null, dtOptions, null)
            .build();
    }
]);
