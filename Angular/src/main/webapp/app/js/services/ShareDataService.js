'use strict';

// AngularJS; Best Practice: Prefer using the definition object over returning a function
var angularPOC = angular.module('angularPOC');
angularPOC.factory('ShareDataService', function() {
	var person = '';

	var service = {
		getPerson : function() {
			return person;
		},
		setPerson : function(personJson) {
			person = personJson;
		},
	};

	return service;
});
