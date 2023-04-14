'use strict';

angular.module('lithium')
.factory('userNotificationRest', ['$log', 'Restangular',
    function($log, Restangular) {
        try {
            var service = {};

            var rest = function() {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl("services/service-user-provider-threshold/backoffice/user-notification");
                });
            }
            service.findAll = function() {
                return rest().all("all").getList();
            }
            service.activateNotifications=function(model){
                return rest().all("activate").post(model);
            }

            service.notificationStatus=function(userGuid){
                return rest().all("status").one("p").get({userGuid:userGuid});
            }
            return service;
        } catch (err) {
            $log.error(err);
            throw err;
        }
    }
]);
