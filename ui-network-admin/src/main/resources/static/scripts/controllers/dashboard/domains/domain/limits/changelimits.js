'use strict';

angular.module('lithium')
    .controller('ChangeDomainLimitsModal',
        ['$uibModalInstance', 'domain', 'dailyLimit', 'weeklyLimit', 'monthlyLimit', 'annualLimit', 'granularity', 'type', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'domainLimitsRest', 'userThresholdRest', 'thresholds', '$q',
            function ($uibModalInstance, domain, dailyLimit, weeklyLimit, monthlyLimit, annualLimit, granularity, type, $scope, notify, errors, bsLoadingOverlayService, domainLimitsRest, userThresholdRest, thresholds, $q) {
                var controller = this;
                controller.options = {};
                controller.model = {};
                controller.fields = [];

                controller.init = function () {
                    if (granularity === domainLimitsRest.GRANULARITY_DAY) {
                        controller.model.dailyLimit = dailyLimit;
                        if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.DAILYWIN';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.DAILYWINLIMIT.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.DAILYWINLIMIT.DESCRIPTION';
                        } else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.DAILYLOSS';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.DAILYLOSSLIMIT.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.DAILYLOSSLIMIT.DESCRIPTION';
                        } else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.THRESHOLD.DAILYTHRESH';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.DAILYDEPOSITTRIGGER.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.DAILYDEPOSITTRIGGER.DESCRIPTION';
                        }
                    } else if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
                        controller.model.weeklyLimit = weeklyLimit;
                        if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.WEEKLYWIN';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.WEEKLYWINLIMIT.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.WEEKLYWINLIMIT.DESCRIPTION';
                        } else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.WEEKLYLOSS';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.WEEKLYLOSSLIMIT.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.WEEKLYLOSSLIMIT.DESCRIPTION';
                        } else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.THRESHOLD.WEEKLYTHRESH';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.WEEKLYDEPOSITTRIGGER.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.WEEKLYDEPOSITTRIGGER.DESCRIPTION';
                        }
                    } else if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
                        controller.model.monthlyLimit = monthlyLimit;
                        if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.MONTHLYWIN';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.MONTHLYWINLIMIT.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.MONTHLYWINLIMIT.DESCRIPTION';
                        } else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.MONTHLYLOSS';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.MONTHLYLOSSLIMIT.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.MONTHLYLOSSLIMIT.DESCRIPTION';
                        } else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                            controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.THRESHOLD.MONTHLYTHRESH';
                            controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.MONTHLYDEPOSITTRIGGER.LABEL';
                            controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.MONTHLYDEPOSITTRIGGER.DESCRIPTION';
                        }
										} else if (granularity === domainLimitsRest.GRANULARITY_YEAR) {
												controller.model.annualLimit = annualLimit;
												if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
													controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ANNUALWIN';
													controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.ANNUALWINLIMIT.LABEL';
													controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.ANNUALWINLIMIT.DESCRIPTION';
												} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
													controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ANNUALLOSS';
													controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.ANNUALLOSSLIMIT.LABEL';
													controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.ANNUALLOSSLIMIT.DESCRIPTION';
												} else if (type === domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
													controller.title = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.THRESHOLD.ANNUALTHRESH';
													controller.label = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.ANNUALDEPOSITTRIGGER.LABEL';
													controller.description = 'UI_NETWORK_ADMIN.LIMITS.DOMAIN.ANNUALDEPOSITTRIGGER.DESCRIPTION';
												}
                    }
                    controller.thresholdTitle = 'UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.THRESHOLD_TITTLE';
                    controller.thresholdLabel = 'UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.THRESHOLD_LABEL';
                    controller.thresholdDescription = 'UI_NETWORK_ADMIN.PLAYER.LOSS_LIMITS.LABELS.THRESHOLD_DESCRIPTION';

                    var field = 'dailyLimit';
                    if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
                        field = 'weeklyLimit';
                    } else if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
                        field = 'monthlyLimit';
                    } else if (granularity === domainLimitsRest.GRANULARITY_YEAR) {
												field = 'annualLimit';
										}
                    controller.fields.push({
                        className: 'col-xs-6',
                        key: field,
                        type: 'ui-money-mask',
                        templateOptions: {
                            label: '',
                            description: '',
                            placeholder: '',
                            type: 'text',
                            required: true,
                            addFormControlClass: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"' + controller.label + '"' + ' | translate',
                            'templateOptions.placeholder': '"' + controller.label + '"' + ' | translate',
                            'templateOptions.description': '"' + controller.description + '"' + ' | translate'
                        }
                    });
                    if (angular.isDefined(thresholds)) {
                        controller.model.threshold = thresholds.current.percentage;
                    }

                    if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                        controller.fields.push({
                            className: 'col-xs-6',
                            key: 'threshold',
                            type: 'ui-money-mask',
                            templateOptions: {
                                label: '',
                                description: '',
                                placeholder: '',
                                type: 'text',
                                required: true,
                                addFormControlClass: true
                            },
                            expressionProperties: {
                                'templateOptions.label': '"' + controller.thresholdLabel + '"' + ' | translate',
                                'templateOptions.placeholder': '"' + controller.thresholdLabel + '"' + ' | translate',
                                'templateOptions.description': '"' + controller.thresholdDescription + '"' + ' | translate'
                            }
                        });
                    }

                }

                controller.init();

                controller.referenceId = 'changedomainlimits-overlay';

                controller.submit = function () {
                    bsLoadingOverlayService.start({referenceId: controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        return false;
                    }
                    var limit = null;
                    if (granularity === domainLimitsRest.GRANULARITY_DAY) {
                        limit = controller.model.dailyLimit;
                    } else if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
                        limit = controller.model.weeklyLimit;
                    } else if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
                        limit = controller.model.monthlyLimit;
                    } else if (granularity === domainLimitsRest.GRANULARITY_YEAR) {
												limit = controller.model.annualLimit;
										}

                    if (type===domainLimitsRest.TYPE_DEPOSIT_LIMIT){

                    } else {
                        limit = Math.round(limit * 100)
                    }


                    var promises = [];
                    if (type===domainLimitsRest.TYPE_DEPOSIT_LIMIT) {
                        var threshold = userThresholdRest.saveLossLimitThreshold(domain.name,((angular.isDefined(thresholds)) ? thresholds.id : null),null,limit,'TYPE_DEPOSIT_LIMIT', granularity,null,null).then(function (response) {
                            return response;
                        }).catch(function () {
                            errors.catch('', false);
                        });
                        promises.push(threshold);
                    } else {
                        var domainLimit = domainLimitsRest.setDomainLimit(domain.name, granularity, limit, type).then(function (response) {
                            return response;
                        }).catch(function () {
                            errors.catch('', false);
                        });
                        promises.push(domainLimit);
                    }

                    if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
                        var thresholdUpdate = userThresholdRest.saveLossLimitThreshold(domain.name, ((angular.isDefined(thresholds)) ? thresholds.id : null), controller.model.threshold,null,'TYPE_LOSS_LIMIT', granularity, null, null).then(function (response) {
                            return response;
                        }).catch(function () {
                            errors.catch('Could not save threshold.', false);
                        });
                        promises.push(thresholdUpdate);
                    }

                    $q.all(promises).then(function (results) {
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        var resultsLength = Object.keys(results).length;
                        if (resultsLength === 0) {
                        } else if (resultsLength === 1) {
                            if (angular.isDefined(results[0]) && (results[0].id > 0)) {
                                notify.success('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.SUCCESS');
                                $uibModalInstance.close(results[0]);
                            } else {
                                notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                                $uibModalInstance.close();
                            }
                        } else if (resultsLength === 2) {
                            if ((angular.isDefined(results[0]) && (results[0].id > 0))
                                && (angular.isDefined(results[1]) && (results[1].id > 0))) {
                                notify.success('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.SUCCESS');
                                $uibModalInstance.close(results[0]);
                            } else if ((angular.isDefined(results[0]) && (results[0].id > 0))
                                || (angular.isDefined(results[1]) && (results[1].id > 0))) {
                                notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                                $uibModalInstance.close();
                            } else {
                                notify.error('UI_NETWORK_ADMIN.LIMITS.DOMAIN.CHANGE.LIMIT.ERROR');
                                $uibModalInstance.close();
                            }
                        }
                    });
                };

                controller.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            }]);
