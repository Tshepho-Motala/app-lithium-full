Test scenario:

- VUs=100
- Duration=10m

## Load test statistics

[Full report](k6_run_report.txt)

```
    █ balance

       █ balance

         ✓ is status 200
         ✓ is body errorCode 0

     █ redeemToken

       █ redeemToken

         ✓ is status 200
         ✓ is body errorCode 0

     █ createToken

       █ createToken

         ✓ is status 200
         ✓ is body errorCode 0

     █ balance->placeBet

       █ balance

         ✓ is status 200
         ✓ is body errorCode 0

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

     █ placeBet->end

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ end

         ✓ is status 200
         ✓ is body errorCode 0

     █ balance->placeBet->end

       █ balance

         ✓ is status 200
         ✓ is body errorCode 0

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ end

         ✓ is status 200
         ✓ is body errorCode 0

     █ placeBet->awardWinnings

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ awardWinnings

         ✓ is status 200
         ✓ is body errorCode 0

     █ balance->placeBet->awardWinnings->end

       █ balance

         ✓ is status 200
         ✓ is body errorCode 0

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ awardWinnings

         ✓ is status 200
         ✓ is body errorCode 0

       █ end

         ✓ is status 200
         ✓ is body errorCode 0

     █ startGame

       █ startGame

         ✓ is status 200
         ✓ is body status 0

     █ demoGame

       █ demoGame

         ✓ is status 200
         ✓ is body status 0

     █ placeBet->rollbackBet

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ rollbackBet

         ✓ is status 200
         ✓ is body errorCode 0

     █ balance->placeBet->rollbackBet

       █ balance

         ✓ is status 200
         ✓ is body errorCode 0

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ rollbackBet

         ✓ is status 200
         ✓ is body errorCode 0

     █ balance->placeBet->rollbackBet->end

       █ balance

         ✓ is status 200
         ✓ is body errorCode 0

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ rollbackBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ end

         ✓ is status 200
         ✓ is body errorCode 0

     █ balance->placeBet->end->rollbackBet

       █ balance

         ✓ is status 200
         ✓ is body errorCode 0

       █ placeBet

         ✓ is status 200
         ✓ is body errorCode 0

       █ end

         ✓ is status 200
         ✓ is body errorCode 0

       █ rollbackBet

         ✓ is status 200
         ✓ is body errorCode 0

     █ voidBet

       █ voidBet

         ✓ is status 200
         ✓ is body errorCode 0

     █ alertWalletCallbackNotification

       █ alertWalletCallbackNotification

         ✓ is status 200
         ✓ is body errorCode 0

     █ credit

       █ credit

         ✓ is status 200
         ✓ is body errorCode 0

     checks..............................................: 100.00% ✓ 740860     ✗ 0     
     data_received.......................................: 202 MB  334 kB/s
     data_sent...........................................: 203 MB  335 kB/s
     group_duration......................................: avg=217.37ms min=6.34ms   med=167.62ms max=1.04s    p(90)=415.76ms p(95)=633.98ms
     http_req_blocked....................................: avg=13.37µs  min=1.44µs   med=4µs      max=21.71ms  p(90)=5.73µs   p(95)=6.54µs  
     http_req_connecting.................................: avg=5.97µs   min=0s       med=0s       max=18.75ms  p(90)=0s       p(95)=0s      
     http_req_duration...................................: avg=162.47ms min=6ms      med=163.46ms max=875.66ms p(90)=189.5ms  p(95)=204.02ms
       { expected_response:true }........................: avg=162.47ms min=6ms      med=163.46ms max=875.66ms p(90)=189.5ms  p(95)=204.02ms
       { requestType:alertWalletCallbackNotification }...: avg=137.11ms min=6.18ms   med=136.18ms max=193.69ms p(90)=151.85ms p(95)=156.71ms
       { requestType:awardWinnings }.....................: avg=174.62ms min=117.08ms med=171.95ms max=406.86ms p(90)=192.2ms  p(95)=200.54ms
       { requestType:balance }...........................: avg=144.24ms min=80.08ms  med=141.3ms  max=875.66ms p(90)=158.09ms p(95)=164.82ms
       { requestType:createToken }.......................: avg=112.93ms min=58.16ms  med=109ms    max=437.01ms p(90)=124.18ms p(95)=130.7ms 
       { requestType:credit }............................: avg=112.58ms min=6ms      med=111.41ms max=177.68ms p(90)=125.96ms p(95)=131.24ms
       { requestType:demoGame }..........................: avg=158.16ms min=67.68ms  med=157.65ms max=257.6ms  p(90)=181.1ms  p(95)=189.09ms
       { requestType:end }...............................: avg=174.94ms min=72.03ms  med=171.9ms  max=427.97ms p(90)=193.27ms p(95)=202.35ms
       { requestType:placeBet }..........................: avg=173.89ms min=88.92ms  med=171.42ms max=563.42ms p(90)=190.28ms p(95)=197.81ms
       { requestType:redeemToken }.......................: avg=113.99ms min=78.34ms  med=109.79ms max=491.72ms p(90)=125.2ms  p(95)=131.34ms
       { requestType:startGame }.........................: avg=239.78ms min=136.91ms med=235.54ms max=463.06ms p(90)=275.6ms  p(95)=294.56ms
       { requestType:voidBet }...........................: avg=170.14ms min=17.86ms  med=168.81ms max=293.43ms p(90)=186.63ms p(95)=193.43ms
     http_req_failed.....................................: 0.00%   ✓ 0          ✗ 370430
     http_req_receiving..................................: avg=105.21µs min=20.79µs  med=56.91µs  max=28.47ms  p(90)=104.7µs  p(95)=244.97µs
     http_req_sending....................................: avg=38.5µs   min=8.88µs   med=23.52µs  max=20.56ms  p(90)=34.55µs  p(95)=45.27µs 
     http_req_tls_handshaking............................: avg=0s       min=0s       med=0s       max=0s       p(90)=0s       p(95)=0s      
     http_req_waiting....................................: avg=162.33ms min=5.84ms   med=163.32ms max=875.35ms p(90)=189.35ms p(95)=203.87ms
     http_reqs...........................................: 370430  612.903871/s
       { requestType:alertWalletCallbackNotification }...: 10895   18.026584/s
       { requestType:awardWinnings }.....................: 21790   36.053169/s
       { requestType:balance }...........................: 76265   126.186091/s
       { requestType:createToken }.......................: 10895   18.026584/s
       { requestType:credit }............................: 10895   18.026584/s
       { requestType:demoGame }..........................: 10895   18.026584/s
       { requestType:end }...............................: 54475   90.132922/s
       { requestType:placeBet }..........................: 98055   162.23926/s
       { requestType:redeemToken }.......................: 10895   18.026584/s
       { requestType:startGame }.........................: 10895   18.026584/s
       { requestType:voidBet }...........................: 10895   18.026584/s
     iteration_duration..................................: avg=5.54s    min=4.39s    med=5.47s    max=10.2s    p(90)=5.76s    p(95)=5.82s   
     iterations..........................................: 10895   18.026584/s
     vus.................................................: 90      min=90       max=100 
     vus_max.............................................: 100     min=100      max=100 
```

## Profiling statistics

![Telemetry](jprofile/telemetry.png)

Legend for this graph:
1. Memory:
- ![#04B404](https://placehold.it/15/04B404/000000?text=+) `Free size`
- ![#2E2EFE](https://placehold.it/15/2E2EFE/000000?text=+) `Used size`
2. Classes
- ![#04B404](https://placehold.it/15/04B404/000000?text=+) `Non-CPU profiled classes`
- ![#2E2EFE](https://placehold.it/15/2E2EFE/000000?text=+) `CPU profiled classes`
3. Threads
- ![#04B404](https://placehold.it/15/04B404/000000?text=+) `Runnable threads`
- ![#DF0101](https://placehold.it/15/DF0101/000000?text=+) `Blocked threads`
- ![#2EFEF7](https://placehold.it/15/2EFEF7/000000?text=+) `Threads in net I/O`
- ![#FACC2E](https://placehold.it/15/FACC2E/000000?text=+) `Waiting threads`