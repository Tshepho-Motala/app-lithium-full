'use strict'

angular.module('lithium').controller('RewardsController', ["$q", "$timeout", "$state","$scope", '$translate', "$rootScope",
    function($q, $timeout, $state, $scope, $translate, $rootScope) {
        var controller = this;
        controller.providers = [
            {
                id: 1,
                name: 'roxor',
                url: "svc-reward-pr-casino-roxor"
            },
            {
                id: 2,
                name: 'sportsbook',
                url: "svc-reward-pr-sportsbook-sbt"
            },
            {
                id: 4,
                name: 'iforium',
                url: "svc-reward-pr-casino-iforium"
            },
            {
                id: 5,
                name: 'svc-reward',
                url: "svc-reward"
            }
        ]

        controller.rewardTypes = [
            {
                id: 1,
                name: 'Freespins',
                url: "svc-reward-pr-casino-roxor",
                rewardTypeFields: []
            },
            {
                id: 2,
                name: 'Instant rewards',
                url: "svc-reward-pr-casino-roxor",
                rewardTypeFields: []
            },
            {
                id: 3,
                name: 'Freebets',
                url: "svc-reward-pr-sportsbook-sbt",
                rewardTypeFields: []
            },
            {
                id: 4,
                name: 'Freespins',
                url: "svc-reward-pr-casino-iforium",
                rewardTypeFields: []
            },
            {
                id: 5,
                name: 'Cash',
                url: "svc-reward",
                rewardTypeFields: []
            },
        ]

        controller.promoRewards = [
            {
                id: 1,
                type: "Cash",
                name: "Freespins",
                code: "FREE_CASH",
                enabled: false,
                description: "10 freespins",
                domain: "livescore_media"
            },
            {
                id: 2,
                type: "Unlock Games",
                name: "Freebets",
                code: "UNLOCK_FREE_BETS",
                enabled: true,
                description: "15 freebets",
                domain: "livescore_bet_uk"
            },
            {
                id: 3,
                type: "Cash",
                name: "Instant rewards",
                code: "INSTANT_CASH_REWARS",
                enabled: false,
                description: "$10 cash reward",
                domain: "livescore_bet_uk"
            },
            {
                id: 4,
                type: "Unlock Games",
                name: "Instant reward freespins",
                code: "FREE_REWARD",
                enabled: true,
                description: "10 freespins",
                domain: "livescore_media"
            },
        ]

        controller.selectedRewardTypes = [
            {
                id: 1,
                name: 'Freespins',
                url: "svc-reward-pr-casino-roxor",
                rewardTypeFields: []
            }
        ]

        $rootScope.provide.rewardProvider['loadProviders'] = () => {
            return new Promise((res, rej) => {
                setTimeout(() => {
                    res(controller.providers)
                }, 1500)
            })
        }

        $rootScope.provide.rewardProvider['loadSelectedRewardTypes'] = () => {
            return new Promise((res, rej) => {
                setTimeout(() => {
                    res(controller.selectedRewardTypes)
                }, 1500)
            })
        }

        $rootScope.provide.rewardProvider['saveSelectedRewardType'] = (rewardType) => {
            return new Promise((res, rej) => {
                setTimeout(() => {
                    // reward.modifiedDate = Date.now()
                    if (rewardType.id === undefined) {
                        let maxId = 1
                        if (controller.loadSelectedRewardTypes.length > 0) {
                            maxId = controller.loadSelectedRewardTypes.sort((a, b) => b.id - a.id)[0].id
                        }
                        rewardType.id = maxId + 1
                    }
                    res(reward)
                }, 1500)
            })
        }

        $rootScope.provide.rewardProvider['loadRewards'] = () => {
            return new Promise((res, rej) => {
                setTimeout(() => {
                    res(controller.promoRewards)
                }, 1500)
            })
        }

        $rootScope.provide.rewardProvider['loadRewardTypes'] = () => {
            return new Promise((res, rej) => {
                setTimeout(() => {
                    res(controller.rewardTypes)
                }, 1500)
            })
        }

        $rootScope.provide.rewardProvider['saveReward'] = (reward) => {
            return new Promise((res, rej) => {
                setTimeout(() => {
                    // reward.modifiedDate = Date.now()
                    if (reward.id === undefined) {
                        let maxId = 1
                        if (controller.promoRewards.length > 0) {
                            maxId = controller.promoRewards.sort((a, b) => b.id - a.id)[0].id
                        }
                        reward.id = maxId + 1
                        console.log('Added id for reward: ' + reward.id)
                    }
                    res(reward)
                }, 1500)
            })
        }
        window.VuePluginRegistry.loadByPage("Rewards");
    }
]);