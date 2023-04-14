'use strict';

angular.module('lithium').controller('lossLimitsController', ['$scope', '$translate', '$stateParams', '$uibModal', 'domainAgeLimitsRest', 'domainLimitsRest', 'errors', 'notify', '$q', 'userThresholdRest',
    function ($scope, $translate, $stateParams, $uibModal, domainAgeLimitsRest, domainLimitsRest, errors, notify, $q, userThresholdRest) {
        const controller = this;
        $scope.dailyLossLimit = undefined;
        $scope.weeklyLossLimit = undefined;
        $scope.monthlyLossLimit = undefined;
        $scope.annualLossLimit = undefined;
        $scope.dailyWarningThreshold = undefined;
        $scope.weeklyWarningThreshold = undefined;
        $scope.monthlyWarningThreshold = undefined;
        $scope.annualWarningThreshold = undefined;
        $scope.warningThreshold = undefined;

        $scope.dailyWinLimit = undefined;
        $scope.weeklyWinLimit = undefined;
        $scope.monthlyWinLimit = undefined;
        $scope.annualWinLimit = undefined;

        $scope.dailyDepositThreshold = undefined;
        $scope.weeklyDepositThreshold = undefined;
        $scope.monthlyDepositThreshold = undefined;
        $scope.annualDepositThreshold = undefined;

        controller.ageLimits = [];
        controller.ageExceptionList = undefined;
        $scope.domainName = $stateParams.domainName;
        controller.changelogs = undefined;

        controller.reload = function () {
            if ($scope.domainName) {
                controller.changelogs = {
                    domainName: $scope.domainName,
                    entityId: $stateParams.domain.id,
                    restService: domainLimitsRest,
                    reload: 0
                }

                domainAgeLimitsRest.findAllByDomain($scope.domainName).then(function (response) {
                    controller.ageLimits = (response !== []) ? response.plain() : [];

                    controller.ageExceptionList = controller.ageLimits.reduce(function (r, a) {
                        r[a.ageMax] = r[a.ageMax] || [];
                        a.threshold = {};
                        //hack for ordering
                        if (a.granularity === 3) a.screenorder = 1;
                        if (a.granularity === 4) a.screenorder = 2;
                        if (a.granularity === 2) a.screenorder = 3;
                        if (a.granularity === 1) a.screenorder = 4;
                        userThresholdRest.findLossLimitThresholdsBy($scope.domainName, a.granularity, a.ageMin, a.ageMax,'TYPE_LOSS_LIMIT').then(function (response2) {
                            if (angular.isDefined(response2)) {
                                a.thresholdPercentage = response2.current.percentage;
                                a.threshold = response2.plain() || {};
                            }
                        }).catch(function () {
                            errors.catch('', false);
                        });
                        r[a.ageMax].push(a);
                        return r;
                    }, Object.create(null));

                }).catch(function () {
                    errors.catch('', false);
                });

                $scope.dailyWarningThreshold = null;
                domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_DAY, domainLimitsRest.LIMIT_TYPE_LOSS).then(function (response) {
                    controller.dailyDomainlimit = response.plain();
                    $scope.dailyLossLimit = (response.amount !== undefined) ? response.amount / 100 : null;

                    userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_DAY,null,null,'TYPE_LOSS_LIMIT').then(
                        function (response2) {
                            if (angular.isDefined(response2)) {
                                controller.dailyDomainThreshold = response2.plain();
                                $scope.dailyWarningThreshold = response2.current.percentage;
                            }
                        }
                    ).catch(function () {
                        errors.catch('', false);
                    });
                }).catch(function () {
                    errors.catch('', false);
                });

                $scope.weeklyWarningThreshold = null;
                domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_WEEK, domainLimitsRest.LIMIT_TYPE_LOSS).then(function (response) {
                    controller.weeklyDomainlimit = response.plain();
                    $scope.weeklyLossLimit = (response.amount !== undefined) ? response.amount / 100 : null;
                    userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_WEEK,null,null,'TYPE_LOSS_LIMIT').then(
                        function (response2) {
                            if (angular.isDefined(response2)) {
                                controller.weeklyDomainThreshold = response2;
                                $scope.weeklyWarningThreshold = response2.current.percentage;
                            }
                        }
                    ).catch(function () {
                        errors.catch('', false);
                    });
                }).catch(function () {
                    errors.catch('', false);
                });

                $scope.monthlyWarningThreshold = null;
                domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_MONTH, domainLimitsRest.LIMIT_TYPE_LOSS).then(function (response) {
                    controller.monthlyDomainlimit = response.plain();
                    $scope.monthlyLossLimit = (response.amount !== undefined) ? response.amount / 100 : null;
                    userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_MONTH,null,null,'TYPE_LOSS_LIMIT').then(
                        function (response2) {
                            if (angular.isDefined(response2)) {
                                controller.monthlyDomainThreshold = response2;
                                $scope.monthlyWarningThreshold = response2.current.percentage;
                            }
                        }
                    ).catch(function () {
                        errors.catch('', false);
                    });
                }).catch(function () {
                    errors.catch('', false);
                });

                $scope.annualWarningThreshold = null;
                domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_YEAR, domainLimitsRest.LIMIT_TYPE_LOSS).then(function (response) {
                    controller.annualDomainlimit = response.plain();
                    $scope.annualLossLimit = (response.amount !== undefined) ? response.amount / 100 : null;
                    userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_YEAR, null, null, 'TYPE_LOSS_LIMIT').then(
                      function (response2) {
                          if (angular.isDefined(response2)) {
                              controller.annualDomainThreshold = response2;
                              $scope.annualWarningThreshold = response2.current.percentage;
                          }
                      }
                    ).catch(function () {
                        errors.catch('', false);
                    });
                }).catch(function () {
                    errors.catch('', false);
                });

                $scope.dailyDepositThreshold = null;
                userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_DAY,null,null,'TYPE_DEPOSIT_LIMIT').then(
                    function (response) {
                        controller.dailyDomainDepositThreshold = response;
                        $scope.dailyDepositThreshold = (response !== undefined && response.current?.amount !== undefined) ? response.current.amount  : null;
                    }
                ).catch(function () {
                    errors.catch('', false);
                });

                $scope.weeklyDepositThreshold = null;
                userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_WEEK, null, null, 'TYPE_DEPOSIT_LIMIT').then(
                    function (response) {
                        controller.weeklyDomainDepositThreshold = response;
                        $scope.weeklyDepositThreshold = (response !== undefined && response.current?.amount !== undefined) ? response.current.amount : null;
                    }
                ).catch(function () {
                    errors.catch('', false);
                });

                $scope.monthlyDepositThreshold = null;
                userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_MONTH, null, null, 'TYPE_DEPOSIT_LIMIT').then(
                    function (response) {
                        controller.monthlyDomainDepositThreshold = response;
                        $scope.monthlyDepositThreshold = (response !== undefined && response.current?.amount !== undefined) ? response.current.amount : null;
                    }
                ).catch(function () {
                    errors.catch('', false);
                });

                $scope.annualDepositThreshold = null;
                userThresholdRest.findLossLimitThresholdsBy($scope.domainName, domainLimitsRest.GRANULARITY_YEAR, null, null, 'TYPE_DEPOSIT_LIMIT').then(
                  function (response) {
                      controller.annualDomainDepositThreshold = response;
                      $scope.annualDepositThreshold = (response !== undefined && response.current?.amount !== undefined) ? response.current.amount : null;
                  }
                ).catch(function () {
                    errors.catch('', false);
                });
            }
        };

        controller.reload();

        controller.delay = function sleep(ms) {
            return new Promise(resolve => setTimeout(resolve, ms));
        }

        controller.addAgeException = function () {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/brandConfig/losslimits/addAgeLossLimits.html',
                controller: 'addAgeLossLimit',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    type: function () {
                        return $stateParams.type;
                    },
                    domainName: function () {
                        return $stateParams.domainName;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: [
                                'scripts/controllers/dashboard/brandConfig/losslimits/addAgeLossLimits.js'
                            ]
                        })
                    }
                }
            });
            modalInstance.result.then(function (dm) {
                controller.delay(1000).then(() => {
                    controller.reload();
                    controller.refresh();
                });

            });
        };

        controller.editAgeException = function (editException) {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/brandConfig/losslimits/editAgeLossLimit.html',
                controller: 'editAgeLossLimit',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    limitDetails: function () {
                        return editException;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: [
                                'scripts/controllers/dashboard/brandConfig/losslimits/editAgeLossLimit.js'
                            ]
                        })
                    }
                }
            });

            modalInstance.result.then(function (dm) {
                controller.reload();
            });
        };

        controller.removeAgeException = function (id) {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/brandConfig/losslimits/deleteAgeLossLimit.html',
                controller: 'deleteAgeLossLimit',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    limitDetails: function () {
                        return id;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: [
                                'scripts/controllers/dashboard/brandConfig/losslimits/deleteAgeLossLimit.js'
                            ]
                        })
                    }
                }
            });

            modalInstance.result.then(function (dm) {
                controller.reload();
            });
        };

        controller.updateAgeRange = function (domain) {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/brandConfig/losslimits/editAgeGroupLimit.html',
                controller: 'editAgeGroupLimit',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    ageLimits: function () {
                        return domain;
                    },
                    ageRanges: function () {
                        return domain[0];
                    },
                    idsToEdit: function () {
                        let ids = [];
                        domain.forEach(function (entry) {
                            ids.push(entry.id);
                        });
                        return ids;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: [
                                'scripts/controllers/dashboard/brandConfig/losslimits/editAgeGroupLimit.js'
                            ]
                        })
                    }
                }
            });

            modalInstance.result.then(function (dm) {
                controller.reload();
            });
        }

        controller.addGranularity = function (domain) {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/brandConfig/losslimits/addGranularity.html',
                controller: 'addGranularity',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    granularityPending: function () {
                        let granularity = [3, 4, 2, 1];
                        let granularityFound = [];

                        domain.forEach(r => {
                            granularityFound.push(r.granularity)
                        });

                        return granularity.filter(val => !granularityFound.includes(val));
                    },
                    domainLimit: function () {
                        return domain[0];
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: [
                                'scripts/controllers/dashboard/brandConfig/losslimits/addGranularity.js'
                            ]
                        })
                    }
                }
            });

            modalInstance.result.then(function () {
                controller.reload();
            });
        }

        controller.deleteAgeRange = function (domain) {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/brandConfig/losslimits/deleteAgeGroupLimit.html',
                controller: 'deleteAgeGroupLimit',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    ageRanges: function () {
                        return domain[0];
                    },
                    idsToDelete: function () {
                        let ids = [];
                        domain.forEach(function (entry) {
                            ids.push(entry.id);
                        });
                        return ids;
                    },
                    thresholdIds: function () {
                        let thresholdIds = [];
                        domain.forEach(function (d) {
                            if (angular.isDefined(d.threshold.id)) {
                                thresholdIds.push(d.threshold.id);
                            }
                        });
                        return thresholdIds;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: [
                                'scripts/controllers/dashboard/brandConfig/losslimits/deleteAgeGroupLimit.js'
                            ]
                        })
                    }
                }
            });

            modalInstance.result.then(function (dm) {
                controller.reload();
            });
        }

        controller.changeLimits = function (granularity, type) {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/domains/domain/limits/changelimits.html',
                controller: 'ChangeDomainLimitsModal',
                controllerAs: 'controller',
                backdrop: 'static',
                size: 'md',
                resolve: {
                    domain: function () {
                        return $stateParams.domain;
                    },
                    thresholds: function () {
                        if (granularity === domainLimitsRest.GRANULARITY_DAY) {
                            if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return controller.dailyDomainDepositThreshold;
                            } else {
                                return controller.dailyDomainThreshold;
                            }
                        } else if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
                            if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return controller.weeklyDomainDepositThreshold;
                            } else {
                                return controller.weeklyDomainThreshold;
                            }
                        } else if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
                            if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return controller.monthlyDomainDepositThreshold;
                            } else {
                                return controller.monthlyDomainThreshold;
                            }
                        } else if (granularity === domainLimitsRest.GRANULARITY_YEAR) {
                            if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return controller.annualDomainDepositThreshold;
                            } else {
                                return controller.annualDomainThreshold;
                            }
                        }
                        return null;
                    },
                    dailyLimit: function () {
                        if (granularity === domainLimitsRest.GRANULARITY_DAY) {
                            if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
                                return $scope.dailyWinLimit;
                            } else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                                return $scope.dailyLossLimit;
                            } else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return $scope.dailyDepositThreshold;
                            }
                        } else {
                            return null;
                        }
                    },
                    weeklyLimit: function () {
                        if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
                            if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
                                return $scope.weeklyWinLimit;
                            } else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                                return $scope.weeklyLossLimit;
                            } else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return $scope.weeklyDepositThreshold;
                            }
                        } else {
                            return null;
                        }
                    },
                    monthlyLimit: function () {
                        if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
                            if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
                                return $scope.monthlyWinLimit;
                            } else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                                return $scope.monthlyLossLimit;
                            } else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return $scope.monthlyDepositThreshold;
                            }
                        } else {
                            return null;
                        }
                    },
                    annualLimit: function () {
                        if (granularity === domainLimitsRest.GRANULARITY_YEAR) {
                            if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
                                return $scope.annualWinLimit;
                            } else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                                return $scope.annualLossLimit;
                            } else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                                return $scope.annualDepositThreshold;
                            }
                        } else {
                            return null;
                        }
                    },
                    granularity: function () {
                        return granularity;
                    },
                    type: function () {
                        return type;
                    },
                    loadMyFiles: function ($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: ['scripts/controllers/dashboard/domains/domain/limits/changelimits.js']
                        })
                    }
                }
            });

            modalInstance.result.then(function (result) {
                controller.reload();
                controller.refresh();
            });
        };

        controller.removeLimit = function (granularity, type) {
            var promises = [];

                var removeDomainLimit = domainLimitsRest.removeDomainLimit($scope.domainName, granularity, type).then(function (response) {
                    return response;
                }).catch(function () {
                    errors.catch('', false);
                });
                promises.push(removeDomainLimit);

                let thresholdId = null;
                if (granularity === domainLimitsRest.GRANULARITY_DAY) {
                    if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                        thresholdId = controller.dailyDomainDepositThreshold.id;
                    } else if(type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                        thresholdId = controller.dailyDomainThreshold.id;
                    }
                } else if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
                    if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                        thresholdId = controller.weeklyDomainDepositThreshold.id;
                    } else if(type === domainLimitsRest.LIMIT_TYPE_LOSS){
                        thresholdId = controller.weeklyDomainThreshold.id;
                    }
                } else if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
                    if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                        thresholdId = controller.monthlyDomainDepositThreshold.id;
                    } else {
                        thresholdId = controller.monthlyDomainThreshold.id;
                    }
                } else if (granularity === domainLimitsRest.GRANULARITY_YEAR) {
                    if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                        thresholdId = controller.annualDomainDepositThreshold.id;
                    } else {
                        thresholdId = controller.annualDomainThreshold.id;
                    }
                }

                var disableLossLimitThreshold = userThresholdRest.disableLossLimitThreshold($scope.domainName, thresholdId).then(function (response) {
                    return response;
                });
                promises.push(disableLossLimitThreshold);

            $q.all(promises).then(function (results) {
                var resultsLength = Object.keys(results).length;
                if (resultsLength === 0) {
                } else if (resultsLength === 1) {
                    if (angular.isDefined(results[0]) && (results[0].id > 0)) {
                        controller.changelogs.reload++;
                        controller.reload();
                        notify.success('UI_NETWORK_ADMIN.LIMITS.DOMAIN.REMOVE.LIMIT.SUCCESS');
                    } else {
                        // notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                        //TODO: add appropriate error message.
                    }
                } else if (resultsLength === 2) {
                    if ((angular.isDefined(results[0]) && (results[0].status === 0))
                        && (angular.isDefined(results[1]) && (results[1].id > 0))) {
                        controller.changelogs.reload++;
                        controller.reload();
                        notify.success('UI_NETWORK_ADMIN.LIMITS.DOMAIN.REMOVE.LIMIT.SUCCESS');
                    } else if ((angular.isDefined(results[0]) && (results[0].status === 0))
                        || (angular.isDefined(results[1]) && (results[1].id > 0))) {
                        controller.changelogs.reload++;
                        controller.reload();
                        // notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                        // $uibModalInstance.close();
                    } else {
                        // notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                        // $uibModalInstance.close();
                    }
                }
            });
            controller.reload();
            controller.refresh();
        };


        controller.refresh = function () {
            domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_DAY, domainLimitsRest.LIMIT_TYPE_WIN).then(function (response) {
                $scope.dailyWinLimit = (response.amount != undefined) ? response.amount / 100 : null;
            }).catch(function () {
                errors.catch('', false);
            });
            domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_WEEK, domainLimitsRest.LIMIT_TYPE_WIN).then(function (response) {
                $scope.weeklyWinLimit = (response.amount != undefined) ? response.amount / 100 : null;
            }).catch(function () {
                errors.catch('', false);
            });
            domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_MONTH, domainLimitsRest.LIMIT_TYPE_WIN).then(function (response) {
                $scope.monthlyWinLimit = (response.amount != undefined) ? response.amount / 100 : null;
            }).catch(function () {
                errors.catch('', false);
            });
            domainLimitsRest.findDomainLimit($scope.domainName, domainLimitsRest.GRANULARITY_YEAR, domainLimitsRest.LIMIT_TYPE_WIN).then(function (response) {
                $scope.annualWinLimit = (response.amount != undefined) ? response.amount / 100 : null;
            }).catch(function () {
                errors.catch('', false);
            });
        }

        controller.refresh();
    }]
);
