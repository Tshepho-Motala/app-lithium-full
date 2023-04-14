'use strict';

angular.module('lithium')
    .factory('RestLimitSysAccess', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};
                var config = Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/');
                });

                service.all = function(domainName) {
                    //LimitSystemAccessControllerА
                    return config.all(domainName+"/domain-restrictions").all("list-limits").getList({domain:domainName});
                }

                service.saveLimitSystemAccess = function(domainName, limit) {
                    /// /UserController
                    return config.all(domainName+"/domain-restrictions").all("save-limit-system-access").post(limit);
                }

                // Keeping entityId signature since it is being passed in from the changelog directive. However it is unnecessary here.
                // Domain id is used to tie together limit System Access changelogs
                service.changelogs = function(domainName, entityId, page) {
                    return config.all(domainName).all("domain-restrictions").one("changelogs").get({ p: page });
                }

                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);