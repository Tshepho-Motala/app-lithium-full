'use strict';

angular.module('lithium')
  .controller('ProgressiveBalancesListController', ['domainName', 'rest-provider', 'GameSuppliersRest', '$scope', '$stateParams', '$state', '$translate', 'errors', 'notify', '$dt', 'DTOptionsBuilder',
      function(domainName, $providerRest, $gameSupplierRest, $scope, $stateParams, $state, $translate, errors, notify, $dt, DTOptionsBuilder) {
          var controller = this;
          controller.domainName = domainName;

          var filterApplied = true;
          controller.legendCollapsed = true;
          controller.model = {};

          function arrayAsString(arr, fieldName) {
              var str = "";
              angular.forEach(arr, function (d) {
                  if(str != "")
                    str += "," + d[fieldName];
                  else
                      str += d[fieldName];
              });
              return str;
          }


          if (!angular.isUndefined(controller.domainName)) {
              controller.fields = [
                  {
                      className: 'col-md-4 col-xs-12',
                      key: 'providers',
                      type: "ui-select-multiple",
                      templateOptions: {
                          label: 'Provider',
                          description: '',
                          placeholder: '',
                          valueProp: 'url',
                          labelProp: 'url',
                          optionsAttr: 'ui-options',
                          ngOptions: 'ui-options',
                          options: []
                      },
                      expressionProperties: {
                          'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.PROVIDER.LABEL" | translate',
                      },
                      controller: ['$scope', function ($scope) {
                          $providerRest.listByDomainAndType(controller.domainName, 'CASINO').then(function (response) {
                              $scope.options.templateOptions.options = response;
                              return response;
                          });
                      }]
                  },
                  {
                      className: 'col-md-4 col-xs-12',
                      key: 'gameSupplier',
                      type: "ui-select-single",
                      templateOptions: {
                          label: 'Supplier',
                          description: '',
                          placeholder: '',
                          valueProp: 'name',
                          labelProp: 'name',
                          optionsAttr: 'ui-options',
                          ngOptions: 'ui-options',
                          options: []
                      },
                      expressionProperties: {
                          'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.SUPPLIER.LABEL" | translate',
                      },
                      controller: ['$scope', function ($scope) {
                          $gameSupplierRest.findByDomain(controller.domainName).then(function (response) {
                              $scope.options.templateOptions.options = response;
                              return response;
                          });
                      }]
                  }
              ]

              var baseUrl = 'services/service-games/backoffice/jackpot-feeds/progressive/' + controller.domainName + '/table?1=1';
              var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false)
                .withOption('order', [[0, 'asc']]);
              controller.table = $dt.builder()
                .column($dt.column('game.name')
                  .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.GAME_NAME.LABEL')))
                .column($dt.column('amount')
                  .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.AMOUNT.LABEL')))
                .column($dt.column('currency.code')
                  .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.CURRENCY.LABEL')))
                .column($dt.column('game.gameSupplier.name')
                  .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.SUPPLIER.LABEL')))
                .column($dt.column('game.providerGuid')
                  .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.PROVIDER.LABEL')))
                .column($dt.column('progressiveId')
                  .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.PROGRESSIVE_ID.LABEL')))
                .column($dt.column('wonByAmount')
                  .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.FIELDS.WON_BY_AMOUNT.LABEL')))
                .options(
                  {
                      url: baseUrl,
                      type: 'POST',
                      data: function (d) {
                          d.start = 0;
                      }
                  },
                  null,
                  dtOptions,
                  null
                )
                .build();
          }
          controller.progressiveBalancesSearch = function (search) {

          }

          controller.refresh = function() {
              controller.table.instance.reloadData(function(){}, false);
          }

          controller.toggleLegendCollapse = function () {
              controller.legendCollapsed = !controller.legendCollapsed;
          }

          controller.applyFilter = function (toggle) {
              if (toggle === true) {
                  controller.toggleLegendCollapse();
              }
              filterApplied = true;
              controller.refresh();
          }

          controller.resetFilter = function (collapse) {
              if (collapse) {
                  controller.toggleLegendCollapse();
              }

              controller.model.providers = null;
              controller.model.gameSupplier = null;
              controller.applyFilter(true);
          }
      }]);
