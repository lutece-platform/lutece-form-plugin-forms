
<div class="box box-primary">
    <div class="box-body">
    <@tform class='form-inline' action='jsp/admin/plugins/forms/MultiviewForms.jsp' id='searchForm' >
        <#include "includes/include_manage_multiview_forms_header.ftl">
        <#if table_template?has_content>
            <div id="multi-form-list">
                ${table_template}
                <@paginationAdmin paginator=paginator combo=1 showcount=0 form=0 />
            </div>
        <#else>
            <div id="multiview-no-forms">
                <div class="panel">
                    <div class="panel-body">
                        <h2 class="text-muted text-center">#i18n{forms.multiviewForms.noFormResponses}</h2>
                        <img class="img-responsive" src="images/admin/skin/plugins/forms/multiview/no_form_responses.jpg" alt="#i18n{forms.multiviewForms.noFormResponses}" title="">
                    </div>
                </div>
            </div>
        </#if>
    </div>
</div>
</@tform>
<script src="js/plugins/forms/multiview/forms-multiview.js"></script>

<@dateFilterJs  />
<@scrollTopBtn />
<@jsHeaderSort />