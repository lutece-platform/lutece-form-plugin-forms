function openFormsTermsOfService(url)
{
  var forms_win_terms_of_service = self.open(url, 'forms_terms_of_service', 'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,copyhistory=yes,width=500px,height=300px');
  forms_win_terms_of_service.focus();
}

/*
 * The terms of service links (data-forms-terms-of-service) open their page in a small window, instead of an onclick
 * handler written in the template (LUT-33153)
 */
document.addEventListener('click', function(event) {
  var link = event.target.closest('a[data-forms-terms-of-service]');
  if (link) {
    event.preventDefault();
    openFormsTermsOfService(link.href);
  }
});
