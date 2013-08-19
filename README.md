Empirical Geographical Coordinates Converter
================================

Task
-------------------------

Let there is a set of points in a coordinate system that we will call as the source coordinate system.
 
We need to convert coordinates of points from the source coordinate system to another one (say WGS 84) that we will call the destination coordinate system.  

Suppose there is not information what the source coordinate system is or there is no the formula to converse coordinates from the source system to the destination one.

Suppose also we can bind SOME points of the set to the destination coordinate system someway. For instance they are known pois. 

The problem is how to converse coordinates of other points from the set.

Solution 
-------------------------
* Prepare two sets of coordinates. The first one is coordinates of bound points in the source coordinate system. The second one is coordinates of bound points in the destination coordinate system.  
* Run ConverterFinder.  It finds the conversation of coordinates in the source coordinate system of the bound points to coordinates in the destination coordinate system. 
* Check accuracy of the conversation.  If quality of the conversation is not acceptable we can select other points or check binding and run the conversation finder again. 
* It everything is OK run the file converter (FileConverter2) for the whole set of points.  
