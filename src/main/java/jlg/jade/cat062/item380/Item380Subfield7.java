/* 
* Created by dan-geabunea on 4/28/2016.
* This code is the property of JLG Consulting. Please
* check the license terms for this product to see under what
* conditions you can use or modify this source code.
*/
package jlg.jade.cat062.item380;

import jlg.jade.abstraction.FixedLengthAsterixData;
import jlg.jade.asterix.AsterixItemLength;

import java.util.BitSet;

/**
 * Item 380 Subfield #7 - Final State Selected Altitude
 * The vertical intent value that corresponds with the ATC cleared
 * altitude, as derived from the Altitude Control Panel (FCU/MCP)
 */
public class Item380Subfield7 extends FixedLengthAsterixData {
    private boolean manageVerticalModeActive;
    private boolean altitudeHoldActive;
    private boolean approachModeActive;
    private int selectedAltitude;

    @Override
    protected int setSizeInBytes() {
        return AsterixItemLength.TWO_BYTES.getValue();
    }

    @Override
    protected int decodeFromByteArray(byte[] input, int offset) {
        BitSet firstOctetBits = BitSet.valueOf(new byte[]{input[offset]});

        final int MV_BIT_INDEX = 7;
        final int AH_BIT_INDEX = 6;
        final int AM_BIT_INDEX = 5;

        //decode mv
        if (firstOctetBits.get(MV_BIT_INDEX)) {
            manageVerticalModeActive = true;
        } else {
            manageVerticalModeActive = false;
        }
        appendItemDebugMsg("Managed Vertical Mode Active", manageVerticalModeActive);

        //decode ah
        if (firstOctetBits.get(AH_BIT_INDEX)) {
            altitudeHoldActive = true;
        } else {
            altitudeHoldActive = false;
        }
        appendItemDebugMsg("Altitude Hold Active", altitudeHoldActive);

        //decode am
        if (firstOctetBits.get(AM_BIT_INDEX)) {
            approachModeActive = true;
        } else {
            approachModeActive = false;
        }
        appendItemDebugMsg("Approach Mode Active", approachModeActive);

        //decode altitude
        byte firstOctetValueWithoutModes = input[offset];
        if (firstOctetBits.get(MV_BIT_INDEX)) {
            firstOctetValueWithoutModes -= 128;
        }
        if (firstOctetBits.get(AH_BIT_INDEX)) {
            firstOctetValueWithoutModes -= 64;
        }
        if (firstOctetBits.get(AM_BIT_INDEX)) {
            firstOctetValueWithoutModes -= 32;
        }
        this.selectedAltitude =
                firstOctetValueWithoutModes * 256 +
                        Byte.toUnsignedInt(input[offset + 1]);
        appendItemDebugMsg("Selected Altitude (25 ft)", selectedAltitude);

        return offset + sizeInBytes;
    }

    @Override
    protected boolean validate() {
        if (getSelectedAltitudeInFeet() < -1300 || getSelectedAltitudeInFeet() > 100000) {
            return false;
        }
        return true;
    }

    public boolean isManageVerticalModeActive() {
        return manageVerticalModeActive;
    }

    public boolean isApproachModeActive() {
        return approachModeActive;
    }

    public boolean isAltitudeHoldActive() {
        return altitudeHoldActive;
    }

    /**
     * @return The selected altitude in 25 feet values (Asterix unit of measure)
     */
    public int getSelectedAltitude() {
        return selectedAltitude;
    }

    /**
     * @return The selected altitude in feet
     */
    public int getSelectedAltitudeInFeet() {
        return selectedAltitude * 25;
    }
}
