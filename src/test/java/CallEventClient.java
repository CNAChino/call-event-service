import com.three55.callevent.CallEventProto.CallEvent;
import com.three55.callevent.EventCollectorGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class CallEventClient {

    private final ManagedChannel channel;
    private final EventCollectorGrpc.EventCollectorBlockingStub blockingStub;

    public CallEventClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    /** Construct client for accessing RouteGuide server using the existing channel. */
    public CallEventClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = EventCollectorGrpc.newBlockingStub(channel);
    }

    public void call() {
        CallEvent event = CallEvent.newBuilder()
                .setEventTime(System.currentTimeMillis())
                .setDeviceBrand("Samsung")
                .setAppVersion(1)
                .setDeviceModel("GS7")
                .build();
        try {
            blockingStub.publish(event);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return;
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        CallEventClient cec = new CallEventClient("localhost", 8080);
        try {
            for (int i = 0; i < 5; i++) {
                cec.call();
            }
        } finally {
            cec.shutdown();
        }
    }

}
