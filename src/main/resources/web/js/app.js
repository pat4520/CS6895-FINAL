var app = angular.module('app', ['ngRoute', 'ngTouch', 'ui.grid']);

app.factory('_', ['$window',
    function($window) {
        return $window._;
    }
]);

app.directive('uiGridRow', function ($animate, $timeout) {
    return {
        priority: -1,
        link: function ($scope, $elm, $attrs) {
         $scope.$watch('row.entity', function (n, o) {
           if ($scope.row.isNew) {
             $elm.addClass('new-row');

             $timeout(function () {
               $animate.removeClass($elm, 'new-row');
             });

             $scope.row.isNew = false;
           }
         });
        }
    };
});

/**
 * Configure the Routes
 */
app.config(['$routeProvider', function ($routeProvider) {
  $routeProvider
    // Home
    .when("/", {templateUrl: "partials/home.html", controller: "PageCtrl"})
    // Pages
    .when("/portfolio/:portfolioId", {templateUrl: "partials/grid.html", controller: "MainCtrl"})
    // else 404
    .otherwise({redirectTo : "/"});
}]);
