'use strict'

angular.module('lithium').controller('GoGameWinDistributionsAddController', ['comparators', '$translate', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', '$uibModal',
	function(comparators, $translate, $filter, $state, $scope, errors, notify, $q, gogameGamesRest, $uibModal) {
		var controller = this;


		controller.model = { buckets: [] };
		
		controller.fields = [
			{
				key: 'ledgerId',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Ledger',
					required: true,
					description: "",
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: [],
					focus: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//				},
				controller: ['$scope', function($scope) {
					var options = [];
					gogameGamesRest.findAll().then(function(response) {
						$scope.to.options = response.plain();
					});
				}]
			}
		];

		controller.addBucket = function(b, $index) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/windistributions/components/addbucket/addbucket.html',
				controller: 'GoGameWinDistributionsAddBucketController',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					comparators: function() { return comparators; },
					bucket: function() {
						if (b !== undefined && b !== null) return angular.copy(b);
						return null;
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: ['scripts/controllers/dashboard/gogamegames/windistributions/components/addbucket/addbucket.js']
						})
					}
				}
			});

			modalInstance.result.then(function(response) {
				// console.log(response);
				if (b !== undefined && b !== null) {
					controller.model.buckets[$index] = response;
				} else {
					controller.model.buckets.push(response);
				}
			});
		}

		controller.removeBucket = function($index) {
			controller.model.buckets.splice($index, 1);
		}

		controller.findOperatorById = function(id) {
			for (var i = 0; i < comparators.length; i++) {
				if (comparators[i].id === id) return comparators[i].operator;
			}
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			if (controller.model.buckets.length === 0) {
				notify.warning("No buckets configured yet. Add at least one bucket.");
				return false;
			}

			gogameGamesRest.addWinDistributionTest(controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success('Win distribution test added successfully. Processing will begin shortly.');
					$state.go("dashboard.gogamegames.windistributions.windistribution", { id:response.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error('Win distribution test could not be added');
				errors.catch('', false)(error)
			});
		}
	}
]);
