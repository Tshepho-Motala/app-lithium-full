'use strict'

angular.module('lithium').controller('PlayerLoginEventsController', ['user', '$translate', 'DocumentGenerationRest', '$dt', 'DTOptionsBuilder', '$filter', '$rootScope',
	function(user, $translate, documentRest, $dt, DTOptionsBuilder, $filter, $rootScope) {
		var controller = this;
		controller.legendCollapsed = true;
        controller.model = {};
        controller.fields = [
            {
                className: 'col-md-4 col-xs-12',
                key: 'dateRangeStart',
                type: 'datepicker',
                optionsTypes: ['editable'],
                templateOptions: {
                    label: 'Date: Range Start',
                    required: false,
                    datepickerOptions: {
                        format: 'dd/MM/yyyy'
                    },
                    onChange: function() { controller.fields[1].templateOptions.datepickerOptions.minDate = controller.model.dateRangeStart; }
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.DATERANGESTART.LABEL" | translate',
                }
            },
            {
                className: 'col-md-4 col-xs-12',
                key: 'dateRangeEnd',
                type: 'datepicker',
                optionsTypes: ['editable'],
                templateOptions: {
                    label: 'Date: Range End',
                    required: false,
                    datepickerOptions: {
                        format: 'dd/MM/yyyy'
                    },
                    onChange: function() { controller.fields[0].templateOptions.datepickerOptions.maxDate = controller.model.dateRangeEnd; }
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.DATERANGEEND.LABEL" | translate',
                }
            }
        ];

        controller.formatDate = function(date) {
            return $filter('date')(date, 'yyyy-MM-dd');
        }

        var filterApplied = false;
        controller.applyFilter = function(toggle) {
            if (toggle === true) {
                controller.toggleLegendCollapse();
            }
            filterApplied = true;
            controller.refresh();
        }

        controller.toggleLegendCollapse = function() {
            controller.legendCollapsed = !controller.legendCollapsed;
        }

        controller.resetFilter = function(collapse) {
            if (collapse) {
                controller.toggleLegendCollapse();
            }
            controller.model.dateRangeStart = null;
            controller.model.dateRangeEnd = null;
            controller.applyFilter(true);
        }

		var baseUrl = 'services/service-user/'+user.domain.name+'/users/'+user.id+'/loginevents/table';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);

		controller.loginEventsTable = $dt.builder()
            .column($dt.columnformatdatetime('date').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.DATE')))
            .column($dt.columnformatcountryflag('countryCode').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.COUNTRY')))
            .column($dt.column('ipAddress').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.IPADDRESS')))
            .column($dt.column('successful').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.SUCCESSFUL')))
            .column($dt.column('comment').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.COMMENT')))
            .column($dt.columnformatdatetime('logout').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.LOGOUT')))
            .column($dt.column('duration').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.DURATION')))
            .column($dt.column('providerAuthClient').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.AUTHCLIENT')))
            .column($dt.column('browser').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.BROWSER')))
            .column($dt.column('os').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.OS')))
            .column($dt.column('id').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.LOGIN_ID')))
            .column($dt.column('userAgent').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.USERAGENT')))
            .options(
                {
                    url: baseUrl,
                    type: 'GET',
                    data: function(d) {
                        if (filterApplied) {
                            d.start = 0;
                            filterApplied = false;
                        }
                        d.dateRangeStart = (controller.model.dateRangeStart !== undefined && controller.model.dateRangeStart !== null) ? controller.formatDate(controller.model.dateRangeStart) : null;
                        d.dateRangeEnd = (controller.model.dateRangeEnd !== undefined && controller.model.dateRangeEnd !== null) ? controller.formatDate(controller.model.dateRangeEnd) : null;
                    }
                },
                null,
                dtOptions,
                null)
            .build();

        controller.refresh = function() {
            controller.loginEventsTable.instance.reloadData(function(){}, false);
        }

        controller.reference = () => {
            const reference = localStorage.getItem(`export_loginevents_reference_${user.guid}`)

            if(angular.isUndefinedOrNull(reference)) {
                return null;
            }

            return reference
        }
        $rootScope.provide['csvGeneratorProvider'] = {}

        $rootScope.provide.csvGeneratorProvider.generate = async (config) => {
            const response = await documentRest.generateDocument({
                ...config,
            });

            localStorage.setItem(`export_loginevents_reference_${user.guid}`, response.reference);
            return response
        }

        $rootScope.provide.csvGeneratorProvider.progress = async (config) => {
            return documentRest.documentStatus(controller.reference());
        }
        $rootScope.provide.csvGeneratorProvider.cancelGeneration = async (reference) => {
            return documentRest.documentCancel(reference);
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

        $rootScope.provide.csvGeneratorProvider.getConfig = () => {
            return {
                domain: user.domain.name,
                provider: 'service-csv-provider-user',
                page: 0,
                size: 10,
                parameters: $rootScope.apiParams(),
                role: 'PLAYER_INFO_DATA',
                reference: controller.reference()
            };
        }

        $rootScope.apiParams = () => {
            const params = {
                userGuid: user.guid,
                record_type: 'login-events'
            };

            if (controller.model.dateRangeStart) {
                const date = controller.model.dateRangeStart
                params.startDate = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate())
            }

            if (controller.model.dateRangeEnd) {
                const date = controller.model.dateRangeEnd
                params.endDate = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate())
            }

            return params
        }

        window.VuePluginRegistry.loadByPage('dashboard/csv-export')
    }
]);
