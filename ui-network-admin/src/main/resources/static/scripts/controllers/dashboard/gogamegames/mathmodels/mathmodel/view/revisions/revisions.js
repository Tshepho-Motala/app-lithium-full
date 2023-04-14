'use strict'

angular.module('lithium').controller('GoGameMathModelRevisionsListController', ['mathModel', '$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope',
	function(mathModel, $log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/mathmodel/'+mathModel.id+'/revisions';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
		controller.revisionsTable = $dt.builder()
		.column($dt.column('id').withTitle('ID'))
		.column($dt.column('name').withTitle('Name'))
		.column(
			$dt.labelcolumn(
				"",
				[{lclass: function(data) {
					if (data.id === mathModel.current.id) return "success";
					if (mathModel.edit && data.id === mathModel.edit.id) return "primary";
					return "";
				},
				text: function(data) {
					if (data.id === mathModel.current.id) return "CURRENT"
					if (mathModel.edit && data.id === mathModel.edit.id) return "EDIT";
					return "";
				},
				uppercase:true
				}]
			)
		)
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_math_models",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
//						console.log(data);
						return $state.href("dashboard.gogamegames.mathmodels.mathmodel.view", { id:mathModel.id, mathModelRevisionId: data.id });
					}
				}
			]
		))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
