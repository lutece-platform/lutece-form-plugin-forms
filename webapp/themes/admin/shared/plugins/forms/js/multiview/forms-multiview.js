/**
 * Enable drag and drop reordering of the multiview response table columns.
 * Only columns backed by questions (carrying a data-question-ids attribute) can be dragged.
 * When the order changes it is persisted through an AJAX POST to MultiviewForms.jsp (action doReorderMultiviewColumns),
 * updating the multiview_column_order_XX value of each impacted question.
 * The call carries the CSRF token found in the data-reorder-token attribute of #multi-form-list. A token is used once:
 * each response returns the token of the next call, kept in that attribute.
 *
 * @param {string} reorderUrl the JSP url handling the doReorderMultiviewColumns action
 */
function initMultiviewColumnDragAndDrop( reorderUrl ){
	const table = document.querySelector('#multi-form-list table');
	if ( !table || !table.tHead || !table.tHead.rows.length ) {
		return;
	}

	const headerRow = table.tHead.rows[0];
	let draggedIndex = null;
	// The calls are sent one after the other: each one carries the token returned by the previous one (a token is
	// used once, two quick drags sending the same token made the second one refused and the page stuck until reload)
	let pendingCall = Promise.resolve();

	function persistColumnOrder(){
		pendingCall = pendingCall.then(sendColumnOrder);
	}

	function sendColumnOrder(){
		const params = new URLSearchParams();
		params.append('action_doReorderMultiviewColumns', '1');
		const tokenHolder = document.getElementById('multi-form-list');
		params.append('token', tokenHolder ? tokenHolder.dataset.reorderToken || '' : '');
		// The visible order of the columns becomes the new multiview_column_order value of each question
		Array.from(headerRow.cells).forEach(function(th, position){
			const ids = th.getAttribute('data-question-ids');
			if ( ids ) {
				ids.split(',').filter(Boolean).forEach(function(id){
					params.append('multiview_column_order_' + id, position);
				});
			}
		});

		return fetch(reorderUrl, {
			method: 'POST',
			headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
			body: params.toString()
		}).then(function(response){
			return response.json();
		}).then(function(json){
			// Keep the token of the next reordering
			const result = json && json.result;
			if ( tokenHolder && result && result.token ) {
				tokenHolder.dataset.reorderToken = result.token;
			}
		}).catch(function(){ /* silently ignore : order is best effort and will be recomputed on reload */ });
	}

	function moveColumn( fromIndex, toIndex ){
		if ( fromIndex === toIndex ) {
			return;
		}
		const rows = [ headerRow ].concat(Array.from(table.tBodies).flatMap(tb => Array.from(tb.rows)));
		rows.forEach(function(row){
			if ( row.cells.length <= Math.max(fromIndex, toIndex) ) {
				return;
			}
			const moved = row.cells[fromIndex];
			const reference = row.cells[toIndex];
			if ( fromIndex < toIndex ) {
				reference.after(moved);
			} else {
				reference.before(moved);
			}
		});
	}

	Array.from(headerRow.cells).forEach(function(th){
		if ( !th.hasAttribute('data-question-ids') ) {
			return;
		}
		th.setAttribute('draggable', 'true');
		th.classList.add('multiview-draggable-col');
		th.style.cursor = 'move';

		th.addEventListener('dragstart', function(e){
			draggedIndex = th.cellIndex;
			e.dataTransfer.effectAllowed = 'move';
			th.classList.add('dragging');
		});

		th.addEventListener('dragend', function(){
			draggedIndex = null;
			headerRow.querySelectorAll('th').forEach(function(cell){
				cell.classList.remove('dragging', 'drag-over');
			});
		});

		th.addEventListener('dragover', function(e){
			if ( draggedIndex === null ) {
				return;
			}
			e.preventDefault();
			e.dataTransfer.dropEffect = 'move';
			th.classList.add('drag-over');
		});

		th.addEventListener('dragleave', function(){
			th.classList.remove('drag-over');
		});

		th.addEventListener('drop', function(e){
			e.preventDefault();
			th.classList.remove('drag-over');
			if ( draggedIndex === null || draggedIndex === th.cellIndex ) {
				return;
			}
			moveColumn(draggedIndex, th.cellIndex);
			persistColumnOrder();
		});
	});
}

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

function setNavButtons( idResp ){
	const baseUrl = sessionStorage.getItem('multiview_base_url');
	const itemList = sessionStorage.getItem('multiview_current_list');
	const nav = document.getElementById('response-nav');
	if ( !nav || !baseUrl || !itemList ) return;
	// The list holds the ids of the list page the user came from; offset and total place it in the whole result
	const responseList = itemList.split(',');
	const currentIdx = responseList.indexOf( idResp );
	if ( currentIdx < 0 ) return;
	const offset = parseInt( sessionStorage.getItem('multiview_offset'), 10 ) || 0;
	const total = parseInt( sessionStorage.getItem('multiview_total'), 10 ) || responseList.length;
	const respUrl = baseUrl + '&id_form_response=';
	const icon = name => `<i class="ti ti-${name}" aria-hidden="true"></i>`;
	const make = ( idx, html ) => {
		const el = document.createElement( idx >= 0 && idx < responseList.length ? 'a' : 'span' );
		el.className = 'btn btn-light';
		if ( el.tagName === 'A' ) {
			el.href = respUrl + encodeURIComponent( responseList[idx] );
		} else {
			el.classList.add('disabled');
			el.setAttribute('aria-disabled', 'true');
		}
		el.innerHTML = html;
		return el;
	};
	const prev = make( currentIdx - 1, icon('chevron-left') + ' ' );
	prev.append( nav.dataset.labelPrevious || '' );
	const position = document.createElement('span');
	position.className = 'btn btn-light disabled text-body';
	position.setAttribute('aria-current', 'page');
	position.textContent = ( offset + currentIdx + 1 ) + ' / ' + total;
	const next = make( currentIdx + 1, '' );
	next.append( ( nav.dataset.labelNext || '' ) + ' ' );
	next.insertAdjacentHTML( 'beforeend', icon('chevron-right') );
	nav.replaceChildren( prev, position, next );
}

/**
 * Sticks a toolbar right under the page header when the header is sticky (response view), under the top bar otherwise
 * (responses list, small screens). The header height depends on its title: it is measured instead of using a fixed offset
 * @param {HTMLElement} toolbar the toolbar to stick
 */
function setStickyToolbar( toolbar ){
	const header = document.getElementById('page-header');
	const navbar = document.querySelector('.sticky-top');
	const update = function() {
		let top = navbar ? navbar.offsetHeight : 0;
		// The page header sticks right under the top bar, whose height depends on the admin layout
		document.documentElement.style.setProperty('--forms-header-top', top + 'px');
		if (header && getComputedStyle(header).position === 'sticky') {
			top += header.offsetHeight;
		}
		// On the root: the response sidebar sticks at the same height
		document.documentElement.style.setProperty('--forms-toolbar-top', top + 'px');
	};
	update();
	if (window.ResizeObserver) {
		const observer = new ResizeObserver(update);
		if (header) observer.observe(header);
		if (navbar) observer.observe(navbar);
	}
	window.addEventListener('resize', update);
}

/**
 * Fills the step select placed above #steps-content: one option per step, plus one per group when a step has several,
 * scrolls to the chosen one, and follows the reading position while the user scrolls
 * @param {HTMLSelectElement} select the select to fill
 */
function setStepsIndex( select ){
	// Where the content starts under the sticky header and toolbar
	const toolbar = document.getElementById('response-toolbar');
	const contentTop = function() { return toolbar ? toolbar.getBoundingClientRect().bottom + 8 : 30; };
	const targets = [];
	document.querySelectorAll('#steps-content > .step').forEach(function(step) {
		targets.push({ el: step, label: step.dataset.title || '' });
		const groups = step.querySelectorAll('.fieldset-group');
		if (groups.length > 1) {
			groups.forEach(function(group) {
				const legend = group.querySelector('legend');
				targets.push({ el: group, label: '\u00a0\u00a0\u2014 ' + (legend ? legend.textContent.trim() : '') });
			});
		}
	});
	if (targets.length < 2) {
		return false;
	}
	targets.forEach(function(target, index) {
		select.add(new Option(target.label, index));
	});

	let scrolling = false;
	select.addEventListener('change', function() {
		const target = targets[this.value];
		if (target) {
			scrolling = true;
			window.scrollTo({ top: target.el.getBoundingClientRect().top + window.scrollY - contentTop( ), behavior: 'smooth' });
			target.el.focus({ preventScroll: true });
			window.addEventListener('scrollend', function() { scrolling = false; }, { once: true });
			setTimeout(function() { scrolling = false; }, 1000);
		}
	});

	// Keep the select on the step or group being read
	let ticking = false;
	window.addEventListener('scroll', function() {
		if (scrolling || ticking) return;
		ticking = true;
		requestAnimationFrame(function() {
			let current = 0;
			const threshold = contentTop( ) + 40;
			targets.forEach(function(target, index) {
				if (target.el.offsetParent !== null && target.el.getBoundingClientRect().top <= threshold) current = index;
			});
			// At the bottom of the page the last steps never reach the top: keep a chosen one that is on screen
			const chosen = targets[select.value];
			if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 2 && chosen && select.value > current
				&& chosen.el.getBoundingClientRect().top < window.innerHeight) {
				current = select.value;
			}
			select.value = current;
			ticking = false;
		});
	}, { passive: true });
	return true;
}

document.addEventListener('DOMContentLoaded', function() {
	// Toolbars kept under the page header: the steps of a response, the filters of the responses list
	document.querySelectorAll('.forms-sticky-toolbar').forEach(setStickyToolbar);

	// Step index: a select above the steps of a response
	const stepSelect = document.getElementById('step-toc-select');
	if (stepSelect != null && setStepsIndex(stepSelect)) {
		document.getElementById('step-toc').hidden = false;
	}
	
	// Sort links: the core sort macro ends them with #sort_<attribute>, which makes the page jump to the column
	// header after the reload. The list has a single table, so the anchor is not needed to find the sorted column
	// (admin-table-sort.js falls back to the sorted_attribute_name parameter): drop it, and drop it from an URL
	// that already carries it.
	document.querySelectorAll('#multi-form-list .lutece-table-sort a.dropdown-item').forEach(function(link) {
		const href = link.getAttribute('href');
		if (href && href.includes('#')) link.setAttribute('href', href.substring(0, href.indexOf('#')));
	});
	if (window.location.hash.startsWith('#sort_') && document.querySelector('#multi-form-list')) {
		history.replaceState(null, '', window.location.pathname + window.location.search);
		window.scrollTo(0, 0);
	}

	// Set link on whole tr
	const multiFormRows = document.querySelectorAll("#multi-form-list tbody > tr");
	multiFormRows.forEach(function(row) {
		row.addEventListener('click', function(e) {
			if (e.target.closest('a, button, input, select, label')) return;
			redirectOnClick(this);
		});
	});

	// Enable drag and drop reordering of the response table columns
	initMultiviewColumnDragAndDrop("jsp/admin/plugins/forms/MultiviewForms.jsp");

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
