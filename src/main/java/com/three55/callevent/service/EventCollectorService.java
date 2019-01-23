package com.three55.callevent.service;

import com.three55.callevent.CallEventProto;
import com.three55.callevent.EventCollectorGrpc;
import com.three55.callevent.dao.CellUsageDao;
import com.three55.callevent.model.CellUsage;
import com.three55.callevent.utils.NetworkType;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;

public class EventCollectorService extends EventCollectorGrpc.EventCollectorImplBase {

    private static final Logger logger = LogManager.getLogger(CallEventServer.class);

    @Autowired
    private CellUsageDao dao;

    public EventCollectorService() {
        logger.info("EventCollectorService created");
    }

    @Override
    public void publish(CallEventProto.CallEvent request, StreamObserver<CallEventProto.Acknowledgement> responseObserver) {

        logger.info("received event: " + request.toString());

        CellUsage cu = copyData(request);

        dao.saveCellphoneUsage(cu);

        CallEventProto.Acknowledgement ack = CallEventProto.Acknowledgement.newBuilder()
                .setResponseCode(0)
                .build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
        logger.info("response sent");
    }

    private CellUsage copyData(CallEventProto.CallEvent request) {
        ZoneId zoneId = ZoneId.systemDefault();

        CellUsage cu = new CellUsage();
        cu.setDeviceBrand(request.getDeviceBrand());
        cu.setDeviceModel(request.getDeviceModel());
        cu.setDeviceSignalDb(request.getSignalDbm());
        cu.setDate(Instant.ofEpochMilli(request.getEventTime()).atZone(zoneId).toLocalDate());
        cu.setNetworkType(NetworkType.fromString(request.getNetworkType()));
        cu.setNetworkName(request.getNetworkName());
        cu.setMnc(request.getNetworkMnc());
        cu.setMcc(request.getNetworkMcc());

        return cu;

    }

}
