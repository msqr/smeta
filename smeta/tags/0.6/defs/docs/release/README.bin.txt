=======================================================================
sMeta: simple metadata API for Java
Version @VERSION@ @BUILD_DATE@
=======================================================================

This is the binary distribution of sMeta. sMeta is a very simple, 
extensible Java API for reading metadata from files of various formats, 
such as ID3 tags from MP3 files, EXIF data from JPEG files, chunks 
from PNG files, etc.


SUPPORTED FORMATS =====================================================

sMeta presently supports the following file formats:

  1) MP3 - ID3 (versions 1, 1.1, 2.2, 2.3, 2.4)
  2) JPEG - EXIF
  3) PNG
  4) Video, via Java Media Framework or QuickTime


INSTALLATION ==========================================================

sMeta is released as a JAR file, which you can add to your application
classpath to make use of. Some of the metadata implementations depend
on other JARs (included with this release in the "lib" directory).


QUICK START ===========================================================

The general use of sMeta follows this pattern:

  1) Get a MetadataResourceFactoryManager instance. 
  
     In most cases, the following accomplishes this:
    
     MetadataResourceFactoryManager manager 
        = MetadataResourceFactoryManager.getDefaultManagerInstance();
        
  2) Get a MetadataResourceFactory for this file type you are using.
  
     This is done by using a File object or a MIME type. For a File
     the code looks like this:
     
     File file = new File("/some/path/to/file.mp3");
     MetadataResourceFactory factory 
	    = manager.getMetadataResourceFactory(file);
        
  3) Get a MetadataResource for this file.
  
     This is done by passing a File to the MetadataResourceFactory
     you got from step #2:

     MetadataResource metaResource 
        = factory.getMetadataResourceInstance(file);
     
        
From this point you have a few simple methods to use to extract the 
available metadata keys and/or metadata values for specific keys. To
get all the available keys, call

  Iterable<String> getParsedKeys();
  
which returns all the available keys. To then get a specific value for
one of those keys, call

  Object getValue(String key, Locale locale);
  
Each key is allowed to have more than one value. The previous method
simply returns the first available value. To get all available values,
call

  Iterable<?> getValues(String key, Locale locale);


CONFIGURATION =========================================================

sMeta's MetadataResourceFactoryManager uses a set of properties files 
to register support of different file types and MIME types. It will 
look in up to three locations for a given type registration, and use 
the first suitable implementation found. The search paths are classpath
relative:

  1) MetadataResourceFactoryManager's configured managerProperties path
  2) smeta.properties
  3) META-INF/smeta.properties
  
The format of the properties file is just 

  smeta.factory.KEY = CLASS
  
where KEY is either a file extension or a MIME type. The CLASS is the
fully-qualified class name of something that implements the
magoffin.matt.meta.MetadataResourceFactory interface. For example
(class names shorted for brevity):

smeta.factory.mp3 = meta.audio.ID3MetadataResourceFactory
smeta.factory.audio/mpeg = meta.audio.ID3MetadataResourceFactory
smeta.factory.jpg = meta.image.EXIFMetadataResourceFactory
smeta.factory.image/jpeg = meta.image.EXIFMetadataResourceFactory


