document.addEventListener('DOMContentLoaded', function() {
    /* Check if enter key is pressed and force submit */
    const form = document.getElementById('form-validate');
    if (form) {
        form.addEventListener('keypress', function(e) {
            if (e.target.tagName === 'INPUT' && e.which === 13) {
                e.preventDefault();
                const submitBtn = document.querySelector('button[name="action_doSaveStep"]');
                if (submitBtn) {
                    submitBtn.click();
                }
            }
        });
    }
});