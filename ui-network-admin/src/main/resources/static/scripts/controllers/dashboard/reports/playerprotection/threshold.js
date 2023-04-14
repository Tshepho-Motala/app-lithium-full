'use strict'

angular.module('lithium').controller('ThresholdReport', ['errors', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$scope', '$state' ,'$rootScope', '$userService', 'rest-domain'
    ,'bsLoadingOverlayService','userThresholdHistoryRest','$http','$stateParams','DocumentGenerationRest',
    function(errors, $translate, $dt, DTOptionsBuilder, $filter, $scope, $state, $rootScope, $userService, restDomain, bsLoadingOverlayService
    ,userThresholdHistoryRest, $http, $stateParams, documentRest) {
        var controller = this;

        controller.selectedDomain = null;

        // controller.textTitle = 'UI_NETWORK_ADMIN.PLAYER.TAB.LOSS_LIMIT';
        controller.textTitle = 'Limits Threshold History Report';
        controller.textDescr = 'UI_NETWORK_ADMIN.LOSS_LIMIT.LIST.DESCRIPTION';

        // $scope.setDescription("UI_NETWORK_ADMIN.LOSS_LIMIT.LIST.DESCRIPTION");

        $rootScope.provide.pageHeaderProvider.getDomainsList = () => {
            return $userService.playerDomainsWithAnyRole(["ADMIN", "USER_THRESHOLD_HISTORY_VIEW"]);
        }

        $rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
            if ( item === null) {
                $rootScope.provide.pageHeaderProvider.clearSelectedDomain()
                return
            }

            controller.selectedDomain = item.name;
            $stateParams.domainName = item.name;

            if (isAlreadyChecked) {
                $state.go('dashboard.threshold.list', {
                    domainName: item.name
                });
            }
        }

        $rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
            controller.selectedDomain = null;
            // $scope.setDescription("");
            $state.go('dashboard.threshold');
        }

        $rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
            return controller.textTitle ? controller.textTitle : ''
        }

        $rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
            return controller.textDescr ? controller.textDescr : ''
        }

        window.VuePluginRegistry.loadByPage("page-header")
    }]);
