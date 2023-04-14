'use strict';

angular.module('lithium')
	.controller('CsvBonusAllocationController', ["bonus", "bonusRevision", "$scope", "$translate", "$dt", "DTOptionsBuilder", "$rootScope", "$http",
	function(bonus, bonusRevision, $scope, $translate, $dt, DTOptionsBuilder, $rootScope, $http) {
		var controller = this;
		
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;
		
		// console.log(controller.bonus);
		// console.log(controller.bonusRevision);
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.csvAllocationBonusTable = $dt.builder()
		.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.ID")))
		.column($dt.columnformatdatetime('creationDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.CREATEDDATE")))
		.column($dt.columnformatdatetime('completionDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.COMPLETEDATE")))
		.column($dt.column('hadSomeErrors').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.BONUSHISTORY.TABLE.SOMEERRORS")))
		.column($dt.linkscolumn("", [{ permission: "bonus_view", permissionType:"any", permissionDomain: function(data) { return data.bonusRevision.domain.name;}, title: "GLOBAL.ACTION.DOWNLOADCSV", click: function(data){ $scope.downloadCsv(data) } }]))
		.options({ url:"services/service-casino/casino/bonus/table/csv/"+bonusRevision.id, type:"POST" }, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.csvAllocationBonusTable.instance.reloadData(function(){}, false);
		}
//This should possibly go live in a module for easy reuse and just take params
		$scope.downloadCsv = function(data) {
			var req = {
				method: 'POST',
				url: 'services/service-casino/casino/bonus/csv/download',
				headers: {
					'Authorization': 'Bearer '+$rootScope.token
				},
				params: {
					bonusFileUploadId: data.id
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
