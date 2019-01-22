/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.forms.service.search;

import java.util.List;


public interface IFormSearchService
{
    /**
     * Get the search result form response id list
     * @param formSearchConfig
     * @return the list of form response id corresponding to given search config
     */
    List<Integer> getSearchResults( FormSearchConfig formSearchConfig );
}
