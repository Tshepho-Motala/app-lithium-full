'use strict';

angular.module('lithium-dt', ['datatables'])
.factory('$dt', ['$translate', "$security", "$filter", 'DTOptionsBuilder', 'DTColumnBuilder', "$log", "$q",
	function($translate, $security, $filter, DTOptionsBuilder, DTColumnBuilder, $log, $q) {
		var service = {};
		
		service.translate = function () {
			var p = $q.defer();
			var translations = 
				["GLOBAL.DATATABLE.EMPTYTABLE", "GLOBAL.DATATABLE.INFO", "GLOBAL.DATATABLE.INFOEMPTY", 
				 "GLOBAL.DATATABLE.INFOFILTERED", "GLOBAL.DATATABLE.LENGTHMENU", 
				 "GLOBAL.DATATABLE.LOADINGRECORDS", "GLOBAL.DATATABLE.PROCESSING", 
				 "GLOBAL.DATATABLE.SEARCH", "GLOBAL.DATATABLE.ZERORECORDS", 
				 "GLOBAL.ACTION.FIRST", "GLOBAL.ACTION.LAST", "GLOBAL.ACTION.NEXT", "GLOBAL.ACTION.PREVIOUS",
				 "GLOBAL.DATATABLE.SORTASCENDING", "GLOBAL.DATATABLE.SORTDESCENDING"];
						
			$translate(translations).then(function success(t) {
				var language = {
						"emptyTable":     t["GLOBAL.DATATABLE.EMPTYTABLE"],
						"info":           t["GLOBAL.DATATABLE.INFO"],
						"infoEmpty":      t["GLOBAL.DATATABLE.INFOEMPTY"],
						"infoFiltered":   t["GLOBAL.DATATABLE.INFOFILTERED"],
						"lengthMenu":     t["GLOBAL.DATATABLE.LENGTHMENU"],
						"loadingRecords": t["GLOBAL.DATATABLE.LOADINGRECORDS"],
						"processing":     t["GLOBAL.DATATABLE.PROCESSING"],
						"search":         t["GLOBAL.DATATABLE.SEARCH"],
						"zeroRecords":    t["GLOBAL.DATATABLE.ZERORECORDS"],
						"paginate": {
							"first":      t["GLOBAL.ACTION.FIRST"],
							"last":       t["GLOBAL.ACTION.LAST"],
							"next":       t["GLOBAL.ACTION.NEXT"],
							"previous":   t["GLOBAL.ACTION.PREVIOUS"]
						},
						"aria": {
							"sortAscending":  t["GLOBAL.DATATABLE.SORTASCENDING"],
							"sortDescending": t["GLOBAL.DATATABLE.SORTDESCENDING"]
						}
					};
				p.resolve(language);
			}, function fail(translations) {
				p.reject();
			});

			return p.promise;
		}
		
		$.fn.dataTable.ext.errMode = 'none';
		
		service.column = function(name) {
			return DTColumnBuilder.newColumn(name);
		}
		
		service.columnrenderwith = function(name, render) {
			return DTColumnBuilder.newColumn(name).renderWith(render);
		}
		
		service.columnformatdate = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return $filter('date')(data, 'yyyy/MM/dd', 'GMT') }).withOption('type', 'date');
		}
		
		service.columnformatdatetime = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss', 'GMT') }).withOption('type', 'date');
		}

		service.columnformatnotimezone = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss') }).withOption('type', 'date');
		}

		service.columnformatcountryflag = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, response) {
				if (data === undefined || data === null || data === '') return;
				return '<img height="30px" width="42px" src="/images/country_flags/' + data.toLowerCase() + '.png" title="' + response.country + '" alt="' + response.country + '">';
			})
		}

		service.columnformatdatetimems = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss.sss', 'GMT') }).withOption('type', 'date');
		}
		
		service.columncurrency = function(name, symbol, fraction) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return $filter('cents')(data, symbol, fraction)});
		}
		service.columncurrencypos = function(name, symbol, fraction) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { if (data < 0) return ''; return $filter('cents')(data, symbol, fraction)});
		}
		service.columncurrencyneg = function(name, symbol, fraction) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { if (data > 0) return ''; return $filter('cents')(data, symbol, fraction)});
		}
		service.columncurrencysymbol = function(name, symbol, fraction) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, row) {
			 	var symbolVal = symbol.split('.').reduce((accumulator, item) => {
					return accumulator[item];
				}, row);
				return $filter('currency')(data, symbolVal, fraction)
			});
		}
		service.columncurrencywithsymbol = function(name, symbol, fraction) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, row) {
				return $filter('currency')(data, symbol, fraction)
			});
		}
		service.columncurrencysymbolcents = function(name, symbol, fraction) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, row) {
				var symbolVal = symbol.split('.').reduce((accumulator, item) => {
					return accumulator[item];
				}, row);
				return $filter('cents')(data === null ? '0' : data, symbolVal, fraction)
			});
		}
		
		service.columnprovider = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return (!data) ? '' : data.substring(data.lastIndexOf('-')+1)});
		}
		service.columnpercentage = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return (!data) ? '' : data+"%"});
		}
		
		service.columntick = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return (data) ? '<i class="fa fa-times fa-lg" style="color:red"></i>' : '<i class="fa fa-check fa-lg" style="color:green"></i>' });
		}
		//<i class="fa fa-check fa-lg" style="color:green">

		service.columnamountcolor = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return (data < 0) ? '<i style="color:red">' + $filter('cents')( data, '' ) + '</i>' : '<i style="color:green">' + $filter('cents')( data, '' ) + '</i>' });
		}

		service.columnlength = function(name, len, ellipsis) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return (!data) ? '' : ((data.length <= len)?data:(data.substring(0, len)+((ellipsis)?'...':'')))});
		}
		
		service.columnsize = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type) { return (!data) ? '0' : data.length });
		}
		
		service.columncombine = function(name, name2) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, row) {
				if (row[name] && row[name2]) {
					return row[name]+' - '+row[name2];
				} else if (row[name] && !row[name2]) {
					return row[name]+' - &infin;';
				} else if (!row[name] && row[name2]) {
					return ' - '+row[name2];
				} else if (!row[name] && !row[name2]) {
					return ' ';
				}
			});
		}
		
		service.columncombinecurrency = function(name, name2, symbol, fraction) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, row) {
				return $filter('cents')(row[name], symbol, fraction)+' - '+$filter('cents')(row[name2], symbol, fraction)
			});
		}
		
		service.columncombinedates = function(date1, date2, date3, date4) {
			return DTColumnBuilder.newColumn(date1).renderWith(function(data, type, row) {
				var d1s = date1.split(".");
				var d1 = row[date1];
				if (d1s.length > 1) {
					if (row[d1s[0]] === null) {
						d1s = date3.split(".");
						if (d1s.length > 1) {
							d1 = row[d1s[0]][d1s[1]];
						}
					} else {
						d1 = row[d1s[0]][d1s[1]];
					}
				}
				var d2s = date2.split(".");
				var d2 = row[date2];
				if (d2s.length > 1) {
//					d2 = row[d2s[0]][d2s[1]];
					if (row[d2s[0]] === null) {
						d2s = date4.split(".");
						if (d2s.length > 1) {
							d2 = row[d2s[0]][d2s[1]];
						}
					} else {
						d2 = row[d2s[0]][d2s[1]];
					}
				}
				return $filter('date')(d1, 'yyyy/MM/dd', 'GMT')+' - '+$filter('date')(d2, 'yyyy/MM/dd', 'GMT')
			});
		}
		service.columncombineperiod = function(name, period) {
			var translations = [];
			translations.push("GLOBAL.GRANULARITY.CUSTOM");
			translations.push("GLOBAL.GRANULARITY.HOURS");
			translations.push("GLOBAL.GRANULARITY.DAYS");
			translations.push("GLOBAL.GRANULARITY.WEEKS");
			translations.push("GLOBAL.GRANULARITY.MONTHS");
			translations.push("GLOBAL.GRANULARITY.YEARS");
			translations.push("GLOBAL.GRANULARITY.ALL_TIME");
//			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, row) { return row[name]+' - '+row[name2] });
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, row, meta) {
				$translate(translations).then(function success(response) {
					var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
					switch (row[period]) {
					case 0:
						parentDom.html('<div class="">'+row[name]+' '+response["GLOBAL.GRANULARITY.CUSTOM"]+'</div>');
						break;
					case 1:
						parentDom.html('<div class="">'+row[name]+' '+response["GLOBAL.GRANULARITY.YEARS"]+'</div>');
						break;
					case 2:
						parentDom.html('<div class="">'+row[name]+' '+response["GLOBAL.GRANULARITY.MONTHS"]+'</div>');
						break;
					case 3:
						parentDom.html('<div class="">'+row[name]+' '+response["GLOBAL.GRANULARITY.DAYS"]+'</div>');
						break;
					case 4:
						parentDom.html('<div class="">'+row[name]+' '+response["GLOBAL.GRANULARITY.WEEKS"]+'</div>');
						break;
					case 5:
						parentDom.html('<div class="">'+row[name]+' '+response["GLOBAL.GRANULARITY.ALL_TIME"]+'</div>');
						break;
					case 6:
						parentDom.html('<div class="">'+row[name]+' '+response["GLOBAL.GRANULARITY.HOURS"]+'</div>');
						break;
					default:
						break;
					}
					return "";
				});
			});
		}
		
		service.columnperiod = function(name) {
			var translations = [];
			translations.push("GLOBAL.GRANULARITY.CUSTOM");
			translations.push("GLOBAL.GRANULARITY.HOURLY");
			translations.push("GLOBAL.GRANULARITY.DAILY");
			translations.push("GLOBAL.GRANULARITY.WEEKLY");
			translations.push("GLOBAL.GRANULARITY.MONTHLY");
			translations.push("GLOBAL.GRANULARITY.YEARLY");
			translations.push("GLOBAL.GRANULARITY.ALL_TIME");
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, full, meta) {
				$translate(translations).then(function success(response) {
					var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
					switch (data+'') {
					case '0':
						parentDom.html('<div class="">'+response["GLOBAL.GRANULARITY.CUSTOM"]+'</div>');
						break;
					case '1':
						parentDom.html('<div class="">'+response["GLOBAL.GRANULARITY.YEARLY"]+'</div>');
						break;
					case '2':
						parentDom.html('<div class="">'+response["GLOBAL.GRANULARITY.MONTHLY"]+'</div>');
						break;
					case '3':
						parentDom.html('<div class="">'+response["GLOBAL.GRANULARITY.DAILY"]+'</div>');
						break;
					case '4':
						parentDom.html('<div class="">'+response["GLOBAL.GRANULARITY.WEEKLY"]+'</div>');
						break;
					case '5':
						parentDom.html('<div class="">'+response["GLOBAL.GRANULARITY.ALL_TIME"]+'</div>');
						break;
					case '6':
						parentDom.html('<div class="">'+response["GLOBAL.GRANULARITY.HOURLY"]+'</div>');
						break;
					default:
						break;
					}
					return "";
				});
			});
		}
		
		service.columnobscurecc = function(name) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, full, meta) {
				if (full.domainMethod.method.code === 'cc') {
					var len = (data ? data.length : 0);
					if (len > 0)
						return data.substring(0, 6)+'******'+data.substring(len-4, len);
					else
						return '';
				} else {
					return (!data) ? '' : data;
				}
			});
		}
		
		service.selectcolumn = function() {
			return DTColumnBuilder.newColumn(null).withTitle('').notSortable().withClass('select-checkbox').renderWith(function() {return '';});
		}
		
		service.columnWithClass = function(name, withClass) {
			return DTColumnBuilder.newColumn(name).renderWith(function(data, type, full, meta) {
				if (data === undefined || data === null || data === '') return '';
				return '<div class="'+withClass+'">'+data.toUpperCase()+'</div>';
			});
		}

		service.columnWithTransactionStatus = function (name) {
			console.log(name);
			var translations = []
			var res = []
			translations.push('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.WAITFORAPPROVAL');
			translations.push('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.PLAYER_CANCEL');
			translations.push('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.AUTO_APPROVED');
			translations.push('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.FATALERROR');
			translations.push('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.WAITFORPROCESSOR');
			translations.push('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.VALIDATEINPUT');

			$translate(translations).then(function success(response){
				res.WAITFORAPPROVAL = response["UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.WAITFORAPPROVAL"]
				res.PLAYER_CANCEL = response["UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.PLAYER_CANCEL"]
				res.AUTO_APPROVED = response["UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.AUTO_APPROVED"]
				res.FATALERROR = response["UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.FATALERROR"]
				res.WAITFORPROCESSOR = response["UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.WAITFORPROCESSOR"]
				res.VALIDATEINPUT = response["UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.VALIDATEINPUT"]
			})
			return DTColumnBuilder.newColumn(name).renderWith(
				function (data) {
					if (data === undefined || data === null || data === '') return '';
					if (data === "SUCCESS" || data === "APPROVED") {
						return '<span class="badge badge-wrap card-success">'+data+'</span>';
					} else if (data === "AUTO_APPROVED") {
						return '<span class="badge badge-wrap card-success">'+res.AUTO_APPROVED+'</span>';
					} else if (data === "DECLINED" || data === "CANCEL") {
						return '<span class="badge badge-wrap card-danger">'+data+'</span>';
					} else if (data === "FATALERROR") {
						return '<span class="badge badge-wrap card-danger">'+res.FATALERROR+'</span>';
					} else if (data === "PLAYER_CANCEL") {
						return '<span class="badge badge-wrap card-danger">'+res.PLAYER_CANCEL+'</span>';
					} else if (data === "WAITFORAPPROVAL") {
						return '<span class="badge badge-wrap card-info">'+res.WAITFORAPPROVAL +'</span>';
					} else if (data === "WAITFORPROCESSOR") {
						return '<span class="badge badge-wrap">'+res.WAITFORPROCESSOR+'</span>';
					} else if (data === "VALIDATEINPUT") {
						return '<span class="badge badge-wrap">'+res.VALIDATEINPUT+'</span>';
					}
					return '<span class="badge badge-wrap">'+data +'</span>';
				}
			)
		}
		
		service.emptycolumnrenderwith = function(title, renderWith) {
			return DTColumnBuilder.newColumn(null)
			.withTitle(title)
			.notSortable()
//			.renderWith(function(data, type, full, meta) {return renderWith;});
			.renderWith(renderWith);
		}
		
		service.emptycolumn = function(title) {
			return DTColumnBuilder.newColumn(null)
			.withTitle(title)
			.notSortable();
		}
		
		function hasPermission(permissions, permissionType, permissionDomain) {
//			console.log(permissions);
//			console.log(permissionType);
//			console.log(permissionDomain);
			return $security.getPermissionValidation(permissionType)(permissions, permissionDomain, null);
		}
		
		service.textcolumn = function(title, key, dataobject, dataprop, withClass) {
			return service.emptycolumn(title).renderWith(
				function(data, type, full, meta) {
					var translations = [];
					var o;
					if (dataobject === '') {
						o = data;
					} else {
						o = data[dataobject];
					}
					var v = o[dataprop];
					var t = key+v;
					translations.push(t);
					$translate(translations).then(function success(response) {
						var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
						parentDom.html('<div class="'+withClass+'">'+response[t]+'</div>');
					});
					return "...";
				}
			)
		}

		service.labelcolumn = function(title, labels) {
			return service.emptycolumn(title).renderWith(
				function(data, type, full, meta) {
//					console.log(labels);
					var translations = [];

					for (var i = 0; i < labels.length; i++) {
						var text = labels[i].text;
						if (angular.isFunction(text)) text = text(data);
						translations.push(text);
					}
					$translate(translations).then(function success(t) {
						var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
						parentDom.html("");
						for (var i = 0; i < labels.length; i++) {
							var label = labels[i];
							var lclass = label.lclass;
							if (angular.isFunction(lclass)) lclass = lclass(data);
							if (!lclass) lclass = "default";
							var uppercase = label.uppercase;

							var text = labels[i].text;
							if (angular.isFunction(text)) text = text(data);
							var title = t[text];

							if (uppercase === true) {
								var childDom = $('<span class="label label-'+lclass+'">'+$filter('uppercase')(title)+'</span>');
								parentDom.append(childDom);
							} else {
								var childDom = $('<span class="label label-'+lclass+'">'+(title)+'</span>');
								parentDom.append(childDom);
							}
						}
					});
					return "...";
				}
			);
		}

		service.labelcolumnsortable = function(name, labels) {
			return service.columnrenderwith(name, function(data, type, full, meta) {
//					console.log(labels);
				var translations = [];

				for (var i = 0; i < labels.length; i++) {
					var text = labels[i].text;
					if (angular.isFunction(text)) text = text(data);
					translations.push(text);
				}
				$translate(translations).then(function success(t) {
					var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
					parentDom.html("");
					for (var i = 0; i < labels.length; i++) {
						var label = labels[i];
						var lclass = label.lclass;
						if (angular.isFunction(lclass)) lclass = lclass(data);
						if (!lclass) lclass = "default";
						var uppercase = label.uppercase;

						var text = labels[i].text;
						if (angular.isFunction(text)) text = text(data);
						var title = t[text];

						if (uppercase === true) {
							var childDom = $('<span class="label label-'+lclass+'">'+$filter('uppercase')(title)+'</span>');
							parentDom.append(childDom);
						} else {
							var childDom = $('<span class="label label-'+lclass+'">'+(title)+'</span>');
							parentDom.append(childDom);
						}
					}
				});
				return "...";
			});
		}
		
		service.linkscolumn = function(title, links) {
			return service.emptycolumn(title).renderWith(
				function(data, type, full, meta) {
					var translations = [];
					var html = "";
					
					for (var i = 0; i < links.length; i++) {
						if (!angular.isFunction(links[i].title)) translations.push(links[i].title);
					}
					
					$translate(translations).then(function success(t) {
						var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
						parentDom.html("")
						for (var i = 0; i < links.length; i++) {
							var link = links[i];
							var href = link.href;
							if (!href) href = "";
							var css = link.css;
							if (!css) css = "dtlink";
							var target = link.target;
							if (!target) target = "_self";
							var title = (!angular.isFunction(links[i].title))? t[link.title]: link.title(data);
							var condition = link.condition;
							if (!condition) condition = true;
							if (angular.isFunction(condition)) condition = condition(data);
							
							var permissions = link.permission;
							if (angular.isFunction(permissions)) permissions = permissions(data);
							if (!permissions) permissions = "admin";
							permissions = ($filter('uppercase')(permissions))
							permissions = permissions.split(",");
							
							var permissionType = link.permissionType;
							if (!permissionType) permissionType = "any";
							permissionType = ($filter('uppercase')(permissionType))
							
							var permissionDomain = link.permissionDomain;
							if (angular.isFunction(permissionDomain)) permissionDomain = permissionDomain(data);
							if (!permissionDomain) permissionDomain = $security.domainName();
							
							var viewable = hasPermission(permissions, permissionType, permissionDomain);
							if (condition === false) {
								viewable = false;
							}
							
							if (angular.isFunction(href)) {
								href = href(data);
							}
							
							if (viewable) {
								var childDom = $('<a class="'+css+'" target="'+target+'">'+title+'</a>');
								parentDom.append(childDom);
								if (link.href) childDom.attr("href", href);
								if (angular.isFunction(link.click)) {
									childDom.click(function (data) { return function() { link.click(data)}; } (data));
								}
							} else {
								if (link.fallback) {
									var fallback = link.fallback;
									if (angular.isFunction(fallback)) fallback = fallback(data);
									parentDom.append(fallback);
								} else {
									parentDom.append("&nbsp;");
								}
							}
						}
//						$(meta.settings.aoData[meta.row].anCells[meta.col]).html(html);
					});
					
					return "...";
				}
			);	
		}
		
		service.buttoncolumn = function(title, callback) {
			return service.emptycolumn(title).renderWith(
				function(data, type, full, meta) {
					var translations = [];
					var html = "";
					for (var i = 0; i < links.length; i++) {	
						translations.push(links[i].title);
					}
					
					$translate(translations).then(function success(t) {
						$(meta.settings.aoData[meta.row].anCells[meta.col]).html(html);
					});
					
					return "...";
				}
			);	
		}

		service.iconcolumn = function(title, iconData) {
			return service.emptycolumn(title).renderWith(
				function(data, type, full, meta) {
					var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);

					var condition = iconData.condition;
					if (!condition) condition = true;
					if (angular.isFunction(condition)) condition = condition(data);

					if (condition) {
						parentDom.html('<i class="fa fa-'+iconData.icon+'"></i>');
					} else {
						parentDom.html('');
					}
					return "...";
				}
			);
		}

		service.iconcolumnsource = function(title, iconData) {
			return service.emptycolumn(title).renderWith(
				function(data, type, full, meta) {
					var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);

					if (angular.isFunction(iconData)) {
						var iconType = iconData(data).filetype;
						var iconBase64 = iconData(data).base64;
						parentDom.html('<img class="img-responsive" src="data:'+iconType+';base64,'+iconBase64+'" alt="">');
					}
					return "...";
				}
			);
		}
		
		service.builder = function() {
			return new function() {
				var me = this;
				
				this.o = {
					instance: {},
					columns: []
				};
				
				this.column = function(column) {
					this.o.columns.push(column);
					return this;
				};
				
				function rowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
					// Unbind first in order to avoid any duplicate handler (see https://github.com/l-lin/angular-datatables/issues/87)
					$('td', nRow).unbind('click');
					$('td', nRow).bind('click', function() {
						if (angular.isFunction(me.o.rowClickHandler)) me.o.rowClickHandler(aData);
					});
					return nRow;
				}
				
				this.drawCallback = function(drawCallback) {
					this.o.drawCallback = drawCallback;
					return this;
				}
				
//				this.enableSelect = function() {
//					this.o.options.withSelect({
//							style: 'os',
//							selector: 'td:first-child'
//						});
//					return this;
//				}
				
				this.post = function(post) {
					this.o.post = post;
					return this;
				}
				
				this.order = function(order) {
					this.o.options.withOption('order', order);
					return this;
				}
				
				this.nosearch = function() {
					this.o.options.withOption('searching', false);
					return this;
				}
				
				this.dom = function(dom) {
					this.o.options.withOption('dom', dom);
					return this;
				}
				
				this.optionsLocalData = function(data, rowClickHandler, dtOptions, drawCallback) {
					this.o.rowClickHandler = rowClickHandler;
					this.o.options = DTOptionsBuilder.newOptions().withOption('stateSave', false)
						.withOption('data', data)
						.withOption('rowCallback', rowCallback)
						.withOption('drawCallback', drawCallback)
						.withOption('processing', true)
						.withOption('serverSide', false)
						.withOption('autoWidth', true)
//						.withOption('responsive', true)
//						.withOption('scrollX', true)
						.withOption('lengthChange', true)
						.withOption('info', true)
						.withPaginationType('full_numbers')
						.withOption('pageLength', 25);
					
					angular.forEach(dtOptions, function(value, key) {
						this.withOption(key, value);
					}, this.o.options);
//					"lengthMenu": [ 10, 50, 100, 200, 500 ],
//					"pageLength": 100,
//					"paging": true,
//					"searching": true,
//					"ordering": true,
//					"row v-reset-row Id": "id",
					
					return this;
				}
				
				this.options = function(url, rowClickHandler, dtOptions, drawCallback) {
					this.o.rowClickHandler = rowClickHandler;
					this.o.options = DTOptionsBuilder.newOptions().withOption('stateSave', false)
						.withOption('rowCallback', rowCallback)
						.withOption('drawCallback', drawCallback)
						.withDataProp('data')
						.withOption('processing', true)
						.withOption('serverSide', true)
						.withOption('autoWidth', true)
//						.withOption('responsive', true)
//						.withOption('scrollX', true)
						.withOption('lengthChange', true)
						.withOption('info', true)
						.withPaginationType('full_numbers')
						.withOption('pageLength', 25);
					
					if (this.o.post) {
						this.o.options.withOption('ajax', { "url": url, "type": "POST" });
					} else {
						this.o.options.withOption('ajax', url);
					}
					
					angular.forEach(dtOptions, function(value, key) {
						this.withOption(key, value);
					}, this.o.options);
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
					var optionsPromise = $q.defer();
					var columnsPromise = $q.defer();
					var options = this.o.options;
					var columns = this.o.columns;
					
					service.translate().then(function success(translations) {
						options.withOption('language', translations);
						optionsPromise.resolve(options);
						columnsPromise.resolve(columns);
					}, function fail() {
						optionsPromise.reject();
						columnsPromise.reject();
					});
					
					this.o.options = optionsPromise.promise;
					this.o.columns = columnsPromise.promise;
					return this.o;
				}
			}
		}
		return service;
	}
]);
