# Suspected bug reproducer

This project reproduces a bug (or potentially multiple bugs) in either the
SmallRye Reactive Messaging RabbitMQ Connector, or Quarkus, or the underlying Vertx client, or
somewhere else entirely.

## The issue

When the application receives a message that it cannot handle, e.g. because it has no `content_type`
property set (leading to a byte array as payload on the receiving end), that message is correctly
NACKed.
However, valid messages appear to be automatically NACKed as well, without any noticeable error
logging.

## Steps to reproduce

1. Start an instance of RabbitMQ with the web ui port exposed, or find a way to easily publish a
   message.
   A [docker compose file](src/main/resources/docker-compose.yaml) has been added for this purpose.
   The RabbitMQ instance provided by Dev Services currently doesn't expose the web UI port.
2. Start the application, which will declare the required exchange and queue.
3. As a bonus, check the [application's health endpoint output](http://localhost:9090/q/health). It
   should be UP.
4. In the [rabbitMQ web ui](http://localhost:15762/), log in using the default
   credentials (`guest/guest`).
5. In the web ui, go to
   the [exchanges overview page](http://localhost:15672/#/exchanges/%2F/some-message-exchange)
   and publish a message with a property `content_type` set to `application/json` and a simple JSON
   payload, e.g. `{
   "message":"This works"
   }`
6. Check the logs for correct handling of the message. It should log the
   raw `IncomingRabbitMQMessage` being received
   and the payload being deserialized to a `SomeMessage` instance (see
   the [implementation](src/main/java/be/steen/reproducer/quarkus/rabbitmq/SomeMessageListener.java)
   as to why).
7. Optionally check the application health again, which should still be UP.
8. In the RabbitMQ web ui, remove the `content_type` property and hit Publish again.
9. Check the application logs. There should be an exception and a log entry stating the message was
   NACKed.
10. Check the application health. It should now say DOWN, with a new "channel" with the message
    listener method as its name, and the exception as its value.
11. Repeat step 5 (by adding the `content_type` property back). Hit the button a few times for good
    measure.
12. Check the logs, they should not mention the new valid messages. (it's possible new error logs
    are being added due to the NACKed message being redelivered)
13. In the RabbitMQ web UI,
    check [the queue's overview page](http://localhost:15672/#/queues/%2F/some-queue). The number of
    NACKed messages is higher than one, which leads me to believe all subsequent messages are
    silently NACKed despite being valid (due to *something* being in an invalid state, maybe?).

## Expected behaviour

One "invalid" message to not cause subsequent (valid) messages to be silently NACKed, nor cause a
failing
health check.
(Disabling the reactive messaging health checks obviously mitigates this but that does not seem
appropriate, and in any case does not fix the issue of valid messages being NACKed.)

## Note

As described in
the [message listener](src/main/java/be/steen/reproducer/quarkus/rabbitmq/SomeMessageListener.java),
I encountered a different issue while developing this in my production application as well as this
reproducer. I only mention it at all because if this has not been reported/fixed this reproducer can
be used for that issue as well with only one minor code change. See the commented out code there for
more information.