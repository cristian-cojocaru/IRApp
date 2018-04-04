# IRApp
Information retrieval system for Romanian language based on Apache Lucene.

Built with:
- Lucene 7.2.1
- SpringBoot 1.5.10
- Thymeleaf 2.1

The project has two profiles: StartIndexer and StartServer

#### StartIndexer profile
:small_orange_diamond: Place your files you want to be indexed in "IndexedFiles" folder.
Apache Tika is used to extract files content ([see Apache Tika supported document formats](https://tika.apache.org/0.9/formats.html)\)

:small_orange_diamond: Run "StartIndexing" (it will build an index in "Index" folder)

#### StartServer profile
You can make a query in a search box after a word from indexed files.

:small_orange_diamond: Run "StartServer"

:small_orange_diamond:  Access localhost:8080 and search a word

:small_orange_diamond: See results (file name, score file and content)