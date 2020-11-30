var ctx;

$(function () {
    ctx = {
        ajaxUrl: "profile/meals/",
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ],
            // "createdRow": function (row, data, dataIndex) {
            //     $(row).attr("data-mealExcess", data.excess);
            // },
        }, updateFilteredTable)
    };
    makeEditable();
});

$(function () {
    $.datetimepicker.setLocale('en');

//  http://xdsoft.net/jqplugins/datetimepicker/
    $('#dateTime').datetimepicker({
        format: 'Y-m-d H:i'
    });

    var startDate = $('#startDate');
    var endDate = $('#endDate');
    startDate.datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        formatDate: 'Y-m-d',
        onShow: function () {
            this.setOptions({
                maxDate: endDate.val() ? endDate.val() : false
            })
        }
    });
    endDate.datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        formatDate: 'Y-m-d',
        onShow: function () {
            this.setOptions({
                minDate: startDate.val() ? startDate.val() : false
            })
        }
    });

    var startTime = $('#startTime');
    var endTime = $('#endTime');
    startTime.datetimepicker({
        datepicker: false,
        format: 'H:i',
        onShow: function () {
            this.setOptions({
                maxTime: endTime.val() ? endTime.val() : false
            })
        }
    });
    endTime.datetimepicker({
        datepicker: false,
        format: 'H:i',
        onShow: function () {
            this.setOptions({
                minTime: startTime.val() ? startTime.val() : false
            })
        }
    });

});

function clearFilter() {
    $("#filter")[0].reset();
    $.get(ctx.ajaxUrl, updateTableByData);
}

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: ctx.ajaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

// http://api.jquery.com/jQuery.ajax/#using-converters
$.ajaxSetup({
    converters: {
        "text json": function (stringData) {
            var json = JSON.parse(stringData);
            $(json).each(function () {
                this.dateTime = this.dateTime.replace('T', ' ').substr(0, 16);
            });
            return json;
        }
    }
});