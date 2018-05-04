# PDF to Excel Converter
Convert pdf text information to excel.
## Using java application
Convert from the java application in org/eadge/extractpdfexcel/0.1 directory.
```
java -jar extractpdfexcel-0.1.jar source.pdf result.xcl
```

To specify some options, consult the help.
```
java -jar extractpdfexcel-0.1.jar
```
## Convert in java
### Import in your java project with Maven
Add the repository:
```
<repository>
    <id>Extract-PDF-Excel</id>
    <url>https://raw.githubusercontent.com/eadgyo/Extract-PDF-Excel/master/</url>
</repository>
```
And the dependency:
```
<dependency>
    <groupId>org.eadge</groupId>
    <artifactId>extractpdfexcel</artifactId>
    <version>0.1</version>
</dependency>
```
### One step conversion
You can convert your pdf into an Excel file in java application.
```
PdfConverter.createExcelFile("File.pdf", "File.xcl");
```
### Four steps conversion
You can also execute each steps, in case you would like to access to data before creating the file.
#### Extract Data
Text information is extracted in keeps in blocks.
```
ExtractedData extractedData = PdfConverter.extractFromFile(sourcePDFPath, textBlockIdentifier);
```
#### Sort Data
Blocks are sorted, lines and columns are created.
```
SortedData sortedData = PdfConverter.sortExtractedData(extractedData, lineAxis, columnAxis, true);
```
#### Create XCL Pages
2D array containing text blocks are created.
```
ArrayList<XclPage> excelPages = PdfConverter.createExcelPages(sortedData);
```
#### Create sheets
Using POI Library, you can create xcl sheets.
```
HSSFSheet excelSheet = PdfConverter.createExcelSheet("sheetName", workbook, excelPage);
```
### Visualize XCLPage
You can also visualize all the created excel sheets.
```
PdfConverter.displayXCLPages(excelPages);
```
