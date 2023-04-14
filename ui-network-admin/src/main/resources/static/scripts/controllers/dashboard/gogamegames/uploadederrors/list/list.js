'use strict'

angular.module('lithium').controller('GoGameUploadedErrorsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$uibModal',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $uibModal) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/uploadederrors/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
		controller.gogameUploadedErrorsTable = $dt.builder()
		.column($dt.columnformatdatetime('createdDate').withTitle('Created'))
		.column($dt.column('status').withTitle('Status'))
		.column($dt.column('ts').withTitle('TS'))
		.column($dt.column('message').withTitle('Message'))
		.column($dt.column('detail').withTitle('Detail'))
		.options(baseUrl, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameUploadedErrorsTable.instance.reloadData(function(){}, false);
		}
	}
]);
