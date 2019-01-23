package com.three55.callevent;

import com.three55.callevent.dao.CellUsageDao;
import com.three55.callevent.model.CellUsage;
import com.three55.callevent.model.CellAverageUsage;
import com.three55.callevent.utils.NetworkType;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Test cases for CellUsageNeo4jDaoTest.
 * Uri, User and Password is set in src/test/resources/application.properties.
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestApp.class,
        initializers = ConfigFileApplicationContextInitializer.class)
public class CellUsageNeo4jDaoTest {

    @Autowired
    private CellUsageDao dao;

    private CellUsage cu1;
    private CellUsage cu2;
    private CellUsage cu3;

    @Before
    public void setUp() throws Exception {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate ld = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(zoneId).toLocalDate();

        // CellUsage
        cu1 = new CellUsage();
        cu1.setDeviceBrand("SAMSUNG");
        cu1.setDeviceModel("GS7");
        cu1.setDeviceSignalDb(-50);
        cu1.setDate(ld);
        cu1.setNetworkType(NetworkType.fromString("2G"));
        cu1.setNetworkName("Verizon");
        cu1.setMnc("004");
        cu1.setMcc("310");

        // CellUsage
        cu2 = new CellUsage();
        cu2.setDeviceBrand("SAMSUNG");
        cu2.setDeviceModel("GS7");
        cu2.setDeviceSignalDb(-60);
        cu2.setDate(ld);
        cu2.setNetworkType(NetworkType.fromString("3G"));
        cu2.setNetworkName("Verizon");
        cu2.setMnc("004");
        cu2.setMcc("310");

        // CellUsage
        cu3 = new CellUsage();
        cu3.setDeviceBrand("SAMSUNG");
        cu3.setDeviceModel("GS7");
        cu3.setDeviceSignalDb(-70);
        cu3.setDate(ld);
        cu3.setNetworkType(NetworkType.fromString("2G"));
        cu3.setNetworkName("Verizon");
        cu3.setMnc("004");
        cu3.setMcc("310");
    }

    @Test
    public void testSaveOneEvent() {
        try {
            // save to neo4j, (:Cellphone)-[:USED_IN]->(:Telco)
            // expected response is record id of Cellphone node
            String id = dao.saveCellphoneUsage(cu1);
            Assert.assertNotNull(id);

        } finally {
            // deletes  Cellphone and Telco nodes and all of its relationships
            int deletedNodes = dao.deleteCellPhoneUsage(cu1.getDeviceBrand(), cu1.getNetworkName());
            Assert.assertEquals(2, deletedNodes);
        }
    }

    @Test
    public void testGetCellphoneAverageUsage() {

        try {
            String id1 = dao.saveCellphoneUsage(cu1);

            Assert.assertNotNull(id1);


            String id2 = dao.saveCellphoneUsage(cu2);
            Assert.assertNotNull(id2);


            String id3 = dao.saveCellphoneUsage(cu3);
            Assert.assertNotNull(id3);

            CellAverageUsage cau = dao.getCellphoneAverageUsage(cu1.getDeviceBrand());
            Assert.assertNotNull(cau);
            Assert.assertEquals(cu3.getDeviceBrand(), cau.getBrand());
            Assert.assertEquals(-60.00f, cau.getAvgSignalStrength(), 0);
            Assert.assertEquals(3, cau.getUsageCount());
        } finally {
            // deletes  Cellphone and Telco nodes and all of its relationships
            int deletedNodes = dao.deleteCellPhoneUsage(cu1.getDeviceBrand(), cu1.getNetworkName());
            Assert.assertEquals(2, deletedNodes);
        }

    }
}
