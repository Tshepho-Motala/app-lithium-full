'use strict'

angular.module('lithium').controller('ThresholdDepositListReport', ['errors', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$scope', '$state' ,'$rootScope', '$userService', 'rest-domain'
    ,'bsLoadingOverlayService','userThresholdHistoryRest','$http','$stateParams','DocumentGenerationRest', 'notify', 'UserRest',
    function(errors, $translate, $dt, DTOptionsBuilder, $filter, $scope, $state, $rootScope, $userService, restDomain,bsLoadingOverlayService
    ,userThresholdHistoryRest, $http, $stateParams, documentRest, notify, userRest) {
        var controller = this;

        controller.model = {};
        controller.model.startDateTime = moment(moment().startOf('day'), "YYYY-MM-DD HH:mm").toDate();
        controller.model.endDateTime = moment(moment().endOf('day'), "YYYY-MM-DD HH:mm").toDate();
        controller.model.selectedDomain = $stateParams.domainName;

        controller.legendCollapsed = true;
        controller.fields = [{
            className: "form-row row",
            fieldGroup: [{
                key: "startDateTime",
                className:"form-check form-check-inline game-time-picker-formatter",
                type: "datepicker",
                optionsTypes: ['editable'],
                templateOptions: {
                    label: "", description: "", placeholder: "",
                    required: false,
                    datepickerOptions: {
                        format:'yyyy/MM/dd'
                    },
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.THRESHOLD_HEADERS.START_DATE" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.THRESHOLD_HEADERS.START_DATE" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.THRESHOLD_HEADERS.START_DESCRIPTION" | translate'
                },
            },
            {
                key: "startDateTime",
                className:"form-check form-check-inline",
                type: "timepicker",
                optionsTypes: ['editable'],
                templateOptions: {
                    description: "", placeholder: "",
                    formCheck: 'inline',
                    required: false,
                    datepickerOptions: {
                        format: 'HH:mm'
                    }
                }
            }]
        },{
            className: "form-row row",
            fieldGroup: [{
                key: "endDateTime",
                className: "form-check form-check-inline game-time-picker-formatter",
                type: "datepicker",
                optionsTypes: ['editable'],
                templateOptions: {
                    label: "", description: "", placeholder: "",
                    required: false,
                    datepickerOptions: {
                        format: 'yyyy/MM/dd'

                    },
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.THRESHOLD_HEADERS.END_DATE" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.THRESHOLD_HEADERS.END_DATE" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.THRESHOLD_HEADERS.END_DESCRIPTION" | translate'
                },
            },
            {
                key: "endDateTime",
                className: "form-check form-check-inline",
                type: "timepicker",
                optionsTypes: ['editable'],
                templateOptions: {
                    description: "", placeholder: "",
                    formCheck: 'inline',
                    required: false,
                    datepickerOptions: {
                        format: 'HH:mm'
                    }
                }
            }]
        },{
            className: 'col-md-3 col-xs-12',
            key: 'thresholdtype',
            type: 'ui-select-single',
            templateOptions : {
                label: "Threshold Hit",
                valueProp: 'value',
                labelProp: 'label',
                optionsAttr: 'ui-options', ngOptions: 'ui-options',
                options: [
                    {value: 0, label: 'All'},
                    // {value: 1, label: 'Annual'},
                    {value: 2, label: 'Monthly'},
                    {value: 3, label: 'Daily'},
                    {value: 4, label: 'Weekly'}
                ]
            },
            expressionProperties: {
                // 'templateOptions.label': '"UI_NETWORK_ADMIN.DEPOSIT_THRESHOLDS.FILTERS.GRANULARITY.LABEL" | translate'
            }
        }
        ]

        controller.searchUsers = function(search) {
            return userRest.searchAllPlayers(search).then(function(searchResult) {
                return searchResult.plain();
            }).catch(function(error) {
                errors.catch("", false)(error)
            });
        };

        controller.toggleLegendCollapse = function() {
            controller.legendCollapsed = !controller.legendCollapsed;
        }
        controller.resetFilter = function() {
            if (controller.legendCollapsed) {
                controller.toggleLegendCollapse();
            }
            controller.model.successful = 0;
            controller.selectedUser = undefined;
            controller.selectedEmail = undefined;
            controller.model.startDateTime = moment(moment().startOf('day'), "YYYY-MM-DD HH:mm").toDate();
            controller.model.endDateTime = moment(moment().endOf('day'), "YYYY-MM-DD HH:mm").toDate();
            controller.model.playerGuid = null;
            controller.model.thresholdtype = null;
            controller.applyFilter();
        }
        controller.applyFilter = function() {
            controller.toggleLegendCollapse();
            controller.refreshThresholdHistoryTable(controller.model.selectedDomain);
        }
        controller.refresh = function() {
            controller.refreshThresholdHistoryTable(controller.model.selectedDomain);
        }
        controller.formatDate = function (date) {
            date === null ? new Date() : date;
            return $filter('date')(date, 'yyyy-MM-dd HH:mm');
        }

        var baseUrl = "services/service-user-threshold/backoffice/threshold/warnings/"+controller.model.selectedDomain+"/v1/find";
        var dtOptions = DTOptionsBuilder.newOptions()
        .withOption('stateSave', false)
        .withOption('order', [[0, 'asc']])
        // .withOption('paging', false)
        // .withOption('changeLength', false)
        .withOption('searching', false);

        // var baseUrl = 'services/service-user-provider-threshold/backoffice/threshold-history/table';
        // var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('bFilter', false).withOption('order', [3, 'desc']);
        controller.thresholdHistoryTable = $dt.builder()
        .column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_LABELS.PLAYER_PROTECTION.EVENT")).notVisible())
        .column($dt.column('user.username').withTitle($translate('UI_NETWORK_ADMIN.THRESHOLD_HEADERS.PLAYER_NAME')))
        .column($dt.column('user.guid').withTitle($translate('UI_NETWORK_ADMIN.THRESHOLD_HEADERS.ACCOUNT_ID')))
        .column($dt.columnperiod('thresholdRevision.threshold.granularity').withTitle($translate("Threshold Hit")))
        .column($dt.columnformatdatetime('thresholdHitDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_LABELS.PLAYER_PROTECTION.DATE")))

        .column($dt.column('id').withTitle($translate("Loss Limit")).renderWith(function (data, type, row) {
            switch (row.thresholdRevision.threshold.granularity) {
                case '1':
                    return $filter('currency')(row.annualLimit, row.defaultDomainCurrencySymbol, 2);
                case '2':
                    return $filter('currency')(row.monthlyLimit, row.defaultDomainCurrencySymbol, 2);
                case '3':
                    return $filter('currency')(row.dailyLimit, row.defaultDomainCurrencySymbol, 2);
                case '4':
                    return $filter('currency')(row.weeklyLimit, row.defaultDomainCurrencySymbol, 2);
            }
        }))
        .column($dt.column('id').withTitle($translate("Loss Limit Used")).renderWith(function (data, type, row, meta) {
            switch (row.thresholdRevision.threshold.granularity) {
                case '1':
                    return $filter('currency')(row.annualLimitUsed, row.defaultDomainCurrencySymbol, 2);
                case '2':
                    return $filter('currency')(row.monthlyLimitUsed, row.defaultDomainCurrencySymbol, 2);
                case '3':
                    return $filter('currency')(row.dailyLimitUsed, row.defaultDomainCurrencySymbol, 2);
                case '4':
                    return $filter('currency')(row.weeklyLimitUsed, row.defaultDomainCurrencySymbol, 2);
            }
        }))
        .column($dt.column('id').withTitle($translate("Threshold")).renderWith(function (data, type, row, meta) {
            if (row.thresholdRevision.amount !== null) return $filter('currency')(row.thresholdRevision.amount, row.defaultDomainCurrencySymbol, 2);
            return "";
        }))
        .column($dt.column('id').withTitle($translate("Dep Amount")).renderWith(function (data, type, row, meta) {
            if (row.amount !== null) return $filter('currency')(row.amount, row.defaultDomainCurrencySymbol, 2);
            return "";
        }))


        .column($dt.column('id').withTitle($translate('Dep Amount (Period)')).renderWith(function (data, type, row, meta) {
            return $filter('currency')(row.depositAmount, row.defaultDomainCurrencySymbol, 2);
        }))
        .column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.THRESHOLD_HEADERS.WITHDRAWAL_AMOUNT")).renderWith(function (data, type, row, meta) {
            return $filter('currency')(row.withdrawalAmount, row.defaultDomainCurrencySymbol, 2);
        }))
        .column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.THRESHOLD_HEADERS.NET_LIFE_DEPOSIT_AMOUNT")).renderWith(function (data, type, row, meta) {
            return $filter('currency')(row.netLifetimeDepositAmount, row.defaultDomainCurrencySymbol, 2);
        }))
        .column($dt.columnformatdatetime('accountCreationDate').withTitle($translate("UI_NETWORK_ADMIN.THRESHOLD_HEADERS.ACCOUNT_CREATION_DATE")))
        .options({
              url: baseUrl,
              type: 'POST',
              data: function(d, e) {
                  d.domainName = controller.model.selectedDomain;
                  d.dateStart = new Date(controller.model.startDateTime).toISOString();
                  d.dateEnd = new Date(controller.model.endDateTime).toISOString();
                  d.domains = [controller.model.selectedDomain];
                  if (controller.selectedUser) d.playerGuid = controller.selectedUser.guid;
                  d.typeName = "TYPE_DEPOSIT_LIMIT";
                  if ((controller.model.thresholdtype) && (controller.model.thresholdtype !== 0)) d.granularity = controller.model.thresholdtype;
              }
          },
          null,
          dtOptions,
          null
        )
        .build();

        controller.refreshThresholdHistoryTable = function(domainName) {
            baseUrl = "services/service-user-threshold/backoffice/threshold/warnings/"+domainName+"/v1/find";
            baseUrl += "?domainName="+controller.model.selectedDomain;
            if (controller.selectedUser) baseUrl += "&playerGuid="+controller.selectedUser.guid;
            baseUrl += "&dateStart="+new Date(controller.model.startDateTime).toISOString();
            baseUrl += "&dateEnd="+new Date(controller.model.endDateTime).toISOString();
            if ((controller.model.thresholdtype) && (controller.model.thresholdtype !== 0)) baseUrl += "&granularity="+controller.model.thresholdtype;
            controller.thresholdHistoryTable.instance._renderer.options.ajax.url = baseUrl;
            controller.thresholdHistoryTable.instance._renderer.options.ajax.type = 'POST';
            // controller.thresholdHistoryTable.instance.reloadData(() => {}, false);
            controller.thresholdHistoryTable.instance.rerender(true);
        };
        $scope.reference = () => {
            if (controller.model?.selectedDomain) {
                const reference = localStorage.getItem(`export_reference_thresholds_dep_${controller.model.selectedDomain}`)
                if (angular.isUndefinedOrNull(reference)) {
                    return null;
                }
                return reference.replace('export_reference_thresholds_dep_', '')
            }
            const reference = localStorage.getItem(`export_reference_thresholds_dep_list`)

            if (angular.isUndefinedOrNull(reference)) {
                return null;
            }

            return reference.replace('export_reference_thresholds_dep_list', '')
        }

        $scope.apiParams = () => {
            const params = {};
            params.startDateTime = controller.formatDate(controller.model.startDateTime);
            params.endDateTime = controller.formatDate(controller.model.endDateTime);
            params.typeName = "TYPE_DEPOSIT_LIMIT";
            if (controller.selectedUser) params.playerGuid = controller.selectedUser.guid;
            if ((controller.model.thresholdtype) && (controller.model.thresholdtype !== 0)) params.granularity = controller.model.thresholdtype;
            params.domain = controller.model.selectedDomain;
            return params;
        }

        $rootScope.provide['csvGeneratorProvider'] = {}

        $rootScope.provide.csvGeneratorProvider.generate = async (config) => {
            const response = await documentRest.generateDocument({
                ...config,
            });
            if (controller.model?.selectedDomain) {
                localStorage.setItem(`export_reference_thresholds_dep_${controller.model.selectedDomain}`, response.reference);
            } else {
                localStorage.setItem(`export_reference_thresholds_dep_list`, response.reference);
            }
            return response
        }

        $rootScope.provide.csvGeneratorProvider.progress = async (config) => {
            return documentRest.documentStatus($scope.reference());
        }

        $rootScope.provide.csvGeneratorProvider.download = (reference) => {
            const a = document.createElement("a")
            const url = `services/service-document-generation/document/${reference}/download`;
            a.href = url;
            a.setAttribute('download', reference)
            document.body.appendChild(a);
            a.click();

            setTimeout(() => document.body.removeChild(a), 1500)
        }


        $rootScope.provide.csvGeneratorProvider.getConfig =  () => {
            const config =  {
                domain: controller.model.selectedDomain,
                provider: 'service-csv-provider-threshold',
                page: 0,
                size: 10,
                parameters: $scope.apiParams(),
                reference:  $scope.reference(),

            };
            return config
        }
        window.VuePluginRegistry.loadByPage('dashboard/csv-export')
    }
]);
