# JMeter Test

* Test Date: 2016-08-22
* Jmeter v 3.0 r1743807
* Commit: 3d725d487cd3d65456891a43da4135bb455af090
* Test time: 1:20:16

Used mariadb for the first time, with default settings, against the ExtrDB backend (InnoDB replacement).

Concurrent requests were going through without any slowdown seemingly. With mysql, I saw centralised locking on different row updates, causing every request to reach approx 1-2 seconds when doing 30 concurrent requests.

