'use strict';

angular.module('lithium')
    .factory('dev-tool-rest', ['$log', 'Restangular',
        function ($log, Restangular) {
            try {
                var service = {};

                var backofficeRest = function (domainName) {
                    return Restangular.withConfig(function (RestangularConfigurer) {
                        RestangularConfigurer.setBaseUrl("services/service-cashier/backoffice/dev-tools/" + domainName + "/");
                    });
                }

                service.emptyOutPendingWithdrawBalance = function (domainName, guid, updatePlayerBalance) {
                    return backofficeRest(domainName).one("empty-out-pending-withdraw-balance").get({
                        guid: guid,
	                    updatePlayerBalance: updatePlayerBalance
                    });
                }
                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);