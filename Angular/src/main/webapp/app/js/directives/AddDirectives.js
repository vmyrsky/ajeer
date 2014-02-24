'use strict';
// http://docs.angularjs.org/guide/directive

// AngularJS; Best Practice: Prefer using the definition object over returning a
// function

// AngularJS; Best Practice: In order to avoid collisions with some future
// standard, it's best to prefix your own directive names. For instance, if you
// created a <carousel> directive, it would be problematic if HTML7 introduced
// the same element. A two or three letter prefix (e.g. btfCarousel) works well.
// Similarly, do not prefix your own directives with ng or they might conflict
// with directives included in a future version of Angular

// Note: When you create a directive, it is restricted to attribute only by
// default. In order to create directives that are triggered by element name,
// you need to use the restrict option
var angularPOC = angular.module('angularPOC');
angularPOC.directive('addEditTextFields', [function(scope, element) {

	var directive = {
		restrict : 'E',
		replace: true,
		templateUrl : '/Angular/app/views/templates/addEditTextFields.html',
		scope : {
			// Note: These =attr attributes in the scope option of
			// directives are normalized just like directive names.
			// To bind to the attribute in <div bind-to-this="thing">,
			// you'd specify a binding of =bindToThis
			// Note: Using @style1 refers to the exact value
			field1 : '=field1',
			field2 : '=field2',
			style1 : '@style1',
			style2 : '@style2',
		},
		link : function (scope, element, attrs) {
			scope.editable = false;
		    scope.setEditable = function(editable) {
		        scope.editable = editable;
		    };
		}
	};
	return directive;
} ]);

// AngularJS; Best Practice: Unless your template is very small, it's typically
// better to break it apart into its own HTML file and load it with the
// templateUrl option

// AngularJS; Best Practice: Directives should clean up after themselves. You
// can use element.on('$destroy', ...) or scope.$on('$destroy', ...) to run a
// clean-up function when the directive is removed.
