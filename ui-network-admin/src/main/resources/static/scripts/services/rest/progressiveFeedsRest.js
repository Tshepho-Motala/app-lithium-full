'use strict';

angular.module('lithium')
    .factory('rest-progressive-feeds', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};
                var config = Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-games/backoffice');
                });


                service.findByProgressiveFeedRegistrationId = function(progressiveFeedRegistrationId) {
                    return config.one("jackpot-feed/progressive/registered-feed", progressiveFeedRegistrationId).all("toggle-enabled").post();
                }

                service.findProgressiveJackpotGameFeedsByDomain = function(domainName) {
                    return config.one("jackpot-feeds/progressive", domainName).all("progressive-jackpot-game-balance/get").post();
                }

                service.findProgressiveJackpotFeedsByDomain = function(domainName) {
                    return config.one("jackpot-feeds/progressive", domainName).all("progressive-jackpot-balance/get").post();
                }
                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);
