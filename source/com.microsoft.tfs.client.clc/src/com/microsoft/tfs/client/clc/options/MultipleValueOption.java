// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the repository root.

package com.microsoft.tfs.client.clc.options;

import java.text.MessageFormat;

import com.microsoft.tfs.client.clc.Messages;
import com.microsoft.tfs.client.clc.OptionsMap;
import com.microsoft.tfs.client.clc.exceptions.InvalidOptionValueException;

public abstract class MultipleValueOption extends Option {
    private String[] _values;

    public MultipleValueOption() {
        super();
    }

    /**
     * String values that are valid values for this option. If null is returned,
     * any value is permitted. Derived classes are encouraged to define public
     * static string members and return those values in the array.
     *
     * @return an array of strings that are valid options for this option, null
     *         if all values are permitted.
     */
    protected abstract String[] getValidOptionValues();

    /*
     * (non-Javadoc)
     *
     * @see
     * com.microsoft.tfs.client.clc.options.Option#parseValues(java.lang.String)
     */
    @Override
    public void parseValues(String optionValueString) throws InvalidOptionValueException {
        if (optionValueString == null) {
            final String messageFormat = Messages.getString("MultipleValueOption.OptionRequiresOneOrMoreValuesFormat"); //$NON-NLS-1$
            final String message = MessageFormat.format(messageFormat, getMatchedAlias());
            throw new InvalidOptionValueException(message);
        }

        /*
         * Leading and trailing spaces are stripped from the value before
         * comparing.
         */
        optionValueString = optionValueString.trim();

        /*
         * Split options on comma-separation.
         */
        final String[] optionValues = optionValueString.split(","); //$NON-NLS-1$

        /*
         * If this class declares an array of valid values, check them. If the
         * array is null, all strings are valid, so skip the check.
         */
        final String[] validValues = getValidOptionValues();
        if (validValues != null) {
            for (int i = 0; i < optionValues.length; i++) {
                boolean foundMatch = false;

                for (int j = 0; j < validValues.length; j++) {
                    if (validValues[j].equalsIgnoreCase(optionValues[i]) == true) {
                        foundMatch = true;
                        break;
                    }
                }

                if (foundMatch == false) {
                    final String messageFormat =
                        Messages.getString("MultipleValueOption.OptionRequresOneOfTheFollowingValuesFormat"); //$NON-NLS-1$
                    final String message = MessageFormat.format(
                        messageFormat,
                        getMatchedAlias(),
                        Option.makePrettyHelpValuesString(validValues));
                    throw new InvalidOptionValueException(message);
                }
            }
        }

        _values = optionValues;
    }

    /**
     * Gets the multiple values of this option.
     *
     * @return the values of this option.
     */
    public String[] getValues() {
        return _values;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.microsoft.tfs.client.clc.options.Option#getSyntaxString()
     */
    @Override
    public String getSyntaxString() {
        String validValuesString = "<value>[,<value>]"; //$NON-NLS-1$

        final String[] validValues = getValidOptionValues();
        if (validValues != null) {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < validValues.length; i++) {
                if (sb.length() > 0) {
                    sb.append(","); //$NON-NLS-1$
                }

                sb.append(validValues[i]);
            }
            validValuesString = sb.toString();
        }

        return OptionsMap.getPreferredOptionPrefix() + getMatchedAlias() + ":" + validValuesString; //$NON-NLS-1$
    }
}