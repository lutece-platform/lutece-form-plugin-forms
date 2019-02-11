$( function() {
	// Set link on whole tr
	$("tbody > tr").on( 'click', function(e){
		redirectOnClick(this);
	});
	
	var q = $("#searched_text").val().trim();
	if ( q !='' ){
		$("#search-text .input-group-btn > button").after('<button type="button" onclick="resetForm();" class="btn btn-danger"><i class="fa fa-remove"></i></button>');
	}
});
    	
function redirectOnClick( element ){
	if ( $(element).attr("data-url") != undefined ){
		var form = $('<form>', 
			{ 'action':$(element).attr("data-url"), 'method':'post' }
		);
		form.appendTo('body');
		form.submit().remove();
	}
};

function resetForm( ){
	$("#searched_text").val('');
	var filter='#' + $('#formsFilter').val(), filter_to=filter+'_to', filter_from=filter+'_from';
	$(filter_from).val ('');
  	$(filter_to).val ('');
	$("#searchForm").submit();
};
