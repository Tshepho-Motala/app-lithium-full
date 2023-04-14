'use strict';

angular.module('lithium')
    .factory('ClosureReasonsRest', ['$rootScope', 'Restangular', '$security',
        function ($rootScope, Restangular) {
            try {
                var service = {};
                var config = Restangular.withConfig(function (RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-user');
                });

                service.add = function (domainName, closureReason) {
                    return config.all("backoffice").all(domainName).all("closure-reasons-crud").all("add").post(closureReason);
                }
                service.save = function (domainName, closureReason) {
                    return config.all("backoffice").all(domainName).all("closure-reasons-crud").all("save").post(closureReason);
                }
                service.delete = function (domainName, id, deleteReason) {
                    return config.all("backoffice").all(domainName).all("closure-reasons-crud").all("delete").all(id).remove({comment:deleteReason});
                }
                service.find = function (domainName, closureReasonId) {
                    return config.all("backoffice").all(domainName).all("closure-reasons-crud").customGET("findById", {id: closureReasonId});
                }
                service.changelogs = function (domainName, entityId, page) {
                    return config.all("backoffice").all(domainName).all("closure-reasons-crud").all(entityId).one("changelogs").get({p: page});
                }

                return service;
            } catch (err) {
                console.error(err);
                throw err;
            }
        }
    ]);
