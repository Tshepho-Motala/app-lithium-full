'use strict'

angular.module('lithium').controller('PlayerMailHistoryController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', 'user', '$rootScope', '$http', 'DocumentGenerationRest',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, user, $rootScope, $http, documentRest) {
		var controller = this;
		
		var baseUrl = 'services/service-mail/mail/findByUser/table?userGuid='+user.guid;
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.mailHistoryTable = $dt.builder()
		.column($dt.columnformatdatetime('createdDate').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.CREATEDDATE')))
		.column($dt.column(function (data) {
				if (data.author != null) {
					return data.author.guid;
				} else return 'not specified'
			}).withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.AUTHOR_GUID')).notSortable())
		.column($dt.column(function (data) {
				if (data.author != null) {
					if (data.author.lastName == null || data.author.firstName == null) {
						return 'not specified'
					} else {
						return data.author.firstName + ' ' + data.author.lastName;
					}
				} else return 'not specified';
			}).withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.AUTHOR_FULL_NAME')).notSortable())
		.column($dt.linkscolumn("", [{ permission: "player_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href('^.view', {mailId:data.id}) } }]))
		.column($dt.columnformatdatetime('sentDate').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.SENTDATE')))
		.column($dt.column('from').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.FROM')).notSortable())
		.column($dt.column('to').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.TO')).notSortable())
		.column($dt.column('bcc').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.BCC')).notSortable())
		.column($dt.column('subject').withTitle($translate('UI_NETWORK_ADMIN.MAILQUEUE.MAIL.SUBJECT')).notSortable())
		.options(baseUrl, null, dtOptions, null)
		.nosearch()
		.build();


		controller.reference = () => {
			const reference = localStorage.getItem(`export_reference_${user.guid}`)

			if(angular.isUndefinedOrNull(reference)) {
				return null;
			}

			return reference.replace('export_reference_', '')
		}
		$rootScope.provide['csvGeneratorProvider'] = {}

		$rootScope.provide.csvGeneratorProvider.generate = async (config) => {
			const response = await documentRest.generateDocument({
				...config,
			});

			localStorage.setItem(`export_reference_${user.guid}`, response.reference);

			return response
		}

		$rootScope.provide.csvGeneratorProvider.progress = async (config) => {
			return documentRest.documentStatus(controller.reference());
		}
		$rootScope.provide.csvGeneratorProvider.cancelGeneration = async (reference) => {
			return documentRest.documentCancel(reference);
		}

		$rootScope.provide.csvGeneratorProvider.download = (reference) => {
				const a = document.createElement("a")
                const url = `services/service-document-generation/document/${reference}/download`;
                a.href = url;
                a.setAttribute('download', reference)
                document.body.appendChild(a);
                a.click();
				
				setTimeout(() => document.body.removeChild(a), 1500)
		}

		$rootScope.provide.csvGeneratorProvider.getConfig = () => {
			return {
				domain: user.domain.name,
				provider: 'service-csv-provider-mail',
				page: 0,
				size: 10,
				parameters: $rootScope.apiParams(),
				role: 'PLAYER_INFO_DATA',
				reference: controller.reference()
			};
		}

		$rootScope.apiParams = () => {
			const params = {};

			params.userGuid = user.guid;

			return params
		}

		window.VuePluginRegistry.loadByPage('dashboard/csv-export')
	}
]);
