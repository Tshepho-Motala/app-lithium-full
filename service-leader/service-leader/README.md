# Service Leader
A microservice with the sole responsibility of coordinating leadership amongst leader aware module instances.

# Configuration

### Properties

Take note of the external property <b>lithium.leader.heartbeat-ms</b> which is centralized and applicable to all modules that are leader aware.

Property | Description | Example | 
| -------- | -------- | -------- |
| lithium.services.leader.cleanup-job.initial-delay-ms | The initial delay (in milliseconds) before starting the cleanup job | <b>240000</b> <br> <i>This should be larger than lithium.services.leader.instance.keep-alive-ms by a minimum of * 2<i> |
| lithium.services.leader.cleanup-job.delay-ms | The delay (in milliseconds) between the cleanup job cycles | <b>5000</b> <br> <i>This should be as small as possible, a sane default is 5 seconds |
| lithium.services.leader.instance.keep-alive-ms | The amount of time (in milliseconds) to keep an instance alive without receiving a heartbeat. If the threshold is breached, the module instance will be cleaned up. If this module instance is the leader, then another instance of the module type will pick up leadership on that modules next heartbeat | <b>120000</b> <br> This should be larger than lithium.leader.heartbeat-ms by a minimum of * 2 |