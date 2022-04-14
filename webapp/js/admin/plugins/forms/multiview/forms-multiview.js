function redirectOnClick( element ){
	if ( $(element).attr("data-url") != undefined ){
		var form = $('<form>', 
			{ 'action':$(element).attr("data-url"), 'method':'post' }
		);
		form.appendTo('body');
		form.submit().remove();
	}
}

function resetForm( ){
	$("#searched_text").val('');
	var filter='#' + $('#formsFilter').val(), 
		filter_to=filter+'_to', 
		filter_from=filter+'_from';
	$(filter_from).val ('');
  	$(filter_to).val ('');
	$("#searchForm").submit();
}

function setNavButtons( idResp, labelPrev, labelNext ){
	const respUrl=sessionStorage.getItem('multiview_base_url') + '&id_form_response=';
	const item_list=sessionStorage.getItem('multiview_current_list'), boxTool=$('#info-box-header .box-tools');
	var response_list=item_list.split(','), maxIdx=response_list.length - 1, currentIdx=response_list.indexOf(idResp);
	var nNext=currentIdx+1, nPrev=currentIdx-1;
	if( currentIdx > -1 ){
		if( currentIdx > 0 ){
			nPrev=response_list[nPrev];
			boxTool.append( '<a class="btn btn-primary btn-flat" href="' + respUrl + nPrev  + '">'+labelPrev+'</a>' );
		}
		if( currentIdx != maxIdx ){
			nNext=response_list[nNext];
			boxTool.append( '<a class="btn btn-primary btn-flat" href="' + respUrl + nNext  + '">'+labelNext+'</a>' );
		}
	}
}

function setStepsIndex(){
	var stepList=$('.step-group'), stepLinks='<ul class="list-unstyled flex-grow-1">', active=true;
    stepList.each( function( ){
		var pId = $(this).attr('id'), 
		t=  $(this).data('title');
		var groups = $(this).find('fieldset'), listGroup='<ul>';
		if( groups.length > 1 ){
			groups.each( function( ){
				var gId = $(this).attr('id'), 
				gt=$(this).find('legend').text();
				listGroup += '<li tabindex=0><a class="nav-link" href="#' + gId + '" title="' + gt + '"><span>' + gt + '</span></a></li>';
			});
		}
		listGroup +='</ul>';
		let linkActive = active ? ' class="active"' : ''
        stepLinks += '<li' + linkActive + ' tabindex=0><a class="nav-link" href="#' + pId + '" title="' + t + '"><span>' + t + '</span></a>' + listGroup + '</li>';
		active=false;
	});
	$('#step-toc > .nav-pills').html( stepLinks + '</ul>' );
	$('body').scrollspy({ target:'#step-toc', offset: 120 });
	$('#step-toc .nav-pills .nav-link').click( function(e){
		e.preventDefault();
		var elTarget=$(this).attr( 'href' );
		$( elTarget ).addClass('step-active');
		$('html, body').animate( {scrollTop:( $(elTarget).offset().top - 30 )}, 500);
	});
}

$( function() {
	// Set step index
	if( $('.step-group').length > 0 ){
		setStepsIndex();
	}

	if( $('#toc-compress-toggle').length > 0 ){
		const tocState=sessionStorage.getItem('toc_multiview_state');
		$('#toc-expand-toggle').toggle();
		$('#toc-compress-toggle').click( function(){
			$('#toc').toggle();
			$('#toc-expand-toggle').toggle();
			
			$('#steps-content').toggleClass('col-sm-7').toggleClass('col-sm-9');
			sessionStorage.setItem('toc_multiview_state', 'expand' );
		});

		$('#toc-expand-toggle').click( function(){
			$('#steps-content').toggleClass('col-sm-9').toggleClass('col-sm-7');
			$('#toc').toggle();
			$(this).toggle();
			sessionStorage.setItem('toc_multiview_state', 'compress' );
		});

		if( tocState ==='expand' ){ $('#toc-compress-toggle').click() } ;
	}

	// Set link on whole tr
	$("#multi-form-list tbody > tr").on( 'click', function(e){
		redirectOnClick(this);
	});

	// Add reset button to search text
	if( $("#searched_text").length > 0 ){
		if( $("#searched_text").val() != '' ){
			var q = $("#searched_text").val().trim();
			if ( q !='' ){
				$("#search-text .input-group-btn > button").after('<button type="button" onclick="resetForm();" class="btn btn-danger"><i class="fa fa-remove"></i></button>');
			}
		}
	}

	$("section.content-header h1 a").click(function() {
		$('#form-response-detail-header').submit();
		return false;
	});

});
    