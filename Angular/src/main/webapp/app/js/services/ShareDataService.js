'use strict';

// AngularJS; Best Practice: Prefer using the definition object over returning a
// function
var angularPOC = angular.module('angularPOC');
angularPOC.factory('ShareDataService', [ '$timeout', function($timeout) {
	var person = '';
    var useDemoData = false;
	var messages = [ {
		'text' : "from share data",
		'type' : "OK"
	}, {
		'text' : "from share data",
		'type' : "WARNING"
	}, {
		'text' : "from share data",
		'type' : "ERROR"
	} ];

	var service = {
		getPerson : function() {
			return person;
		},
		setPerson : function(personJson) {
			person = personJson;
		},
		getMessages : function() {
			return messages;
		},
		setMessage : function(messageText, messageType) {

            // timeout prevents digest problems
			$timeout(function() {
				messages = [];
				messages.push({
					'text' : messageText,
					'type' : messageType
				});
			});
		},
		addMessage : function(messageText, messageType) {
			
            // timeout prevents digest problems
			$timeout(function() {
				messages.push({
					'text' : messageText,
					'type' : messageType
				});
			});
		},
		clearMessages : function() {
			messages = [];
		}
	};
	
	if (useDemoData) {
		service.messages = messages;
	} else {
		service.clearMessages();
	}

	return service;
} ]);
