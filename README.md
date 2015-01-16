[![Build Status](https://travis-ci.org/fommil/kerbal-calculator.svg?branch=master)](https://travis-ci.org/fommil/kerbal-calculator)

# kerbal-calculator

Once you've designed your payload (or any combination of upper
stages), and plan your manoeuvres, you'll know how much mass you need
to get through a minimum deltav and how fast you'd like to be able to
do it (to avoid missing flight windows).

To work out your engine and fuel requirements, you'll either need to
do a lot of trial and error or solve the ideal rocket equation with
some educated guesses. This calculator will do all that for you.

The calculator is available at [http://fommil.github.io/kerbal](http://fommil.github.io/kerbal)


## Run Locally

To use, clone and run like so

```scala
sbt "run 1200 10 50 false Large"
```

(You'll need [sbt](http://www.scala-sbt.org/download.html) and a Java Runtime)

Input parameters being:

1. **minimum deltav**
2. **payload mass**
3. **minimum acceleration**
4. **atmosphere**
5. **payload size**

Results will be ordered by the minimum initial mass of the engine stage.

e.g. the above returns

```
Rockomax "Mainsail" with 6.9t (86%) in a Rockomax X200-16 [a = 62.8, dv = 1200, cost = 8747, mass = 13.9t]
Rockomax "Mainsail" with 7.0t (87%) in a Rockomax X200-16 [a = 62.6, dv = 1212, cost = 8755, mass = 14.0t]
Rockomax "Mainsail" with 7.0t (88%) in a Rockomax X200-16 [a = 62.4, dv = 1224, cost = 8762, mass = 14.0t]
Rockomax "Mainsail" with 7.1t (89%) in a Rockomax X200-16 [a = 62.2, dv = 1235, cost = 8769, mass = 14.1t]
...
S3 KS-25x4 Cluster with 33.8t (47%) in a Kerbodyne S3-14400 [a = 50.3, dv = 2683, cost = 51697, mass = 53.6t]
```
