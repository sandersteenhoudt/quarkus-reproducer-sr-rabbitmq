package be.steen.reproducer.quarkus.rabbitmq;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SomeMessageListener {

  private static final Logger logger = LoggerFactory.getLogger(SomeMessageListener.class);
  @Incoming("some-channel")
  Uni<Void> handleSomeMessage(final Message<JsonObject> incomingMessage) {

    return Uni.createFrom().item(incomingMessage)
        .log("Raw smallrye message")
        .onItem()
        .transform(message-> message.getPayload().mapTo(SomeMessage.class))
        .log("Deserialized json message")
        .onItem().ignore().andContinueWithNull();
  }
  /*
  This doesn't work due to a mysterious class-cast exception
  SRMSG00200: The method be.steen.reproducer.quarkus.rabbitmq.SomeMessageListener#handleSomeMessage has thrown an exception: java.lang.ClassCastException: class io.vertx.core.json.JsonObject cannot be cast to class be.steen.reproducer.quarkus.rabbitmq.SomeMessage (io.vertx.core.json.JsonObject is in unnamed module of loader io.quarkus.bootstrap.classloading.QuarkusClassLoader @3754a4bf; be.steen.reproducer.quarkus.rabbitmq.SomeMessage is in unnamed module of loader io.quarkus.bootstrap.classloading.QuarkusClassLoader @3e850122)
	at be.steen.reproducer.quarkus.rabbitmq.SomeMessageListener_SmallRyeMessagingInvoker_handleSomeMessage_948b5bf6ca5d9b44585ad0e93ef8bd6e5acc2a1e.invoke(Unknown Source)
	at io.smallrye.reactive.messaging.providers.AbstractMediator.invoke(AbstractMediator.java:95)
	at io.smallrye.reactive.messaging.providers.AbstractMediator.lambda$invokeOnMessageContext$1(AbstractMediator.java:103)
	at io.smallrye.reactive.messaging.providers.locals.LocalContextMetadata.lambda$invokeOnMessageContext$0(LocalContextMetadata.java:34)
	at io.smallrye.reactive.messaging.providers.locals.LocalContextMetadata.lambda$invokeOnMessageContext$2(LocalContextMetadata.java:55)
	at io.smallrye.context.impl.wrappers.SlowContextualConsumer.accept(SlowContextualConsumer.java:21)
	at io.smallrye.mutiny.operators.uni.builders.UniCreateWithEmitter.subscribe(UniCreateWithEmitter.java:22)
	at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
	at io.smallrye.mutiny.operators.uni.UniOnItemTransformToUni.subscribe(UniOnItemTransformToUni.java:25)
	at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
	at io.smallrye.mutiny.operators.uni.UniOnItemOrFailureFlatMap.subscribe(UniOnItemOrFailureFlatMap.java:27)
	at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
	at io.smallrye.mutiny.converters.uni.UniToMultiPublisher$UniToMultiSubscription.request(UniToMultiPublisher.java:74)
	at io.smallrye.mutiny.operators.multi.MultiFlatMapOp$FlatMapInner.onSubscribe(MultiFlatMapOp.java:601)
	at io.smallrye.mutiny.converters.uni.UniToMultiPublisher.subscribe(UniToMultiPublisher.java:26)
	at io.smallrye.mutiny.groups.MultiCreate$1.subscribe(MultiCreate.java:163)
	at io.smallrye.mutiny.operators.multi.MultiFlatMapOp$FlatMapMainSubscriber.onItem(MultiFlatMapOp.java:193)
	at io.smallrye.mutiny.operators.multi.MultiMapOp$MapProcessor.onItem(MultiMapOp.java:50)
	at io.smallrye.mutiny.subscription.MultiSubscriber.onNext(MultiSubscriber.java:61)
	at io.smallrye.mutiny.subscription.SafeSubscriber.onNext(SafeSubscriber.java:99)
	at io.smallrye.mutiny.helpers.HalfSerializer.onNext(HalfSerializer.java:31)
	at io.smallrye.mutiny.helpers.StrictMultiSubscriber.onItem(StrictMultiSubscriber.java:85)
	at io.smallrye.mutiny.operators.multi.MultiOperatorProcessor.onItem(MultiOperatorProcessor.java:100)
	at io.smallrye.reactive.messaging.providers.locals.ContextDecorator$ContextMulti$ContextProcessor.lambda$onItem$1(ContextDecorator.java:78)
	at io.vertx.core.impl.AbstractContext.dispatch(AbstractContext.java:100)
	at io.vertx.core.impl.AbstractContext.dispatch(AbstractContext.java:63)
	at io.vertx.core.impl.EventLoopContext.lambda$runOnContext$0(EventLoopContext.java:38)
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:164)
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:469)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:503)
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:986)
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:833)

2022-05-30 17:09:54,080 WARN  [io.sma.rea.mes.rabbitmq] (vert.x-eventloop-thread-2) SRMSG17013: A message sent to channel `some-channel` has been nacked, ignoring the failure and marking the RabbitMQ message as rejected

   */
//  @Incoming("some-channel")
//  Uni<Void> handleSomeMessage(final SomeMessage someMessage) {
//    return Uni.createFrom().item(someMessage)
//        .log()
//        .onItem().ignore().andContinueWithNull();

//  }


}
