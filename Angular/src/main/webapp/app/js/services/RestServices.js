'use strict';

// AngularJS; Best Practice: Prefer using the definition object over returning a
// function
var angularPOC = angular.module('angularPOC');
angularPOC
		.factory(
				'RestServices',
				[
						'$http',
						'ShareDataService',
						function($http, ShareDataService) {

							var service = {
								getHello : function(successCallback,
										failCallback) {

									// Modify header 'Content-Type' to affect
									// which REST API
									// method will answer to the call (see
									// RestJson in JavaEERest)
									// 'data' needs to be specified => even when
									// empty the call must
									// have ''
									$http(
											{
												url : 'http://localhost:8080/jeer/rest/phonebook?param=value',
												dataType : 'text',
												method : 'GET',
												data : '',
												headers : {
													"Content-Type" : "text/plain"
												}
											})
											.success(
													function(data, status) {

														console
																.log("success: "
																		+ JSON
																				.stringify(data.responseStatus));
														successCallback(data);
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								mockHello : function(successCallback,
										failCallback) {

									successCallback("mock ok");
								},
								getAllPersons : function(successCallback,
										failCallback, criteriaString,
										criteriaType) {
									var requestUrl = 'http://localhost:8080/jeer/rest/phonebook';
									if (typeof(criteriaString) != 'undefined'
											&& typeof(criteriaType) != 'undefined') {
										requestUrl = requestUrl
												+ "?criteriaString="
												+ criteriaString
												+ "&criteriaType="
												+ criteriaType;
									}
									console.log("RequestUrl to get persons list = " + requestUrl);
									// Modify header 'Content-Type' to affect
									// which REST API
									// method will answer to the call (see
									// RestJson in JavaEERest)
									// 'data' needs to be specified => even when
									// empty the call must
									// have ''
									$http({
										url : requestUrl,
										dataType : 'json',
										method : 'GET',
										data : '',
										headers : {
											"Content-Type" : "application/json"
										}
									})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														// MessageController.setMessage(data.description,
														// data.responseStatus);
														if (data.responseStatus == "OK") {
															console
																	.log("success: "
																			+ data.responseStatus);
															successCallback(data);
														} else {
															console
																	.log("success, but responseStatus was: "
																			+ data.responseStatus);
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								getSinglePerson : function(successCallback,
										failCallback, id) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/person?personId='
														+ id
														+ "&phoneNumbers=true",
												dataType : 'json',
												method : 'GET',
												data : '',
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								getPersonPhoneNumbers : function(
										successCallback, failCallback, id) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/person?personId='
														+ id
														+ "&phoneNumbers=true",
												dataType : 'json',
												method : 'GET',
												data : '',
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								addPerson : function(successCallback,
										failCallback, data) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/person?param=value',
												dataType : 'json',
												method : 'POST',
												data : data,
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								removePerson : function(successCallback,
										failCallback, id) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/person?personId='
														+ id,
												dataType : 'json',
												method : 'DELETE',
												data : '',
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								savePersonChanges : function(successCallback,
										failCallback, data) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/phonebook?param=value',
												dataType : 'json',
												method : 'PUT',
												data : data,
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								savePhoneNumberChanges : function(successCallback,
										failCallback, data, personId) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/person?personId='+personId,
												dataType : 'json',
												method : 'PUT',
												data : data,
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								getEmptyPerson : function(successCallback,
										failCallback) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/person?personId=-1',
												dataType : 'json',
												method : 'GET',
												data : '',
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														console
																.log("success: "
																		+ data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								addPhoneNumber : function(successCallback,
										failCallback, data, personId) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/phonenumber?personId='
														+ personId,
												dataType : 'json',
												method : 'POST',
												data : data,
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								removePhoneNumber : function(successCallback,
										failCallback, id) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/phonenumber?numberId='
														+ id,
												dataType : 'json',
												method : 'DELETE',
												data : '',
												headers : {
													"Content-Type" : "application/json"
												}
											})
											.success(
													function(data, status) {

														ShareDataService
																.setMessage(
																		data.description,
																		data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {
														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								getPhonenumberTypes : function(successCallback,
										failCallback) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/phonenumber?param=value',
												dataType : 'json',
												method : 'GET',
												data : '',
												headers : {
													"Content-Type" : "text/plain"
												}
											})
											.success(
													function(data, status) {

														console
																.log("success: "
																		+ data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {

														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								},
								getCriteriaTypes : function(successCallback,
										failCallback) {

									$http(
											{
												url : 'http://localhost:8080/jeer/rest/phonebook?param=value',
												dataType : 'text',
												method : 'GET',
												data : '',
												headers : {
													"Content-Type" : "text/plain"
												}
											})
											.success(
													function(data, status) {

														console
																.log("success: "
																		+ data.responseStatus);
														if (data.responseStatus == "OK") {
															successCallback(data);
														} else {
															console
																	.log("ErrorDescription: "
																			+ data.description);
															failCallback();
														}
													})
											.error(
													function(status) {

														console
																.log("error: "
																		+ JSON
																				.stringify(status));
														failCallback();
													});
								}
							};
							return service;
						} ]);
