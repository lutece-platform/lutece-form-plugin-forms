package fr.paris.lutece.plugins.forms.web.admin;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@Controller(controllerJsp = "ManageOrderExportableQuestion", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_QUESTION_EXPORT_ORDER_MANAGEMENT")
public class ManageOrderExportableQuestion extends AbstractJspBean{
    private static final String PARAMETER_ACTIVE_TAB_PANNEL_2 = "activeTabPannel2";
    private static final String PARAMETER_MANAGE_EXPORT_JSP = "ManageFormExport.jsp";

        public static HttpServletResponse doMoveUpExportableQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException
        {
            Integer nIdForm =  Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
            Integer nIdQuestion =  Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ) );
            Integer nOrderToSet = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_EXPORT_DISPLAY_ORDER));

            if ( nIdQuestion != FormsConstants.DEFAULT_ID_VALUE )
            {
                Question questionToChangeOrder = QuestionHome.findByPrimaryKey( nIdQuestion );

                Integer nCurrentOrder = questionToChangeOrder.getExportDisplayOrder();
                if (nCurrentOrder != nOrderToSet)
                {
                    List<Question> questionList = QuestionHome.getListQuestionByIdForm(nIdForm);
                    Question questionToChangeOrder2 = new Question();
                    questionToChangeOrder.setExportDisplayOrder(nOrderToSet);
                    for (Integer i = 0; i < questionList.size(); i++)
                    {
                        if (nOrderToSet.equals(questionList.get(i).getExportDisplayOrder()) && !nIdQuestion.equals(questionList.get(i).getId()))
                        {
                            questionToChangeOrder2 = questionList.get(i);
                        } else if (nCurrentOrder.equals(questionList.get(i).getExportDisplayOrder()) && nIdQuestion.equals(questionList.get(i).getId()))
                        {
                            questionList.get(i).setExportDisplayOrder(nOrderToSet);
                        }
                    }
                    questionToChangeOrder2.setExportDisplayOrder(nCurrentOrder);
                    QuestionHome.update(questionToChangeOrder);
                    QuestionHome.update(questionToChangeOrder2);
                }
            }
            String strUrl = request.getRequestURL( ).toString();
            strUrl = strUrl.substring( 0, strUrl.lastIndexOf( "/" ) );
            strUrl = strUrl+"/"+PARAMETER_MANAGE_EXPORT_JSP+ "?" + FormsConstants.PARAMETER_TARGET_VIEW+"=" + "manageExport"+ "&"+ FormsConstants.PARAMETER_ID_FORM + "=" + nIdForm + "&" + PARAMETER_ACTIVE_TAB_PANNEL_2 + "=true";
            response.sendRedirect( strUrl);
            return response;
        }

}
