
package org.asnworks.apis.pincodescsvtodbtransformer.processor;


import org.asnworks.apis.pincodescsvtodbtransformer.model.PinCode;
import org.springframework.batch.item.ItemProcessor;


/**
 * @author sudambat
 */
public class PinCodeProcessor implements ItemProcessor<PinCode, PinCode> {


    @Override
    public PinCode process(final PinCode pinCode) throws Exception {
        final String villageName = pinCode.getVillageName();
        final String officeName = pinCode.getOfficeName();
        final String code = pinCode.getCode();
        final String subDistrictName = pinCode.getSubDistrictName();
        final String districtName = pinCode.getDistrictName();
        final String stateName = pinCode.getStateName();

        return new PinCode(villageName, officeName, code, subDistrictName, districtName, stateName);

    }

}
