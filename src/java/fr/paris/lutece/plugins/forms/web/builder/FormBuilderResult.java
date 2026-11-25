/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.forms.web.builder;

/**
 * JSON answer of the form builder operations: the outcome, the refreshed fragments and the security token to send with the next operation
 */
public class FormBuilderResult
{
    public static final String STATUS_OK = "ok";
    public static final String STATUS_ERROR = "error";

    private String _strStatus = STATUS_OK;
    private String _strMessage;
    private String _strCanvasHtml;
    private String _strStepsHtml;
    private int _nIdStep;
    private int _nIdDisplay;
    private int _nMaxIdDisplay;
    private String _strToken;

    /**
     * Returns the outcome of the operation
     *
     * @return {@link #STATUS_OK} or {@link #STATUS_ERROR}
     */
    public String getStatus( )
    {
        return _strStatus;
    }

    /**
     * Sets the outcome of the operation
     *
     * @param strStatus
     *            the outcome
     */
    public void setStatus( String strStatus )
    {
        _strStatus = strStatus;
    }

    /**
     * Returns the localized message to show to the user
     *
     * @return the message
     */
    public String getMessage( )
    {
        return _strMessage;
    }

    /**
     * Sets the localized message to show to the user
     *
     * @param strMessage
     *            the message
     */
    public void setMessage( String strMessage )
    {
        _strMessage = strMessage;
    }

    /**
     * Returns the refreshed canvas
     *
     * @return the HTML of the canvas
     */
    public String getCanvasHtml( )
    {
        return _strCanvasHtml;
    }

    /**
     * Sets the refreshed canvas
     *
     * @param strCanvasHtml
     *            the HTML of the canvas
     */
    public void setCanvasHtml( String strCanvasHtml )
    {
        _strCanvasHtml = strCanvasHtml;
    }

    /**
     * Returns the refreshed list of the steps
     *
     * @return the HTML of the list
     */
    public String getStepsHtml( )
    {
        return _strStepsHtml;
    }

    /**
     * Sets the refreshed list of the steps
     *
     * @param strStepsHtml
     *            the HTML of the list
     */
    public void setStepsHtml( String strStepsHtml )
    {
        _strStepsHtml = strStepsHtml;
    }

    /**
     * Returns the step shown in the canvas
     *
     * @return the step identifier
     */
    public int getIdStep( )
    {
        return _nIdStep;
    }

    /**
     * Sets the step shown in the canvas
     *
     * @param nIdStep
     *            the step identifier
     */
    public void setIdStep( int nIdStep )
    {
        _nIdStep = nIdStep;
    }

    /**
     * Returns the display created or placed by the operation
     *
     * @return the display identifier, 0 if none
     */
    public int getIdDisplay( )
    {
        return _nIdDisplay;
    }

    /**
     * Sets the display created or placed by the operation
     *
     * @param nIdDisplay
     *            the display identifier
     */
    public void setIdDisplay( int nIdDisplay )
    {
        _nIdDisplay = nIdDisplay;
    }

    /**
     * Returns the highest display identifier of the form, used to find the display created by the next creation
     *
     * @return the highest display identifier
     */
    public int getMaxIdDisplay( )
    {
        return _nMaxIdDisplay;
    }

    /**
     * Sets the highest display identifier of the form
     *
     * @param nMaxIdDisplay
     *            the highest display identifier
     */
    public void setMaxIdDisplay( int nMaxIdDisplay )
    {
        _nMaxIdDisplay = nMaxIdDisplay;
    }

    /**
     * Returns the security token to send with the next operation
     *
     * @return the token
     */
    public String getToken( )
    {
        return _strToken;
    }

    /**
     * Sets the security token to send with the next operation
     *
     * @param strToken
     *            the token
     */
    public void setToken( String strToken )
    {
        _strToken = strToken;
    }
}
