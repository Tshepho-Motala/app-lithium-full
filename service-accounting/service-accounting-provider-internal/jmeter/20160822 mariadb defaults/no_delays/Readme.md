# JMeter Test

* Test Date: 2016-08-22
* Jmeter v 3.0 r1743807
* Commit: 3d725d487cd3d65456891a43da4135bb455af090
* Test time: 1:20:16

Used mariadb for the first time, with default settings, against the XtraDB backend (InnoDB replacement).

I max my machine out with to 100% cpu, with java taking 70% and mysql only 30%. 

8 threads, no delays, just hammering the bet transaction, and I am able to get up to 60 bets per second through. Clearly my machine is the limit though.

Will rerun this as soon as the cluster is up and running.
