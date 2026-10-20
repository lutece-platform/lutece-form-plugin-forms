function redirectOnClick( element ){
    const url = element.getAttribute("data-url");
    if (url !== null) {
        const form = document.createElement("form");
        form.action = url;
        form.method = "post";
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
    }
}

/**
 * Builds the link to a response, with the DOM: neither its label nor the identifier in its URL is read as HTML.
 */
function createNavButton( url, label, iconClass, iconFirst ){
	const link = document.createElement( 'a' );
	link.className = 'btn btn-primary btn-flat';
	link.href = url;
	const icon = document.createElement( 'i' );
	icon.className = iconClass;
	if( iconFirst ){
		link.append( icon, ' ', label );
	} else {
		link.append( label, ' ', icon );
	}
	return link;
}

/**
 * Adds the links to the previous and next responses of the current list; the labels are text.
 */
function setNavButtons( idResp, labelPrev, labelNext ){
	const respUrl=sessionStorage.getItem('multiview_base_url') + '&id_form_response=';
	const item_list=sessionStorage.getItem('multiview_current_list');
	const boxTool = document.querySelector('#info-box-header.boxTool');
	if( boxTool && item_list )	{
        var response_list=item_list.split(','), maxIdx=response_list.length - 1, currentIdx=response_list.indexOf(idResp);
        if( currentIdx > -1 ){
            if( currentIdx > 0 ){
                boxTool.append( createNavButton( respUrl + encodeURIComponent( response_list[currentIdx - 1] ), labelPrev, 'bi bi-arrow-left', true ) );
            }
            if( currentIdx != maxIdx ){
                boxTool.append( createNavButton( respUrl + encodeURIComponent( response_list[currentIdx + 1] ), labelNext, 'bi bi-arrow-right', false ) );
            }
        }
	}
}

/**
 * Swaps the icon class of a toggle button: the button carries the name, its icon is decorative.
 */
function setToggleIcon( toggle, iconAdded, iconRemoved ){
	const icon = toggle ? toggle.querySelector( 'i' ) : null;
	if ( icon ) {
		icon.classList.add( iconAdded );
		icon.classList.remove( iconRemoved );
	}
}

function setToc( toc, tocBox, tocCompressToggle, tocSideToggle, tocExpandToggle ){
	if (tocSideToggle && tocBox) {
		tocSideToggle.addEventListener('click', function() {
			if (tocBox.style.left === '85%'){
				tocBox.style.left = '0';
				setToggleIcon( tocExpandToggle, 'ti-layout-sidebar-right-expand', 'ti-layout-sidebar-left-expand' );
				setToggleIcon( tocSideToggle, 'ti-arrow-right', 'ti-arrow-left' );
				tocExpandToggle.style.left = '0';
				localStorage.setItem('toc_side_state', '0');
			} else {
				tocBox.style.left='85%';
				setToggleIcon( tocExpandToggle, 'ti-layout-sidebar-left-expand', 'ti-layout-sidebar-right-expand' );
				setToggleIcon( tocSideToggle, 'ti-arrow-left', 'ti-arrow-right' );
				tocExpandToggle.style.left = '98%';
				localStorage.setItem('toc_side_state', '98%');
			}
		});
	}
	
	if (tocCompressToggle) {
		const tocState = localStorage.getItem('toc_multiview_state');
		const tocSide = localStorage.getItem('toc_side_state');
		if (tocExpandToggle){
			tocExpandToggle.style.display = 'none';
			tocExpandToggle.style.left = tocSide;
			if (tocSide === '98%'){
				tocBox.style.left='85%';
				setToggleIcon( tocExpandToggle, 'ti-layout-sidebar-left-expand', 'ti-layout-sidebar-right-expand' );
				setToggleIcon( tocSideToggle, 'ti-arrow-left', 'ti-arrow-right' );
			} 
		} 

		// The focus follows the toggle that replaces the one hidden, on a click of the user only (not the restored state)
		tocCompressToggle.addEventListener('click', function( e ) {
			if (toc) toc.style.display = toc.style.display === 'none' ? '' : 'none';
			tocCompressToggle.setAttribute( 'aria-expanded', toc && toc.style.display !== 'none' ? 'true' : 'false' );
			if (tocExpandToggle) {
				tocExpandToggle.style.display = '';
				tocExpandToggle.setAttribute( 'aria-expanded', 'false' );
				if ( e.isTrusted ) tocExpandToggle.focus();
			}
			localStorage.setItem('toc_multiview_state', 'expand');
		});

		if (tocExpandToggle) {
			tocExpandToggle.addEventListener('click', function( e ) {
				if (toc) toc.style.display = '';
				this.style.display = 'none';
				tocCompressToggle.setAttribute( 'aria-expanded', 'true' );
				if ( e.isTrusted ) tocCompressToggle.focus();
				localStorage.setItem('toc_multiview_state', 'compress');
			});
		}
		
		if (tocState === 'expand' && tocCompressToggle) {
			tocCompressToggle.click();
		}
	}

}

/**
 * Escapes a text read from the page (a title, an identifier) before it is put in HTML.
 */
function escapeHtml( text ){
	return String( text ).replace( /&/g, '&amp;' ).replace( /</g, '&lt;' ).replace( />/g, '&gt;' ).replace( /"/g, '&quot;' ).replace( /'/g, '&#39;' );
}

function setStepsIndex(){
	let stepList=document.querySelectorAll('#steps-content > .step'), stepLinks='<ul class="list-unstyled flex-grow-1">', active=true;
	let stepLinksContent = '';
	let isActive = true;
	stepList.forEach(function(step) {
		const pId = escapeHtml( step.id );
		const t = escapeHtml( step.dataset.title || '' );
		const groups = step.querySelectorAll('.fieldset-group');
		let listGroup = '';
		if (groups.length > 1) {
			listGroup = '<ul>';
			groups.forEach(function(group) {
				const gId = escapeHtml( group.id );
				const gt = escapeHtml( group.querySelector('legend') ? group.querySelector('legend').textContent : '' );
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
	// Set step index
	const toc = document.getElementById('toc');
	if( toc != null ){
		const tocBox = document.getElementById('toc-box');
		const tocCompressToggle = document.getElementById('toc-compress-toggle');
		const tocSideToggle = document.getElementById('toc-side-toggle');
		const tocExpandToggle = document.getElementById('toc-expand-toggle');
		
		if ( document.querySelectorAll('.fieldset-group').length > 1 ) {
			setStepsIndex();
			setToc( toc, tocBox, tocCompressToggle, tocSideToggle, tocExpandToggle );
		} else {
			toc.style.display = 'none';
			tocExpandToggle.style.display = 'none';
			
		}
	}
	
	// The click on a whole row is a mouse shortcut: the first cell of each row holds the link to the response
	const multiFormRows = document.querySelectorAll("#multi-form-list tbody > tr");
	multiFormRows.forEach(function(row) {
		row.addEventListener('click', function(e) {
			if ( e.target.closest( 'a, button, input, select, textarea' ) ) {
				return;
			}
			redirectOnClick(this);
		});
	});

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
