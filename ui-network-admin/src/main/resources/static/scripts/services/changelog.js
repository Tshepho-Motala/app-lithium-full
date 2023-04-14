'use strict'

angular.module('changelogService', [])
    .factory('$changelogService',['UserRest', function(userRest) {
        const service = {}

        service.mapAuthorNameToChangeLogs = async (domainName, changeLogs) => {
            try {

                if(!Array.isArray(changeLogs) || changeLogs.length === 0) {
                    return []
                }

                const response =  await userRest.findUsersByGuidsOrUsernames(domainName, changeLogs.map(cl => cl.authorGuid))

                const authors = response.plain()

                return changeLogs.map(cl => {

                    if(cl.authorFullName) {
                        return cl;
                    }

                    let  author = authors.find(a => a.guid === cl.authorGuid);

                    if(!author) {
                        author = authors.find(a => a.username === cl.authorGuid);
                    }

                    const fullname = author ? `${author.firstName} ${author.lastName}` : 'System';

                    cl.authorFullName =  fullname

                    return cl;
                });
            }
            catch(e) {
                console.log(e)
            }
        }
        return service;
    }]);