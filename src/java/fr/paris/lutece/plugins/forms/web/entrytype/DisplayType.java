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
package fr.paris.lutece.plugins.forms.web.entrytype;

/**
 * This class represents a type of display
 *
 */
public final class DisplayType
{
    public static final DisplayType EDITION_BACKOFFICE = new DisplayType( Mode.EDITION, false );
    public static final DisplayType SUBMIT_BACKOFFICE = new DisplayType( Mode.EDITION, false );
    public static final DisplayType RESUBMIT_BACKOFFICE = new DisplayType( Mode.SELECT, false );
    public static final DisplayType RESUBMIT_FRONTOFFICE = new DisplayType( Mode.EDITION, true );
    public static final DisplayType COMPLETE_BACKOFFICE = new DisplayType( Mode.SELECT, false );
    public static final DisplayType COMPLETE_FRONTOFFICE = new DisplayType( Mode.EDITION, true );
    public static final DisplayType EDITION_FRONTOFFICE = new DisplayType( Mode.EDITION, true );
    public static final DisplayType READONLY_BACKOFFICE = new DisplayType( Mode.READONLY, false );
    public static final DisplayType READONLY_FRONTOFFICE = new DisplayType( Mode.READONLY, true );

    /**
     * The display mode
     *
     */
    public enum Mode
    {
        EDITION,
        READONLY,
        SELECT
    }

    private final Mode _mode;
    private final boolean _bIsFront;

    /**
     * Constructor
     * 
     * @param mode
     *            the mode
     * @param bIsFront
     *            {@code true} if the display is for front-office, {@code false} if it is for back-office
     */
    private DisplayType( Mode mode, boolean bIsFront )
    {
        _mode = mode;
        _bIsFront = bIsFront;
    }

    /**
     * Gives the mode
     * 
     * @return the mode
     */
    public Mode getMode( )
    {
        return _mode;
    }

    /**
     * Tests if the display is for front-office or for back-office
     * 
     * @return {@code true} if the display is for front-office, {@code false} if it is for back-office
     */
    public boolean isFront( )
    {
        return _bIsFront;
    }
}
