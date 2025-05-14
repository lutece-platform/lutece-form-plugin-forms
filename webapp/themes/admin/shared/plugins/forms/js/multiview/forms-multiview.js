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
	document.getElementById("searched_text").value = '';
	var filter = '#' + document.getElementById('formsFilter').value,
		filter_to = filter + '_to',
		filter_from = filter + '_from';
	document.querySelector(filter_from).value = '';
	document.querySelector(filter_to).value = '';
	document.getElementById("searchForm").submit();
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
	let stepList=document.querySelectorAll('#steps-content > .step'), stepLinks='<ul class="list-unstyled flex-grow-1">', active=true;
	let stepLinksContent = '';
	let isActive = true;
	stepList.forEach(function(step) {
		const pId = step.id;
		const t = step.dataset.title || '';
		const groups = step.querySelectorAll('.fieldset-group');
		let listGroup = '';
		if (groups.length > 1) {
			listGroup = '<ul>';
			groups.forEach(function(group) {
				const gId = group.id;
				const gt = group.querySelector('legend') ? group.querySelector('legend').textContent : '';
				listGroup += `<li tabindex="0"><a class="nav-link" href="#${gId}" title="${gt}"><span>${gt}</span></a></li>`;
			});
			listGroup += '</ul>';
		}
		const linkActive = isActive ? ' class="active"' : '';
		stepLinksContent += `<li${linkActive} tabindex="0"><a class="nav-link" href="#${pId}" title="${t}"><span>${t}</span></a>${listGroup}</li>`;
		isActive = false;
	});
	stepLinks = `<ul class="list-unstyled flex-grow-1">${stepLinksContent}</ul>`;
	document.querySelector('#step-toc > .nav-pills').innerHTML = stepLinks + '</ul>';
	document.body.setAttribute('data-bs-spy', 'scroll');
	document.body.setAttribute('data-bs-target', '#step-toc');
	document.body.setAttribute('data-bs-offset', '120');
	bootstrap.ScrollSpy.getOrCreateInstance(document.body);

	document.querySelectorAll('#step-toc .nav-pills .nav-link').forEach(function(link) {
		link.addEventListener('click', function(e) {
			e.preventDefault();
			var elTarget = document.querySelector(this.getAttribute('href'));
			if (elTarget) {
				elTarget.classList.add('step-active');
				window.scrollTo({
					top: elTarget.getBoundingClientRect().top + window.scrollY - 30,
					behavior: 'smooth'
				});
			}
		});
	});
}
document.addEventListener('DOMContentLoaded', function() {
	// Rewrite title
	const mainTitle = document.querySelector('#feature-title a');
	const title = document.querySelector('#lutece-main').dataset.feature;
	if (mainTitle) {
		mainTitle.textContent = title;
		mainTitle.setAttribute('href', 'jsp/admin/plugins/forms/ManageForms.jsp?plugin_name=forms');
	}

	// Set step index
	if (document.querySelectorAll('.fieldset-group').length > 0) {
		setStepsIndex();
	}

	const tocCompressToggle = document.getElementById('toc-compress-toggle');
	const tocExpandToggle = document.getElementById('toc-expand-toggle');
	const toc = document.getElementById('toc');
	const stepsContent = document.getElementById('steps-content');

	if (tocCompressToggle) {
		const tocState = sessionStorage.getItem('toc_multiview_state');
		if (tocExpandToggle) tocExpandToggle.style.display = 'none';

		tocCompressToggle.addEventListener('click', function() {
			if (toc) toc.style.display = toc.style.display === 'none' ? '' : 'none';
			if (tocExpandToggle) tocExpandToggle.style.display = '';
			if (stepsContent) {
				stepsContent.classList.toggle('col-sm-7');
				stepsContent.classList.toggle('col-sm-9');
			}
			sessionStorage.setItem('toc_multiview_state', 'expand');
		});

		if (tocExpandToggle) {
			tocExpandToggle.addEventListener('click', function() {
				if (stepsContent) {
					stepsContent.classList.toggle('col-sm-9');
					stepsContent.classList.toggle('col-sm-7');
				}
				if (toc) toc.style.display = '';
				this.style.display = 'none';
				sessionStorage.setItem('toc_multiview_state', 'compress');
			});
		}

		if (tocState === 'expand' && tocCompressToggle) {
			tocCompressToggle.click();
		}
	}

	// Set link on whole tr
	const multiFormRows = document.querySelectorAll("#multi-form-list tbody > tr");
	multiFormRows.forEach(function(row) {
		row.addEventListener('click', function(e) {
			redirectOnClick(this);
		});
	});

	// Add reset button to search text
	const searchedText = document.getElementById("searched_text");
	if (searchedText) {
		if (searchedText.value.trim() !== '') {
			const searchTextGroup = document.querySelector("#search-text .input-group-btn");
			if (searchTextGroup) {
				const btn = document.createElement('button');
				btn.type = "button";
				btn.className = "btn btn-danger";
				btn.innerHTML = '<i class="fa fa-remove"></i>';
				btn.onclick = resetForm;
				searchTextGroup.appendChild(btn);
			}
		}
	}

	const headerLink = document.querySelector("section.content-header h1 a");
	if (headerLink) {
		headerLink.addEventListener('click', function(e) {
			e.preventDefault();
			const form = document.getElementById('form-response-detail-header');
			if (form) form.submit();
			return false;
		});
	}
});
    