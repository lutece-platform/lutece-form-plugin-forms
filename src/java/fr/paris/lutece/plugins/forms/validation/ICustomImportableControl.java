package fr.paris.lutece.plugins.forms.validation;

import fr.paris.lutece.plugins.forms.business.Control;

import java.util.Map;

public interface ICustomImportableControl {

    /**
     * from the imported control find the new control value
     *
     * @param oldControl
     *          import control
     * @param mapIdFields
     *          map of the old field id to find the new field id
     * @return the new control value
     */
    String getNewValidatorValue( Control oldControl, Map<Integer, Integer> mapIdFields );

}
