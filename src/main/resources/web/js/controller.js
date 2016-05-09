angular.module("app")
.controller('MainCtrl', ['$scope', '$timeout', '_', function ($scope, $timeout) {
    var socket;
    if (window.WebSocket) {
        socket = new WebSocket("ws://localhost:8080/myapp");
        socket.onmessage = function (event) {
            console.log("Received data ", event.data);
            $scope.gridData = updateGridData(event.data, $scope.gridData);
            $scope.refresh = true;
            $timeout(function() {
              $scope.refresh = false;
            }, 0);
        }
        socket.onopen = function (event) {
            alert("Web Socket opened!");
        };
        socket.onclose = function (event) {
            alert("Web Socket closed.");
        };
    } else {
        alert("Your browser does not support Websockets. (Use Chrome)");
    }

    var myData = [];
    $scope.refresh = false;
    $scope.gridData = {data: myData};
    $scope.gridData.columnDefs = [{ field: 'accountId', groupable: true },
                                  { field: 'accountName' },
                                  { field: 'productId' },
                                  { field: 'product' },
                                  { field: 'quantity', cellClass: 'cell-blue' },
                                  { field: 'price', cellClass: 'cell-blue' },
                                  { field: 'deltaPrediction', cellClass: 'cell-blue' },
                                  { field: 'holding', cellClass: 'cell-green' },
                                  { field: 'sentiment', cellClass: 'cell-green' }];
    $scope.gridData.enableFiltering = true;
    $scope.gridData.enableRowSelection = true;

    $scope.gridData.onRegisterApi = function( gridApi ) {
        gridApi.grid.registerRowBuilder(function (row, gridOptions) {
            row.isNew = true;
        });
    };
}]).controller('PageCtrl', function (/* $scope, $location, $http */) {
  console.log("Page Controller reporting for duty.");

  // Activates the Carousel
  $('.carousel').carousel({
    interval: 5000
  });

  // Activates Tooltips for Social Links
  $('.tooltip-social').tooltip({
    selector: "a[data-toggle=tooltip]"
  })
}).controller('ProductsCtrl', function($scope) {
  Highcharts.chart('container', {
    chart: {
        type: 'bar'
    },
    title: {
        text: 'Product by Account'
    },
    xAxis: {
        categories: ['XYZ Personal Account', 'Science Center', 'A1 Pension Fund']
    },
    yAxis: {
        title: {
            text: 'Product Holdings'
        }
    },
    series: [{
        name: 'Microsoft',
        data: [50, 100, 100]
    }, {
        name: 'Berkshire Hathaway',
        data: [0, 170, 100]
    }]
  })
}).controller('AssetsCtrl', function($scope) {
  Highcharts.chart('container', {
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
    },
    title: {
        text: 'Portfolio Allocation'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            }
        }
    },
    series: [{
        name: 'Brands',
        colorByPoint: true,
        data: [{
            name: 'Alcoa Inc',
            y: 473
        }, {
            name: 'Amazon',
            y: 33987.5
        }, {
            name: 'Berkshire Hathaway',
            y: 38585.7
        }, {
            name: 'Cablevision Systems',
            y: 13840
        }, {
            name: 'Goldman Sachs',
            y: 78755
        }, {
            name: 'IBM',
            y: 14734
        }, {
            name: 'Coca Cola',
            y: 23751
        }, {
            name: 'Microsoft',
            y: 12517.5,
            sliced: true,
            selected: true
        }, {
            name: 'QUALCOMM Inc',
            y: 2560
        }, {
            name: 'Starbucks',
            y: 2832
        }, {
            name: 'Visa',
            y: 3911
        }]
    }]
  })
});