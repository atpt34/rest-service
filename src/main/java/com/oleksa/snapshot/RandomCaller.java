package com.oleksa.snapshot;

import com.oleksa.snapshot.entity.RandomRequest;
import com.oleksa.snapshot.entity.RandomResponse;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class RandomCaller {

    private WebClient webClient1 = WebClient.create("https://api.random.org/json-rpc/2/invoke");
    private WebClient webClient2 = WebClient.create("https://api.random.org/json-rpc/2/invoke");
    private WebClient webClient3 = WebClient.create("https://api.random.org/json-rpc/2/invoke");

    public RandomCaller() {
        System.out.println("rc ctor!");
    }

    private Mono<RandomResponse> makeCall(RandomRequest request, WebClient webClient) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(RandomResponse.class);
    }

    @Test
    public void test() {
        monitorMethod("blah",
                () -> makeCall(RandomRequest.builder().id(13).build(), webClient1).block());
//        RandomRequest request = RandomRequest.builder().id(13).build();
//        System.out.println(request);
//        RandomResponse block = makeCall(request, webClient1).block();
//        System.out.println(block);
    }

    @Test
    public void testImprove() { //2955
        monitorMethod("NEW", this::doNew);
    }

    public RandomResponse doNew() { // 3619 -> 619
        return Mono.zip(
            makeCall(RandomRequest.builder().id(1).build(), webClient1),
            makeCall(RandomRequest.builder().id(8).build(), webClient2)
                .zipWhen(r -> makeCall(RandomRequest.builder().id(r.getId() - 1).build(), webClient3),
                    (r1, r2) -> {
                        String s1 = r1.getResult().getRandom().getData().get(0).toString();
                        String s2 = r2.getResult().getRandom().getData().get(0).toString();
                        r1.setJsonrpc(s1 + s2);
                        return r1; }))
            .map(tuple2 -> {
                String s = tuple2.getT1().getResult().getRandom().getData().get(0).toString();
                String s12 = tuple2.getT2().getJsonrpc();
                tuple2.getT1().setJsonrpc(s + s12);
                return tuple2.getT1();
            })
            .block();
    }

    public RandomResponse doOld() { // 3308 -> 634
        return
                makeCall(RandomRequest.builder().id(1).build(), webClient1)
                .zipWith(
                    makeCall(RandomRequest.builder().id(8).build(), webClient2)
                        .zipWhen(r -> makeCall(RandomRequest.builder().id(r.getId() - 1).build(), webClient3),
                              (r1, r2) -> {
                               String s1 = r1.getResult().getRandom().getData().get(0).toString();
                               String s2 = r2.getResult().getRandom().getData().get(0).toString();
                               r1.setJsonrpc(s1 + s2);
                               return r1; }))
                .map(tuple2 -> {
                    String s = tuple2.getT1().getResult().getRandom().getData().get(0).toString();
                    String s12 = tuple2.getT2().getJsonrpc();
                    tuple2.getT1().setJsonrpc(s + s12);
                    return tuple2.getT1();
                })
                .block();
    }

    void monitorMethod(String name, Runnable runnable) {
        privateMonitorMethod(name, runnable);
    }

    private void privateMonitorMethod(String name, Runnable runnable) {
        StopWatch watch = new StopWatch();
        watch.start();
        runnable.run();
        watch.stop();
        System.out.println("========================================");
        System.out.println(name + " took " + watch.getLastTaskTimeMillis() + " ms.");
        System.out.println("========================================");
    }
}
