# SortableChallenge

A implementation to solve the coding challenge posted by [Sortable](http://sortable.com/blog/coding-challenge/). It's made with funzies and little giggles! oh.... and Scala :)

## How to run it
*Note: I recomended to run this on a machine with quite a bit of ram since it forks of a JVM with -Xmx2G as an argument, also the more cores the better :)*  
 1. Clone the repo (or download and extract it). 
 2. go into project dir
 3. type: `chmod u+x ./sbt`
 4. type: `./sbt run`

There should now be two new files in your dir:

* `result_with_threshold_0.0114.txt`
* `highRecall_with_threshold_0.txt` 

Using `sbt "run 0.015"` or similar you can run the code with an alternate threshold for what 
is written to the result file (the highRecall file will still get generated every run)


## Note on the implementation

To start with the program reads the input files located in src/main/resources and parses these into case classes (Product and Listing). The values in the products are then cleaned up using the ProductCleaner in the cleaners package. 

In the probability package resides the estimators that in essence computes the probability that the words occuring in a listing (after it's values has been cleaned) would classify it as a certain product. This estimation uses a similar style to spam detection where each Product is considered a category (like Spam or not Spam) for messages, i.e. the listings. After the estimation is complete a selector chooses the likeliest matching product for each listing. However, it also ensures that the manufacturer is similar (i.e. that some word in the listings manufacturer matches some word in the products manufacturer).