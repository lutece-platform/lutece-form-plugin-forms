package fr.paris.lutece.plugins.forms.web.admin;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Controller(controllerJsp = "", controllerPath = "", right = "FORMS_QUESTION_EXPORT_ORDER_MANAGEMENT")
public class ManageOrderExportableQuestion {

    private static final String PARAMETER_ACTIVE_TAB_PANNEL_2 = "activeTabPannel2";

      /*public static HttpServletResponse doMoveUpExportableQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
      /*  int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), FormsConstants.DEFAULT_ID_VALUE );
        int nOrderToSet = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_EXPORT_DISPLAY_ORDER + "_" + nIdQuestion), 0 );

        if ( nIdQuestion != FormsConstants.DEFAULT_ID_VALUE )
        {
            Question questionToChangeOrder = QuestionHome.findByPrimaryKey( nIdQuestion );

            int nCurrentOrder = questionToChangeOrder.getExportDisplayOrder();
            if (nCurrentOrder != nOrderToSet)
            {
                    questionToChangeOrder.setExportDisplayOrder(nOrderToSet);
                    Question questionToChangeOrder2 = new Question();
                    List<Integer> arrayOrder = new ArrayList<Integer>();
                    Integer valueToReAssign = null;
                    List<Question> questionList = QuestionHome.getListQuestionByIdForm( nIdForm );
                    for (int i = 0; i < questionList.size(); i++)
                    {
                        if (questionList.get(i).getExportDisplayOrder() == nOrderToSet && questionList.get(i).getId() != nIdQuestion)
                        {
                            questionToChangeOrder2 = questionList.get(i);
                        }
                        arrayOrder.add(questionList.get(i).getExportDisplayOrder());
                    }
                    for (int i = 1; i < questionList.size() + 1; i++)
                    {
                        if (!arrayOrder.contains(i))
                        {
                            valueToReAssign = i;
                        }
                    }
                    questionToChangeOrder2.setExportDisplayOrder(valueToReAssign);
                QuestionHome.update(questionToChangeOrder);
                QuestionHome.update(questionToChangeOrder2);
            }
        }
        String strUrl = request.getRequestURL( ).toString();
        strUrl = strUrl.substring( 0, strUrl.lastIndexOf( "/" ) ) + "/"+"ManageFormExport.jsp"+ "?" + "view=manageExport&"+ FormsConstants.PARAMETER_ID_FORM + "=" + nIdForm + "&" + PARAMETER_ACTIVE_TAB_PANNEL_2 + "=true";
        response.sendRedirect( strUrl);
       return response;
    } */

        @Action( value = "moveUpExportableQuestion")
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
                    List<Integer> arrayOrder = new ArrayList<Integer>();
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
                        arrayOrder.add(questionList.get(i).getExportDisplayOrder());
                    }

                    for (Integer i = 1; i < questionList.size() + 1; i++)
                    {
                        if (!arrayOrder.contains(i))
                        {
                            questionToChangeOrder2.setExportDisplayOrder(i);
                        }
                    }
                    QuestionHome.update(questionToChangeOrder);
                    QuestionHome.update(questionToChangeOrder2);
                }
            }

            String strUrl = request.getRequestURL( ).toString();
            strUrl = strUrl.substring( 0, strUrl.lastIndexOf( "/" ) );
            strUrl = strUrl+"/"+"ManageFormExport.jsp"+ "?" + "view=manageExport&"+ FormsConstants.PARAMETER_ID_FORM + "=" + nIdForm + "&" + PARAMETER_ACTIVE_TAB_PANNEL_2 + "=true";

            response.sendRedirect( strUrl);
            return response;
        }

}
