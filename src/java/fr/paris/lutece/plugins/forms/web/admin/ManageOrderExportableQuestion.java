package fr.paris.lutece.plugins.forms.web.admin;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManageOrderExportableQuestion {

    private static final String PARAMETER_ACTIVE_TAB_PANNEL_2 = "activeTabPannel2";

    public String doMoveUpExportableQuestion(HttpServletRequest request, HttpServletResponse response)
    {
        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), FormsConstants.DEFAULT_ID_VALUE );
        int nOrderToSet = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_EXPORT_DISPLAY_ORDER + "_" + nIdQuestion), 0 );

        if ( nIdQuestion != FormsConstants.DEFAULT_ID_VALUE )
        {
            Question questionToChangeOrder = QuestionHome.findByPrimaryKey( nIdQuestion );

            int nCurrentOrder = questionToChangeOrder.getExportDisplayOrder();
            if (nCurrentOrder != nOrderToSet)
            {
                if (nOrderToSet > nCurrentOrder)
                {
                    List<Question> questionList = QuestionHome.getListQuestionBetweenExportOrders(nIdForm, nCurrentOrder, nOrderToSet);
                    for (Question question : questionList)
                    {
                        if (question.getExportDisplayOrder() != nCurrentOrder)
                        {
                            question.setExportDisplayOrder(question.getExportDisplayOrder() - 1);
                            QuestionHome.update(question);
                        }
                    }
                }
                else
                {
                    List<Question> questionList = QuestionHome.getListQuestionBetweenExportOrders(nIdForm, nOrderToSet, nCurrentOrder);
                    for (Question question : questionList)
                    {
                        if (question.getExportDisplayOrder() != nCurrentOrder)
                        {
                            question.setExportDisplayOrder(question.getExportDisplayOrder() + 1);
                            QuestionHome.update(question);
                        }
                    }
                }
                questionToChangeOrder.setExportDisplayOrder(nOrderToSet);
                QuestionHome.update(questionToChangeOrder);
            }
        }

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( nIdForm ) );
        mapParameters.put( PARAMETER_ACTIVE_TAB_PANNEL_2, String.valueOf(true));
        // redirect to the form page
        // get the base url
        String strBaseUrl = request.getRequestURL( ).toString( );

        return strBaseUrl +"/" + FormsConstants.JSP_MANAGE_FORMS + "?" + FormsConstants.PARAMETER_ID_FORM + "=" + nIdForm + "&" + PARAMETER_ACTIVE_TAB_PANNEL_2 + "=true";
    }

}
