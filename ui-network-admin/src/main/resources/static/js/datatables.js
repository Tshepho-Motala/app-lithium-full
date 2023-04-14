function _t(key, eng) {
	return eng;
};

var datatable_language = {
	"emptyTable":     _t('Datatable.emptyTable', 'No data available in table'),
	"info":           _t('Datatable.sInfo', 'Showing _START_ to _END_ of _TOTAL_ entries'),
	"infoEmpty":      _t('Datatable.infoEmpty', 'Showing 0 to 0 of 0 entries'),
	"infoFiltered":   _t('Datatable.infoFiltered', '(filtered from _MAX_ total entries)'),
	"lengthMenu":     _t('Datatable.sLengthMenu', 'Show _MENU_ entries'),
	"loadingRecords": _t('Datatable.loadingRecords', 'Loading...'),
	"processing":     _t('Datatable.processing', 'Processing...'),
	"search":         _t('Datatable.search', 'Search:'),
	"zeroRecords":    _t('Datatable.zeroRecords', 'No matching records found'),
	"paginate": {
		"first":      _t('Button.First', 'First'),
		"last":       _t('Button.Last', 'Last'),
		"next":       _t('Button.Next', 'Next'),
		"previous":   _t('Button.Previous', 'Previous')
	},
	"aria": {
		"sortAscending":  _t('Datatable.sortAscending', ': activate to sort column ascending'),
		"sortDescending": _t('Datatable.sortDescending', ': activate to sort column descending')
	}
};

function datatable_ajax(jqueryelement, url, columns, options) {	
	$(function () {

		var coreOptions = {
			"lengthMenu": [ 10, 50, 100, 200, 500 ],
			"pageLength": 100,
			"processing": "true",
			"serverSide": "true",
			"ajax": url,
			"paging": true,
			"searching": true,
			"ordering": true,
			"info": true,
			"rowId": "id",
			"autoWidth": true,
			"scrollX": true,
			"lengthChange": true,
//			"language": datatable_language,
			"columns": columns
		}

		$.extend(true, coreOptions, options);

		var table = jqueryelement.DataTable(coreOptions);
		
		if (options.clickUrl) {
			$('tbody', jqueryelement).on( 'click', 'tr', function () {
				var id = table.row( this ).id();
				if (id === undefined)
					return;
				var url = id + '/' + options.clickUrl;
				if (options.popup) {
					window.open(url, '_blank');
				} else {
					window.location = url;
				}
			});
		}
		
		if (options.clickCallback) {
			$('tbody', jqueryelement).on( 'click', 'tr', function () {
				var id = table.row( this ).id();
				options.clickCallback(id);
			});
		}

		if (options.addUrl) {
			var th = $('.dataTables_length', table.table().container() );
//			th = $('.row:first div:first', table.table().container() );
			th.prepend('<a href="'+options.addUrl+'" class="btn btn-default"><i class="fa fa-pencil"></i> '
					+ _t('Datatable.add', 'Add') 
					+'</a> &nbsp; &nbsp;');
		}
		
//		var s = $('.dataTables_scroll', table.table().container() );
//		s.css("padding-bottom", "10px");
		
	});
}

function datatable_ajax_clientside(jqueryelement, url, columns, options) {	
	$(function () {

		var coreOptions = {
			"lengthMenu": [ 10, 50, 100, 200, 500 ],
			"pageLength": 100,
			"processing": "true",
			"ajax": url,
			"paging": true,
			"searching": true,
			"ordering": true,
			"info": true,
			"row v-reset-row Id": "id",
			"autoWidth": true,
			"scrollX": true,
			"lengthChange": true,
//			"language": datatable_language,
			"columns": columns
		}

		$.extend(true, coreOptions, options);

		var table = jqueryelement.DataTable(coreOptions);
		
		if (options.clickUrl) {
			$('tbody', jqueryelement).on( 'click', 'tr', function () {
				var id = table.row( this ).id();
				if (id === undefined)
					return;
				var url = id + '/' + options.clickUrl;
				if (options.popup) {
					window.open(url, '_blank');
				} else {
					window.location = url;
				}
			});
		}
		
		if (options.clickCallback) {
			$('tbody', jqueryelement).on( 'click', 'tr', function () {
				var id = table.row( this ).id();
				options.clickCallback(id);
			});
		}

		if (options.addUrl) {
			var th = $('.dataTables_length', table.table().container() );
//			th = $('.row:first div:first', table.table().container() );
			th.prepend('<a href="'+options.addUrl+'" class="btn btn-default"><i class="fa fa-pencil"></i> '
					+ _t('Datatable.add', 'Add') 
					+'</a> &nbsp; &nbsp;');
		}
				
	});
}
