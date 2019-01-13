package com.three55.callevent.service;

import com.three55.callevent.CallEventProto;
import com.three55.callevent.EventCollectorGrpc;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventCollectorService extends EventCollectorGrpc.EventCollectorImplBase {

    private static final Logger logger = LogManager.getLogger(CallEventServer.class);

    public EventCollectorService() {
        logger.info("EventCollectorService created");
    }

    @Override
    public void publish(CallEventProto.CallEvent request, StreamObserver<CallEventProto.Acknowledgement> responseObserver) {

        logger.info("received event: " + request.toString());
        CallEventProto.Acknowledgement ack = CallEventProto.Acknowledgement.newBuilder()
                .setResponseCode(0)
                .build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
        logger.info("response sent");
    }
}
