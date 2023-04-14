'use strict';

angular.module('lithium-dt2', ['datatables', 'ngResource'])
	.factory('$dt2', ['$translate', 'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder', '$http', '$log',
	function($translate, DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $http, $log) {
		var service = {};
				
		service.column = function(name) {
			return DTColumnBuilder.newColumn(name);
		}
		service.columndef = function(number, title) {
			return DTColumnDefBuilder.newColumnDef(number).withTitle(title);
		}
		
		service.builder = function() {
			return new function() {
				var me = this;
				
				this.o = {
					instance: {},
					columns: [],
					columndefs: []
				};
				
				this.column = function(column) {
					this.o.columns.push(column);
					return this;
				};
				this.columndef = function(columndef) {
					this.o.columndefs.push(columndef);
					return this;
				};
				
				function rowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
					// Unbind first in order to avoid any duplicate handler (see https://github.com/l-lin/angular-datatables/issues/87)
					$('td', nRow).unbind('click');
					$('td', nRow).bind('click', function() {
						me.o.rowClickHandler(aData);
					});
					return nRow;
				}
				
				function initCompleteCallback(settings, json) {
					var translateKeys = [];
					for (var col in me.o.columns) {
						translateKeys.push(me.o.columns[col].sTitle);
					}
					$translate(translateKeys).then(function (translations) {
						for (var col in me.o.columns) {
							me.o.columns[col].sTitle = translations[me.o.columns[col].sTitle];
						}
					});
				};
				
				this.data = function(data) {
					this.o.data = data;
					return this;
				}
				
				this.rowClickHandler = function(rowClickHandler) {
					this.o.rowClickHandler = rowClickHandler;
					return this;
				}
				
				this.options = function(dtOptions) {
					this.o.options = DTOptionsBuilder.newOptions()
						.withOption('rowCallback', rowCallback).withOption('stateSave', false)
//						.withDataProp('data')
//						.withOption('processing', true)
//						.withOption('serverSide', false)
//						.withOption('autoWidth', true)
//						.withOption('scrollX', true)
//						.withOption('lengthChange', true)
//						.withOption('info', true)
						.withPaginationType('full_numbers')
						.withOption('pageLength', 25);
					
//					angular.forEach(dtOptions, function(value, key) {
//						this.withOption(key, value);
//					}, this.o.options);
					
//					"lengthMenu": [ 10, 50, 100, 200, 500 ],
//					"pageLength": 100,
//					"paging": true,
//					"searching": true,
//					"ordering": true,
//					"row v-reset-row Id": "id",
					return this;
				}
				
				this.optionsCallback = function(callback) {
					callback(this.o.options);
					return this;
				}
				
				this.build = function() {
					return this.o;
				}
			}
		}
		
		return service;
	}])
	
;
