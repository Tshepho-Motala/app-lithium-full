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

     checks............................: 100.00% ✓ 732800     ✗ 0     
     data_received.....................: 194 MB  323 kB/s
     data_sent.........................: 178 MB  295 kB/s
     group_duration....................: avg=218.6ms  min=8.24ms  med=171.95ms max=1.21s    p(90)=376.75ms p(95)=529.43ms
     http_req_blocked..................: avg=10.3µs   min=1.33µs  med=3.79µs   max=16.77ms  p(90)=5.51µs   p(95)=6.31µs  
     http_req_connecting...............: avg=3.33µs   min=0s      med=0s       max=15.75ms  p(90)=0s       p(95)=0s      
     http_req_duration.................: avg=163.39ms min=7.91ms  med=168.39ms max=911.41ms p(90)=190.59ms p(95)=199.93ms
       { expected_response:true }......: avg=163.39ms min=7.91ms  med=168.39ms max=911.41ms p(90)=190.59ms p(95)=199.93ms
       { requestType:awardWinnings }...: avg=178.44ms min=7.91ms  med=176.16ms max=417.22ms p(90)=194.99ms p(95)=204.11ms
       { requestType:balance }.........: avg=147.71ms min=60.71ms med=144.83ms max=911.41ms p(90)=161.75ms p(95)=170.15ms
       { requestType:createToken }.....: avg=114.12ms min=89.02ms med=111.47ms max=424.25ms p(90)=126.52ms p(95)=132.9ms 
       { requestType:end }.............: avg=177.89ms min=8.9ms   med=175.12ms max=438.63ms p(90)=194.66ms p(95)=205.1ms 
       { requestType:placeBet }........: avg=180.64ms min=24.34ms med=177.15ms max=706.82ms p(90)=197.04ms p(95)=209.75ms
       { requestType:redeemToken }.....: avg=115.63ms min=89.68ms med=112.46ms max=518.89ms p(90)=127.62ms p(95)=134.44ms
     http_req_failed...................: 0.00%   ✓ 0          ✗ 366400
     http_req_receiving................: avg=105.88µs min=19.96µs med=58.89µs  max=35.27ms  p(90)=105.84µs p(95)=246.74µs
     http_req_sending..................: avg=37.58µs  min=9.72µs  med=23.55µs  max=26.16ms  p(90)=34.54µs  p(95)=44.4µs  
     http_req_tls_handshaking..........: avg=0s       min=0s      med=0s       max=0s       p(90)=0s       p(95)=0s      
     http_req_waiting..................: avg=163.25ms min=7.8ms   med=168.25ms max=911.12ms p(90)=190.43ms p(95)=199.77ms
     http_reqs.........................: 366400  609.652614/s
       { requestType:awardWinnings }...: 45800   76.206577/s
       { requestType:balance }.........: 91600   152.413153/s
       { requestType:createToken }.....: 22900   38.103288/s
       { requestType:end }.............: 68700   114.309865/s
       { requestType:placeBet }........: 114500  190.516442/s
       { requestType:redeemToken }.....: 22900   38.103288/s
     iteration_duration................: avg=2.62s    min=1.97s   med=2.6s     max=6.21s    p(90)=2.71s    p(95)=2.78s   
     iterations........................: 22900   38.103288/s
     vus...............................: 100     min=100      max=100 
     vus_max...........................: 100     min=100      max=100
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