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
package fr.paris.lutece.plugins.forms.export.pdf;

import org.apache.commons.lang3.StringUtils;

public class PdfCell
{
    private String _title;
    private String _value;
    private String _group;
    private String _step;
    private int _formResponseNumber;
    private String _formResponseDate;

    /**
     * @return the title
     */
    public String getTitle( )
    {
        return _title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle( String title )
    {
        this._title = title;
    }

    /**
     * @return the value
     */
    public String getValue( )
    {
        return _value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue( String value )
    {
        this._value = value;
    }

    /**
     * @return the group
     */
    public String getGroup( )
    {
        return _group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup( String group )
    {
        this._group = group;
    }
    

    public String getStep() {
		return _step;
	}

	public void setStep(String step) {
		this._step = step;
	}

	public int getFormResponseNumber() {
		return _formResponseNumber;
	}

	public void setFormResponseNumber(int formResponseNumber) {
		this._formResponseNumber = formResponseNumber;
	}

	public String getFormResponseDate() {
		return _formResponseDate;
	}

	public void setFormResponseDate(String formResponseDate) {
		this._formResponseDate = formResponseDate;
	}

	public boolean isDrawable( )
    {
        return StringUtils.isNoneEmpty( _title, _value );
    }
}
