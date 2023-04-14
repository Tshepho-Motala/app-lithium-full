'use strict';

angular.module('lithium').factory('CasinoCMSRest', ['Restangular', '$q',
    function(Restangular, $q) {
        try {
            var service = {};
            var rest = function(domainName) {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-casino-cms/backoffice/' + domainName);
                });
            }

            var restV1 = function(domainName) {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-casino-cms/backoffice/' + domainName + '/v1');
                });
            }

            service.lobbyExists = function(domainName) {
                return rest(domainName).all('lobbies').one('lobby-exists').get();
            }

            service.add = function(domainName, lobby) {
                return rest(domainName).all('lobbies').all('add').post(lobby);
            }

            service.find = function(domainName, id) {
                return rest(domainName).one('lobby', id).get();
            }

            service.findRevision = function(domainName, id, lobbyRevisionId) {
                return rest(domainName).one('lobby', id).one('revision', lobbyRevisionId).get();
            }

            service.modifyLobby = function(domainName, id) {
                return rest(domainName).one('lobby', id).one('modify').get();
            }

            service.modifyLobbyPost = function(domainName, id, lobby) {
                return rest(domainName).all('lobby').all(id).all('modify').post(lobby);
            }

            service.modifyAndSaveCurrentLobby = function(domainName, id, lobby) {
                return rest(domainName).all('lobby').all(id).all('modifyAndSaveCurrent').post(lobby);
            }

            service.getLobbies = function(domain) {
                return restV1(domain).all('lobbies').getList();
            }

            service.getDomainBanners = function(domain) {
                return rest(domain).all('banners').all('find-all').post();
            }

            service.getBanner = function(domain, bannerId) {
                return rest(domain).one('banners', bannerId).all('get').post();
            }

            service.saveBanner = function(domain, banner) {
                return rest(domain).all('banners').all('create').post(banner);
            }

            service.updateBanner = function(domain, id,  banner) {
                return rest(domain).one('banners', id).all('update').post(banner);
            }

            service.deleteBanner = function(domain, id) {
                return rest(domain).one('banners', id).all('remove').post();
            }

            service.getBannersForPage = function (domain, lobbyId, channel, primaryNavCode, secondaryNavCode) {
                return rest(domain).one('lobby', lobbyId).all('banners').all('get-page-banners').post(
                    {
                        channel: channel,
                        primaryNavCode: primaryNavCode,
                        secondaryNavCode: secondaryNavCode
                    }
                );
            }

            service.updatePageBannersPosition = function (domain, lobbyId, pageBannerList) {
                return rest(domain).one('lobby', lobbyId).all('banners').all('update-positions').post(pageBannerList);
            }

            service.removePageBanner = function (domain, lobbyId, pageBannerId) {
                return rest(domain).one('lobby', lobbyId).all('banners').one('remove-from-page', pageBannerId).post();
            }

            service.addPageBanner = function (domain, lobbyId, bannerId, pageBanner) {
                return rest(domain).one('lobby', lobbyId).one('banners', bannerId).all('add-page-banner').post(pageBanner);
            }

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);
