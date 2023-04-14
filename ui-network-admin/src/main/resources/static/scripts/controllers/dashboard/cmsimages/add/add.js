"use strict";

angular.module("lithium").controller("ImagesAddController", [
  "domainName",
  "$uibModalInstance",
  "AssetTemplatesRest",
  "cdn-google-rest",
  "notify",
  "errors",
  "$q",
  function (domainName, $uibModalInstance, rest, cdnRest, notify, errors, $q) {
    var controller = this;

    controller.model = {
      domain: { name: domainName },
      lang: "en",
      enabled: true,
    };
    controller.progressStyles = {
		width: '0%'
	};

	controller.progressValue = '0'
    controller.options = {};

    controller.fields = [
      {
        className: "col-xs-12",
        key: "name",
        type: "input",
        templateOptions: {
          label: "",
          description: "",
          placeholder: "",
          required: true,
          minlength: 2,
          maxlength: 100,
        },
        modelOptions: {
          updateOn: "default blur",
          debounce: { default: 1000, blur: 0 },
        },
        expressionProperties: {
          "templateOptions.label":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.NAME.NAME" | translate',
          "templateOptions.placeholder":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.NAME.NAME" | translate',
          "templateOptions.description":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.NAME.DESCRIPTION" | translate',
        },
        asyncValidators: {
          nameUnique: {
            expression: async function ($viewValue, $modelValue, scope) {
              let success = false;

              try {
                const image = await rest.findByNameAndDomainNameAndLang(
                  encodeURIComponent($viewValue),
                  domainName,
                  controller.model.lang
                );

                success = typeof image.domain === 'undefined' || image.domain === null;
              }
              catch(e) {
                scope.options.validation.show = true;
                errors.catch(
                  "UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.NAME.UNIQUE",
                  false
                );
              }
              finally {
                scope.options.templateOptions.loading = false;
                  if (success) {
                    return $q.resolve("No such template");
                  } else {
                    return $q.reject("The template already exists");
                  }
              }
            },
            message:
              '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.NAME.UNIQUE" | translate',
          },
        },
      },
      {
        className: "col-xs-12",
        key: "lang",
        type: "ui-select-single",
        templateOptions: {
          label: "",
          description: "",
          placeholder: "",
          required: true,
          optionsAttr: "bs-options",
          valueProp: "locale2",
          labelProp: "description",
          optionsAttr: "ui-options",
          ngOptions: "ui-options",
          options: [],
        },
        expressionProperties: {
          "templateOptions.label":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.LANG.NAME" | translate',
          "templateOptions.placeholder":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.LANG.NAME" | translate',
          "templateOptions.description":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.LANG.DESCRIPTION" | translate',
        },
        controller: [
          "$scope",
          "$http",
          function ($scope, $http) {
            $http
              .get("services/service-translate/apiv1/languages/all")
              .then(function (response) {
                $scope.to.options = response.data;
              });
          },
        ],
      },
      {
        className: "col-xs-12",
        key: "description",
        type: "input",
        optionsTypes: ["editable"],
        templateOptions: {
          label: "",
          description: "",
          placeholder: "",
          required: false,
        },
        expressionProperties: {
          "templateOptions.label":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.DESCRIPTION.NAME" | translate',
          "templateOptions.placeholder":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.DESCRIPTION.NAME" | translate',
          "templateOptions.description":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.DESCRIPTION.DESCRIPTION" | translate',
        },
      },
      {
        className: "col-xs-12",
        type: "image-upload",
        key: "image",
        templateOptions: {
          label: "",
          required: true,
          description: "",
          accept: "image/*",
          preview: true,
          maxsize: 10000
        },
        expressionProperties: {
          "templateOptions.label":
            '"UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.FILE.NAME" | translate',
        },
      },
    ];

    controller.base64ToFile = function (data,mime, filename) {
      var bstr = atob(data),
        n = bstr.length,
        u8arr = new Uint8Array(n);
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
      }
      return new File([u8arr], filename, { type: mime });
    };

    controller.onSubmit = function () {
      if (controller.form.$invalid) {
        angular
          .element("[name='" + controller.form.$name + "']")
          .find(".ng-invalid:visible:first")
          .focus();
        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
        return false;
      }


	  const image = controller.model.image
	  const data = new FormData()
	  
    const filename = `${controller.model.name}.${image.filename.split('.').pop()}`

    const model = {...controller.model, name: filename}

	  data.append('file',  controller.base64ToFile(image.base64, image.filetype, filename))
      rest
        .add(model, domainName)
        .then(function (response) {
          cdnRest
            .uploadImage({domain: domainName, lang: controller.model.lang,data:data}, (progress) => {
              controller.progressStyles = { width: progress+ '%'};
			  controller.progressValue = Math.floor(parseFloat(progress).toFixed(2));
            })
            .then(() => {
              notify.success("UI_NETWORK_ADMIN.CMS.IMAGES.SUCCESS.ADD");
              $uibModalInstance.close(response);
            });
        })
        .catch(function (error) {
          notify.error("UI_NETWORK_ADMIN.CMS.IMAGES.ERROR.ADD");
          errors.catch("", false)(error);
        });
    };

    controller.cancel = function () {
      $uibModalInstance.dismiss("cancel");
    };
  },
]);
