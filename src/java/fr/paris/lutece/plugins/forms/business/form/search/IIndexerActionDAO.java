/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.business.form.search;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;

/**
 *
 * IIndexerActionDAO
 *
 */
public interface IIndexerActionDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param indexerAction
     *            instance of the IndexerAction object to insert
     * @param plugin
     *            the plugin
     */
    void insert( IndexerAction indexerAction, Plugin plugin );

    /**
     * Load the data of the IndexerAction from the table
     *
     * @param nId
     *            The identifier of the action
     * @param plugin
     *            the plugin
     * @return the instance of the IndexerAction
     */
    IndexerAction load( int nId, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nId
     *            The identifier of the action
     * @param plugin
     *            the plugin
     */
    void delete( int nId, Plugin plugin );

    /**
     * Delete records form the table
     *
     * @param idList The identifier list of action
     * @param plugin the plugin
     */
    void delete( List<Integer> idList, Plugin plugin );

    /**
     * Delete all records form the table
     *
     * @param plugin the plugin
     */
    void deleteAll( Plugin plugin );

    /**
     * Update the indexerAction in the table
     *
     * @param indexerAction
     *            instance of the IndexerAction object to update
     * @param plugin
     *            the plugin
     */
    void store( IndexerAction indexerAction, Plugin plugin );

    /**
     * Load the data of all indexerAction and returns them in a list
     * 
     * @param filter
     *            the search filter
     * @param plugin
     *            the plugin
     * @return The List which contains the data of all action
     */
    List<IndexerAction> selectList( IndexerActionFilter filter, Plugin plugin );

    /**
     * Load the data of all indexerAction and returns them in a list
     *
     * @param plugin
     *            the plugin
     * @return The List which contains the data of all action
     */
    public List<IndexerAction> selectList( Plugin plugin );

}
