'use strict';

angular.module('lithium')
.factory('userThresholdRest', ['$log', 'Restangular',
    function($log, Restangular) {
        try {
            var service = {};

            var rest = Restangular.withConfig(function(RestangularConfigurer) {
                RestangularConfigurer.setBaseUrl("services/service-user-threshold/backoffice/threshold");
            });

            service.findLossLimitThresholdsBy = function(domainName, granularity, ageMin, ageMax,eType) {
                return rest.all("loss-limit").all(domainName).all("v1").one("find").get({
                    granularity: granularity,
                    ageMin: ageMin,
                    ageMax: ageMax,
                    eType:eType
                });
            }

            service.saveLossLimitThreshold = function(domainName, id, percentage,amount,eType, granularity, ageMin, ageMax) {
                return rest.all("loss-limit").all(domainName).all("v1").one("save").customPOST('', '', {
                    granularity: granularity,
                    id: id,
                    percentage: percentage,
                    amount:amount,
                    eType:eType,
                    ageMin: ageMin,
                    ageMax: ageMax
                });
            }
            service.disableLossLimitThreshold = function(domainName, id,eType) {
                return rest.all("loss-limit").all(domainName).all("v1").one("disable").customPOST('', '', {
                    id: id,
                    eType: eType
                });
            }

            service.setNotifications = function(domainName, playerGuid, notifications) {
                return rest.all("warnings").all(domainName).all("v1").one("set-notifications").customPOST('', '', {
                    playerGuid: playerGuid,
                    notifications: notifications
                });
            }
            service.getNotifications = function(domainName, playerGuid) {
                return rest.all("warnings").all(domainName).all("v1").one("get-notifications").customPOST('', '', {
                    playerGuid: playerGuid
                });
            }

            return service;
        } catch (err) {
            $log.error(err);
            throw err;
        }
    }
]);
