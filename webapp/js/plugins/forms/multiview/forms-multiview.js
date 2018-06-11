$(document).ready(function() {
	// Set link on whole tr
	$("tbody > tr").on( 'click', function(e){
		redirectOnClick(this);
	});
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