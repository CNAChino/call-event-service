package com.three55.callevent.dao;

import com.three55.callevent.model.CellAverageUsage;
import com.three55.callevent.model.CellUsage;

public interface CellUsageDao {

    /**
     * Store cell usage data into the database
     *
     * @param newCellUsage
     * @return
     */
    String saveCellphoneUsage(CellUsage newCellUsage);

    /**
     * get the cellphone usage by brand return the number of occurences it has been used
     *      * and the average signal strength.
     *
     * @param deviceBrand
     * @return
     */
    CellAverageUsage getCellphoneAverageUsage(String deviceBrand);

    /**
     * deletes nodes by deviceBrand and networkName and all of its relationships
     *
     * @param deviceBrand
     * @param networkName
     * @return deleted nodes
     */
    int deleteCellPhoneUsage(String deviceBrand, String networkName);

}
