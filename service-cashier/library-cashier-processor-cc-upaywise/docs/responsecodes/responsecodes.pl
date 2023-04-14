#!/usr/bin/perl -W

use strict;

while (1) {

  my $line = <>;
  if (!$line) { exit 0 };
  my $line2 = <>;
  if (!$line2) { exit 0 };

  chomp($line);
  chomp($line2);

#  print "Code: " . $line . "\n";
#  print "Message: " . $line2 . "\n";

  print "RC" . $line . '("' . $line . '", "' .  $line2 . '"),' . "\n";

}