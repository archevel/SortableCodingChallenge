# SortableChallenge

A implementation to solve the coding challenge posted by [Sortable](http://sortable.com/blog/coding-challenge/). It's made with funzies and little giggles! oh.... and Scala :)

## How to run it
 1. Clone the repo (or download and extract it). 
 2. go into project dir
 3. type: `chmod u+x ./sbt`
 4. type: `./sbt run`

There should now be three new files in your dir:

* result_with_threshold_0.0111.txt
* highRecall_with_threshold_0.txt
* highPrecision_with_threshold_0.02.txt

Using `sbt "run 0.015"` or similar you can run the code with an alternate threshold for what 
is written to the result file (highRecall and highPrecision files will still get generated)

  