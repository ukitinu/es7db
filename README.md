# Elasticsearch 7 Database
## Simple Java implementation of Elasticsearch 7 APIs
* Java 8+.
* Easily configurable through an external *.properties* file.  
* Simple interface to manage document creation/deletion/update, both in single and in bulk.  
* Rich and adaptable query creation.  
* Flexible and easy-to-use "generic document" class, to have a more convenient class to use if deserialisation is not possible.  
* Streamlined conversion from JSON to internal classes, to simplify communication with frontend.  

This project is a simplified version of one I had to make for a customer not too long ago: we needed an internal version of ES7's APIs, easy to configure, understand and use.  
I decided to (at least temporarily) remove all of its specialised searches/methods/aggregations and publish it, both for future me and for anyone in need of code or examples.  

I wouldn't use this as a library, I don't think that this could ever become a "complete" project.  
It is rather a starting point to customise as needed, no restrictions whatsoever.  

Some (basic) examples can be found in *src/test*.
