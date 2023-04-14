'use strict';

angular.module('lithium')
	.controller('PlayerAccountingHistoryController', ["domain", "user","notify", "$state", "$rootScope", "$dt", "$translate", "$http", "$interval", "rest-domain",
	function(domain, user, notify, $state, $rootScope, $dt, $translate, $http, $interval, domainRest) {
		var controller = this;
		controller.loading = false;
		controller.dataReady = false;
		controller.user = user;
		controller.dateError = false;
//		controller.currency = "$";
		
//		domainRest.findByName(domain).then(function(domain) {
//			controller.currency = domain.currencySymbol + " ";
//		});
		
		controller.userGuid = user.guid;
		var sdate = new Date();
		sdate.setUTCHours(0,0,0,0);
		var edate = new Date();
		edate.setUTCHours(23,59,59,999);
		
		controller.options = {};
		
		controller.model = {"startDate" : sdate,
							"endDate" : edate
							}
		
		controller.fields = [
			{
				className: "col-lg-5 col-md-6 ",
				key: "startDate",
				type: "datepicker",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "",
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy 00:00:00.000 UTC'
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYER.TRAN.STARTDATE.NAME" | translate'
				}
			},
			{
				className: "col-lg-5 col-md-6",
				key: "endDate",
				type: "datepicker",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "",
					required: false,
					datepickerOptions: {
						format: 'dd/MM/yyyy 23:59:59.999 UTC'
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYER.TRAN.ENDDATE.NAME" | translate'
				}
			}
		];
		
		controller.generateData = function() {
			var baseUrl = "services/service-report-player-trans/generateByDateRangeAndUserGuid?";
			baseUrl += "userGuid=" + controller.userGuid;
			baseUrl += "&startDate=" + controller.model.startDate.toISOString();
			baseUrl += "&endDate=" + controller.model.endDate.toISOString();

			$http.post(baseUrl).then(function(response) {
				controller.dataReady = response.data.data;
			});
		}
		
		controller.submit = function() {
			controller.loading = true;
			controller.dataReady = false;
			var nowDate = new Date();
			if(controller.model.endDate > nowDate) {
				controller.model.endDate = nowDate;
			} else {
				controller.model.endDate.setUTCHours(23,59,59,999);
			}
			
			if (controller.model.startDate > controller.model.endDate) {
				controller.dateError = true;
				controller.loading = false;
				return;
			} else {
				controller.dateError = false;
			}
			//controller.model.startDate.setUTCHours(0,0,0,0);
			//controller.model.endDate.setUTCHours(23,59,59,999);
			
			if (angular.isDefined(controller.intervalPromise) && controller.intervalPromise != null) {
				$interval.cancel(controller.intervalPromise);
				controller.intervalPromise = null;
			}
			if (angular.isDefined(controller.table)) {
				controller.table.instance.DataTable.clear();
			}
			controller.dataRetrieval(); // call it here so we don't waste time
			controller.intervalPromise = $interval(controller.dataRetrieval, 2000, 0, false);
		}
		
		controller.showTable = function() {
			var baseUrl = "services/service-report-player-trans/findByDateRangeAndUserGuid?";
			baseUrl += "userGuid=" + controller.userGuid;
			baseUrl += "&startDate=" + controller.model.startDate.toISOString();
			baseUrl += "&endDate=" + controller.model.endDate.toISOString();
			if (!angular.isUndefined(controller.table)) {
				//controller.table.instance._renderer.options.ajax = baseUrl;
				controller.table.instance.DataTable.ajax.url(baseUrl).load();
			} else {
				
				controller.table = $dt.builder()
				.column($dt.column('tranId').withTitle("Tran Id"))
				//.column($dt.column('externalTransactionDetailUrl')).withTitle("External Detail URL")
				//.column($dt.column('tranEntryId').withTitle("Entry Id"))
				.column($dt.columnformatdatetime('tranEntryDate').withTitle("Date"))
				.column($dt.column('tranType').withTitle("Transaction Type"))
				.column($dt.column('tranCurrency').withTitle('Transaction Currency'))
				.column($dt.columncurrencypos('tranEntryAmount', '').withTitle("Amount (+)"))
				.column($dt.columncurrencyneg('tranEntryAmount', '').withTitle("Amount (-)"))
				//.column($dt.column('tranEntryAccountType').withTitle("Account Type"))
				.column($dt.column('tranEntryAccountCode').withTitle("Account Code"))
				.column($dt.columncurrency('tranEntryAccountBalance', '').withTitle("Account Balance"))
				.column($dt.columnprovider('providerGuid').withTitle("Provider"))
				//.column($dt.column('externalTranId').withTitle("Provider Tran Id"))
				.column($dt.linkscolumn('Provider Tran Id',
					[ {
								title: function (data) {
									return data.externalTranId;
								},
								href: function(data) {
									return data.externalTransactionDetailUrl ? data.externalTransactionDetailUrl : null;
								},
								target: "_blank",
								condition: function(data) {
									return  data.externalTranId !== null &&
										(data.externalTransactionDetailUrl !== null &&
										 data.externalTransactionDetailUrl.length > 0);
								},
								fallback: function(data) {
									return data.externalTranId !== null ? data.externalTranId : '&nbsp;';
								}
							}
						]))
				//.column($dt.column('gameGuid').withTitle("Game Id"))
				.column($dt.column('gameName').withTitle("Game Name"))
				.column($dt.column('bonusRevisionId').withTitle("Bonus Revision"))
				.column($dt.column('bonusName').withTitle("Bonus Name"))
				.column($dt.column('bonusCode').withTitle("Bonus Code"))
				//.column($dt.column('playerBonusHistoryId').withTitle("Bonus Id"))
				.column($dt.column('processingMethod').withTitle("Processing Method"))
				.column($dt.column('accountingClientTranId').withTitle("Accounting Client Transaction Id"))
				.column($dt.column('accountingClientExternalId').withTitle("Accounting Client External Id"))
				.options({url: baseUrl, type: 'POST'})
				.build();
				
				var dtInstanceListener = $rootScope.$watch(
						function(scope) { 
							return controller.table.instance;
						} , 
						function(newValue, oldValue, scope) {
							if(newValue !== oldValue) {
								dtInstanceListener(); //clears the watcher
								
								controller.table.instance.DataTable.on('error.dt', function(e, settings, techNote, message) { console.log( 'An error occurred: ', message); });
								
								controller.table.instance.DataTable.on('xhr.dt', function ( e, settings, json, xhr ) { 
									if(xhr.status != 200) {
										controller.table.instance.DataTable.ajax.reload();
									}
								} );
							}
				});
			}

		
		}
		
		controller.dataRetrieval = function() {
			controller.generateData();
			
			if(controller.dataReady) {
				controller.showTable();
				
				$interval.cancel(controller.intervalPromise);
				controller.intervalPromise = null;
				controller.loading = false;
			}
		}
		
		controller.downloadxls = function() {
			var baseUrl = "services/service-report-player-trans/xls?";
			var req = {
				method: 'POST',
				url: baseUrl,
				headers: {
					'Authorization': 'Bearer '+$rootScope.token
				},
				params: {
					userGuid: controller.userGuid,
					startDate: controller.model.startDate.toISOString(),
					endDate: controller.model.endDate.toISOString()
				},
				responseType: 'arraybuffer'
			}
			$http(req).success(function (data, status, headers) {
				headers = headers();
				var filename = headers['x-filename'];
				var contentType = headers['content-type'];

				var linkElement = document.createElement('a');
				try {
					var blob = new Blob([data], { type: contentType });
					var url = window.URL.createObjectURL(blob);

					linkElement.setAttribute('href', url);
					linkElement.setAttribute("download", filename);

					var clickEvent = new MouseEvent("click", {
						"view": window,
						"bubbles": true,
						"cancelable": false
					});
					linkElement.dispatchEvent(clickEvent);
				} catch (ex) {
					console.log(ex);
				}
			}).error(function (data) {
				console.log(data);
			});
		}
	}
]);
