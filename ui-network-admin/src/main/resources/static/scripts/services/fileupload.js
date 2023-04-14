'use strict';

angular.module('lithium')
.factory('file-upload', ['$http', function ($http) {
	var service = {};
	service.uploadFileToUrl = function(file, uploadUrl, extraKeyVals){
		var fd = new FormData();
		var i=0;
		for(i; i < extraKeyVals.length; ++i) {
			fd.append(extraKeyVals[i].key, extraKeyVals[i].value)
		}
		fd.append('image', file);
		var uploadPromise = $http.post(uploadUrl, fd, {
			transformRequest: angular.identity,
			headers: {'Content-Type': undefined, 'Transfer-Encoding': 'chunked'}
		});
		return uploadPromise;
	}
	return service;
}]);
