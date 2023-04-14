'use strict'

angular.module('lithium').controller('AutoRestrictionRulesetsListController', ['$state', '$rootScope', '$scope', '$filter', '$dt', 'DTOptionsBuilder', '$translate', '$userService',
    function($state, $scope, $rootScope, $filter, $dt, DTOptionsBuilder, $translate, $userService) {
        var controller = this;

        controller.legendCollapsed = true;
        controller.model = {};
        controller.fields = [
            {
                className: 'col-md-3 col-xs-12',
                key: 'enabled',
                type: 'ui-select-single',
                templateOptions : {
                    label: 'Enabled',
                    valueProp: 'value',
                    labelProp: 'label',
                    optionsAttr: 'ui-options', ngOptions: 'ui-options',
                    options: [
                        {value: 0, label: 'Both'},
                        {value: 1, label: 'Enabled'},
                        {value: 2, label: 'Disabled'},
                    ]
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.LIST.FIELDS.ENABLED.NAME" | translate'
                }
            }, {
                className: 'col-md-3 col-xs-12',
                key: 'name',
                type: 'input',
                templateOptions: {
                    label: 'Name',
                    type: 'text'
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.LIST.FIELDS.NAME.NAME" | translate'
                }
            }, {
                className: 'col-md-3 col-xs-12',
                key: 'lastUpdatedStart',
                type: 'datepicker',
                optionsTypes: ['editable'],
                templateOptions: {
                    label: 'Last Updated: Range Start',
                    required: false,
                    datepickerOptions: {
                        format: 'dd/MM/yyyy'
                    },
                    onChange: function() { controller.fields[3].templateOptions.datepickerOptions.minDate = controller.model.lastUpdatedStart; }
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.LIST.FIELDS.LASTUPDATEDSTART.NAME" | translate'
                }
            }, {
                className: 'col-md-3 col-xs-12',
                key: 'lastUpdatedEnd',
                type: 'datepicker',
                optionsTypes: ['editable'],
                templateOptions: {
                    label: 'Last Updated: Range End',
                    required: false,
                    datepickerOptions: {
                        format: 'dd/MM/yyyy'
                    },
                    onChange: function() { controller.fields[2].templateOptions.datepickerOptions.maxDate = controller.model.lastUpdatedEnd; }
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.LIST.FIELDS.LASTUPDATEDEND.NAME" | translate'
                }
            }
        ];

        controller.domains = $userService.domainsWithAnyRole(["ADMIN", "AUTORESTRICTION_RULESETS_VIEW"]);

        controller.domainSelect = function() {
            controller.selectedDomains = [];
            for (var d = 0; d < controller.domains.length; d++) {
                if (controller.domains[d].selected)
                    controller.selectedDomains.push(controller.domains[d].name);
            }
            if (controller.selectedDomains.length == controller.domains.length) {
                controller.selectedDomainsDisplay = "Domain";
            } else {
                controller.selectedDomainsDisplay = "Selected (" + controller.selectedDomains.length + ")";
            }
        };

        controller.domainSelectAll = function() {
            for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
            controller.domainSelect();
        };

        controller.domainSelectAll();

        controller.commaSeparatedSelectedDomains = function() {
            var s = '';
            for (var i = 0; i < controller.selectedDomains.length; i++) {
                if (s.length > 0) s += ',';
                s += controller.selectedDomains[i];
            }
            return s;
        }

        controller.toggleLegendCollapse = function() {
            controller.legendCollapsed = !controller.legendCollapsed;
        }

        controller.resetFilter = function(collapse) {
            if (collapse) {
                controller.toggleLegendCollapse();
            }
            controller.model.enabled = null;
            controller.model.name = null;
            controller.model.lastUpdatedStart = null;
            controller.model.lastUpdatedEnd = null;
            controller.applyFilter(true);
        }

        controller.applyFilter = function(toggle) {
            if (toggle === true) {
                controller.toggleLegendCollapse();
            }
            controller.refresh();
        }

        controller.formatDate = function(date) {
            return $filter('date')(date, 'yyyy-MM-dd', 'GMT');
        }

        controller.isEnabled = function(isEnabled) {
            if (isEnabled === undefined || isEnabled === null) return null; // Both
            switch (isEnabled) {
                case 0:
                    return null; // Both
                case 1:
                    return true; // Enabled entries
                case 2:
                    return false; // Disabled entries
            }
        }

        var baseUrl = "services/service-limit/backoffice/auto-restriction/rulesets/table?1=1";
        var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
        controller.autoRestrictionRulesetsTable = $dt.builder()
        .column($dt.column('id').withTitle('ID').notVisible())
        .column($dt.column('name').withTitle($translate('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.NAME.NAME')))
        .column(
            $dt.linkscolumn(
                "",
                [
                    {
                        permission: "autorestriction_*",
                        permissionType: "any",
                        permissionDomain: function(data) {
                            return data.domain.name;
                        },
                        title: "GLOBAL.ACTION.OPEN",
                        href: function(data) {
                            return $state.href("dashboard.restrictions.autorestrictions.rulesets.view", { id:data.id });
                        }
                    }
                ]
            )
        )
        .column($dt.column('restrictionSet.name').withTitle($translate('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.RESTRICTION.NAME')))
        .column($dt.column('domain.name').withTitle($translate('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.DOMAIN.NAME')))
        .column(
            $dt.labelcolumn(
                $translate('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.ENABLED.NAME'),
                [{lclass: function(data) {
                        if (data.enabled) return 'success';
                        return 'danger';
                    },
                    text: function(data) {
                        if (data.enabled) return 'UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.ENABLED.NAME';
                        return 'UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.DISABLED.NAME';
                    },
                    uppercase: true
                }]
            )
        )
        .column($dt.columnformatdatetime('lastUpdated').withTitle($translate('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.LASTUPDATED.NAME')))
        .column($dt.column('lastUpdatedBy').withTitle($translate('UI_NETWORK_ADMIN.RESTRICTIONS.AUTORESTRICTIONS.RULESETS.FIELDS.LASTUPDATEDBY.NAME')))
        .options(
            {
                url: baseUrl,
                type: 'GET',
                data: function(d) {
                    d.domains = controller.commaSeparatedSelectedDomains(),
                    d.enabled = controller.isEnabled(controller.model.enabled),
                    d.name = controller.model.name,
                    d.lastUpdatedStart = (controller.model.lastUpdatedStart !== undefined && controller.model.lastUpdatedStart !== null) ? controller.formatDate(controller.model.lastUpdatedStart) : null,
                    d.lastUpdatedEnd = (controller.model.lastUpdatedEnd !== undefined && controller.model.lastUpdatedEnd !== null) ? controller.formatDate(controller.model.lastUpdatedEnd) : null;
                }
            },
            null,
            dtOptions,
            null
        )
        .build();

        controller.refresh = function() {
            controller.autoRestrictionRulesetsTable.instance.rerender(true);
        }


        // Domain select
        $rootScope.provide.dropDownMenuProvider['domainList']  = () => {
            return controller.domains
        }
        $rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
            const domainNames = []
            data.forEach(el=> {
                domainNames.push(el.name)
            })
            controller.selectedDomains = domainNames
            controller.selectedDomainsCommaSeperated = domainNames.join(',')
            controller.refresh();
        }
        window.VuePluginRegistry.loadByPage("DomainSelect")
    }
]);
