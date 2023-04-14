'use strict';

angular.module('lithium')
.controller('BonusAddModal', ['$uibModalInstance', "UserRest", 'userFields', 'notify',
function ($uibModalInstance, UserRest, userFields, notify) {
	var vm = this;
	
	vm.options = {};
	vm.model = {};
	
	vm.fields = [{
		className: "col-xs-8",
		key: "bonusName",
		type: "input",
		templateOptions: {
			label: "Bonus Name", description: "", placeholder: "First deposit bonus", required: false,
			minlength: 2, maxlength: 35
		}
	},{
		className: "col-xs-4",
		key: "bonusEnabled",
//		type: "toggle-switch",
		type: "checkbox2",
		templateOptions: {
			label: "Bonus Enabled", description: "Should this bonus be enabled once created?", placeholder: "",
			required: false, fontWeight:'bold'
			//, onLabel:'enabled2', offLabel:'disabled2'
		}
	},{
		className: "col-xs-12",
		key: "bonusCode",
		type: "input",
		templateOptions: {
			label: "Bonus Code", description: "", placeholder: "AFB001", required: false,
			minlength: 2, maxlength: 35
		},
	},{
		className: "col-xs-12",
		key: "depositRequirements",
		type: "depositRequirements",
		templateOptions: {
			label: 'Trigger on deposit',
			required: true,
			fields: [{
				className: 'row',
				fieldGroup: [{
					className: 'col-xs-3',
					type: 'input',
					key: 'minDeposit',
					templateOptions: {
						label: 'Min Deposit', placeholder: "$10", required: false
					}
				},{
					className: 'col-xs-3',
					type: 'input',
					key: 'maxDeposit',
					templateOptions: {
						label: 'Max Deposit', placeholder: "$99", required: false
					}
				},{
					className: "col-xs-2",
					key: "bonusPercentage",
					type: "input",
					templateOptions: {
						label: "Bonus %",
						description: "", placeholder: "200%", required: false,
						minlength: 2, maxlength: 5
					}
				},{
					className: "col-xs-2",
					key: "wagerReq",
					type: "disabled-input",
					templateOptions: {
						label: "Wager Req.",
						description: "", placeholder: "10", required: false,
						minlength: 2, maxlength: 5, size: 3, keyDisabledInputCheck:'wagerReqCheck2'
					}
				},{
					className: "col-xs-1",
					key: "depReqBtn",
					type: "bonusFieldRemove",
					templateOptions: {
						onClick : function($event, model) {
							for (var i=0; i < vm.model['depositRequirements'].length; ++i) {
								if (vm.model['depositRequirements'][i] === model) {
									vm.model['depositRequirements'].splice(i, 1);
								}
							}
						}
					}
				}]
			}]
		}
	},{
		className: "col-xs-12",
		key: "signupRequirements",
		type: "signupRequirements",
		templateOptions: {
			label: 'Trigger on signup',
			required: true,
			fields: [{
				className: 'row',
				fieldGroup: [{
					className: "col-xs-2",
					key: "bonusPercentage",
					type: "input",
					templateOptions: {
						label: "Bonus %",
						description: "", placeholder: "200%", required: false,
						minlength: 2, maxlength: 5
					}
				},{
					className: "col-xs-2",
					key: "wagerReq",
					type: "disabled-input",
					templateOptions: {
						label: "Wager Req.",
						description: "", placeholder: "10", required: false,
						minlength: 2, maxlength: 5, size: 3, keyDisabledInputCheck:'wagerReqCheck'
					}
				},{
					className: "col-xs-1",
					key: "depReqBtn",
					type: "bonusFieldRemove",
					templateOptions: {
						onClick : function($event, model) {
							for (var i=0; i < vm.model['signupRequirements'].length; ++i) {
								if (vm.model['signupRequirements'][i] === model) {
									vm.model['signupRequirements'].splice(i, 1);
								}
							}
						}
					}
				}]
			}]
		}
	},{
		className: "col-xs-12",
		key: "applicableGames",
		type: "applicableGames",
		templateOptions: {
			label: 'Applicable Games',
			fields: [{
				className: 'row',
				fieldGroup: [{
					className: 'col-xs-3',
					type: 'select',
					key: 'gameType',
					templateOptions: {
						label: 'Game Type',
						required: false,
						options: [
							{name: 'Single Game', value: 'single_game'},
							{name: 'Game Category', value: 'category'}
						]
					}
				},{
					className: 'col-xs-4',
					type: 'select',
					key: 'game',
					templateOptions: {
						label: 'Game / Category',
						required: false,
						options: [
							{name: 'Iron Man', value: 'iron_man'},
							{name: 'Captain America', value: 'captain_america'},
							{name: 'Black Widow', value: 'black_widow'},
							{name: 'Hulk', value: 'hulk'},
							{name: 'Captain Marvel', value: 'captain_marvel'}
						]
					}
				},{
					className: "col-xs-4",
					key: "betPercentage",
					type: "input",
					templateOptions: {
						label: "Bet Percentage towards play through",
						description: "Percentage of bet that counts towards play through", placeholder: "70%", required: false,
						minlength: 2, maxlength: 35
					}
				},{
					className: "col-xs-1",
					key: "depReqBtn",
					type: "bonusFieldRemove",
					templateOptions: {
						onClick : function($event, model) {
							for (var i=0; i < vm.model['applicableGames'].length; ++i) {
								if (vm.model['applicableGames'][i] === model) {
									vm.model['applicableGames'].splice(i, 1);
								}
							}
						}
					}
				}]
			}]
		}
	},{
		className: "col-xs-12",
		key: "freeSpins",
		type: "freeSpins",
		templateOptions: {
			label: 'Allocate Free Spins',
			fields: [{
				className: 'row',
				fieldGroup: [{
					className: 'col-xs-5',
					type: 'select',
					key: 'game',
					templateOptions: {
						label: 'Game',
						required: false,
						options: [
							{name: 'Iron Man', value: 'iron_man'},
							{name: 'Captain America', value: 'captain_america'},
							{name: 'Black Widow', value: 'black_widow'},
							{name: 'Hulk', value: 'hulk'},
							{name: 'Captain Marvel', value: 'captain_marvel'}
						]
					}
				},{
					className: "col-xs-4",
					key: "freeSpinAmount",
					type: "input",
					templateOptions: {
						label: "Free Spins",
						description: "Number of free spins to allocate for this game", placeholder: "5", required: false,
						minlength: 2, maxlength: 35
					}
				},{
					className: "col-xs-1",
					key: "freeSpinBtn",
					type: "bonusFieldRemove",
					templateOptions: {
						onClick : function($event, model) {
							for (var i=0; i < vm.model['freeSpins'].length; ++i) {
								if (vm.model['freeSpins'][i] === model) {
									vm.model['freeSpins'].splice(i, 1);
								}
							}
						}
					}
				}]
			}]
		}
	},{
		className: "col-xs-12",
		key: "maxPayout",
		type: "input",
		templateOptions: {
			label: "Max Payout", description: "", placeholder: "$500", required: false,
			minlength: 2, maxlength: 35
		},
	},{
		className: "col-xs-12",
		key: "takeup",
		type: "input",
		templateOptions: {
			label: "Max Redeemable", description: "Maximum times a user may take up the bonus", placeholder: "1", required: false,
			minlength: 2, maxlength: 35
		},
	},{
		className: "col-xs-12",
		key: "valid",
		type: "checkboxWrapped",
		optionsTypes: ['editable'],
		templateOptions: {
			label: 'Valid for amount of days :',
			description: "The amount of days available for player to fulfil bonus requirements.",
			placeholder: "1", required: false, minlength: 2, maxlength: 35
		}
	},{
		className: "col-xs-12",
		key: "fordeposit",
		type: "checkboxWrapped",
		optionsTypes: ['editable'],
		templateOptions: {
			label: "Only applicable for deposit #",
			description: "For example, to limit this bonus only for a first deposits, enter 1",
			placeholder: "1", required: false, minlength: 2, maxlength: 35
		}
	},{
		key: "dependson",
		type: "uib-typeahead",
		className: "col-xs-12",
//		optionsTypes: ['editable'],
		templateOptions: {
			label: "Depends on bonus code :",
			description: "Choose a bonus that should be completed before this one may be used.",
			placeholder: "", required: false,
			valueProp: 'code', labelProp: 'name', displayProp: 'display', displayOnly: false
		},
		controller: ['$scope', function($scope) {
//			$scope.enable = enable;
//			function enable(selected) {
//				$scope.options.templateOptions.disabled = !selected;
//			}
			$scope.searchTypeAhead = function(searchValue) {
				console.log(searchValue);
				return [
					{code:'AFB001', name:'AFBonus1', display:'AF Bonus1!'},
					{code:'AFB002', name:'AFBonus2', display:'AF Bonus2!'},
					{code:'AFB003', name:'AFBonus3', display:'AF Bonus3!'}
				];
			}
			$scope.resetTypeAhead = function() {
				console.log("reset");
			}
			$scope.selectTypeAhead = function($item, $model, $label, $event) {
				console.log("select");
			}
		}]
	},{
//		backend date times are off, needs to be revisited after demo.
		className: "col-xs-12",
		key: "startingDate",
		type: "fullDate",
//		optionsTypes: ['editable'],
		templateOptions: {
			label: "Starting On",
			description: "Date,time and timezone for this bonus to start on",
			required: false,
			disabled: true,
			keyTime: 'startingTime',
			keyDate: 'startingDate',
			keyTimezone: "startingTimezone",
			labelTime: 'Time',
			labelDate: 'Date',
			labelTimezone: 'Timezone',
			descTime: '',
			descDate: '',
			descTimezone: ''
		}
	},{
//		backend date times are off, needs to be revisited after demo.
		className: "col-xs-12",
		key: "expiry",
		type: "fullDate",
		templateOptions: {
			label: "Expiring On",
			description: "Date,time and timezone for this bonus to end on",
			required: false,
			keyTime: 'expiryTime',
			keyDate: 'expiryDate',
			keyTimezone: "expiryTimezone",
			labelTime: 'Time',
			labelDate: 'Date',
			labelTimezone: 'Timezone',
			descTime: '',
			descDate: '',
			descTimezone: ''
		}
	}];
	vm.originalFields = angular.copy(vm.fields);
	
	vm.submit = function() {
		if (vm.form.$invalid) {
			angular.element("[name='" + vm.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		$uibModalInstance.close(JSON.stringify(vm.model));
	};
	
	vm.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);