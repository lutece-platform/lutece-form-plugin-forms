/**
 * Form builder - drag and drop edition of the steps and fields of a form.
 *
 * Every change is sent to ManageFormBuilder.jsp (action builderOperation), one at a time: the server answers the
 * refreshed fragments and the security token of the next change. Field properties are edited with the existing
 * screens, loaded in the side panel iframe.
 */
const CONTROLLER_URL = 'jsp/admin/plugins/forms/ManageFormBuilder.jsp';
const ACTION_OPERATION = 'builderOperation';
const LIST_PAGES = [
    { path: 'ManageQuestions.jsp', views: [null, 'manageQuestions'] },
    { path: 'ManageSteps.jsp', views: [null, 'manageSteps'] },
    { path: 'ManageForms.jsp', views: null }
];
const LAST_POSITION = 2147483647;
const EDITOR_FRAME_NAME = 'fbEditorFrame';
const TOP_TARGETS = ['_top', '_parent'];
const STEP_ORDER_TRANSITIONS = 'transitions';
const STEP_ORDER_STORAGE_KEY = 'lutece-forms-builder-step-order';

export default class FormBuilder {

    /**
     * @param {HTMLElement} root - the builder row, carrying the form, the step, the token and the messages
     */
    constructor(root) {
        this.root = root;
        this.formId = root.dataset.formId;
        this.stepId = root.dataset.stepId;
        this.token = root.dataset.token;
        this.maxIdDisplay = parseInt(root.dataset.maxIdDisplay, 10) || 0;
        this.queue = Promise.resolve();
        this.sortables = [];
        this.pendingCreation = null;
        this.editor = null;
        this.confirmCallback = null;

        this.stepsContainer = document.getElementById('fb-steps');
        this.canvasContainer = document.getElementById('fb-canvas');
        this.palette = document.getElementById('fb-palette');
        this.paletteList = document.getElementById('fb-palette-list');
        this.paletteSearch = document.getElementById('fb-palette-search');
        this.editorBox = document.getElementById('fb-editor');
        this.editorFrame = document.getElementById('fb-editor-frame');
        this.panelTitle = document.getElementById('fb-panel-title');
        this.panelClose = document.getElementById('fb-panel-close');
        this.confirmModal = document.getElementById('fb-confirm-modal');
        this.stepOrderButton = document.getElementById('fb-step-order');
        this.importModal = document.getElementById('fb-import-modal');
        this.importStepAfter = 0;
        this.templateModal = document.getElementById('fb-template-modal');
        this.stepOrder = this.readStepOrder();

        this.bindEvents();
        this.initSortables();
        this.updateStepOrderButton();
        if (this.stepOrder === STEP_ORDER_TRANSITIONS) {
            this.refresh();
        }
    }

    /**
     * @returns {string} the order of the step list remembered for this browser, empty for the creation order
     */
    readStepOrder() {
        try {
            return window.localStorage.getItem(STEP_ORDER_STORAGE_KEY) || '';
        } catch (e) {
            return '';
        }
    }

    /**
     * Switches the step list between the creation order and the transition order, and remembers the choice.
     */
    toggleStepOrder() {
        this.stepOrder = this.stepOrder === STEP_ORDER_TRANSITIONS ? '' : STEP_ORDER_TRANSITIONS;
        try {
            window.localStorage.setItem(STEP_ORDER_STORAGE_KEY, this.stepOrder);
        } catch {
        }
        this.updateStepOrderButton();
        this.refresh();
    }

    /**
     * Reflects the current step order on the toggle button (pressed state and title).
     */
    updateStepOrderButton() {
        if (!this.stepOrderButton) {
            return;
        }
        const pressed = this.stepOrder === STEP_ORDER_TRANSITIONS;
        this.stepOrderButton.setAttribute('aria-pressed', String(pressed));
        this.stepOrderButton.classList.toggle('active', pressed);
        this.stepOrderButton.title = pressed ? this.stepOrderButton.dataset.titleOn : this.stepOrderButton.dataset.titleOff;
    }

    /**
     * Binds the delegated click, keyboard and search handlers, the iframe load handler and the confirmation modal.
     */
    bindEvents() {
        document.addEventListener('click', event => this.onClick(event));
        this.root.addEventListener('dblclick', event => {
            const node = event.target.closest('.fb-node');
            if (node && !event.target.closest('button, a')) {
                this.openEditor(node.dataset.editUrl, node.dataset.title, node.dataset.displayId);
            }
        });
        this.paletteList.addEventListener('keydown', event => {
            const item = event.target.closest('.fb-palette-item');
            if (item && (event.key === 'Enter' || event.key === ' ')) {
                event.preventDefault();
                this.openCreation(item, 0, LAST_POSITION);
            }
        });
        this.paletteSearch.addEventListener('input', () => this.filterPalette());
        this.panelClose.addEventListener('click', () => this.closeEditor());
        this.stepOrderButton?.addEventListener('click', () => this.toggleStepOrder());
        document.getElementById('fb-import-ok')?.addEventListener('click', () => this.submitImport());
        document.getElementById('fb-template-ok')?.addEventListener('click', () => this.submitTemplate());
        this.templateModal?.addEventListener('change', () => {
            document.getElementById('fb-template-ok').disabled = !this.templateModal.querySelector('input[name="fb_template"]:checked');
        });
        this.editorFrame.addEventListener('load', () => this.onEditorLoad());
        document.getElementById('fb-confirm-ok').addEventListener('click', () => {
            this.getModal()?.hide();
            if (this.confirmCallback) {
                this.confirmCallback();
                this.confirmCallback = null;
            }
        });
        window.addEventListener('popstate', () => {
            const stepId = new URL(window.location.href).searchParams.get('id_step');
            if (stepId && stepId !== this.stepId) {
                this.switchStep(stepId, false);
            }
        });
    }

    /**
     * Dispatches a click on a builder control (data-fb-action), a step link or a palette item.
     * @param {MouseEvent} event
     */
    onClick(event) {
        const stepLink = event.target.closest('.fb-step-link');
        if (stepLink && this.root.contains(stepLink)) {
            event.preventDefault();
            this.switchStep(stepLink.dataset.stepId, true);
            return;
        }

        const paletteItem = event.target.closest('.fb-palette-item');
        if (paletteItem && this.paletteList.contains(paletteItem)) {
            this.openCreation(paletteItem, 0, 1, LAST_POSITION);
            return;
        }

        const control = event.target.closest('[data-fb-action]');
        if (!control || !this.root.contains(control)) {
            return;
        }

        const node = control.closest('.fb-node');
        const displayId = node?.dataset.displayId;

        switch (control.dataset.fbAction) {
            case 'edit':
                this.openEditor(node.dataset.editUrl, node.dataset.title, displayId);
                break;
            case 'panel':
                this.openEditor(control.dataset.fbUrl, control.dataset.fbTitle, displayId);
                break;
            case 'duplicate':
                this.operation({ operation: 'duplicate', id_display: displayId });
                break;
            case 'remove':
                this.confirm(this.withTitle(this.root.dataset.msgConfirmRemove, node.dataset.title),
                    () => this.operation({ operation: 'remove', id_display: displayId }));
                break;
            case 'moveUp':
            case 'moveDown':
                this.moveBy(node, control.dataset.fbAction === 'moveUp' ? -1 : 1);
                break;
            case 'moveToColumn':
                this.operation({
                    operation: 'move',
                    id_display: displayId,
                    id_step_target: this.stepId,
                    id_parent: node.parentElement.dataset.parentId,
                    column: control.dataset.columnTarget,
                    position: LAST_POSITION
                });
                break;
            case 'moveToStep':
                this.operation({ operation: 'move', id_display: displayId, id_step_target: control.dataset.stepTarget, id_parent: 0, position: LAST_POSITION });
                break;
            case 'duplicateStep':
                this.operation({ operation: 'duplicateStep', id_step_target: control.dataset.stepTarget });
                break;
            case 'importStep':
                this.openImport(control.dataset.stepAfter, control.dataset.title);
                break;
            case 'importTemplate':
                this.openTemplate();
                break;
            case 'removeStep':
                this.confirm(this.withTitle(this.root.dataset.msgConfirmRemoveStep, control.dataset.title),
                    () => this.operation({ operation: 'removeStep', id_step_target: control.dataset.stepTarget }));
                break;
            default:
                break;
        }
    }

    /**
     * Creates the Sortable instances: every drop zone of the canvas, the palette (clone only) and the steps (drop only).
     * Called again each time the fragments are replaced.
     */
    initSortables() {
        this.sortables.forEach(sortable => sortable.destroy());
        this.sortables = [];

        if (typeof Sortable === 'undefined') {
            return;
        }

        const onStart = () => this.root.classList.add('fb-dragging');

        this.canvasContainer.querySelectorAll('.fb-dropzone').forEach(zone => {
            this.sortables.push(new Sortable(zone, {
                group: { name: 'fb', pull: true, put: true },
                draggable: '.fb-node',
                handle: '.fb-handle',
                animation: 150,
                fallbackOnBody: true,
                swapThreshold: 0.65,
                onStart,
                onEnd: event => this.onNodeDrop(event)
            }));
        });

        this.sortables.push(new Sortable(this.paletteList, {
            group: { name: 'fb', pull: 'clone', put: false },
            draggable: '.fb-palette-item',
            sort: false,
            animation: 150,
            onStart,
            onEnd: event => this.onPaletteDrop(event)
        }));

        this.stepsContainer.querySelectorAll('.fb-step-dropzone').forEach(zone => {
            this.sortables.push(new Sortable(zone, {
                group: { name: 'fb', pull: false, put: (to, from, dragEl) => dragEl.classList.contains('fb-node') },
                draggable: '.fb-node'
            }));
        });
    }

    /**
     * End of the drag of a canvas node: a move inside the canvas, or a move to another step dropped on the step list.
     * @param {Object} event - the Sortable event
     */
    onNodeDrop(event) {
        this.root.classList.remove('fb-dragging');
        const displayId = event.item.dataset.displayId;

        if (event.to.classList.contains('fb-step-dropzone')) {
            event.item.remove();
            this.operation({ operation: 'move', id_display: displayId, id_step_target: event.to.dataset.stepTarget, id_parent: 0, position: LAST_POSITION });
            return;
        }

        if (event.from === event.to && event.oldDraggableIndex === event.newDraggableIndex) {
            return;
        }

        this.operation({
            operation: 'move',
            id_display: displayId,
            id_step_target: this.stepId,
            id_parent: event.to.dataset.parentId,
            column: event.to.dataset.column || 1,
            position: event.newDraggableIndex + 1
        });
    }

    /**
     * End of the drag of a palette entry type: opens its creation screen for the drop position.
     * @param {Object} event - the Sortable event
     */
    onPaletteDrop(event) {
        this.root.classList.remove('fb-dragging');
        if (event.to === this.paletteList) {
            return;
        }
        const parentId = event.to.dataset.parentId || 0;
        const column = event.to.dataset.column || 1;
        const position = event.newDraggableIndex + 1;
        event.item.remove();
        this.openCreation(event.clone, parentId, column, position);
    }

    /**
     * Moves a node one position up or down among its siblings of the same column (keyboard alternative to the drag and drop).
     * @param {HTMLElement} node
     * @param {number} delta - -1 or 1
     */
    moveBy(node, delta) {
        const zone = node.parentElement;
        const siblings = [...zone.children].filter(child => child.classList.contains('fb-node'));
        const position = siblings.indexOf(node) + 1 + delta;
        if (position < 1 || position > siblings.length) {
            return;
        }
        this.operation({ operation: 'move', id_display: node.dataset.displayId, id_step_target: this.stepId, id_parent: zone.dataset.parentId, column: zone.dataset.column || 1, position });
    }

    /**
     * Opens the creation screen of an entry type in the side panel; the created display is placed at the given position once saved.
     * @param {HTMLElement} item - the palette item
     * @param {number|string} parentId - the parent display, 0 for the root of the step
     * @param {number|string} column - the target column of the parent, starting at 1
     * @param {number} position - the target position within the column, starting at 1
     */
    openCreation(item, parentId, column, position) {
        if (!this.stepId || this.stepId === '0') {
            return;
        }
        const url = new URL('jsp/admin/plugins/forms/ManageQuestions.jsp', document.baseURI);
        url.searchParams.set('id_step', this.stepId);
        url.searchParams.set('id_parent', parentId);
        url.searchParams.set('display_column', column);
        if (item.dataset.group === 'true') {
            url.searchParams.set('view', 'createGroup');
        } else {
            url.searchParams.set('view_createQuestion', item.dataset.typeId);
        }
        this.pendingCreation = { stepId: this.stepId, parentId, column, position, knownMaxIdDisplay: this.maxIdDisplay };
        this.openEditor(url.toString(), item.dataset.title, null);
    }

    /**
     * Shows an existing back office screen in the side panel, widened over the center column while it is open.
     * The page scroll is kept to be restored when the panel closes.
     * @param {string} url
     * @param {string} title
     * @param {string|null} displayId - the display being edited, kept selected in the canvas
     */
    openEditor(url, title, displayId) {
        if (!url) {
            return;
        }
        if (!this.root.classList.contains('fb-editing')) {
            this.canvasScroll = window.scrollY;
            this.root.classList.add('fb-editing');
            this.root.scrollIntoView({ block: 'start' });
        }
        this.editor = { url, title, displayId, loads: 0, submitted: false };
        this.panelTitle.textContent = title || '';
        this.palette.classList.add('d-none');
        this.editorBox.classList.remove('d-none');
        this.panelClose.classList.remove('d-none');
        this.editorFrame.style.opacity = '0';
        this.editorFrame.src = url;
        this.markSelected(displayId);
    }

    /**
     * Closes the side panel, shows the palette and the center column again.
     */
    closeEditor() {
        if (this.root.classList.contains('fb-editing')) {
            this.root.classList.remove('fb-editing');
            window.scrollTo({ top: this.canvasScroll || 0 });
        }
        this.editor = null;
        this.pendingCreation = null;
        this.editorFrame.src = 'about:blank';
        this.editorBox.classList.add('d-none');
        this.palette.classList.remove('d-none');
        this.panelClose.classList.add('d-none');
        this.panelTitle.textContent = this.root.dataset.labelPalette;
        this.markSelected(null);
    }

    /**
     * Each page loaded in the side panel: the first one is the screen asked for; the next ones follow a save, a cancel or
     * a navigation inside it. Back on a list screen (save or cancel), the edition is over: the canvas is refreshed and the
     * panel closes on the palette, the new or edited field scrolled into view. A save that stays on the screen (apply)
     * keeps the panel open.
     */
    async onEditorLoad() {
        if (!this.editor) {
            return;
        }
        let frameDocument;
        try {
            frameDocument = this.editorFrame.contentDocument;
        } catch (e) {
            return;
        }
        if (!frameDocument || frameDocument.URL === 'about:blank') {
            return;
        }

        this.editor.loads++;
        const editor = this.editor;
        const atList = this.isListPage(this.editorFrame.contentWindow.location);

        if (editor.loads > 1 || atList) {
            this.relayMessages(frameDocument, editor.submitted);
            const result = this.pendingCreation ? await this.placePendingCreation() : await this.refresh();
            const createdId = result?.idDisplay > 0 ? String(result.idDisplay) : null;

            if (atList && createdId) {
                this.closeEditor();
                this.canvasContainer.querySelector(`.fb-node[data-display-id="${createdId}"]`)?.scrollIntoView({ behavior: 'smooth', block: 'center' });
                return;
            }
            if (atList) {
                this.closeEditor();
                if (editor.displayId) {
                    this.canvasContainer.querySelector(`.fb-node[data-display-id="${editor.displayId}"]`)?.scrollIntoView({ block: 'center' });
                }
                return;
            }
            if (createdId) {
                editor.displayId = createdId;
                this.markSelected(createdId);
            }
        }

        this.cleanFrame(frameDocument);
        this.focusEntryOptions(frameDocument, this.editorFrame.contentWindow);
        this.keepNavigationInPanel(frameDocument, '_self');
        this.watchNestedFrames(frameDocument);
        frameDocument.querySelectorAll('form').forEach(form => form.addEventListener('submit', event => {
            if (!(event.submitter?.name || '').startsWith('view_')) {
                editor.submitted = true;
            }
        }));
        this.editorFrame.contentWindow.addEventListener('beforeunload', () => { this.editorFrame.style.opacity = '0'; });
        this.editorFrame.style.opacity = '1';
    }

    /**
     * Places the display created by the creation screen at the position where its entry type was dropped.
     * @returns {Promise<Object|null>} the operation result
     */
    async placePendingCreation() {
        const pending = this.pendingCreation;
        const result = await this.operation({
            operation: 'placeNew',
            id_step_target: pending.stepId,
            id_parent: pending.parentId,
            column: pending.column,
            position: pending.position,
            known_max_id_display: pending.knownMaxIdDisplay
        }, true);
        if (result?.idDisplay > 0) {
            this.pendingCreation = null;
        }
        return result;
    }

    /**
     * Tells whether a page loaded in the side panel is a list screen, reached after a save or a cancel.
     * @param {Location} location
     * @returns {boolean}
     */
    isListPage(location) {
        const params = new URLSearchParams(location.search);
        const prefixed = [...params.keys()].find(key => key.startsWith('view_'));
        const view = params.get('view') ?? (prefixed ? prefixed.substring('view_'.length) : null);
        return LIST_PAGES.some(page => location.pathname.endsWith(page.path) && (page.views === null || page.views.includes(view)));
    }

    /**
     * Removes the back office chrome (menus, header, breadcrumb, tabs leading to other pages) from the page shown in the
     * side panel, and keeps its forms in the panel (a form targeting _top would replace the whole builder). Tabs switching
     * the content of the page itself (data-bs-toggle="tab") are kept.
     * @param {Document} frameDocument
     */
    cleanFrame(frameDocument) {
        const removeAll = selector => frameDocument.querySelectorAll(selector).forEach(el => el.remove());
        frameDocument.querySelectorAll('.skip-links').forEach(el => (el.closest('nav') || el).remove());
        removeAll('header, footer, aside.navbar, #navbar-menu, #sidebar-menu, .page > .sticky-top, .page > .navbar-expand-md');
        if (frameDocument.querySelector('.page > .page-wrapper')) {
            removeAll('.page > :not(.page-wrapper):not(script)');
        }
        removeAll('.page-header, #page-header, .breadcrumb');
        // the header naming the question on its standalone edition screen: the builder shows the question next to the panel
        removeAll('#entry-title');
        frameDocument.querySelectorAll('.card-header-tabs').forEach(tabList => {
            if (!tabList.querySelector('[data-bs-toggle="tab"], [data-bs-toggle="pill"]')) {
                (tabList.closest('.card-header') || tabList).remove();
            }
        });
        frameDocument.querySelector('.page-body')?.classList.remove('page-body');
        frameDocument.querySelector('.container-xl')?.classList.remove('container-xl');
        frameDocument.body?.classList.add('bg-white');
    }

    /**
     * On the edition screen of a question with options (#manage-field-options), puts the options first: the parameters of
     * the question (#entry-parameters) are collapsed behind a toggle, unless the page shows an error; and after an option
     * was added, modified, moved or removed (the screen comes back on the #list anchor), the options box is scrolled into
     * view. Only the panel scrolls, never the builder page.
     * @param {Document} frameDocument
     * @param {Window} frameWindow
     */
    focusEntryOptions(frameDocument, frameWindow) {
        const options = frameDocument.getElementById('manage-field-options');
        const parameters = frameDocument.getElementById('entry-parameters');
        if (!options || !parameters) {
            return;
        }
        if (!frameDocument.querySelector('.alert-danger, .is-invalid')) {
            const toggle = frameDocument.createElement('button');
            toggle.type = 'button';
            toggle.className = 'btn btn-link w-100 d-flex justify-content-between align-items-center text-reset fw-bold px-3 py-2';
            toggle.setAttribute('aria-controls', parameters.id);
            const label = frameDocument.createElement('span');
            label.className = 'card-title mb-0';
            label.textContent = parameters.dataset.fbToggleLabel || '';
            const chevron = frameDocument.createElement('i');
            chevron.className = 'ti ti-chevron-down';
            chevron.setAttribute('aria-hidden', 'true');
            toggle.append(label, chevron);
            const setExpanded = expanded => {
                parameters.classList.toggle('d-none', !expanded);
                toggle.setAttribute('aria-expanded', String(expanded));
                chevron.className = expanded ? 'ti ti-chevron-up' : 'ti ti-chevron-down';
            };
            toggle.addEventListener('click', () => setExpanded(parameters.classList.contains('d-none')));
            parameters.before(toggle);
            setExpanded(false);
        }
        if (frameWindow.location.hash === '#list') {
            const top = options.getBoundingClientRect().top + frameWindow.scrollY - 16;
            frameWindow.scrollTo({ top: Math.max(top, 0) });
        }
    }

    /**
     * Keeps the navigation of a page shown in the panel inside the builder: the screens were written for full pages or for
     * the core offcanvas, and send their links and forms to _top or _parent. Checked at click and submit time, because the
     * core offcanvas script sets those targets again after its iframes load.
     * @param {Document} doc - the page shown in the panel, or a page nested in it
     * @param {string} target - where _top and _parent go: '_self' for the panel page, the panel iframe name for nested pages
     */
    keepNavigationInPanel(doc, target) {
        const retarget = element => {
            ['target', 'formtarget'].forEach(attribute => {
                if (element?.getAttribute && TOP_TARGETS.includes(element.getAttribute(attribute))) {
                    element.setAttribute(attribute, target);
                }
            });
        };
        doc.addEventListener('click', event => {
            const element = event.target.closest?.('a, button, input');
            if (element) {
                retarget(element);
                retarget(element.form);
            }
        }, true);
        doc.addEventListener('submit', event => {
            retarget(event.target);
            retarget(event.submitter);
        }, true);
    }

    /**
     * Applies the panel navigation rule to the iframes the panel page opens itself (core offcanvas with an iframe, such as
     * the values of a choice question or the controls of a transition): their _top becomes the panel iframe.
     * @param {Document} doc - the page shown in the panel
     */
    watchNestedFrames(doc) {
        const bind = frame => {
            if (frame.dataset.fbBound) {
                return;
            }
            frame.dataset.fbBound = 'true';
            frame.addEventListener('load', () => {
                try {
                    if (frame.contentDocument) {
                        this.keepNavigationInPanel(frame.contentDocument, EDITOR_FRAME_NAME);
                    }
                } catch (e) {
                    frame.dataset.fbBound = 'cross-origin';
                }
            });
        };
        doc.querySelectorAll('iframe').forEach(bind);
        new MutationObserver(mutations => mutations.forEach(mutation => mutation.addedNodes.forEach(node => {
            if (node.nodeType !== 1) {
                return;
            }
            if (node.tagName === 'IFRAME') {
                bind(node);
            }
            node.querySelectorAll?.('iframe').forEach(bind);
        }))).observe(doc.documentElement, { childList: true, subtree: true });
    }

    /**
     * Shows in the builder the messages that the page in the side panel received (toasts and alerts).
     * @param {Document} frameDocument
     * @param {boolean} submitted - a form of the panel was submitted
     */
    relayMessages(frameDocument, submitted) {
        let relayed = false;
        frameDocument.querySelectorAll('.toast, .alert').forEach(element => {
            const body = element.querySelector('.toast-body') || element;
            const text = body.textContent.trim();
            if (!text) {
                return;
            }
            const type = ['danger', 'warning', 'success', 'info'].find(t => element.className.includes(t)) || 'info';
            this.toast(text, type);
            relayed = true;
            element.remove();
        });
        if (!relayed && submitted) {
            this.toast(this.root.dataset.msgSaved, 'success');
        }
    }

    /**
     * Shows another step in the canvas, without reloading the page.
     * @param {string} stepId
     * @param {boolean} pushHistory - add the step to the browser history
     */
    async switchStep(stepId, pushHistory) {
        this.closeEditor();
        this.stepId = stepId;
        await this.refresh();
        if (pushHistory) {
            const url = new URL(window.location.href);
            url.searchParams.set('view', 'builder');
            url.searchParams.set('id_step', this.stepId);
            window.history.pushState({}, '', url);
        }
    }

    /**
     * Reloads the fragments of the builder without changing anything (a 'refresh' operation, which renews the token like
     * any other).
     * @returns {Promise<Object|null>}
     */
    refresh() {
        return this.operation({ operation: 'refresh' }, true);
    }

    /**
     * Sends one change to the server, after the previous one: each change needs the token answered by the previous one.
     * @param {Object} params - the operation and its parameters
     * @param {boolean} silent - do not show the success message
     * @returns {Promise<Object|null>} the result, null on a network failure
     */
    operation(params, silent = false) {
        return this.enqueue(async () => {
            const body = new URLSearchParams({ action: ACTION_OPERATION, _csrftoken: this.token, id_form: this.formId, id_step: this.stepId, step_order: this.stepOrder });
            Object.entries(params).forEach(([key, value]) => body.set(key, value));
            const response = await fetch(new URL(CONTROLLER_URL, document.baseURI), {
                method: 'POST',
                body,
                credentials: 'same-origin',
                headers: { Accept: 'application/json' }
            });
            return this.apply(await response.json(), silent);
        });
    }

    /**
     * Chains a request after the pending ones. A failed request (session expired, token refused) reloads the page, which
     * restores a valid token and the actual state of the form.
     * @param {Function} task
     * @returns {Promise<Object|null>}
     */
    enqueue(task) {
        const run = this.queue.then(task).catch(() => {
            this.toast(this.root.dataset.msgNetwork, 'danger');
            window.setTimeout(() => window.location.reload(), 1500);
            return null;
        });
        this.queue = run;
        return run;
    }

    /**
     * Applies a server answer: new token, refreshed fragments, message.
     * @param {Object} result
     * @param {boolean} silent - do not show the success message
     * @returns {Object} the result
     */
    apply(result, silent) {
        if (result.token) {
            this.token = result.token;
        }
        if (result.maxIdDisplay !== undefined) {
            this.maxIdDisplay = result.maxIdDisplay;
        }
        if (result.idStep) {
            this.stepId = String(result.idStep);
            this.root.dataset.stepId = this.stepId;
        }
        if (typeof result.stepsHtml === 'string') {
            this.stepsContainer.innerHTML = result.stepsHtml;
        }
        if (typeof result.canvasHtml === 'string') {
            this.canvasContainer.innerHTML = result.canvasHtml;
        }
        this.initSortables();
        this.markSelected(this.editor?.displayId);

        if (result.status === 'error') {
            this.toast(result.message, 'danger');
        } else if (!silent && result.message) {
            this.toast(result.message, 'success');
        }
        return result;
    }

    /**
     * Highlights the node edited in the side panel.
     * @param {string|null} displayId
     */
    markSelected(displayId) {
        this.canvasContainer.querySelectorAll('.fb-node.fb-selected').forEach(node => node.classList.remove('fb-selected'));
        if (displayId) {
            this.canvasContainer.querySelector(`.fb-node[data-display-id="${displayId}"]`)?.classList.add('fb-selected');
        }
    }

    /**
     * Filters the palette on the entry type titles.
     */
    filterPalette() {
        const term = this.paletteSearch.value.trim().toLowerCase();
        this.paletteList.querySelectorAll('.fb-palette-item').forEach(item => {
            item.classList.toggle('d-none', term !== '' && !item.dataset.title.toLowerCase().includes(term));
        });
    }

    /**
     * Opens the import dialog, for a step placed after the given one (a transition is created) or for an independent step.
     * @param {string} stepAfter - the step the imported step follows, '0' for none
     * @param {string} title - the title of that step
     */
    openImport(stepAfter, title) {
        this.importStepAfter = parseInt(stepAfter, 10) || 0;
        const after = document.getElementById('fb-import-after');
        after.textContent = this.importStepAfter ? `${after.dataset.label} « ${title || ''} »` : '';
        after.classList.toggle('d-none', !this.importStepAfter);
        document.getElementById('fb-import-error').classList.add('d-none');
        document.getElementById('fb-import-file').value = '';
        this.getModalOf(this.importModal)?.show();
    }

    /**
     * Sends the chosen JSON file (read in the browser), then closes the dialog. The canvas shows the imported step.
     */
    async submitImport() {
        const file = document.getElementById('fb-import-file').files[0];
        if (!file) {
            document.getElementById('fb-import-error').classList.remove('d-none');
            return;
        }
        const params = { operation: 'importStep', id_step_after: this.importStepAfter, step_json_base64: await this.readAsBase64(file) };
        this.getModalOf(this.importModal)?.hide();
        await this.sendImport(params);
    }

    /**
     * Opens the dialog listing the step templates, with no template chosen.
     */
    openTemplate() {
        this.templateModal.querySelectorAll('input[name="fb_template"]').forEach(input => { input.checked = false; });
        document.getElementById('fb-template-ok').disabled = true;
        this.getModalOf(this.templateModal)?.show();
    }

    /**
     * Creates a step from the chosen template, then closes the dialog. The canvas shows the new step.
     */
    async submitTemplate() {
        const chosen = this.templateModal.querySelector('input[name="fb_template"]:checked');
        if (!chosen) {
            return;
        }
        this.getModalOf(this.templateModal)?.hide();
        await this.sendImport({ operation: 'importStep', id_step_after: 0, id_template: chosen.value });
    }

    /**
     * Runs an import operation and, when it succeeds, closes the editor and puts the imported step in the address.
     * @param {Object} params - the parameters of the importStep operation
     */
    async sendImport(params) {
        const result = await this.operation(params);
        if (result?.status === 'ok') {
            this.closeEditor();
            const url = new URL(window.location.href);
            url.searchParams.set('view', 'builder');
            url.searchParams.set('id_step', this.stepId);
            window.history.pushState({}, '', url);
        }
    }

    /**
     * Reads a file as base64: the admin XSS filter rewrites the quotes of the request parameters, the base64 alphabet goes through unchanged.
     * @param {File} file
     * @returns {Promise<string>} the base64 content, without the data URL prefix
     */
    readAsBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => resolve(String(reader.result).substring(String(reader.result).indexOf(',') + 1));
            reader.onerror = () => reject(reader.error);
            reader.readAsDataURL(file);
        });
    }

    /**
     * Appends the title of the element concerned to a confirmation message.
     * @param {string} message
     * @param {string} title
     * @returns {string}
     */
    withTitle(message, title) {
        return title ? `${message} « ${title} »` : message;
    }

    /**
     * Asks a confirmation in the Tabler modal before a removal.
     * @param {string} message
     * @param {Function} callback - run when confirmed
     */
    confirm(message, callback) {
        const modal = this.getModal();
        if (!modal) {
            if (window.confirm(message)) {
                callback();
            }
            return;
        }
        document.getElementById('fb-confirm-message').textContent = message;
        this.confirmCallback = callback;
        modal.show();
    }

    /**
     * @returns {Object|null} the Bootstrap modal of the confirmation
     */
    getModal() {
        return this.getModalOf(this.confirmModal);
    }

    /**
     * @param {HTMLElement} element - a modal of the page
     * @returns {Object|null} its Bootstrap modal
     */
    getModalOf(element) {
        return window.bootstrap && element ? window.bootstrap.Modal.getOrCreateInstance(element) : null;
    }

    /**
     * Shows a Tabler toast.
     * @param {string} message
     * @param {string} type - danger, warning, success or info
     */
    toast(message, type) {
        if (!message) {
            return;
        }
        const container = document.getElementById('fb-toasts');
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-bg-${type} border-0`;
        toast.setAttribute('role', type === 'danger' ? 'alert' : 'status');
        toast.setAttribute('aria-live', type === 'danger' ? 'assertive' : 'polite');
        toast.setAttribute('aria-atomic', 'true');
        const wrapper = document.createElement('div');
        wrapper.className = 'd-flex';
        const body = document.createElement('div');
        body.className = 'toast-body';
        body.textContent = message;
        const close = document.createElement('button');
        close.type = 'button';
        close.className = 'btn-close btn-close-white me-2 m-auto';
        close.setAttribute('data-bs-dismiss', 'toast');
        close.setAttribute('aria-label', 'Close');
        wrapper.append(body, close);
        toast.append(wrapper);
        container.append(toast);
        toast.addEventListener('hidden.bs.toast', () => toast.remove());
        if (window.bootstrap) {
            window.bootstrap.Toast.getOrCreateInstance(toast, { delay: 4000 }).show();
        } else {
            toast.classList.add('show');
            window.setTimeout(() => toast.remove(), 4000);
        }
    }
}

const builderRoot = document.getElementById('form-builder');
if (builderRoot) {
    new FormBuilder(builderRoot);
}
