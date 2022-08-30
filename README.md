***VALS library***

**VVal** 
represent a variable-type value.
Currently following basic types are supported:
string, double, long (and int as long), datetime (see *VType0* type).
There are also a special "null" type and nested/array types (for values of VVals).
VVal can also contain an option of any those types (see *VType* type).
The type of value in VVal are specified using **VT** enum.
Each VVal also supports tag string label and string attributes that may or may not be used.

**VVals**
represent a sequence of VVal objects. This may be a document (noSQL) or a row of some table. 
The tag of VVal's may be id (i.e. interpreted as unique key) or may not - it depends on application.
There is also the tag of whole VVals - it may be used as class id (or not).
The descr of VVals is like attributes of VVal and may describe the header of the table or the metadata of the fields used in documents.

**VTable**
represent a set or array of VVals objects. This may be a collection of documents or a table.
The tag of VVal's may be id of the collection or describe a class of document's in the collection.
The header VVals are to describe the header of table (if given).
There is a flag "isArray" of VVals that shows it VVal's have similar structures so they may represent the rows of tables either set of documents (noSQL meaning). 
